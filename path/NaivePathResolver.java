package path;

import graph.Edge;
import graph.Graph;
import graph.Node;
import input.Query;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class NaivePathResolver extends PathResolver {
    private Graph tree;

    /**
     * Creates a naive path resolver.
     * A naive path resolver is obtained by performing a depth first search from the start node.
     * @param tree: the graph where the path is to be computed on.
     */
    public NaivePathResolver(Graph tree) {
        this.tree = tree;
    }

    public int getMaximumNoise(Query query) {
        HashMap<Node, Edge> path = computePathRecursively(new HashMap<>(), query.getStart(), query.getEnd());
        if (path == null) {
            return 0;
        }

        Node currentNode = query.getStart();
        Node endNode = query.getEnd();

        Edge edge;
        int maximumNoise = 0;

        while (!currentNode.equals(endNode)) {
            edge = path.get(currentNode);
            maximumNoise = max(maximumNoise, edge.getNoise());
            currentNode = edge.getOtherEnd(currentNode);
        }
        return maximumNoise;
    }

    public Collection<Edge> getOrderedPath(Query query) {
        HashMap<Node, Edge> path = computePathRecursively(new HashMap<>(), query.getStart(), query.getEnd());
        if (path == null) {
            return null;
        }

        Node currentNode = query.getStart();
        Node endNode = query.getEnd();

        Edge edge;
        Collection<Edge> ordered = new LinkedList<>();

        while (!currentNode.equals(endNode)) {
            edge = path.get(currentNode);
            ordered.add(edge);
            currentNode = edge.getOtherEnd(currentNode);
        }
        return ordered;
    }

    private HashMap<Node, Edge> computePathRecursively(HashMap<Node, Edge> currentPath, Node currentNode, Node endNode) {
        if (currentNode.equals(endNode)) {
            return currentPath;
        }

        for (Edge edge : tree.getAdjacentEdges(currentNode)) {
            // Get the edge's other extremity
            Node nextNode = edge.getOtherEnd(currentNode);

            // Look for already visited nodes to avoid looping through them again
            if (!currentPath.containsKey(nextNode)) {
                // Try adding edge
                currentPath.put(currentNode, edge);
                HashMap<Node, Edge> tentativePath = computePathRecursively(currentPath, nextNode, endNode);
                if (tentativePath == null) {
                    // Failed, remove edge and continue
                    currentPath.remove(currentNode);
                } else {
                    return tentativePath;
                }
            }
        }

        // If reached this point then the path is wrong
        return null;
    }

    @Override
    public String toString() {
        return "NaivePathResolver";
    }
}
