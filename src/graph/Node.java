package graph;

import java.util.Set;

/**
 * @author Roy Gabriel Crivolotti
 */

public interface Node {
    int getId();
    boolean addNeighbour(Edge edge);
    Set<Integer> getNeighbours();
}
