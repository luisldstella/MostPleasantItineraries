package input;

import graph.Edge;
import graph.Graph;
import graph.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

public class Problem {
    private Scanner input;
    private Graph graph;
    private Collection<Query> queries;

    /**
     * Create a new instance of the problem
     * @param filename: input file with the graph and query data to be parsed
     * @throws FileNotFoundException: if the input file cannot be found in the system
     * @throws ParseException: if the file does not respect the parsing strategy
     */
    public Problem(String filename) throws FileNotFoundException, ParseException {
        input = new Scanner(new File(filename));
        graph = parseGraph();
        queries = parseQuery();

        try {
            // If there are still strings left to be parsed then something went wrong
            if (input.hasNext()) {
                throw new ParseException("There was a problem parsing the file.", 0);
            }
        } finally {
            input.close();
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public Collection<Query> getQueries() {
        return queries;
    }

    private Graph parseGraph() {
        // Line 0: get the number of nodes and vertices
        int nbNodes = input.nextInt();
        int nbEdges = input.nextInt();

        // Lines 1 .. m: parse the graph
        Graph graph = new Graph(nbNodes);
        for (int i = 0; i < nbEdges; i++) {
            Node node1 = new Node(input.nextInt());
            Node node2 = new Node(input.nextInt());
            int noise = input.nextInt();
            graph.addEdge(new Edge(node1, node2, noise));
        }

        return graph;
    }

    private Collection<Query> parseQuery() {
        // Line m + 1: get the number of queries
        int nbQueries = input.nextInt();

        // Lines (m + 2) .. (m + 1 + l): parse the queries
        LinkedList<Query> queries = new LinkedList<>();
        for (int i = 0; i < nbQueries; i++) {
            Node start = new Node(input.nextInt());
            Node end = new Node(input.nextInt());
            queries.add(new Query(start, end));
        }

        return queries;
    }
}
