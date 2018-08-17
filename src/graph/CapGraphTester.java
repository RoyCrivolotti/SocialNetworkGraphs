package graph;

/**
 * @author Roy Gabriel Crivolotti
 * This set of tests don't aim to be exhaustive. I do not test here the egonet and SCC methods,
 * since they are more complicated to develop tests on and for that I have the graders. This is
 * just to test a couple of things I wanted to make sure wouldn't bug the more complex algorithms,
 * causing a mistake that I'd chase in the wrong place
 */

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Set<Integer> testNeighbours = new HashSet<>(Arrays.asList(2, 4));
        assertEquals(node1.getNeighbours(), testNeighbours);
    }

    @Test
    public void testGetEgonet() {
        Graph egonet = notEmptyGraph.getEgonet(1);
        Set<Integer> nodesInEgonet = new HashSet<>(Arrays.asList(1, 2, 4));
        assertEquals(nodesInEgonet, ((CapGraph)egonet).getNodes());
    }

    /*
    Lists of 0's friends of friends (Facebook data):
    [0, 334], [0, 64, 480, 226, 355, 356, 622, 207, 562, 312, 698, 671], [0, 195, 693, 438, 391, 424, 552]
    [0, 873, 463], [0, 558, 559], [0, 32, 449, 326, 678, 489, 521, 809, 270, 689, 211, 183, 631, 637]
    [0, 354, 346], [0, 65, 243], [0, 264, 968, 687]
     */
    @Test
    public void testGetSecondLevelFriends() {
        CapGraph testGraph = new CapGraph();
        util.GraphLoader.loadGraph(testGraph, "data/facebook_1000.txt");

// Set<Integer> handmadeAnswer = new HashSet<>(Arrays.asList(334, 64, 207, 226, 312, 355, 356, 480, 562, 558, 559, 622, 671, 698, 195, 391, 424, 438, 552, 693, 463, 873, 32, 183, 211, 270, 326, 449, 489, 521, 631, 637, 678, 689, 809, 346, 354, 65, 243, 264, 687, 968));
// assertEquals(handmadeAnswer, ((CapGraph) testGraph).get2ndLevelFriends(0));

        Set<Integer> nodeNeighbors = testGraph.getNode(0).getNeighbours();
        Set<Integer> secondLevelFriends = new HashSet<>();

        for (Integer neighbor : nodeNeighbors) {
            secondLevelFriends.addAll(testGraph.getNode(neighbor).getNeighbours());
        }

        secondLevelFriends.removeAll(nodeNeighbors);
        secondLevelFriends.remove(0);
        assertEquals(secondLevelFriends, testGraph.get2ndLevelFriends(0));
        assertEquals(secondLevelFriends.size(), testGraph.get2ndLevelFriends(0).size());
    }

    /**
     * Second test of this method with another case, this one done entirely by hand while testing the
     * getHighestTwoHop() method
     */
    public void testGet2ndLevelFriends() {
        CapGraph smallTestGraph = new CapGraph();
        util.GraphLoader.loadGraph(smallTestGraph, "data/small_test_graph.txt");
        Map<Integer, Set<Integer>> expected = new HashMap<>();

        expected.put(1, Stream.of(2, 3, 7).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(1));

        expected.put(2, Stream.of(1, 3, 7).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(2));

        expected.put(3, Stream.of(2, 1, 7, 6, 8).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(3));

        expected.put(4, Stream.of(5, 6, 7).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(4));

        expected.put(5, Stream.of(4, 6, 7).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(5));

        expected.put(6, Stream.of(4, 5, 7, 3, 8).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(6));

        expected.put(7, Stream.of(4, 5, 1, 2, 9, 12).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(7));

        expected.put(8, Stream.of(2, 3, 6, 10, 11, 13, 14).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(8));

        expected.put(9, Stream.of(8, 10, 7, 11, 12).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(9));

        expected.put(10, Stream.of(8, 9, 10, 11).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(10));

        expected.put(11, Stream.of(8, 9, 10).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(11));

        expected.put(12, Stream.of(8, 13, 14, 7, 9).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(12));

        expected.put(13, Stream.of(12, 14, 8).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(13));

        expected.put(14, Stream.of(12, 13, 8).collect(Collectors.toCollection(HashSet::new)));
        assertEquals(expected, smallTestGraph.get2ndLevelFriends(14));
    }

    @Test
    public void testGetHighestTwoHop() {
        CapGraph smallTestGraph = new CapGraph();
        util.GraphLoader.loadGraph(smallTestGraph, "data/small_test_graph.txt");

        Map<Integer, Set<Integer>> expected = new HashMap<>();
        expected.put(7, Stream.of(4, 5, 1, 2, 9, 12).collect(Collectors.toCollection(HashSet::new)));
        expected.put(8, Stream.of(3, 6, 10, 11, 13, 14).collect(Collectors.toCollection(HashSet::new)));

        assertEquals(expected, smallTestGraph.getHighestTwoHop());
    }
}
