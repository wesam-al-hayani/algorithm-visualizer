package dev.wesam.visualizer.catalog;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GraphComparisonTest {
  @Test
  void dijkstraAndAStarUseOneGridAndAgreeOnOptimalCost() {
    var run = GraphCatalog.gridComparison("S...../.##.../...#../.#..../.....T");
    assertTrue(run.steps().get(0).message().contains("agree"));
    assertEquals(1, run.steps().get(0).statistics().get("Algorithms agree"));
    assertTrue(run.result().contains("Dijkstra=Path length"));
  }

  @Test
  void shortestPathComparisonExplainsScopesAndRejectsDijkstraOnNegativeEdges() {
    var run = GraphCatalog.shortestPathComparison("0>1:4,0>2:1,2>1:2,1>3:1,2>3:5,3>4:3 ; 0,4");
    var step = run.steps().get(0);
    assertEquals(1, step.statistics().get("Algorithms agree"));
    assertTrue(step.labels().contains("single source"));
    assertTrue(step.labels().contains("all pairs"));
    assertTrue(step.labels().contains("no negative weights"));
    assertThrows(
        IllegalArgumentException.class,
        () -> GraphCatalog.shortestPathComparison("0>1:-1,1>2:2 ; 0,2"));
  }

  @Test
  void flowComparisonSeparatesOperationsFromActualVisualizationFrames() {
    var run =
        GraphCatalog.flowComparison(
            "0,16,13,0,0,0/0,0,10,12,0,0/0,4,0,0,14,0/0,0,9,0,0,20/0,0,0,7,0,4/0,0,0,0,0,0 ; 0,5");
    var labels = run.steps().get(0).labels();
    assertTrue(labels.contains("Flow Operations"));
    assertTrue(labels.contains("Visual Steps"));
    assertTrue(run.result().contains("agree=true"));
  }
}
