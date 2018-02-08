package graph;

import java.util.Comparator;

public class NoiseComparator implements Comparator<Edge> {
    @Override
    public int compare(Edge firstEdge, Edge secondEdge) {
        return Integer.compare(firstEdge.getNoise(), secondEdge.getNoise());
    }
}
