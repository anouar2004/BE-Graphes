package org.insa.graphes.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Arrays;

import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.NodeLabels;
import org.junit.BeforeClass;
import org.junit.Test;

public class NodeLabelsTest {

    private static Graph graph;
    private static Node[] nodes;

    @BeforeClass
    public static void initAll() throws IOException {
        nodes = new Node[4];
        for (int i = 0; i < nodes.length; ++i) {
            nodes[i] = new Node(i, null);
        }
        graph = new Graph("ID", "", Arrays.asList(nodes), null);
    }

    @Test
    public void testNodeLabelsLifecycle() {
        NodeLabels labels = new NodeLabels(graph);

        for (Node node : nodes) {
            assertNull(labels.getLabel(node));
            assertNull(labels.getLabel(node.getId()));
        }

        labels.setLabel(nodes[0], "start");
        labels.setLabel(nodes[2], "middle");

        assertEquals("start", labels.getLabel(nodes[0]));
        assertEquals("start", labels.getLabel(nodes[0].getId()));
        assertEquals("middle", labels.getLabel(nodes[2]));
        assertEquals("middle", labels.getLabel(nodes[2].getId()));
        assertNull(labels.getLabel(nodes[1]));

        labels.clearLabel(nodes[0]);
        assertNull(labels.getLabel(nodes[0]));
        assertEquals("middle", labels.getLabel(nodes[2]));

        labels.clearAllLabels();
        for (Node node : nodes) {
            assertNull(labels.getLabel(node));
        }
    }
}
