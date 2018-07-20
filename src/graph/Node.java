package graph;

import java.util.List;

/**
 * @author Roy Gabriel Crivolotti
 */

public interface Node {
    public int getId();
    public boolean addNeighbour(int neighbour);
    public List<Integer> getNeighbours();
}
