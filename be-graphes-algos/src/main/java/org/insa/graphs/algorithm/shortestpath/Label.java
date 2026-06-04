package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label implements Comparable<Label> {
    private final Node courantSommet;
    private boolean marque;
    private double costRealised;
    private Arc pere;

    public Label(Node courantSommet){
        this.courantSommet = courantSommet;
        this.marque = false;
        this.costRealised = Double.POSITIVE_INFINITY;
        this.pere = null;
    }

    // Getters
    public Node getCourantSommet() {
        return courantSommet;
    }
    public boolean isMarque() {
        return marque;
    }
    public double getCostRealised() {
        return costRealised;
    }
    public Arc getPere() {
        return pere;
    }

    // Setters
    public void setMarque(boolean marque) {
        this.marque = marque;
    }
    public void setCostRealised(double costRealised) {
        this.costRealised = costRealised;
    }
    public void setPere(Arc pere) {
        this.pere = pere;
    }

    //pour comparer les Labels 
    @Override
    public int compareTo(Label autre) {
        // On compare sur le Coût Total
        int cmp = Double.compare(this.getTotalCost(), autre.getTotalCost());
        if (cmp == 0) {
            // En cas d'égalité, on prend celui qui a la plus petite estimation (consigne)
            cmp = Double.compare(this.getEstimatedCost(), autre.getEstimatedCost());
        }
        return cmp;
    }
    //autre methode 
    public double getcost(){
        return this.costRealised;
    }

    public double getTotalCost() {
        return this.costRealised;
    }

    public double getEstimatedCost() {
        return 0;
    }
}
