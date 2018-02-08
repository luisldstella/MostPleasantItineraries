package graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public class Edge {
    private Node source;
    private Node target;
    private int noise;

    /**
     * Creates a representation of an edge in a graph.
     * An edge is identified by its vertices (ordered by index) and its noise (an integer value).
     * @param node1: one of its vertices.
     * @param node2: one of its vertices.
     * @param noise: the noise assigned to this edge.
     */
    public Edge(Node node1, Node node2, int noise) {
        switch (node1.compareTo(node2)) {
            case -1:
                source = node1;
                target = node2;
                break;
            case +1:
                source = node2;
                target = node1;
                break;
            default:
                throw new RuntimeException("An edge must have two different nodes.");
        }
        this.noise = noise;
    }

    public Node getOtherEnd(Node currentNode) {
        if (currentNode.equals(source)) {
            return target;
        } else if (currentNode.equals(target)){
            return source;
        } else {
            throw new IllegalArgumentException(String.format("%s does not contain %s", this, currentNode));
        }
    }

    public Collection<Node> getNodes() {
        Collection<Node> nodes = new LinkedList<>();
        nodes.add(source);
        nodes.add(target);
        return nodes;
    }

    public int getNoise() {
        return noise;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Edge) && this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, noise);
    }

    @Override
    public String toString() {
        return String.format("Edge(%s, %s, %s)", source, target, noise);
    }
}
