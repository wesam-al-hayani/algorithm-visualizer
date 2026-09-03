package dev.wesam.visualizer.algorithms;

import static dev.wesam.visualizer.algorithms.GraphAlgorithms.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GraphAlgorithmsTest {
  @Test
  void traversalsComponentsAndDag() {
    Graph graph =
        new Graph(
            6, List.of(new Edge(0, 1), new Edge(0, 2), new Edge(1, 3), new Edge(4, 5)), false);
    assertEquals(List.of(0, 1, 2, 3), bfs(graph, 0));
    assertEquals(Set.of(0, 1, 2, 3), Set.copyOf(dfs(graph, 0)));
    assertEquals(2, connectedComponents(graph).size());
    Graph dag =
        new Graph(4, List.of(new Edge(0, 1), new Edge(0, 2), new Edge(1, 3), new Edge(2, 3)), true);
    List<Integer> order = topologicalSort(dag);
    assertTrue(order.indexOf(0) < order.indexOf(3));
    assertThrows(
        IllegalArgumentException.class,
        () -> topologicalSort(new Graph(2, List.of(new Edge(0, 1), new Edge(1, 0)), true)));
  }

  @Test
  void stronglyConnectedComponentsAreGrouped() {
    Graph graph =
        new Graph(
            5,
            List.of(
                new Edge(0, 1),
                new Edge(1, 0),
                new Edge(1, 2),
                new Edge(2, 3),
                new Edge(3, 2),
                new Edge(3, 4)),
            true);
    List<Set<Integer>> parts = stronglyConnectedComponents(graph);
    assertTrue(parts.contains(Set.of(0, 1)));
    assertTrue(parts.contains(Set.of(2, 3)));
    assertTrue(parts.contains(Set.of(4)));
  }

  @Test
  void tarjanReportsTheSameStrongComponentsWithIndexAndStackFrames() {
    Graph graph =
        new Graph(
            6,
            List.of(
                new Edge(0, 1),
                new Edge(1, 0),
                new Edge(1, 2),
                new Edge(2, 3),
                new Edge(3, 2),
                new Edge(3, 4),
                new Edge(4, 5),
                new Edge(5, 4)),
            true);
    TarjanResult result = tarjanStronglyConnectedComponents(graph);
    assertEquals(Set.copyOf(stronglyConnectedComponents(graph)), Set.copyOf(result.components()));
    assertTrue(java.util.Arrays.stream(result.index()).allMatch(value -> value >= 0));
    assertTrue(result.frames().stream().anyMatch(frame -> !frame.completedComponent().isEmpty()));
  }

  @Test
  void lowLinksFindBridgesAndArticulationPointsIncludingParallelEdgeSafety() {
    Graph graph =
        new Graph(
            7,
            List.of(
                new Edge(0, 1),
                new Edge(1, 2),
                new Edge(2, 0),
                new Edge(1, 3),
                new Edge(3, 4),
                new Edge(4, 5),
                new Edge(5, 3),
                new Edge(4, 6)),
            false);
    LowLinkResult result = undirectedLowLinks(graph);
    assertEquals(Set.of(new Edge(1, 3), new Edge(4, 6)), Set.copyOf(result.bridges()));
    assertEquals(Set.of(1, 3, 4), result.articulationPoints());
    assertTrue(result.frames().stream().allMatch(frame -> frame.discovery()[frame.current()] >= 0));

    Graph parallel = new Graph(2, List.of(new Edge(0, 1), new Edge(0, 1)), false);
    assertTrue(undirectedLowLinks(parallel).bridges().isEmpty());
  }

  @Test
  void eulerSupportsUndirectedAndDirectedTrailsAndExplainsFailure() {
    EulerResult circuit =
        eulerTrail(
            new Graph(
                5,
                List.of(
                    new Edge(0, 1),
                    new Edge(1, 2),
                    new Edge(2, 0),
                    new Edge(0, 3),
                    new Edge(3, 4),
                    new Edge(4, 0)),
                false));
    assertTrue(circuit.exists());
    assertTrue(circuit.circuit());
    assertEquals(7, circuit.trail().size());
    assertEquals(circuit.trail().get(0), circuit.trail().get(circuit.trail().size() - 1));

    EulerResult directed = eulerTrail(new Graph(3, List.of(new Edge(0, 1), new Edge(1, 2)), true));
    assertTrue(directed.exists());
    assertFalse(directed.circuit());
    assertEquals(List.of(0, 1, 2), directed.trail());

    EulerResult missing =
        eulerTrail(
            new Graph(
                5, List.of(new Edge(0, 1), new Edge(0, 2), new Edge(0, 3), new Edge(0, 4)), false));
    assertFalse(missing.exists());
    assertTrue(missing.reason().contains("odd-degree"));
  }

  @Test
  void bipartiteCheckColorsComponentsAndReturnsAConflictEdge() {
    BipartiteResult square =
        bipartiteCheck(
            new Graph(
                5, List.of(new Edge(0, 1), new Edge(1, 2), new Edge(2, 3), new Edge(3, 0)), false));
    assertTrue(square.bipartite());
    assertEquals(0, square.color()[4]);
    BipartiteResult triangle =
        bipartiteCheck(
            new Graph(3, List.of(new Edge(0, 1), new Edge(1, 2), new Edge(2, 0)), false));
    assertFalse(triangle.bipartite());
    assertNotNull(triangle.conflict());
  }

  @Test
  void shortestPathsHandleDisconnectedAndNegativeEdges() {
    Graph positive =
        new Graph(
            5,
            List.of(new Edge(0, 1, 4), new Edge(0, 2, 1), new Edge(2, 1, 2), new Edge(1, 3, 1)),
            true);
    ShortestPaths dijkstra = dijkstra(positive, 0);
    assertArrayEquals(new long[] {0, 3, 1, 4, INF}, dijkstra.distance());
    assertEquals(List.of(0, 2, 1, 3), dijkstra.pathTo(3));
    Graph negative =
        new Graph(
            4,
            List.of(new Edge(0, 1, 4), new Edge(0, 2, 5), new Edge(1, 2, -2), new Edge(2, 3, 3)),
            true);
    assertArrayEquals(new long[] {0, 4, 2, 5}, bellmanFord(negative, 0).distance());
    Graph cycle =
        new Graph(3, List.of(new Edge(0, 1, 1), new Edge(1, 2, -2), new Edge(2, 1, -2)), true);
    assertTrue(bellmanFord(cycle, 0).negativeCycle());
  }

  @Test
  void allPairsAlgorithmsReconstructPathsAndDetectNegativeCycles() {
    Graph graph =
        new Graph(
            4,
            List.of(
                new Edge(0, 1, 1),
                new Edge(0, 2, 4),
                new Edge(1, 2, -2),
                new Edge(1, 3, 5),
                new Edge(2, 3, 2),
                new Edge(3, 0, 3)),
            true);
    AllPairsShortestPaths floyd = floydWarshall(graph);
    AllPairsShortestPaths johnson = johnson(graph);
    assertFalse(floyd.negativeCycle());
    assertFalse(johnson.negativeCycle());
    for (int row = 0; row < graph.vertices(); row++)
      assertArrayEquals(floyd.distance()[row], johnson.distance()[row]);
    assertEquals(List.of(0, 1, 2, 3), floyd.path(0, 3));
    assertEquals(List.of(0, 1, 2, 3), johnson.path(0, 3));
    assertTrue(floyd.path(-1, 3).isEmpty());

    Graph cycle =
        new Graph(3, List.of(new Edge(0, 1, 1), new Edge(1, 2, -3), new Edge(2, 0, 1)), true);
    assertTrue(floydWarshall(cycle).negativeCycle());
    assertTrue(johnson(cycle).negativeCycle());
    assertTrue(floydWarshall(cycle).path(0, 2).isEmpty());
  }

  @Test
  void graphAStarUsesEuclideanScoresAndReconstructsThePath() {
    Graph graph =
        new Graph(
            5,
            List.of(new Edge(0, 1, 1), new Edge(1, 3, 1), new Edge(0, 2, 2), new Edge(2, 3, 3)),
            true);
    List<Point> points =
        List.of(
            new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(2, 0), new Point(9, 9));
    AStarResult found = aStar(graph, points, 0, 3);
    assertTrue(found.found());
    assertEquals(2, found.cost());
    assertEquals(List.of(0, 1, 3), found.path());
    assertEquals(3, found.frames().get(found.frames().size() - 1).current());
    assertEquals(2.0, found.hScore()[0], 1e-9);
    AStarResult missing = aStar(graph, points, 0, 4);
    assertFalse(missing.found());
    assertTrue(missing.path().isEmpty());
  }

  @Test
  void minimumSpanningTreesHaveExpectedWeight() {
    Graph graph =
        new Graph(
            4,
            List.of(
                new Edge(0, 1, 1),
                new Edge(0, 2, 4),
                new Edge(1, 2, 2),
                new Edge(1, 3, 5),
                new Edge(2, 3, 3)),
            false);
    assertEquals(6, kruskal(graph).totalWeight());
    assertEquals(6, prim(graph, 0).totalWeight());
    assertTrue(kruskal(graph).spanning());
  }

  @Test
  void maxFlowAndMinimumCut() {
    int[][] capacity = {
      {0, 16, 13, 0, 0, 0},
      {0, 0, 10, 12, 0, 0},
      {0, 4, 0, 0, 14, 0},
      {0, 0, 9, 0, 0, 20},
      {0, 0, 0, 7, 0, 4},
      {0, 0, 0, 0, 0, 0}
    };
    FlowResult result = edmondsKarp(capacity, 0, 5);
    assertEquals(23, result.maximumFlow());
    assertTrue(result.sourceSideOfMinCut()[0]);
    assertFalse(result.sourceSideOfMinCut()[5]);
    assertTrue(result.bfsPhases() > 0);
    assertEquals(result.bfsPhases(), result.augmentations());

    DinicResult dinic = dinic(capacity, 0, 5);
    assertEquals(23, dinic.maximumFlow());
    assertTrue(dinic.sourceSideOfMinCut()[0]);
    assertFalse(dinic.sourceSideOfMinCut()[5]);
    assertTrue(dinic.bfsPhases() > 0);
    assertTrue(dinic.dfsPushes() >= dinic.augmentations());
    assertTrue(dinic.frames().stream().anyMatch(frame -> !frame.augmentingPath().isEmpty()));
    assertTrue(dinic.frames().stream().anyMatch(frame -> frame.event().startsWith("Blocking")));
  }

  @Test
  void matchingUnionFindAndGridPaths() {
    boolean[][] bipartite = {{true, true, false}, {false, true, false}, {false, true, true}};
    assertEquals(3, maximumBipartiteMatching(bipartite));
    MatchingResult matching = hopcroftKarp(bipartite);
    assertEquals(3, matching.size());
    assertEquals(3, matching.augmentations());
    assertTrue(matching.bfsPhases() > 0);
    for (int left = 0; left < matching.leftMatch().length; left++)
      assertEquals(left, matching.rightMatch()[matching.leftMatch()[left]]);
    UnionFind sets = new UnionFind(5);
    sets.union(0, 1);
    sets.union(1, 2);
    assertTrue(sets.connected(0, 2));
    assertFalse(sets.connected(0, 4));
    boolean[][] walls = new boolean[4][4];
    walls[1][1] = true;
    walls[1][2] = true;
    for (GridPathfinding.Method method : GridPathfinding.Method.values()) {
      GridPathfinding.Result path =
          GridPathfinding.find(
              walls, new GridPathfinding.Cell(0, 0), new GridPathfinding.Cell(3, 3), method);
      assertEquals(6, path.cost());
      assertFalse(path.path().isEmpty());
    }
  }
}
