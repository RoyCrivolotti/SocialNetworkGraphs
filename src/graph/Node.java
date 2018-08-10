package graph;

import java.util.List;

/**
 * @author Roy Gabriel Crivolotti
 */

public interface Node {
    int getId();
    boolean addNeighbour(Edge edge);
    List<Integer> getNeighbours();
}
