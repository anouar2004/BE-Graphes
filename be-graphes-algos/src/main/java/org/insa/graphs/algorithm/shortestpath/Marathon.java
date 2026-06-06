package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Point;

public class Marathon {

    // ---------------------------------------------------------------
    // Paramètres
    // ---------------------------------------------------------------

    private static final int    N             = 4;

    // Facteur de tortuosité : le chemin routier est ~1.3x le vol d'oiseau
    private static final double ALPHA         = 1.3;

    // Tolérance initiale sur le rayon vol d'oiseau (±15%)
    private static final double TOL_INIT_RATIO = 0.15;

    // Tolérance acceptable sur la longueur ROUTIÈRE d'un segment (±5%)
    // Un segment est "bon" si sa longueur est dans [(L/N)*(1-SEG_TOL), (L/N)*(1+SEG_TOL)]
    private static final double SEG_TOL       = 0.05;

    // Nombre max de candidats à tester pour trouver un bon segment
    private static final int    MAX_CANDIDATS = 10;

    // ---------------------------------------------------------------
    // sommetRayon
    // ---------------------------------------------------------------
    private static List<Node> sommetRayon(Node sc, Node s0,
                                          double rayon, double tol,
                                          Graph graphe) {
        if (tol > rayon) {
            return new ArrayList<>();
        }

        List<Node> admissibles = new ArrayList<>();
        for (Node s : graphe.getNodes()) {
            double distSc = Point.distance(sc.getPoint(), s.getPoint());
            double distS0 = Point.distance(s0.getPoint(), s.getPoint());

            if (distSc >= rayon - tol && distSc <= rayon + tol
             && distS0 >= rayon - tol && distS0 <= rayon + tol) {
                admissibles.add(s);
            }
        }

        if (admissibles.isEmpty()) {
            return sommetRayon(sc, s0, rayon, tol * 2, graphe);
        }
        return admissibles;
    }

    // ---------------------------------------------------------------
    // runAStar — retourne null si infaisable
    // ---------------------------------------------------------------
    private static Path runAStar(Node origin, Node destination,
                                 Set<Arc> arcsInterdits, Graph graphe) {

        ArcInspector base = ArcInspectorFactory.getAllFilters().get(0);

        ArcInspector inspector = new ArcInspector() {
            @Override public boolean isAllowed(Arc arc) {
                return base.isAllowed(arc) && !arcsInterdits.contains(arc);
            }
            @Override public double getCost(Arc arc) { return arc.getLength(); }
            @Override public int getMaximumSpeed()   { return base.getMaximumSpeed(); }
            @Override public org.insa.graphs.algorithm.AbstractInputData.Mode getMode() {
                return base.getMode();
            }
        };

        ShortestPathSolution sol = new AStarAlgorithm(
                new ShortestPathData(graphe, origin, destination, inspector)).run();

        return sol.isFeasible() ? sol.getPath() : null;
    }

    // ---------------------------------------------------------------
    // choisirMeilleurSegment
    //
    // Parmi les candidats, on lance A* sur les MAX_CANDIDATS premiers
    // et on retourne celui dont la longueur routière est la plus proche
    // de la cible segCible, en évitant de revenir sur precedent.
    // ---------------------------------------------------------------
    private static Path[] choisirMeilleurSegment(
            Node sc, List<Node> candidats, Node precedent,
            double segCible, Set<Arc> arcsInterdits, Graph graphe) {

        Path meilleurPath = null;
        Node meilleurNoeud = null;
        double meilleurEcart = Double.MAX_VALUE;

        // On mélange légèrement pour ne pas toujours prendre les mêmes
        List<Node> sousListe = new ArrayList<>();
        for (Node n : candidats) {
            if (!n.equals(precedent)) sousListe.add(n);
            if (sousListe.size() >= MAX_CANDIDATS) break;
        }
        // Si tous les candidats sont le précédent, on les accepte quand même
        if (sousListe.isEmpty()) {
            sousListe.addAll(candidats.subList(0, Math.min(MAX_CANDIDATS, candidats.size())));
        }

        for (Node candidat : sousListe) {
            Path seg = runAStar(sc, candidat, arcsInterdits, graphe);
            if (seg == null) continue;

            double ecart = Math.abs(seg.getLength() - segCible);
            System.out.printf("[Marathon]     candidat #%d : %.0f m (écart %.0f m)%n",
                    candidat.getId(), seg.getLength(), ecart);

            if (ecart < meilleurEcart) {
                meilleurEcart = ecart;
                meilleurPath  = seg;
                meilleurNoeud = candidat;
            }

            // Si on est dans la tolérance, on s'arrête
            if (ecart <= segCible * SEG_TOL) break;
        }

        if (meilleurPath == null) return null;
        return new Path[]{ meilleurPath, null };  // [0]=path, [1] non utilisé
        // on retourne aussi le nœud via un tableau de Node séparé
    }

    // ---------------------------------------------------------------
    // dorun — algorithme principal
    // ---------------------------------------------------------------
    public static Path dorun(Graph graphe, Node s0, double L) {

        double rayon    = (L / N) / ALPHA;
        double tol      = rayon * TOL_INIT_RATIO;
        double segCible = L / N;   // longueur routière cible par segment

        System.out.printf("[Marathon] L=%.0fm  N=%d  segCible=%.0fm  rayon=%.0fm%n",
                L, N, segCible, rayon);

        Node[]    sommetsDepart  = new Node[N + 1];
        sommetsDepart[0]         = s0;
        Set<Arc>  arcsInterdits  = new HashSet<>();
        List<Arc> tousLesArcs    = new ArrayList<>();
        Random    rand           = new Random();

        for (int i = 0; i < N; i++) {

            List<Node> admissibles = sommetRayon(
                    sommetsDepart[i], s0, rayon, tol, graphe);

            if (admissibles.isEmpty()) {
                System.err.println("[Marathon] INFEASIBLE : aucun sommet admissible étape " + i);
                return null;
            }

            System.out.printf("[Marathon] étape %d : %d candidats géographiques%n",
                    i, admissibles.size());

            // Pour le premier segment : on choisit parmi des candidats aléatoires
            // pour ne pas toujours faire la même boucle
            if (i == 0) {
                // Mélange partiel : on décale aléatoirement le point de départ dans la liste
                int offset = rand.nextInt(admissibles.size());
                List<Node> shuffled = new ArrayList<>();
                for (int k = 0; k < admissibles.size(); k++) {
                    shuffled.add(admissibles.get((k + offset) % admissibles.size()));
                }
                admissibles = shuffled;
            }

            // Trouver le candidat dont la longueur routière est la plus proche de segCible
            Node meilleurNoeud = null;
            Path meilleurSeg   = null;
            double meilleurEcart = Double.MAX_VALUE;

            Node precedent = (i > 0) ? sommetsDepart[i - 1] : null;

            List<Node> aTester = new ArrayList<>();
            for (Node n : admissibles) {
                if (!n.equals(precedent)) aTester.add(n);
                if (aTester.size() >= MAX_CANDIDATS) break;
            }
            if (aTester.isEmpty()) aTester = admissibles.subList(
                    0, Math.min(MAX_CANDIDATS, admissibles.size()));

            for (Node candidat : aTester) {
                Path seg = runAStar(sommetsDepart[i], candidat, arcsInterdits, graphe);
                if (seg == null) continue;

                double ecart = Math.abs(seg.getLength() - segCible);
                System.out.printf("[Marathon]   candidat #%d : %.0f m (écart %.0f m)%n",
                        candidat.getId(), seg.getLength(), ecart);

                if (ecart < meilleurEcart) {
                    meilleurEcart = ecart;
                    meilleurSeg   = seg;
                    meilleurNoeud = candidat;
                }

                if (ecart <= segCible * SEG_TOL) break;  // assez bon, on s'arrête
            }

            if (meilleurSeg == null) {
                System.err.println("[Marathon] INFEASIBLE : aucun segment valide étape " + i);
                return null;
            }

            System.out.printf("[Marathon]   → segment %d choisi : %.0f m (écart %.1f%%)%n",
                    i, meilleurSeg.getLength(),
                    meilleurEcart / segCible * 100.0);

            sommetsDepart[i + 1] = meilleurNoeud;
            arcsInterdits.addAll(meilleurSeg.getArcs());
            tousLesArcs.addAll(meilleurSeg.getArcs());
        }

        // Segment de retour
        Path retour = runAStar(sommetsDepart[N - 1], s0, arcsInterdits, graphe);
        if (retour == null) {
            System.err.println("[Marathon] INFEASIBLE : A* échoue segment retour");
            return null;
        }
        System.out.printf("[Marathon]   retour : %.0f m%n", retour.getLength());
        tousLesArcs.addAll(retour.getArcs());

        Path boucle   = new Path(graphe, tousLesArcs);
        double longueur = boucle.getLength();
        System.out.printf("[Marathon] Boucle : %.2f km (cible %.2f km, écart %.1f%%)%n",
                longueur / 1000.0, L / 1000.0, Math.abs(longueur - L) / L * 100.0);

        return boucle;
    }
}