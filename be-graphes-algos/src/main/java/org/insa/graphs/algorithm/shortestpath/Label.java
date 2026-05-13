package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Graph;



public class Label implements Comparable<Label> {
  
    private final Node node;
    private boolean marked;
    private double costRealised;
    private Arc previousArc;

    public Label(Node node, boolean marked, double costRealised, Arc previousArc) {
        this.node = node;
        this.marked = marked;
        this.costRealised = costRealised;
        this.previousArc = previousArc;
    }

    public Node getNode() {
        return this.node;
    }


    public int getNodeId() {
        return this.node.getId();
    }

    public boolean isMarked() {
        return this.marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public double getCostRealised() {
        return this.costRealised;
    }


    public void setCostRealised(double costRealised) {
        this.costRealised = costRealised;
    }

    public Arc getPreviousArc() {
        return this.previousArc;
    }


    public void setPreviousArc(Arc previousArc) {
        this.previousArc = previousArc;
    }


    public double getCost() {
        return this.costRealised;
    }


    public int compareTo(Label other) {
        return Double.compare(this.getCost(), other.getCost());
    }

    public static Label[] createLabel(Graph graph){
        Label[] array = new Label[graph.size()];
        for(int i = 0; i < graph.size(); i++){
            array[i] = new Label(graph.get(i), false, Double.POSITIVE_INFINITY, null);
        }
        return array;
    }


}