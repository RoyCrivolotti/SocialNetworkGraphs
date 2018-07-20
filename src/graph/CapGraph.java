package graph;

import java.util.*;

/**
 * @author Roy Gabriel Crivolotti.
 * For the warm up assignment, I must implement my Graph in this class.
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
                egonet.addVertex(center);
                egonet.addEdge(node, currNode);
            });
	    }

		return egonet;
    }

    /**
     * {@inheritDoc}
     * @return A copy of every strongly connected component in the Graph as a list of sub-graphs
     */
	@Override
	public List<Graph> getSCCs() {
        Stack<Integer> toExplore = new Stack<>();
        toExplore.addAll(this.nodeSet);
        List<Integer> componentsIDList = new ArrayList<>();

        Stack<Integer> finished = dfs(this, toExplore, componentsIDList);

        Graph transposedGraph = transposeGraph(this);

        componentsIDList.clear();
	    List<Graph> SCCList = findSCCsDFS(transposedGraph, finished, componentsIDList);
	    transposedGraph = null;

	    return SCCList;
    }

    /**
     * @param graph The graph to traverse
     * @param toExplore Stack of ints to explore –in order–
     * @param SCCList List of SCC; it's filled when iterating on the original
     * graph and the transposed, but cleared in the first case
     * @return Stack of ints i the order in which they were finished
     */
    private Stack<Integer> dfs(Graph graph, Stack<Integer> toExplore, List<Integer> SCCList) {
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> finished = new Stack<>();

        while (!toExplore.isEmpty()) {
            int currNodeID = toExplore.pop();
            if (!visited.contains(currNodeID)) dfsVisit(graph, currNodeID, visited, finished, SCCList);
        }

        return finished;
    }

    /**
     * @param graph The graph to explore
     * @param toExplore The stack of vertices to explore –in order–
     * @param componentsIDList List of node's IDs in each SCC; keeps track of each sub-graph's components
     * @return List of sub-graphs, each a strongly connected component
     */
    private List<Graph> findSCCsDFS(Graph graph, Stack<Integer> toExplore, List<Integer> componentsIDList) {
        List<Graph> SCCList = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> finished = new Stack<>();

        while (!toExplore.isEmpty()) {
            int currNodeID = toExplore.pop();
            Graph currGraph = new CapGraph();
            if (!visited.contains(currNodeID)) {
                currGraph.addVertex(currNodeID);
                dfsVisit(graph, currNodeID, visited, finished, componentsIDList);
                toExplore.removeAll(componentsIDList);
            }

            for (int id : componentsIDList) {
                currGraph.addVertex(id);
                for (int neighbourID : this.getNode(id).getNeighbourSet()) {
                    currGraph.addEdge(id, neighbourID);
                    ((CapGraph) currGraph).getNode(id).addNeighbour(neighbourID);
                }
            }

            SCCList.add(currGraph);
            componentsIDList.clear();
        }

        return SCCList;
    }

    /**
     * Helper method to recursively visit the nodes's neighbours, adding them to the visited set
     * Once a node with no unvisited neighbours is reached, it's pushed into the finished stack
     * @param graph The graph on which DFS in being performed
     * @param currNodeID The node at which DFS is at the moment of the call
     * @param visited Set of already visited nodes
     * @param finished Stack of finished nodes in order
     * @param SCCList List of SCC
     */
    private void dfsVisit(Graph graph, int currNodeID, Set<Integer> visited, Stack<Integer> finished, List<Integer> SCCList) {
	    visited.add(currNodeID);
	    for (Integer currNodeNeighbour : ((CapGraph)graph).getNode(currNodeID).getNeighbourSet()) {
	        if (!visited.contains(currNodeNeighbour)) dfsVisit(graph, currNodeNeighbour, visited, finished, SCCList);
        }

        finished.push(currNodeID);
	    SCCList.add(currNodeID);
	}

    /**
     * @param graph The graph to transpose
     * @return The transposed version of the graph
     * This method takes a graph as a parameter in case it is needed to transpose a subgraph
     * at a later date, maybe to find sub-communities and the like.
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

	public Set<Integer> getNodes() {
        return new HashSet<>(this.nodeSet);
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

    public static void main(String[] args) {
        Graph TwitterGraph = new CapGraph();
        util.GraphLoader.loadGraph(TwitterGraph, "data/twitter_combined.txt");

        Graph TestGraph = new CapGraph();
        util.GraphLoader.loadGraph(TestGraph, "data/small_test_graph.txt");

    }
}
