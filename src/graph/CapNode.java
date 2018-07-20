package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CapNode implements Node {
    private int id;
    private List<Integer> neighbours;
    private Set<Integer> neighbourSet;

    public CapNode(int id) {
        this.id = id;
        this.neighbours = new ArrayList<>();
        this.neighbourSet = new HashSet<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean addNeighbour(int neighbour) {
        return (this.neighbours.add(neighbour) && this.neighbourSet.add(neighbour));
    }

    /**
     * Get the list of neighbours.
     * @return a copy of the list; the user can access the neighbour node
     * objects but not modify the list.
     */
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
}
