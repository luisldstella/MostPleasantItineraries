package path;

import graph.Edge;
import graph.Graph;
import graph.Node;
import input.Query;

import java.util.HashMap;

public class LCAPathResolver extends PathResolver {
    private Node root;
    private HashMap<Node, HashMap<Integer, Node>> parentsMap;
    private HashMap<Node, HashMap<Integer, Integer>> maxNoiseMap;
    private HashMap<Node, Integer> depthMap;

    /**
     * Creates a path resolver based on the Lowest Common Ancestor.
     * A LCA path resolver identifies the lowest common ancestor of the query's extremities to compute the path.
     * The private attributes below represent different views of the graph for caching purposes.
     * <li> - root: the root of the tree (chosen arbitrarily).
     * <li> - parentsMap: hash map of parents with depths 2^0 .. 2^(log(maxDepth)).
     * <li> - maxNoiseMap: list of maximum noise to each parent in parentsMap.
     * <li> - depthMap: the depth of the node with respect to the tree's root.
     * @param tree: the graph where the path is to be computed on.
     */
    public LCAPathResolver(Graph tree) {
        // This choice is completely arbitrary, any other node could be chosen as the root
        root = tree.getNodeIterator().next();
        parentsMap = new HashMap<>();
        maxNoiseMap = new HashMap<>();
        depthMap = new HashMap<>();

        // Populate root
        parentsMap.put(root, new HashMap<>());
        maxNoiseMap.put(root, new HashMap<>());
        depthMap.put(root, 0);

        // Populate the direct parents through a depth first search
        // Complexity: O(n + m) because we need to go through all nodes and all edges (n = nbNodes, m = nbEdges)
        populateAdjacentParents(tree, root, 0);

        // Populate k-parents (k-parent is the parent of distance 2^k)
        // Complexity: O(n log(n)) because d varies in {1 .. 2^log(n)} and for each d we go through all nodes
        int nbNodes = parentsMap.keySet().size();
        for (int d = 1; d < nbNodes; d *= 2) {
            for (Node node : parentsMap.keySet()) {
                Node kParent = parentsMap.get(node).get(d);
                Integer maxNoiseToKParent = maxNoiseMap.get(node).get(d);
                if (kParent != null) {
                    Node kGrandParent = parentsMap.get(kParent).get(d);
                    Integer maxNoiseToKGrandParent = maxNoiseMap.get(kParent).get(d);
                    if (kGrandParent != null) {
                        parentsMap.get(node).put(2*d, kGrandParent);
                        maxNoiseMap.get(node).put(2*d, max(maxNoiseToKParent, maxNoiseToKGrandParent));
                    }
                }
            }
        }
    }

    public int getMaximumNoise(Query query) {
        Node node1 = query.getStart();
        Node node2 = query.getEnd();
        Node lca = findLowestCommonAncestor(node1, node2);

        // Complexity: O(log(d)) where d = distance(lca, node)
        int maximumNoise1 = getMaximumNoiseAtDistance(lca, node1);
        int maximumNoise2 = getMaximumNoiseAtDistance(lca, node2);
        return max(maximumNoise1, maximumNoise2);
    }

    public Node findLowestCommonAncestor(Node node1, Node node2) {
        if (node1.equals(root) || node2.equals(root)) {
            return root;
        }

        // Node 'node1' will be the one with smaller depth
        if (depthMap.get(node1) > depthMap.get(node2)) {
            Node buffer = node1;
            node1 = node2;
            node2 = buffer;
        }

        // Balance depth between the two nodes logarithmically
        // Complexity: O(log(d)) where d = distance(node1, node2)
        int biggestPowerOf2;
        int remainingDistance = depthMap.get(node2) - depthMap.get(node1);
        while (remainingDistance > 0) {
            biggestPowerOf2 = Integer.highestOneBit(remainingDistance);
            remainingDistance = remainingDistance - biggestPowerOf2;
            node2 = parentsMap.get(node2).get(biggestPowerOf2);
        }

        if (node1.equals(node2)) {
            return node1;
        }

        // Reduce the distance to the LCA logarithmically
        // Start from the highest bit possible for max depth (worst-case is a straight line, where max depth = n)
        // Complexity: O(log(d)) where d = distance(node1, lca) (lca is unknown but unique)
        Node kParent1;
        Node kParent2;
        int distance = Integer.highestOneBit(parentsMap.keySet().size());
        for (int k = distance; k > 0; k /= 2) {
            kParent1 = parentsMap.get(node1).get(k);
            kParent2 = parentsMap.get(node2).get(k);
            if (kParent1 != null && kParent2 != null && !kParent1.equals(kParent2)) {
                node1 = kParent1;
                node2 = kParent2;
            }
        }

        return parentsMap.get(node1).get(1);
    }

    private int getMaximumNoiseAtDistance(Node node1, Node node2) {
        int biggestPowerOf2;
        int remainingDistance = depthMap.get(node2) - depthMap.get(node1);
        int maximumNoise = 0;
        while (remainingDistance > 0) {
            biggestPowerOf2 = Integer.highestOneBit(remainingDistance);
            remainingDistance = remainingDistance - biggestPowerOf2;
            maximumNoise = max(maximumNoise, maxNoiseMap.get(node2).get(biggestPowerOf2));
            node2 = parentsMap.get(node2).get(biggestPowerOf2);
        }
        return maximumNoise;
    }

    private void populateAdjacentParents(Graph tree, Node currentNode, int currentDepth) {
        int nextDepth = currentDepth + 1;
        for (Edge edge : tree.getAdjacentEdges(currentNode)) {
            // Get the edge's other extremity
            Node nextNode = edge.getOtherEnd(currentNode);

            // Look for already visited nodes to avoid looping through them again
            if (!parentsMap.containsKey(nextNode)) {
                HashMap<Integer, Node> newParents = new HashMap<>();
                HashMap<Integer, Integer> newMaxNoiseToParents = new HashMap<>();

                newParents.put(1, currentNode);
                newMaxNoiseToParents.put(1, edge.getNoise());

                parentsMap.put(nextNode, newParents);
                maxNoiseMap.put(nextNode, newMaxNoiseToParents);

                depthMap.put(nextNode, nextDepth);
                populateAdjacentParents(tree, nextNode, nextDepth);
            }
        }
    }

    @Override
    public String toString() {
        return "LCAPathResolver";
    }
}
