import graph.Graph;
import graph.Node;
import input.Problem;
import input.Query;
import path.LCAPathResolver;
import path.NaivePathResolver;
import path.PathResolver;
import path.TarjanLCAPathResolver;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Collection;

public class Main {
    enum Version {
        V1, V2, V3;
    }

    private static void itineraries(Graph mst, Collection<Query> queries, Version version) {
        long startTime, endTime;
        PathResolver pathResolver;

        startTime = System.currentTimeMillis();
        switch (version) {
            case V1: pathResolver = new NaivePathResolver(mst); break;
            case V2: pathResolver = new LCAPathResolver(mst); break;
            case V3: pathResolver = new TarjanLCAPathResolver(mst, queries); break;
            default: throw new RuntimeException("Unrecognized version.");
        }
        endTime = System.currentTimeMillis();
        System.out.println(String.format("[%s][warming-up] %s ms", pathResolver, endTime - startTime));

        try (PrintWriter writer = new PrintWriter(new FileWriter("itineraries.out"))) {
            startTime = System.currentTimeMillis();
            for (Query query : queries) {
                writer.println(pathResolver.getMaximumNoise(query));
            }
            endTime = System.currentTimeMillis();
            System.out.println(String.format("[%s][processing] %s ms", pathResolver, endTime - startTime));
        } catch (IOException e) {
            throw new RuntimeException("There was a problem writing to the output file: " + e.getMessage());
        }
    }

    private static void itineraries_v1(Graph mst, Collection<Query> queries) {
        itineraries(mst, queries, Version.V1);
    }

    private static void itineraries_v2(Graph mst, Collection<Query> queries) {
        itineraries(mst, queries, Version.V2);
    }

    private static void itineraries_v3(Graph mst, Collection<Query> queries) {
        itineraries(mst, queries, Version.V3);
    }

    private static void itineraries_test(Graph mst, Collection<Query> queries) {
        long startTime = System.currentTimeMillis();
        NaivePathResolver naive = new NaivePathResolver(mst);
        LCAPathResolver lca = new LCAPathResolver(mst);
        TarjanLCAPathResolver tarjan = new TarjanLCAPathResolver(mst, queries);
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("[%s][warming-up] %s ms", tarjan, endTime - startTime));

        startTime = System.currentTimeMillis();
        for (Query query : queries) {
            System.out.println("Input: " + query);
            Node lcaV2 = lca.findLowestCommonAncestor(query.getStart(), query.getEnd());
            Node lcaV3 = tarjan.findLowestCommonAncestor(query);
            int noiseV1 = naive.getMaximumNoise(query);
            int noiseV2 = lca.getMaximumNoise(query);
            int noiseV3 = tarjan.getMaximumNoise(query);
            System.out.println("Output V1: " + noiseV1);
            System.out.println("Output V2: " + noiseV2);
            System.out.println("Output V3: " + noiseV3);
            if (!lcaV2.equals(lcaV3)) {
                throw new RuntimeException("LCAs don't match: " + lcaV2 + " vs " + lcaV3);
            }
            if (noiseV1 != noiseV2 || noiseV2 != noiseV3) {
                throw new RuntimeException("Noises don't match: " + noiseV1 + " vs " + noiseV2 + " vs " + noiseV3);
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println(String.format("[%s][processing] %s ms", tarjan, endTime - startTime));
    }

    public static void main(String[] args) throws FileNotFoundException, ParseException {
        String fileName = "resources/itineraries.9.in";
        Problem problem = new Problem(fileName);
        Graph graph = problem.getGraph();
        Collection<Query> queries = problem.getQueries();

        System.out.println("Original graph:");
        System.out.println(graph);

        Graph mst = graph.getMinimumSpanningTree();

        System.out.println("Minimum spanning tree:");
        System.out.println(mst);

        System.out.println("------------------------------------------------------------------------------");
        itineraries_test(mst, queries);
        System.out.println("------------------------------------------------------------------------------");
        /*
        System.out.println("------------------------------------------------------------------------------");
        itineraries_v1(mst, queries);
        System.out.println("------------------------------------------------------------------------------");
        itineraries_v2(mst, queries);
        System.out.println("------------------------------------------------------------------------------");
        itineraries_v3(mst, queries);
        System.out.println("------------------------------------------------------------------------------");
        */
    }
}
