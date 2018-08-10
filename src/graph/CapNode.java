package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Rou Gabriel Crivolotti
 * This class represents the node objects/the graph vertices. There will be no comments on each method
 * because of how simple the class is
 */

public class CapNode implements Node {
    private int id;
    private List<Integer> neighbours;
    private Set<Integer> neighbourSet;
    private Set<Edge> outgoingEdges;

    public CapNode(int id) {
        this.id = id;
        this.neighbours = new ArrayList<>();
        this.neighbourSet = new HashSet<>();
        this.outgoingEdges = new HashSet<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean addNeighbour(Edge outEdge) {
        this.outgoingEdges.add(outEdge);
        return (this.neighbours.add(outEdge.getTo()) && this.neighbourSet.add(outEdge.getTo()));
    }

    @Override
    public List<Integer> getNeighbours() {
        return new ArrayList<>(this.neighbours);
    }

    public boolean hasNeighbour(int neighbourID) {
        return this.neighbourSet.contains(neighbourID);
    }

    public HashSet<Integer> getNeighbourSet() {
        return new HashSet<>(this.neighbourSet);
    }

    Set<Edge> getOutgoingEdges() {
        return this.outgoingEdges;
    }
}
