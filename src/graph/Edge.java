/**
 * @author Roy Gabriel Crivolotti
 * The purpose of such a simple edge object is to make the transposing of graphs
 * a lot faster than O(n^2)
 */
package graph;

public class Edge {
    private int from;
    private int to;

    public Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
