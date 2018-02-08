package graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Graph {
    private int totalNoise;
    private HashMap<Node, List<Edge>> adjacentEdges;

    /**
     * Creates a representation of a graph.
     * A graph is identified by its nodes and their adjacent edges.
     * Note: the constructor creates an empty graph and allocates memory based on its expected size.
     * @param nbNodes: expected number of nodes (used for memory allocation).
     */
    public Graph(int nbNodes) {
        totalNoise = 0;
        adjacentEdges = new HashMap<>(nbNodes);
    }

    public List<Edge> getAdjacentEdges(Node node) {
        List<Edge> edges = adjacentEdges.get(node);
        if (edges == null) {
            throw new IllegalArgumentException(String.format("Graph does not contain %s", node));
        }
        return edges;
    }

    public Iterator<Node> getNodeIterator() {
        return adjacentEdges.keySet().iterator();
    }

    private boolean contains(Node node) {
        return adjacentEdges.containsKey(node);
    }

    public void addEdge(Edge edge) {
        totalNoise += edge.getNoise();
        for (Node node : edge.getNodes()) {
            addNode(node);
            adjacentEdges.get(node).add(edge);
        }
    }

    private void addNode(Node node) {
        adjacentEdges.putIfAbsent(node, new LinkedList<>());
    }

    /**
     * Computes a minimum spanning tree using Prim's algorithm.
     * @return the minimum spanning tree of this instance of Graph.
     */
    public Graph getMinimumSpanningTree() {
        // Choose a starting point
        Node startNode = adjacentEdges.keySet().iterator().next();

        // Initialize spanning tree and edge queue from starting point
        Graph mst = new Graph(adjacentEdges.size());
        mst.addNode(startNode);

        PriorityQueue<Edge> edgeQueue = new PriorityQueue<>(new NoiseComparator());
        edgeQueue.addAll(adjacentEdges.get(startNode));

        // Run algorithm
        while (!edgeQueue.isEmpty()) {
            // Get current edge and its non-visited nodes
            Edge currentEdge = edgeQueue.poll();
            List<Node> nonVisitedNodes = currentEdge
                .getNodes()
                .stream()
                .filter(node -> !mst.contains(node))
                .collect(Collectors.toList());

            // Add edge if it is in the boundary of visited/non-visited nodes
            if (nonVisitedNodes.size() == 1) {
                Node nonVisitedNode = nonVisitedNodes.iterator().next();
                edgeQueue.addAll(adjacentEdges.get(nonVisitedNode));
                mst.addEdge(currentEdge);
            }
        }
        return mst;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // Include total noise
        builder.append("Total noise: ");
        builder.append(totalNoise);
        builder.append("\n");

        // Include adjacent edges
        builder.append("Adjacent edges:\n");
        for (Node node : adjacentEdges.keySet()) {
            builder.append(node);
            builder.append(" -> ");
            builder.append(adjacentEdges.get(node));
            builder.append("\n");
        }
        return builder.toString();
    }
}
