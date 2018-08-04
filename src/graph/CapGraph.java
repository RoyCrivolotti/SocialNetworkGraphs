package graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Roy Gabriel Crivolotti
 * For the warm up assignment, I must implement my Graph in this class
 */

public class CapGraph implements Graph {
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
	    this.map.get(from).addNeighbour(to);
	    this.edgeList.add(new Edge(from, to));
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

        Set<Integer> centerNeighbours = this.map.get(center).getNeighbourSet();

        for (Integer node : centerNeighbours) {
            egonet.addVertex(node);
            egonet.addEdge(center, node);

            Set<Integer> nodeNeighbour = this.map.get(node).getNeighbourSet();

            nodeNeighbour.stream().filter(centerNeighbours::contains).forEach(currNode -> {
                egonet.addEdge(node, currNode);
            });
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
     * @param graph The graph to transpose
     * @return The transposed version of the graph
     * This method takes a graph as a parameter in case it is needed to transpose a subgraph
     * at a later date, maybe to find sub-communities within sub-comminities and the like
     */
    private Graph transposeGraph(CapGraph graph) {
        Graph transposedGraph = new CapGraph();

        for (Integer id : graph.map.keySet()) {
            transposedGraph.addVertex(id);
        }

        for (Edge edge : graph.edgeList) {
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
        List<Integer> nodeNeighbors = node.getNeighbours();
        Set<Integer> secondLevelFriends = new HashSet<>();

        for (Integer neighbour : nodeNeighbors) {
            Set<Integer> secondNeighbors = this.map.get(neighbour).getNeighbourSet();
            secondLevelFriends.addAll(secondNeighbors.stream().filter(i -> !nodeNeighbors.contains(i)).collect(Collectors.toSet()));
        }

        secondLevelFriends.remove(node.getId());
        return secondLevelFriends;
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
	        HashSet<Integer> currNeighbours = this.map.get(id).getNeighbourSet();
	        mapToExport.put(id, currNeighbours);
        }

	    return mapToExport;
	}

    /**
     * @return A copy of the set containing node's IDs
     */
	public Set<Integer> getNodes() {
        return new HashSet<>(this.nodeSet);
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
     * @return A new HashSet containing a copy of the edges in the graph; this public method, unlike its resembling package-private
     * getEdges() containing the actual edges, is meant to add the functionality for whomever might implement this classes in
     * the future. The functionality had to exist, but I didn't want the actual edges returned, to avoid unintentional
     * tampering of the data
     */
    public Set<Edge> getEdgesCopy() {
        Set<Edge> edgeCopies = new HashSet<>();
        for (Edge edge : this.edgeList) {
            edgeCopies.add(new Edge(edge.getFrom(), edge.getTo()));
        }
        return edgeCopies;
    }

    /**
     * @return The amount of nodes in the graph
     */
	public int getSize() {
	    return this.size;
    }

    /**
     * @return The amount of edges in the graph
     */
    public int getEdgeAmount() {
	    return this.edgeList.size();
    }

    /**
     * @param id
     * @return A boolean which states if the ID corresponds to a node in the loaded graph
     */
    public boolean isNodeContained(int id) {
        return this.nodeSet.contains(id);
    }

    public static void main(String[] args) {
        Graph TwitterGraph = new CapGraph();
        util.GraphLoader.loadGraph(TwitterGraph, "data/twitter_combined.txt");

        Graph TestGraph = new CapGraph();
        util.GraphLoader.loadGraph(TestGraph, "data/small_test_graph.txt");

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
