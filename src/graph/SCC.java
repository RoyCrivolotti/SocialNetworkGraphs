package graph;

import java.util.*;

public class SCC {
    private List<Graph> SCCs;

    public SCC(CapGraph g) {
        SCCs = findSCCs(g);
    }

    /**
     * @return A copy of every strongly connected component in the Graph as a list of sub-graphs
     */
    public List<Graph> findSCCs(CapGraph g) {
        Stack<Integer> toExplore = new Stack<>();
        toExplore.addAll(g.getNodes());
        List<Integer> componentsIDList = new ArrayList<>();

        Stack<Integer> finished = dfs(g, toExplore, componentsIDList);

        Graph transposedGraph = g.transposeGraph();

        componentsIDList.clear();
        List<Graph> SCCList = findSCCsDFS(transposedGraph, finished, componentsIDList, g);

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
    private List<Graph> findSCCsDFS(Graph graph, Stack<Integer> toExplore, List<Integer> componentsIDList, CapGraph g) {
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
                for (int neighbourID : g.getNode(id).getNeighbours()) {
                    currGraph.addEdge(id, neighbourID);
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
        for (Integer currNodeNeighbour : ((CapGraph)graph).getNode(currNodeID).getNeighbours()) {
            if (!visited.contains(currNodeNeighbour)) dfsVisit(graph, currNodeNeighbour, visited, finished, SCCList);
        }

        finished.push(currNodeID);
        SCCList.add(currNodeID);
    }

    public List<Graph> getSCCs() {
        return this.SCCs;
    }
}
