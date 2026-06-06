package org.insa.graphs.algorithm.shortestpath;

import java.io.PrintStream;

import org.insa.graphs.model.Node;

public class ShortestPathTextObserver implements ShortestPathObserver {

    private final PrintStream stream;

    // Compteurs
    private int nbExplores = 0;  // notifyNodeReached : ajouté au tas (exploré)
    private int nbMarques  = 0;  // notifyNodeMarked  : sorti du tas (marqué définitivement)

    public ShortestPathTextObserver(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void notifyOriginProcessed(Node node) {
        stream.println("Origine traitée : nœud #" + node.getId());
    }

    @Override
    public void notifyNodeReached(Node node) {
        nbExplores++;
        // Décommenter pour afficher chaque nœud exploré :
        // stream.println("Node " + node.getId() + " reached.");
    }

    @Override
    public void notifyNodeMarked(Node node) {
        nbMarques++;
        // Décommenter pour afficher chaque nœud marqué :
        // stream.println("Node " + node.getId() + " marked.");
    }

    @Override
    public void notifyDestinationReached(Node node) {
        stream.println("Destination atteinte : nœud #" + node.getId());
        stream.println("  Labels explorés (insérés dans le tas) : " + nbExplores);
        stream.println("  Labels marqués  (définitifs)          : " + nbMarques);
    }

}