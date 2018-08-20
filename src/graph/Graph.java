package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Graph {
    public boolean addVertex(int num);

    public void addEdge(int from, int to);

    public void addEdge(Edge edge);

    /* Finds the egonet centered at a given node. */
    public Graph getEgonet(int center);

    /* Returns all SCCs in a directed graph. Recall that the warm up  assignment assumes
     * all Graphs are directed, and we will only test on directed graphs. */
    public List<Graph> getSCCs();

    /* Return the graph's connections in a readable format. The keys in this HashMap
     * are the vertices in the graph. The values are the nodes that are reachable via a directed
     * edge from the corresponding key. The returned representation ignores edge weights and
     * multi-edges. */
    public HashMap<Integer, HashSet<Integer>> exportGraph();

    public Set<Integer> getNodes();

    public CapNode getNode(int id);

    public boolean containsNode(int id);

    public boolean containsEdge(Edge edge);

    public boolean deleteEdge(Edge edge);

    public int getEdgeAmount();

    public int getSize();
}
