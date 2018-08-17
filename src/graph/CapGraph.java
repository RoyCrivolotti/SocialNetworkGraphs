package graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Roy Gabriel Crivolotti
 * For the warm up assignment, I must implement my Graph in this class
 */

public class CapGraph implements Graph, Cloneable {
    private Map<Integer, CapNode> map;
    private Set<Integer> nodeSet;
    private List<Edge> edgeList;
    private int size;

    public CapGraph() {
        this.map = new HashMap<>();
        this.nodeSet = new HashSet<>();
        this.edgeList = new LinkedList<>();
    }

    /**
     * Copy constructor to clone the object, instead of using the Cloneable interface's clone() method, per
     * Bloch's recommendations; I simply implement the Cloneable interface to catch the exception
     * @param graph The Graph object to be deep copied
     */
    public CapGraph(CapGraph graph) {
        this();
        if (graph.nodeSet.isEmpty() || graph.edgeList.isEmpty())
            throw new NullPointerException("Properly load data to the Graph object before cloning it");

        CapGraph clone = new CapGraph();

        // Every member variable is updated in the two main method addVertex and addEdge
        graph.getNodes().forEach(clone::addVertex);
        graph.getEdges().forEach(edge -> clone.addEdge(edge.getFrom(), edge.getTo()));
    }

    /**
     * {@inheritDoc}
     * @param id Node being added
     */
	@Override
	public void addVertex(int id) {
	    if (this.nodeSet.contains(id)) return;
        this.map.put(id, new CapNode(id));
        this.nodeSet.add(id);
        this.size++;
	}

    /**
     * {@inheritDoc}
     * @param from Node where the edge begins
     * @param to Node where the edge points towards
     */
	@Override
	public void addEdge(int from, int to) {
	    Edge newEdge = new Edge(from, to);
	    this.map.get(from).addNeighbour(newEdge);
	    this.edgeList.add(newEdge);
	}

    /**
     * @param id ID of the node to return
     * @return The actual node with the ID matching the parameter
     */
    public CapNode getNode(int id) {
	    return this.map.get(id);
    }

    /**
     * {@inheritDoc}
     * @param center The node/user at the center of the desired egonet
     * @return A copy of the sub-graph consisting of the center user and its neighbours
     * with no object being shared with the original graph
     */
	@Override
	public Graph getEgonet(int center) {
	    CapGraph egonet = new CapGraph();
	    egonet.addVertex(center);

        Set<Integer> centerNeighbours = this.map.get(center).getNeighbours();

        for (Integer node : centerNeighbours) {
            egonet.addVertex(node);
            egonet.addEdge(center, node);

            Set<Integer> nodeNeighbour = this.map.get(node).getNeighbours();

            nodeNeighbour.stream().filter(centerNeighbours::contains).forEach(currNode -> egonet.addEdge(node, currNode));
	    }

		return egonet;
    }

    /**
     * A new class was created to contain the algorithms processing getting the SCCs to keep
     * classes a little bit shorter and more consice and readable
     * @return A copy of every strongly connected component in the Graph as a list of sub-graphs
     */
	@Override
	public List<Graph> getSCCs() {
	    return new SCC(this).getSCCs();
    }

    /**
     * This method is public since there are no objects related to the original graph that run a danger of being
     * unintentionally modified
     * @return The transposed version of this graph
     */
    public Graph transposeGraph() {
        Graph transposedGraph = new CapGraph();

        for (Integer id : this.map.keySet()) {
            transposedGraph.addVertex(id);
        }

        for (Edge edge : this.edgeList) {
            transposedGraph.addEdge(edge.getTo(), edge.getFrom());
        }

        return transposedGraph;
    }

    /**
     * @param id of the user of which the second level friends are to be returned
     * @return A set of IDs corresponding to the friends of friends of the ID passed as a parameter
     * Obviously, direct friends of the center node are filtered
     */
    public Set<Integer> get2ndLevelFriends(int id) {
        if (!isNodeContained(id)) return new HashSet<>();
        return get2ndLevelFriends(this.map.get(id));
    }

    /**
     * @param node corresponding to the user of which the second level friends are to be returned
     * @return A set of IDs corresponding to the friends of friends of the ID passed as a parameter
     */
    public Set<Integer> get2ndLevelFriends(CapNode node) {
        Set<Integer> nodeNeighbors = node.getNeighbours();
        Set<Integer> secondLevelFriends = new HashSet<>();

        for (Integer neighbour : nodeNeighbors) {
            Set<Integer> secondNeighbors = this.map.get(neighbour).getNeighbours();
            secondLevelFriends.addAll(secondNeighbors.stream().filter(i -> !nodeNeighbors.contains(i)).collect(Collectors.toSet()));
        }

        secondLevelFriends.remove(node.getId());
        return secondLevelFriends;
    }

    /**
     * @return A Map where the key represents the ID(s) of the node(s) with a highest reach in two hops, and
     * the value being a Set of IDs representing those two hop potentials (first hop friends/direct friends NOT included);
     * if several nodes share that 'highest two hop reach potential', they are both added.
     */
    public Map<Integer, Set<Integer>> getHighestTwoHop() {
        Map<Integer, Set<Integer>> returnValue = new HashMap<>();
        Map<Integer, Set<Integer>> unfilteredMap = new HashMap<>();
        Map<Integer, Integer> unfilteredMapSizes = new HashMap<>();

        for (Integer id : this.map.keySet()) {
            Set<Integer> curr2ndLevelFriends = this.get2ndLevelFriends(id);
            unfilteredMap.put(id, curr2ndLevelFriends);
            unfilteredMapSizes.put(id, curr2ndLevelFriends.size());
        }

        if (unfilteredMap.isEmpty()) return unfilteredMap;

        Set<Integer> mostConnectedIDs = getHighestMapValues(unfilteredMap, unfilteredMapSizes);
        mostConnectedIDs.forEach(id -> returnValue.put(id, unfilteredMap.get(id)));

        return returnValue;
    }

    /**
     * @param map A map with IDs mapped with a set of their second level connections/friends
     * @param mapSizes A map with IDs mapped to the size of their set of their second level connections/friends
     * @return A set with the IDs that have the highest amount of second level connections
     */
    private Set<Integer> getHighestMapValues(Map<Integer, Set<Integer>> map, Map<Integer, Integer> mapSizes) {
        long max = mapSizes.values().stream().max(Comparator.naturalOrder()).get();
        if (max == 0) return new HashSet<>();
        return map.entrySet().stream()
                .filter(e -> e.getValue().size() == max)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     * @return A Map with every node in the graph, each associated
     * with a Set of the nodes that are reachable from said vertex
     */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
	    HashMap<Integer, HashSet<Integer>> mapToExport = new HashMap<>();

	    for (Integer id : this.nodeSet) {
	        Set<Integer> currNeighbours = this.map.get(id).getNeighbours();
	        mapToExport.put(id, (HashSet<Integer>) currNeighbours);
        }
	    return mapToExport;
	}

    /**
     * @throws CloneNotSupportedException to specify that this class uses a copy constructor to perform a deep copy
     */
	@Override
	public Graph clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("To deep copy a CapGraph instance use the copy constructor by using the 'new' keyword and passing the object to be copied as a parameter.");
	}

    /**
     * @return A copy of the set containing node's IDs
     */
	public Set<Integer> getNodes() {
        return new HashSet<>(this.nodeSet);
    }

    /**
     * @param id of the node to check
     * @return A boolean which states if the ID corresponds to a node in the loaded graph
     */
    public boolean isNodeContained(int id) {
        return this.nodeSet.contains(id);
    }

    /**
     * This method is package private so that the nodes to be accessible from the outside, since edges might get
     * unintentionally deleted and references/pointers to objects changed, which is not how this class was meant to be used;
     * it is only to be used in the construction of the classes and not by whoever might implement it at a later date
     * @return A HashSet containing the edges in the graph
     */
    Set<Edge> getEdges() {
	    return new HashSet<>(this.edgeList);
    }

    /**
     * @return A new HashSet containing a copy of the edges in the graph; this public method, unlike its resembling
     * package-private getEdges() containing the actual edges, is meant to add the functionality for whomever might
     * implement this classes in the future. The functionality had to exist, but I didn't want the actual edges
     * returned, to avoid unintentional tampering of the data
     */
    public Set<Edge> getEdgesCopy() {
        Set<Edge> edgeCopies = new HashSet<>();
        for (Edge edge : this.edgeList) {
            edgeCopies.add(new Edge(edge.getFrom(), edge.getTo()));
        }
        return edgeCopies;
    }

    boolean deleteEdge(Edge edge) {
        if (edge == null) throw new NullPointerException("Attempted to delete an edge pointing to a null value.");
        boolean deletedProperly = false;

        CapNode fromNode = this.getNode(edge.getFrom());
        CapNode toNode = this.getNode(edge.getTo());
        deletedProperly = this.edgeList.remove(edge) && fromNode.removeNeighbor(toNode.getId()) && toNode.removeNeighbor(fromNode.getId());

        return deletedProperly;
    }

    /**
     * @return The amount of edges in the graph
     */
    public int getEdgeAmount() {
        return this.edgeList.size();
    }

    /**
     * @return The amount of nodes in the graph
     */
	public int getSize() {
	    return this.size;
    }

    public static void main(String[] args) {
//        Graph TwitterGraph = new CapGraph();
//        util.GraphLoader.loadGraph(TwitterGraph, "data/twitter_combined.txt");

        Graph TestGraph = new CapGraph();
        util.GraphLoader.loadGraph(TestGraph, "data/small_test_graph.txt");
        Map<Integer, Set<Integer>> highestTwoHopMap = ((CapGraph) TestGraph).getHighestTwoHop();

        highestTwoHopMap.forEach((key, value) -> {
            System.out.println("The user with highest reach is: " + key);
            value.forEach(System.out::println);
        });


//        Set<Integer> secondLevelFriends = ((CapGraph) TestGraph).get2ndLevelFriends(0);
//        System.out.println(secondLevelFriends);
//
//        Graph FacebookGraph = new CapGraph();
//        util.GraphLoader.loadGraph(FacebookGraph, "data/facebook_1000.txt");
//
//        secondLevelFriends = ((CapGraph) FacebookGraph).get2ndLevelFriends(0);
//        System.out.println(secondLevelFriends);
//        System.out.println(secondLevelFriends.size());
    }
}
