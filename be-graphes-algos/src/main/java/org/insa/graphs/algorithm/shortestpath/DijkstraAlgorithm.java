package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    protected ShortestPathSolution doRun() {
        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();
        final Graph graph = data.getGraph();
        final Node origin = data.getOrigin();
        final Node destination = data.getDestination();

        // variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;

        final int nbNodes = graph.size();

        double[] distances = new double[nbNodes];
        Arrays.fill(distances, Double.POSITIVE_INFINITY);
        distances[origin.getId()] = 0;

        Arc[] predecessorArcs = new Arc[nbNodes];

  
        boolean[] reached = new boolean[nbNodes];
        boolean[] marked = new boolean[nbNodes];

        notifyOriginProcessed(origin);


        if (origin.equals(destination)) {
            notifyNodeMarked(origin);
            notifyDestinationReached(destination);
            solution = new ShortestPathSolution(data, ShortestPathSolution.Status.OPTIMAL,
                    new Path(graph, origin));
            return solution;
        }


        PriorityQueue<NodeEntry> queue = new PriorityQueue<>();
        queue.add(new NodeEntry(origin, 0));
        reached[origin.getId()] = true;

        while (!queue.isEmpty()) {
            final NodeEntry currentEntry = queue.poll();
            final Node currentNode = currentEntry.getNode();
            final int currentId = currentNode.getId();

            if (marked[currentId]) {
                continue;
            }

            if (currentEntry.getCost() > distances[currentId]) {
                continue;
            }

  
            marked[currentId] = true;
            notifyNodeMarked(currentNode);

            if (currentNode.equals(destination)) {
                notifyDestinationReached(destination);
                break;
            }

            for (Arc arc : currentNode.getSuccessors()) {
                if (!data.isAllowed(arc)) {
                    continue;
                }

                final int successorId = arc.getDestination().getId();
                if (marked[successorId]) {
                    continue;
                }

                final double newDistance = distances[currentId] + data.getCost(arc);
                if (newDistance < distances[successorId]) {
                    distances[successorId] = newDistance;
                    predecessorArcs[successorId] = arc;
                    queue.add(new NodeEntry(arc.getDestination(), newDistance));
                    if (!reached[successorId]) {
                        reached[successorId] = true;
                        notifyNodeReached(arc.getDestination());
                    }
                }
            }
        }


        if (predecessorArcs[destination.getId()] == null) {
            solution = new ShortestPathSolution(data, ShortestPathSolution.Status.INFEASIBLE);
        } else {
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = predecessorArcs[destination.getId()];
            while (arc != null) {
                arcs.add(arc);
                arc = predecessorArcs[arc.getOrigin().getId()];
            }
            Collections.reverse(arcs);
            solution = new ShortestPathSolution(data, ShortestPathSolution.Status.OPTIMAL,
                    new Path(graph, arcs));
        }

        return solution;
    }

    private static class NodeEntry implements Comparable<NodeEntry> {

        private final Node node;
        private final double cost;

        public NodeEntry(Node node, double cost) {
            this.node = node;
            this.cost = cost;
        }

        public Node getNode() {
            return this.node;
        }

        public double getCost() {
            return this.cost;
        }
        public int compareTo(NodeEntry other) {
            return Double.compare(this.cost, other.cost);
        }
    }
}
