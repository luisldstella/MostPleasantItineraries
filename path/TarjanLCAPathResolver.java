package path;

import graph.Edge;
import graph.Graph;
import graph.Node;
import input.Query;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TarjanLCAPathResolver extends PathResolver {
    private HashMap<Node, Node> unionFindNodeMap;
    private HashMap<Node, Integer> unionFindNoiseMap;

    private HashMap<Node, Collection<Query>> queriesMap;
    private HashMap<Query, Node> lcaQueryMap;
    private HashMap<Query, Integer> noiseQueryMap;

    /**
     * Creates a path resolver based on Tarjan's Lowest Common Ancestor algorithm.
     * A Tarjan's LCA path resolver identifies the lowest common ancestor of the query's extremities to compute the path.
     * The private attribute below represents a different view of the graph for caching purposes.
     * <li> - unionFindNodeMap: used by UnionFind.
     * <li> - unionFindNoiseMap: used by UnionFind.
     * <li> - queriesMap: hash map of nodes to queries that contain them (used by Tarjan to reduce complexity to O(1)).
     * <li> - lcaQueryMap: hash map of queries to their LCA (used for debugging purposes).
     * <li> - noiseQueryMap: hash map of queries to the maximum noise of the path connecting its extremities.
     * @param tree: the graph where the path is to be computed on.
     */
    public TarjanLCAPathResolver(Graph tree, Collection<Query> queries) {
        queriesMap = new HashMap<>();
        lcaQueryMap = new HashMap<>();
        noiseQueryMap = new HashMap<>();

        // Populate queries map
        for (Query query : queries) {
            Node start = query.getStart();
            Node end = query.getEnd();
            queriesMap.putIfAbsent(start, new LinkedList<>());
            queriesMap.putIfAbsent(end, new LinkedList<>());
            queriesMap.get(start).add(query);
            queriesMap.get(end).add(query);
        }

        buildTarjanLCA(tree);
    }

    public int getMaximumNoise(Query query) {
        return noiseQueryMap.get(query);
    }

    public Node findLowestCommonAncestor(Query query) {
        return lcaQueryMap.get(query);
    }

    private void buildTarjanLCA(Graph tree) {
        // This choice is completely arbitrary, any other node could be chosen as the root
        Node root = tree.getNodeIterator().next();

        // Compute all queries' LCAs and the maximum noise for one side of the tree
        unionFindNodeMap = new HashMap<>();
        unionFindNoiseMap = new HashMap<>();
        tarjanLCA(tree, root, false);

        // Compute all queries' LCAs again but run in reversed order to get the maximum noise for the side of the tree
        unionFindNodeMap = new HashMap<>();
        unionFindNoiseMap = new HashMap<>();
        tarjanLCA(tree, root, true);
    }

    private void tarjanLCA(Graph tree, Node currentNode, boolean reversed) {
        // Initialize the disjoint set containing currentNode.
        // This also serves as a 'marking as visited' mechanism
        makeSet(currentNode);

        // Get all adjacent edges (reversed if requested)
        List<Edge> adjacent = tree.getAdjacentEdges(currentNode);
        if (reversed) {
            Collections.reverse(adjacent);
        }

        for (Edge edge : adjacent) {
            // Get the edge's other extremity
            Node nextNode = edge.getOtherEnd(currentNode);

            // Look for already visited nodes to avoid looping through them again
            // This also serves as a mechanism to differentiate between parents and children:
            // - Parents have already been visited.
            // - Children have not yet been visited.
            if (!unionFindNodeMap.containsKey(nextNode)) {
                tarjanLCA(tree, nextNode, reversed);
                union(currentNode, nextNode, edge.getNoise());
                find(nextNode);
            }
        }
        for (Query query: queriesMap.getOrDefault(currentNode, new LinkedList<>())) {
            Node otherEnd = query.getOtherEnd(currentNode);
            // The node 'otherEnd' has already been visited if and only if it has been marked as visited by makeSet
            if (unionFindNodeMap.containsKey(otherEnd)) {
                // Run 'find' to update unionFind
                Node lca = find(otherEnd);

                // Update the query map for LCA
                lcaQueryMap.put(query, lca);

                // Update the query map for maximum noise
                int storedMaxNoise = noiseQueryMap.getOrDefault(query, 0);
                int maxNoiseToOtherEnd = unionFindNoiseMap.get(otherEnd);
                noiseQueryMap.put(query, max(storedMaxNoise, maxNoiseToOtherEnd));
            }
        }
    }

    /**
     * Modified MakeSet operation of the union-find data structure.
     * Makes a new set by initializing a new disjoint set for its argument whose root is itself
     * @param source: the node whose disjoint set is to be initialized.
     */
    private void makeSet(Node source) {
        // If source is not in the HashMap, then add it as a root
        unionFindNodeMap.putIfAbsent(source, source);
        unionFindNoiseMap.putIfAbsent(source, 0);
    }

    /**
     * Modified Find operation of the union-find data structure.
     * Computes the union between two adjacent edges.
     * Note: updates the following disjoint sets for optimization purposes:
     * <li> - unionFindNodeMap: by replacing the root with the new one.
     * <li> - unionFindNoiseMap: by replacing the maximum noise with the max between the current max and the new one.
     * @param currentNode: the node whose root is to be computed.
     * @return the root of currentNode's disjoint set.
     */
    private Node find(Node currentNode) {
        makeSet(currentNode);

        // Getting from the HashMap is now safe (it cannot be null)
        Node currentRoot = unionFindNodeMap.get(currentNode);
        Integer currentNoiseToRoot = unionFindNoiseMap.get(currentNode);

        // If source matches its parent, then it's the root of its set
        // Otherwise, repeat the same step from its parent
        if (currentNode.equals(currentRoot)) {
            return currentNode;
        }

        // Update parent to optimize further look-ups
        Node newRoot = find(currentRoot);
        Integer newNoiseToRoot = unionFindNoiseMap.get(currentRoot);
        unionFindNodeMap.put(currentNode, newRoot);
        unionFindNoiseMap.put(currentNode, max(currentNoiseToRoot, newNoiseToRoot));
        return newRoot;
    }

    /**
     * Modified Union operation of the union-find data structure.
     * Merges the disjoint sets of adjacent nodes.
     * When inspected by Tarjan's algorithm, the roots of these disjoint sets represent:
     * <li> - unionFindNodeMap: the furthest ancestor of all its nodes.
     * <li> - unionFindNoiseMap: the maximum noise to the furthest ancestor of all its nodes.
     * Note: parent must be the node with smaller depth so that the root of the union is the node with smallest depth.
     * @param parent: node with smaller depth
     * @param child: node with higher depth
     * @param noise: noise between parent and child
     */
    private void union(Node parent, Node child, int noise) {
        // If the roots match, nothing needs to be done
        // Otherwise, update child's root to include parent
        if (parent.equals(child)) {
            return;
        }
        unionFindNodeMap.put(child, parent);
        unionFindNoiseMap.put(child, noise);
    }

    @Override
    public String toString() {
        return "TarjanLCAPathResolver";
    }
}
