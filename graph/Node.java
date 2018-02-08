package graph;

import java.util.Objects;

public class Node {
    private int index;

    /**
     * Creates a representation of a node in a graph.
     * A node is identified by its index (an integer value).
     * @param index: the integer that represents the node.
     */
    public Node(int index){
        this.index = index;
    }

    public int compareTo(Node that) {
        return Integer.compare(index, that.index);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Node) && this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    @Override
    public String toString() {
        return String.format("Node(%s)", index);
    }
}
