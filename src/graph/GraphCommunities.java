package graph;

import java.util.*;

/**
 * @author Roy Gabriel Crivolotti
 * This class is meant to take an already built CapGraph object and apply Girvan-Newman's algorithm with Brandes's algorithm to
 * find the sub-communities in the graph. However, it is a very costly algorithm.
 */

public class GraphCommunities {
    private Map<Integer, Set<Graph>> communities;

    public GraphCommunities(Graph graph, int amount) {
        this.communities = new HashMap<>();
        detectCommunities(graph, amount);
    }

    /**
     * Method to implement Girvan-Newman's algorithm to detect communities, based on Brandes algorithm
     * ----- As I start this last part of my project (I started with Brandes algorithm and just now started to
     * implement G-M's loop), I realize that I'm not sure how to remove the edges and keep track of the blocks
     * that get disconnected to form the sub-communities.
     * Note: it is essential to note that this algorithms tend to chop off “leaf” nodes –the ones that belong to a community but
     * are in its periphery, that is they are only loosely connected to that community–, and that it works best on networks that have
     * a naturally hierarchical (nested) structure; nonetheless, it is a key algorithm, and extremely important in the history
     * of community detection algorithms.
     * @param graph The Graph to be copied and on which to apply said algorithms
     */
    private void detectCommunities(Graph graph, int communityAmount) {
        Graph graphCopy = new CapGraph(graph);
        Map<Edge, Double> betweenness;
        int iteration = 0;

        do {
            iteration++;
            System.out.println("\nIteration number: " + iteration);

            long start = System.nanoTime();
            betweenness = getBetweennessScore(graphCopy);
            long end = System.nanoTime();
            System.out.println((end - start) / 1000000 + " seconds to get bet. score.");

            Edge deletedEdge1 = null;
            Edge deletedEdge2 = null;
            Double highestScore = 0.0;

            for (Edge edge : betweenness.keySet()) {
                if (betweenness.get(edge) > highestScore) {
                    highestScore = betweenness.get(edge);
                    deletedEdge1 = edge;
                }
            }

            if (deletedEdge1 == null) return;
            graphCopy.deleteEdge(deletedEdge1);

            for (Edge edge : betweenness.keySet()) {
                if (edge.getFrom() == deletedEdge1.getTo() && edge.getTo() == deletedEdge1.getFrom()) {
                    graphCopy.deleteEdge(edge);
                    deletedEdge2 = edge;
                    System.out.println(edge.getFrom() + " " + edge.getTo());
                }
            }

            // Instead of doing BFS on graphCopy, I search for the sub-graph where the high-scoring edge is, and process that one
            if (iteration == 1) findCommunities(graphCopy, deletedEdge1, deletedEdge2, iteration);
            else {
                for (Graph g : this.communities.get(iteration-1)) {
                    if (g.containsNode(deletedEdge1.getTo())) findCommunities(g, deletedEdge1, deletedEdge2, iteration);
                }
            }
            System.out.println("Amt. of comms. found: " + this.communities.get(iteration).size());
        }
        while (this.communities.get(iteration).size() < communityAmount);
        System.out.println("It took " + iteration + " iterations.");
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
    private Map<Edge, Double> getBetweennessScore(Graph graph) {
        if (graph == null) throw new NullPointerException("The argument passed to this function points to a null value");

        Map<Edge, Double> edgeBetweenness = new HashMap<>();

        // The betweenness centrality score of each edge is initialized to zero (stage 0)
        for (Edge edge : ((CapGraph)graph).getEdges()) {
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
            for (Edge edge : ((CapGraph)graph).getEdges()) {
                dependency.put(edge, 0.0);
            }

            while (!endNodeStack.isEmpty()) {
                Node node = endNodeStack.pop();
                double sum = 0.0;

                for (Edge edge : ((CapNode) node).getOutgoingEdges()) {
//                    System.out.println("Edge from: " + edge.getFrom() + ", to: " + edge.getTo());
//                    System.out.println("Score of said edge: " + dependency.get(edge));
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
     * I use a BFS-type-algorithm approach to get every connected node in the sub-graph
     * @param graph The sub-graph where the edge being cut is located
     */
    private void findCommunities(Graph graph, Edge deletedEdge, Edge deletedEdge2, int iteration) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> allNodes = new ArrayList<>(graph.getNodes());

        if (allNodes.isEmpty()) return;

        do {
            Set<Integer> ids = new HashSet<>();
            queue.add(allNodes.get(new Random().nextInt(allNodes.size())));

            while (!queue.isEmpty()) {
                int currNode = queue.remove();
                ids.add(currNode);
                allNodes.remove(Integer.valueOf(currNode));

                List<Integer> currNeighbors = new LinkedList<>(graph.getNode(currNode).getNeighbours());
                if (currNode == deletedEdge.getFrom()) currNeighbors.remove(Integer.valueOf(deletedEdge.getTo()));
                if (currNode == deletedEdge2.getFrom()) currNeighbors.remove(Integer.valueOf(deletedEdge2.getTo()));
                ListIterator<Integer> it = currNeighbors.listIterator(currNeighbors.size());

                while (it.hasPrevious()) {
                    int next = it.previous();
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
//            System.out.println("Amount of IDs in subgraph: " + ids.size());
            constructSubgraph(graph, ids, deletedEdge, deletedEdge2, iteration);
        }
        while (!allNodes.isEmpty());
    }

    /**
     * From the set of connected ids/nodes found, it constructs the sub-graph and adds it to the set of sub-communities
     * @param graph The sub-graph where the edge being cut is located
     * @param ids The set of connected ids in the sub-graph after removing the edge with highest betweenness score
     */
    private void constructSubgraph(Graph graph, Set<Integer> ids, Edge deletedEdge, Edge deletedEdge2, int iteration) {
        Graph subGraph = new CapGraph();
        ids.forEach(subGraph::addVertex);
//        System.out.println("Constructing a Graph with " + ids.size() + " nodes.");

        for (Integer id : ids) {
            Set<Integer> neighbors = graph.getNode(id).getNeighbours();
            for (Integer id2ndLevel : neighbors) {
                if (ids.contains(id2ndLevel)) {
                    Edge newEdge = new Edge(id, id2ndLevel);
                    subGraph.addEdge(newEdge);
                    if (deletedEdge.getFrom() == id && deletedEdge.getTo() == id2ndLevel) subGraph.deleteEdge(newEdge);
                    if (deletedEdge2.getFrom() == id && deletedEdge2.getTo() == id2ndLevel) subGraph.deleteEdge(newEdge);
                }
            }
        }

//        System.out.println("Amt. of edges: " + ((CapGraph) subGraph).getEdges().size());

        if (this.communities.containsKey(iteration)) this.communities.get(iteration).add(subGraph);
        else {
            this.communities.put(iteration, new HashSet<>());
            this.communities.get(iteration).add(subGraph);
        }

        Set<Graph> helperSet = new HashSet<>();

        // Add all previously had/found sub-graphs that aren't the one being processed/divided in this iteration
        if (iteration > 1) {
            for (Graph prevGraph : this.communities.get(iteration-1)) {
//                this.communities.get(iteration).forEach(subgraph -> {{
                    if (!prevGraph.getNodes().containsAll(subGraph.getNodes())) helperSet.add(prevGraph);
//                }});
            }
            this.communities.get(iteration).addAll(helperSet);
        }
    }

    /**
     * @return The actual map with a set of the sub-graphs detected in each iteration
     */
    public Map<Integer, Set<Graph>> getCommunities() {
        return this.communities;
    }
}
