package dev.wesam.visualizer.algorithms;

import static dev.wesam.visualizer.algorithms.GraphAlgorithms.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RandomizedGraphTest {
  @Test
  void randomizedNonnegativeShortestPathsCrossValidate() {
    Random random = new Random(0xD15A);
    for (int trial = 0; trial < 80; trial++) {
      int vertices = 2 + random.nextInt(10);
      List<Edge> edges = new ArrayList<>();
      for (int from = 0; from < vertices; from++)
        for (int to = 0; to < vertices; to++)
          if (from != to && random.nextDouble() < .24)
            edges.add(new Edge(from, to, 1 + random.nextInt(30)));
      Graph graph = new Graph(vertices, edges, true);
      AllPairsShortestPaths floyd = floydWarshall(graph);
      AllPairsShortestPaths johnson = johnson(graph);
      assertFalse(floyd.negativeCycle());
      assertFalse(johnson.negativeCycle());
      for (int source = 0; source < vertices; source++) {
        assertArrayEquals(
            bellmanFord(graph, source).distance(),
            dijkstra(graph, source).distance(),
            "trial " + trial + ", source " + source);
        assertArrayEquals(dijkstra(graph, source).distance(), floyd.distance()[source]);
        assertArrayEquals(floyd.distance()[source], johnson.distance()[source]);
      }
    }
  }

  @Test
  void randomizedNegativeAcyclicGraphsCrossValidateAllPairsAlgorithms() {
    Random random = new Random(0xA11FA1);
    for (int trial = 0; trial < 100; trial++) {
      int vertices = 2 + random.nextInt(10);
      List<Edge> edges = new ArrayList<>();
      for (int from = 0; from < vertices; from++)
        for (int to = from + 1; to < vertices; to++)
          if (random.nextDouble() < .35) edges.add(new Edge(from, to, random.nextInt(16) - 5));
      Graph graph = new Graph(vertices, edges, true);
      AllPairsShortestPaths floyd = floydWarshall(graph);
      AllPairsShortestPaths johnson = johnson(graph);
      assertFalse(floyd.negativeCycle());
      assertFalse(johnson.negativeCycle());
      for (int source = 0; source < vertices; source++) {
        assertArrayEquals(bellmanFord(graph, source).distance(), floyd.distance()[source]);
        assertArrayEquals(floyd.distance()[source], johnson.distance()[source]);
      }
    }
  }

  @Test
  void randomizedConnectedGraphsGiveTheSameMstWeight() {
    Random random = new Random(0xA157);
    for (int trial = 0; trial < 100; trial++) {
      int vertices = 2 + random.nextInt(18);
      List<Edge> edges = new ArrayList<>();
      Set<String> used = new HashSet<>();
      for (int vertex = 1; vertex < vertices; vertex++)
        addUndirected(edges, used, vertex, random.nextInt(vertex), random.nextInt(51) - 20);
      for (int i = 0; i < vertices * 2; i++)
        addUndirected(
            edges,
            used,
            random.nextInt(vertices),
            random.nextInt(vertices),
            random.nextInt(51) - 20);
      Graph graph = new Graph(vertices, edges, false);
      MstResult kruskal = kruskal(graph);
      MstResult prim = prim(graph, random.nextInt(vertices));
      assertTrue(kruskal.spanning());
      assertTrue(prim.spanning());
      assertEquals(kruskal.totalWeight(), prim.totalWeight(), "trial " + trial);
    }
  }

  @Test
  void randomizedTarjanPartitionsMatchKosaraju() {
    Random random = new Random(0x5CC7A);
    for (int trial = 0; trial < 150; trial++) {
      int vertices = 1 + random.nextInt(18);
      List<Edge> edges = new ArrayList<>();
      for (int from = 0; from < vertices; from++)
        for (int to = 0; to < vertices; to++)
          if (from != to && random.nextDouble() < .16) edges.add(new Edge(from, to));
      Graph graph = new Graph(vertices, edges, true);
      assertEquals(
          Set.copyOf(stronglyConnectedComponents(graph)),
          Set.copyOf(tarjanStronglyConnectedComponents(graph).components()),
          "trial " + trial);
    }
  }

  @Test
  void randomizedHopcroftKarpMatchesSimpleAugmentingPaths() {
    Random random = new Random(0xB1A274);
    for (int trial = 0; trial < 300; trial++) {
      int leftSize = 1 + random.nextInt(16), rightSize = 1 + random.nextInt(16);
      boolean[][] edges = new boolean[leftSize][rightSize];
      for (int left = 0; left < leftSize; left++)
        for (int right = 0; right < rightSize; right++)
          edges[left][right] = random.nextDouble() < .28;
      MatchingResult result = hopcroftKarp(edges);
      assertEquals(maximumBipartiteMatching(edges), result.size(), "trial " + trial);
      assertEquals(result.size(), result.augmentations());
    }
  }

  @Test
  void randomizedLowLinksMatchRemovalBasedDefinitions() {
    Random random = new Random(0x10A11);
    for (int trial = 0; trial < 140; trial++) {
      int vertices = 2 + random.nextInt(11);
      List<Edge> edges = new ArrayList<>();
      for (int first = 0; first < vertices; first++)
        for (int second = first + 1; second < vertices; second++)
          if (random.nextDouble() < .24) edges.add(new Edge(first, second));
      Graph graph = new Graph(vertices, edges, false);
      LowLinkResult result = undirectedLowLinks(graph);
      int baseline = componentCount(graph, -1, -1);
      Set<Edge> expectedBridges = new HashSet<>();
      for (int edge = 0; edge < edges.size(); edge++)
        if (componentCount(graph, -1, edge) > baseline) expectedBridges.add(edges.get(edge));
      Set<Integer> expectedArticulation = new HashSet<>();
      for (int vertex = 0; vertex < vertices; vertex++)
        if (componentCount(graph, vertex, -1) > baseline) expectedArticulation.add(vertex);
      assertEquals(expectedBridges, Set.copyOf(result.bridges()), "bridges trial " + trial);
      assertEquals(expectedArticulation, result.articulationPoints(), "cuts trial " + trial);
    }
  }

  @Test
  void randomizedDinicAndEdmondsKarpAlwaysAgree() {
    Random random = new Random(0xD1A1C);
    for (int trial = 0; trial < 500; trial++) {
      int vertices = 2 + random.nextInt(9);
      int[][] capacity = new int[vertices][vertices];
      for (int from = 0; from < vertices; from++)
        for (int to = 0; to < vertices; to++)
          if (from != to && random.nextDouble() < .27) capacity[from][to] = random.nextInt(21);
      FlowResult edmonds = edmondsKarp(capacity, 0, vertices - 1);
      DinicResult dinic = dinic(capacity, 0, vertices - 1);
      assertEquals(edmonds.maximumFlow(), dinic.maximumFlow(), "trial " + trial);
      assertEquals(edmonds.sourceSideOfMinCut()[0], dinic.sourceSideOfMinCut()[0]);
      assertFalse(dinic.sourceSideOfMinCut()[vertices - 1]);
    }
  }

  @Test
  void randomizedTraversalsVisitExactlyTheReachableVertices() {
    Random random = new Random(0xBF5);
    for (int trial = 0; trial < 60; trial++) {
      int vertices = 1 + random.nextInt(20);
      List<Edge> edges = new ArrayList<>();
      for (int from = 0; from < vertices; from++)
        for (int to = from + 1; to < vertices; to++)
          if (random.nextDouble() < .18) edges.add(new Edge(from, to));
      Graph graph = new Graph(vertices, edges, false);
      int start = random.nextInt(vertices);
      assertEquals(Set.copyOf(bfs(graph, start)), Set.copyOf(dfs(graph, start)));
      Set<Integer> all = new HashSet<>();
      connectedComponents(graph).forEach(all::addAll);
      assertEquals(vertices, all.size());
    }
  }

  @ParameterizedTest
  @EnumSource(GridPathfinding.Method.class)
  void everyGridMethodFindsAPathOnGuaranteedSolvableRandomGrids(GridPathfinding.Method method) {
    Random random = new Random(0x621D + method.ordinal());
    for (int trial = 0; trial < 30; trial++) {
      int rows = 5 + random.nextInt(8), columns = 5 + random.nextInt(8);
      boolean[][] walls = new boolean[rows][columns];
      for (int row = 0; row < rows; row++)
        for (int column = 0; column < columns; column++)
          walls[row][column] = random.nextDouble() < .25;
      Arrays.fill(walls[0], false);
      for (int row = 0; row < rows; row++) walls[row][columns - 1] = false;
      GridPathfinding.Result result =
          GridPathfinding.find(
              walls,
              new GridPathfinding.Cell(0, 0),
              new GridPathfinding.Cell(rows - 1, columns - 1),
              method);
      assertFalse(result.path().isEmpty());
      assertEquals(new GridPathfinding.Cell(0, 0), result.path().get(0));
      assertEquals(
          new GridPathfinding.Cell(rows - 1, columns - 1),
          result.path().get(result.path().size() - 1));
      if (method != GridPathfinding.Method.DFS) assertEquals(rows + columns - 2, result.cost());
    }
  }

  @Test
  void graphAndGridInputValidationRejectMalformedData() {
    assertThrows(IllegalArgumentException.class, () -> new Graph(-1, List.of(), false));
    assertThrows(IllegalArgumentException.class, () -> new Graph(2, List.of(new Edge(0, 2)), true));
    assertThrows(IllegalArgumentException.class, () -> bfs(new Graph(1, List.of(), false), 2));
    assertThrows(
        IllegalArgumentException.class,
        () -> dijkstra(new Graph(2, List.of(new Edge(0, 1, -1)), true), 0));
    assertThrows(
        IllegalArgumentException.class,
        () -> aStar(new Graph(2, List.of(new Edge(0, 1)), true), List.of(new Point(0, 0)), 0, 1));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            aStar(
                new Graph(2, List.of(new Edge(0, 1, -1)), true),
                List.of(new Point(0, 0), new Point(1, 0)),
                0,
                1));
    assertThrows(
        IllegalArgumentException.class,
        () -> undirectedLowLinks(new Graph(2, List.of(new Edge(0, 1)), true)));
    assertThrows(
        IllegalArgumentException.class,
        () -> bipartiteCheck(new Graph(2, List.of(new Edge(0, 1)), true)));
    assertThrows(
        IllegalArgumentException.class,
        () -> hopcroftKarp(new boolean[][] {{true}, {true, false}}));
    assertThrows(IllegalArgumentException.class, () -> dinic(new int[][] {{0, -1}, {0, 0}}, 0, 1));
    assertThrows(IllegalArgumentException.class, () -> dinic(new int[][] {{0}, {0, 0}}, 0, 1));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            GridPathfinding.find(
                new boolean[][] {{false}, {false, true}},
                new GridPathfinding.Cell(0, 0),
                new GridPathfinding.Cell(1, 0),
                GridPathfinding.Method.BFS));
  }

  private static void addUndirected(
      List<Edge> edges, Set<String> used, int first, int second, int weight) {
    if (first == second) return;
    int low = Math.min(first, second), high = Math.max(first, second);
    if (used.add(low + ":" + high)) edges.add(new Edge(low, high, weight));
  }

  private static int componentCount(Graph graph, int removedVertex, int removedEdge) {
    List<List<Integer>> adjacency = new ArrayList<>();
    for (int vertex = 0; vertex < graph.vertices(); vertex++) adjacency.add(new ArrayList<>());
    for (int index = 0; index < graph.edges().size(); index++) {
      if (index == removedEdge) continue;
      Edge edge = graph.edges().get(index);
      if (edge.from() == removedVertex || edge.to() == removedVertex) continue;
      adjacency.get(edge.from()).add(edge.to());
      adjacency.get(edge.to()).add(edge.from());
    }
    boolean[] seen = new boolean[graph.vertices()];
    int components = 0;
    for (int start = 0; start < graph.vertices(); start++) {
      if (start == removedVertex || seen[start]) continue;
      components++;
      java.util.ArrayDeque<Integer> queue = new java.util.ArrayDeque<>();
      queue.add(start);
      seen[start] = true;
      while (!queue.isEmpty()) {
        int current = queue.remove();
        for (int next : adjacency.get(current))
          if (!seen[next]) {
            seen[next] = true;
            queue.add(next);
          }
      }
    }
    return components;
  }
}
