package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Graph;

public class LabelStar extends Label {

    public LabelStar(Node node, boolean marked, double costRealised, Arc previousArc){
        super(node,marked,costRealised,previousArc);
    }

    
}