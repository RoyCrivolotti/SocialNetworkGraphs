package graph;

import com.sun.xml.internal.bind.v2.TODO;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Rou Gabriel Crivolotti
 * This class represents the node objects/the graph vertices. There will be no comments on each method
 * because of how simple the class is
 */

public class CapNode implements Node {
    private int id;
    private Set<Integer> neighbours;
    private Set<Edge> outgoingEdges;

    public CapNode(int id) {
        this.id = id;
        this.neighbours = new HashSet<>();
        this.outgoingEdges = new HashSet<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean addNeighbour(Edge outEdge) {
        this.outgoingEdges.add(outEdge);
        return this.neighbours.add(outEdge.getTo());
    }

    @Override
    public Set<Integer> getNeighbours() {
        return new HashSet<>(this.neighbours);
    }

    public boolean hasNeighbour(int neighbourID) {
        return this.neighbours.contains(neighbourID);
    }

    // TODO: 17/08/2018
    boolean removeNeighbor(Integer id, Edge edge) {
        boolean check = this.neighbours.contains(id) && this.outgoingEdges.contains(edge);
//        System.out.println("First contains check: " + check);
        this.neighbours.remove(id);
        this.outgoingEdges.remove(edge);
        check = this.neighbours.contains(id) && this.outgoingEdges.contains(edge);
//        System.out.println("Second contains check: " + check);
        return true;
    }

    Set<Edge> getOutgoingEdges() {
        return this.outgoingEdges;
    }
}
