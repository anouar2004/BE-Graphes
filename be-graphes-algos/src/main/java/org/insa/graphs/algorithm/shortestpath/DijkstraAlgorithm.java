package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;

import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Node;
import org.insa.graphs.algorithm.AbstractSolution.Status;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {

        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();

        // variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;
        //dans un premier temps, on va initialiser le tableau de lablels
        int nbNodes = data.getGraph().size();
        Label[] labels = new Label[nbNodes];
        for (int i = 0; i < nbNodes; i++) {
            labels[i] = new Label(data.getGraph().get(i));
        }

        // 2. Initialisation du tas et de l'origine
        BinaryHeap<Label> heap = new BinaryHeap<>();

        //Initialisation du noeud de départ
        Node origin = data.getOrigin();
        labels[origin.getId()].setCostRealised(0);
        notifyOriginProcessed(origin);
        heap.insert(labels[origin.getId()]);

        //Boucle principale
        while (!heap.isEmpty()) {
        Label current = heap.deleteMin();
        if (current.isMarque()) {
            continue;
        }
        // On marque le nœud comme "visité définitivement"
        current.setMarque(true);
        // Si on a atteint la destination, on peut s'arrêter
        if (current.getCourantSommet().equals(data.getDestination())) {
            notifyDestinationReached(current.getCourantSommet());
            break;
        }
        // On explore les voisins du nœud courant
        for (Arc arc : current.getCourantSommet().getSuccessors()) {
            // On vérifie si la route est autorisée (voitures, pieds, etc.)
            if (!data.isAllowed(arc)) {
                continue;
            }
            Label dest = labels[arc.getDestination().getId()];
            // Si le voisin n'est pas encore traité
            if (!dest.isMarque()) {
                double newCost = current.getCostRealised() + data.getCost(arc);
                // Si on a trouvé un meilleur chemin pour arriver à ce voisin
                if (newCost < dest.getCostRealised()) {
                    // On met à jour le tas : on retire l'ancien s'il existe
                    if (dest.getCostRealised() == Double.POSITIVE_INFINITY) {
                        notifyNodeReached(arc.getDestination());
                }
                    try {
                        heap.remove(dest);
                    } catch (ElementNotFoundException e) {
                        // Pas grave, c'est que le voisin n'était pas encore dans le tas
                    }
                    // On met à jour les infos du voisin
                    dest.setCostRealised(newCost);
                    dest.setPere(arc);
                    // On le (re)met dans le tas avec sa nouvelle priorité
                    heap.insert(dest);
                }
            }
        }
    }

    //Maintemant que la boucle est terminée, on peut construire la solution à partir des labels
    if (labels[data.getDestination().getId()].getPere() == null) {
        // La destination n'est pas atteignable depuis l'origine
        solution = new ShortestPathSolution(data, Status.INFEASIBLE);
    } else {
        // On construit le chemin en remontant les arcs depuis la destination
        ArrayList<Arc> arcs = new ArrayList<>();
        Label current = labels[data.getDestination().getId()];
        while (current.getPere() != null) {
            arcs.add(current.getPere());
            current = labels[current.getPere().getOrigin().getId()];
        }
        // On inverse la liste des arcs pour avoir le bon ordre
        java.util.Collections.reverse(arcs);
        // On crée la solution finale
        solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(data.getGraph(), arcs));
    }
        // TODO: implement the Dijkstra algorithm

        // when the algorithm terminates, return the solution that has been found
        return solution;
    }

}
