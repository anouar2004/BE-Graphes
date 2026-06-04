package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class LabelStar extends Label {
    
    // Le coût estimé à vol d'oiseau (heuristique)
    private double estimatedCost;

    // Constructeur
    public LabelStar(Node courantSommet, double estimatedCost) {
        // On appelle le constructeur de la classe mère (Label)
        super(courantSommet);
        this.estimatedCost = estimatedCost;
    }

    // Redéfinition du coût total pour A*
    @Override
    public double getTotalCost() {
        return this.getCostRealised() + this.estimatedCost;
    }

    // Redéfinition du coût estimé (utile pour le compareTo de la classe mère)
    @Override
    public double getEstimatedCost() {
        return this.estimatedCost;
    }

    // Tu peux aussi rajouter un setter si l'estimation change (rare, mais pratique)
    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
}