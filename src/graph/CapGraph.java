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

        Set<Integer> centerNeighbours = this.map.get(center).getNeighbourSet();

        for (Integer node : centerNeighbours) {
            egonet.addVertex(node);
            egonet.addEdge(center, node);

            Set<Integer> nodeNeighbour = this.map.get(node).getNeighbourSet();

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
        List<Integer> nodeNeighbors = node.getNeighbours();
        Set<Integer> secondLevelFriends = new HashSet<>();

        for (Integer neighbour : nodeNeighbors) {
            Set<Integer> secondNeighbors = this.map.get(neighbour).getNeighbourSet();
            secondLevelFriends.addAll(secondNeighbors.stream().filter(i -> !nodeNeighbors.contains(i)).collect(Collectors.toSet()));
        }

        secondLevelFriends.remove(node.getId());
        return secondLevelFriends;
    }

    /** todo TEST WITH JUNIT
     * @return A Map with 1 single key-value pair; the key represents the ID of the node with a highest
     * reach in two hops, amd the value being a Set of IDs representing those two hop potentials (first
     * hop friends/direct friends NOT included).
     */
    public Map<Integer, Set<Integer>> getHighestTwoHop() {
        Map<Integer, Set<Integer>> returnValue = new HashMap<>();
        int highestTwoHopReachID = 0;
        Set<Integer> highestTwoHop = new HashSet<>();

        for (Integer id : this.map.keySet()) {
            HashSet<Integer> currTwoHopSet = (HashSet<Integer>) this.get2ndLevelFriends(this.map.get(id));
            if (currTwoHopSet.size() > highestTwoHop.size()) {
                highestTwoHopReachID = id;
                highestTwoHop = (Set<Integer>) currTwoHopSet.clone();
            }
        }

        if (highestTwoHopReachID != 0) returnValue.put(highestTwoHopReachID, highestTwoHop);
        return returnValue;
    }

    /**
     * See Brandes paper to get an in depth explanation of the algorithm, or the other references cited in
     * the project's scope definition file
     * @param graph on which the edge betweenness centrality is to be calculated
     * @return A Map which pairs said edges with their score
     * Given the complexity of the algorithm, the stages where divided (and appropriately commented) as
     * Green, McColl and Bader do in their paper from 2012 (see the file with all academic references used
     * for the project)
     */
    public Map<Edge, Double> getWeight(CapGraph graph) {
        if (graph == null) throw new NullPointerException("The argument passed to this function points to a null value");

        Map<Edge, Double> edgeBetweenness = new HashMap<>();

        // The betweenness centrality score of each edge is initialized to zero (stage 0)
        for (Edge edge : graph.getEdges()) {
            edgeBetweenness.put(edge, 0.0);
        }

        // Stages 1, 2 and 3 are performed for every node in the graph
        for (Integer id : graph.getNodes()) {
            Node currNode = graph.getNode(id);

            // Initialization of data structures: a Stack, a Queue and three collections –one collection
            // to count the number of shortest paths from each node to the current root, one to measure
            // the distance from each node to the current root, and one final collection of linked lists
            // to keep track of the vertices that precede each root in a traversal – Stage 1

            Queue<Node> queue = new LinkedList<>();
            Stack<Node> endNodeStack = new Stack<>();
            Map<Node, List<Edge>> shortestPath = new HashMap<>();
            Map<Node, Integer> amountOfShortestPaths = new HashMap<>();
            Map<Node, Double> shortestPathLength = new HashMap<>();

            for (Integer endNodeID : graph.getNodes()) {
                Node currEndNode = graph.getNode(endNodeID);
                shortestPath.put(currEndNode, new LinkedList<>());
                amountOfShortestPaths.put(currEndNode, 0); //
                shortestPathLength.put(currEndNode, Double.POSITIVE_INFINITY);
            }

            // By definition in Brandes's original paper, the distance from a node to itself is zero
            amountOfShortestPaths.put(currNode, 1);
            shortestPathLength.put(currNode, 0.0);

            queue.add(currNode);

            // Main loop – Stage 2
            while (!queue.isEmpty()) {
                Node prevNode = queue.remove();
                endNodeStack.push(prevNode);

                for (Edge edge : ((CapNode) prevNode).getOutgoingEdges()) {
                    Node neighbour = graph.getNode(edge.getTo());

                    // If its the first time its encountered
                    if (shortestPathLength.get(neighbour) == Double.POSITIVE_INFINITY) {
                        queue.add(neighbour);
                        shortestPathLength.put(neighbour, shortestPathLength.get(prevNode) + 1);
                    }

                    // If its a shortest path
                    if (shortestPathLength.get(neighbour) == shortestPathLength.get(prevNode) + 1) {
                        amountOfShortestPaths.put(neighbour, amountOfShortestPaths.get(neighbour) + amountOfShortestPaths.get(prevNode));
                        shortestPath.get(neighbour).add(edge);
                    }
                }
            }

            // Dependency accumulation based on the back-propagation of dependencies from a node to
            // its predecessors – Stage 3
            Map<Edge, Double> dependency = new HashMap<>();
            for (Edge edge : graph.getEdges()) {
                dependency.put(edge, 0.0);
            }

            while (!endNodeStack.isEmpty()) {
                Node node = endNodeStack.pop();
                double sum = 0.0;

                for (Edge edge : ((CapNode) node).getOutgoingEdges()) {
                    sum += dependency.get(edge);
                }

                for (Edge edge : shortestPath.get(node)) {
                    dependency.put(edge, (double) (amountOfShortestPaths.get(graph.getNode(edge.getFrom())) / amountOfShortestPaths.get(node)) * (1.0 + sum));
                    edgeBetweenness.put(edge, edgeBetweenness.get(edge) + dependency.get(edge));
                }
            }
        }

        return edgeBetweenness;
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
    private Set<Edge> getEdges() {
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
     * @param id of the node to check
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
