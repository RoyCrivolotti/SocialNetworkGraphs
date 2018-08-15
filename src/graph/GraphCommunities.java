package graph;

import java.util.*;

public class GraphCommunities {
    private Set<CapGraph> communities;

    public GraphCommunities(CapGraph capGraph) {
        this.communities = new HashSet<>();
        detectCommunities(capGraph);
    }

    /**
     * TODO Write JUnit tests to test the implementation of the algorithm to find communities writing some toy graphs workable by hand and, with that, check the correctness of the implementation of Brandes algorithm
     * Method to implement Girvan-Newman's algorithm to detect communities, based on Brandes algorithm
     * ----- As I start this last part of my project (I started with Brandes algorithm and just now started to
     * implement G-M's loop), I realize that I'm not sure how to remove the edges and keep track of the blocks
     * that get disconnected to form the sub-communities; hence, I'm not sure how I could, for example, have a set
     * of sub-graphs representing sub-communities. I'll try to see if there is a visualization tool that can process
     * a graph with disconnected sub-communities, alleviating me of the work of keeping track of such.
     * @param capGraph The Graph to be copied and on which to apply said algorithms
     */
    public void detectCommunities(CapGraph capGraph) {
        CapGraph graphCopy = new CapGraph(capGraph);
        Map<Edge, Double> betweenness;

        for (int i = 0; i < 10; i++) {
            betweenness = getWeight(graphCopy);
            Edge highScoreEdge = null;
            Double highestScore = 0.0;

            for (Edge edge : betweenness.keySet()) {
                if (betweenness.get(edge) > highestScore) {
                    highScoreEdge = edge;
                    highestScore = betweenness.get(edge);
                }
            }
            boolean deleteEdge = graphCopy.deleteEdge(highScoreEdge);
        }

        // At this point the graph (clone-graph) might have disconnected communities and loosely
        // connected sub-communities
        differentiateCommunities(graphCopy);
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
    private Map<Edge, Double> getWeight(CapGraph graph) {
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
     * TODO Test the traversal algorithm and its helper method constructSubgraph() with JUnit testing
     * I use a BFS-type-algorithm approach to get every connected node in the sub-graph
     * @param graph
     */
    private void differentiateCommunities(CapGraph graph) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> allNodes = new ArrayList<>(graph.getNodes());

        do {
            Set<Integer> ids = new HashSet<>();

            queue.add(allNodes.get(new Random().nextInt(allNodes.size())));
            while (!queue.isEmpty()) {
                int currNode = queue.remove();
                ids.add(currNode);

                List<Integer> currNeighbors = new LinkedList<>(graph.getNode(currNode).getNeighbours());
                ListIterator<Integer> it = currNeighbors.listIterator(currNeighbors.size());

                while (it.hasPrevious()) {
                    int next = it.previous();
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
            constructSubgraph(graph, ids);
            allNodes.removeAll(visited);
        }
        while (!allNodes.isEmpty());
    }

    /**
     * From the set of connected ids/nodes found, it constructs the sub-graph and adds it to the set of sub-communities
     * @param graph The already processed graph (through the G-M & Brandes algorithms)
     * @param ids The set of connected ids after G-Ms algorithm removes the edges with highest betweenness
     */
    private void constructSubgraph(CapGraph graph, Set<Integer> ids) {
        CapGraph subGraph = new CapGraph();
        ids.forEach(graph::addVertex);
        for (Integer id : ids) {
            Set<Integer> neighbors = graph.getNode(id).getNeighbours();
            for (Integer id2ndLevel : neighbors) {
                if (ids.contains(id2ndLevel)) subGraph.addEdge(id, id2ndLevel);
            }
        }
        this.communities.add(subGraph);
    }
}
