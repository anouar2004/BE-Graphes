package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Point;


public class Marathon {

    private static final int N = 4;

    private static final double ESP_FACTOR = 0.10;

    public static Path compute(Graph graph, Node origin, double L) {

        Node[] sommetsDepart = new Node[N + 1];
        sommetsDepart[0] = origin;

        double rayon  = L / N;          // distance idéale vol-d'oiseau entre 2 sommets consécutifs
        double esp    = rayon * ESP_FACTOR;
        double tol    = esp / 2.0;

        HashSet<Arc> arcsInterdits = new HashSet<>();

        Path[] segments = new Path[N + 1]; // N segments + retour

        Random rng = new Random();

        for (int i = 0; i < N; i++) {

            List<Node> admissibles = sommetsRayon(
                    graph, sommetsDepart[i], origin, rayon, tol, i);

            if (admissibles.isEmpty()) {
                System.err.println("[Marathon] Aucun sommet admissible à l'étape " + i);
                return null;
            }
            if (i == 0) {
                int idx = rng.nextInt(admissibles.size());
                sommetsDepart[i + 1] = admissibles.get(idx);
            } else {
                Node precedent = sommetsDepart[i - 1];
                Node choisi = null;
                for (Node s : admissibles) {
                    if (!s.equals(precedent)) {
                        choisi = s;
                        break;
                    }
                }
                if (choisi == null) {
                    choisi = admissibles.get(0);
                }
                sommetsDepart[i + 1] = choisi;
            }

            Path seg = aStarInterdit(graph, sommetsDepart[i], sommetsDepart[i + 1], arcsInterdits);
            if (seg == null || seg.isEmpty()) {
                System.err.println("[Marathon] A* infaisable sur le segment " + i);
                return null;
            }
            segments[i] = seg;
            arcsInterdits.addAll(seg.getArcs());
        }

        Path retour = aStarInterdit(graph, sommetsDepart[N], origin, arcsInterdits);
        if (retour == null || retour.isEmpty()) {
            System.err.println("[Marathon] A* infaisable pour le retour à l'origine");
            return null;
        }
        segments[N] = retour;
        arcsInterdits.addAll(retour.getArcs());

        try {
            ArrayList<Arc> tousLesArcs = new ArrayList<>();
            for (Path seg : segments) {
                tousLesArcs.addAll(seg.getArcs());
            }
            Path boucle = new Path(graph, tousLesArcs);
            double longueur = boucle.getLength();
            System.out.printf("[Marathon] Boucle trouvée : %.2f km (cible %.2f km, écart %.2f%%)%n",
                    longueur / 1000.0, L / 1000.0, Math.abs(longueur - L) / L * 100.0);
            return boucle;
        } catch (Exception e) {
            System.err.println("[Marathon] Erreur lors de la concaténation : " + e.getMessage());
            return null;
        }
    }

    private static List<Node> sommetsRayon(Graph graph, Node Sc, Node S0,
            double rayon, double tol, int i) {

        // Cas d'arrêt : tolérance trop grande
        if (tol >= rayon) {
            return new ArrayList<>();
        }

        Point pSc = Sc.getPoint();
        Point pS0 = S0.getPoint();

        List<Node> admissibles = new ArrayList<>();

        for (Node s : graph.getNodes()) {
            if (s.equals(Sc) || s.equals(S0)) {
                continue;
            }
            double distSc = pSc.distanceTo(s.getPoint());

            // Le sommet doit être dans l'anneau autour de Sc
            if (distSc < rayon - tol || distSc > rayon + tol) {
                continue;
            }

            if (i != 0) {
                double distS0 = pS0.distanceTo(s.getPoint());
                if (distS0 < rayon - tol) {
                    continue;
                }
            }

            admissibles.add(s);
        }

        if (admissibles.isEmpty()) {
            // On élargit la tolérance et on réessaie
            return sommetsRayon(graph, Sc, S0, rayon, tol * 2.0, i);
        }

        return admissibles;
    }

    private static Path aStarInterdit(Graph graph, Node origine, Node destination,
            HashSet<Arc> arcsInterdits) {

        int nbNodes = graph.size();

        // Initialisation des labels A* (LabelStar avec heuristique euclidienne)
        LabelStar[] labels = new LabelStar[nbNodes];
        for (int idx = 0; idx < nbNodes; idx++) {
            double h = destination.getPoint().distanceTo(graph.get(idx).getPoint());
            labels[idx] = new LabelStar(graph.get(idx), h);
        }

        // Tas binaire
        org.insa.graphs.algorithm.utils.BinaryHeap<Label> heap =
                new org.insa.graphs.algorithm.utils.BinaryHeap<>();

        // Initialisation de l'origine
        labels[origine.getId()].setCostRealised(0);
        heap.insert(labels[origine.getId()]);

        boolean found = false;

        // Boucle A*
        while (!heap.isEmpty()) {
            Label current;
            try {
                current = heap.deleteMin();
            } catch (Exception e) {
                break;
            }

            if (current.isMarque()) {
                continue;
            }
            current.setMarque(true);

            if (current.getCourantSommet().equals(destination)) {
                found = true;
                break;
            }

            for (Arc arc : current.getCourantSommet().getSuccessors()) {

                // On ignore les arcs interdits
                if (arcsInterdits.contains(arc)) {
                    continue;
                }

                LabelStar dest = labels[arc.getDestination().getId()];
                if (!dest.isMarque()) {
                    double newCost = current.getCostRealised() + arc.getLength();
                    if (newCost < dest.getCostRealised()) {
                        if (dest.getCostRealised() == Double.POSITIVE_INFINITY) {
                            // premier passage
                        }
                        try {
                            heap.remove(dest);
                        } catch (org.insa.graphs.algorithm.utils.ElementNotFoundException e) {
                            // pas encore dans le tas, normal
                        }
                        dest.setCostRealised(newCost);
                        dest.setPere(arc);
                        heap.insert(dest);
                    }
                }
            }
        }

        if (!found && labels[destination.getId()].getPere() == null) {
            return null;
        }

        ArrayList<Arc> arcs = new ArrayList<>();
        Label cur = labels[destination.getId()];
        while (cur.getPere() != null) {
            arcs.add(cur.getPere());
            cur = labels[cur.getPere().getOrigin().getId()];
        }
        java.util.Collections.reverse(arcs);

        if (arcs.isEmpty()) {
            return null;
        }

        return new Path(graph, arcs);
    }
}