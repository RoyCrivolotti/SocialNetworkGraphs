/**
 * This set of tests don't aim to be exhaustive, in that I do not test here
 * the egonet and SCC methods, since for that I have the graders and they are more complicated to
 * develop tests on...this is just to test a couple of simple things I wanted to make sure
 * wouldn't bug the more complex algorithms, causing a mistake that I'd chase in the wrong place
 */
package graph;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class CapGraphTester {
    private CapGraph emptyGraph;
    private CapGraph notEmptyGraph;
    private Set<Integer> emptySet;
    private Set<Integer> notEmptySet;

    @Before
    public void setUp() {
        emptyGraph = new CapGraph();
        notEmptyGraph = new CapGraph();
        emptySet = new HashSet<>();
        notEmptySet = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            notEmptyGraph.addVertex(i);
            notEmptySet.add(i);
        }

        notEmptyGraph.addEdge(1,2);
        notEmptyGraph.addEdge(3,4);
        notEmptyGraph.addEdge(1,4);
        notEmptyGraph.addEdge(2,5);
    }

    @Test
    public void testAddVertex() {
        notEmptyGraph.addVertex(1);
        assertEquals(10, notEmptyGraph.getSize());
        notEmptyGraph.addVertex(11);
        assertEquals(11, notEmptyGraph.getSize());
    }

    @Test
    public void testGetVertex() {
        for (int i = 0; i < notEmptyGraph.getSize(); i++) {
            Node node = notEmptyGraph.getNode(i);
            assertEquals(node.getId(), i);
        }
    }

    @Test
    public void testGetNodes() {
        assertEquals(emptySet, emptyGraph.getNodes());
        assertEquals(notEmptySet, notEmptyGraph.getNodes());
    }

    @Test
    public void testAddEdge() {
        Node node1 = notEmptyGraph.getNode(1);

        assertTrue(node1.getNeighbours().contains(2));
        assertEquals(2, node1.getNeighbours().size());
        assertTrue(notEmptyGraph.getNode(6).getNeighbours().isEmpty());

        List<Integer> testNeighbours = Arrays.asList(2, 4);
        assertEquals(node1.getNeighbours(), testNeighbours);
    }

    @Test
    public void testGetEgonet() {
        Graph egonet = notEmptyGraph.getEgonet(1);
        Set<Integer> nodesInEgonet = new HashSet<>(Arrays.asList(1, 2, 4));
        assertEquals(nodesInEgonet, ((CapGraph)egonet).getNodes());
    }
}
