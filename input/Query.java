package input;

import graph.Node;

public class Query {
    private Node start;
    private Node end;

    /**
     * Creates a representation of a query in a problem.
     * A query is identified by two nodes: start and end.
     * @param start: the beginning of the path.
     * @param end: the end of the path.
     */
    Query(Node start, Node end) {
        this.start = start;
        this.end = end;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public Node getOtherEnd(Node currentNode) {
        if (currentNode.equals(start)) {
            return end;
        } else if (currentNode.equals(end)){
            return start;
        } else {
            throw new IllegalArgumentException(String.format("%s does not contain %s", this, currentNode));
        }
    }

    @Override
    public String toString() {
        return String.format("Query(%s, %s)", start, end);
    }
}
