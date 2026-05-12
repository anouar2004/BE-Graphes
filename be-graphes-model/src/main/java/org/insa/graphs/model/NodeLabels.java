package org.insa.graphs.model;

/**
 * Associates labels to nodes in a graph without modifying the Node or Graph classes.
 * 
 * Since nodes are numbered from 0 to N-1, this class uses an array for efficient access.
 */
public class NodeLabels {
    
    // Array to store labels indexed by node ID
    private final String[] labels;
    
    /**
     * Create a new NodeLabels instance for the given graph.
     * All labels are initially null.
     * 
     * @param graph The graph whose nodes will have labels.
     */
    public NodeLabels(Graph graph) {
        this.labels = new String[graph.size()];
    }
    
    /**
     * Set the label for a given node.
     * 
     * @param node The node to label.
     * @param label The label to assign (can be null).
     */
    public void setLabel(Node node, String label) {
        this.labels[node.getId()] = label;
    }
    
    /**
     * Get the label for a given node.
     * 
     * @param node The node whose label is requested.
     * @return The label, or null if no label has been set.
     */
    public String getLabel(Node node) {
        return this.labels[node.getId()];
    }
    
    /**
     * Get the label for the node with the given ID.
     * 
     * @param nodeId The ID of the node.
     * @return The label, or null if no label has been set.
     */
    public String getLabel(int nodeId) {
        return this.labels[nodeId];
    }
    
    /**
     * Clear the label for a given node.
     * 
     * @param node The node whose label should be cleared.
     */
    public void clearLabel(Node node) {
        this.labels[node.getId()] = null;
    }
    
    /**
     * Clear all labels.
     */
    public void clearAllLabels() {
        for (int i = 0; i < this.labels.length; i++) {
            this.labels[i] = null;
        }
    }
}
