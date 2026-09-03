package dev.wesam.visualizer.algorithms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RandomizedHashOptimizationTest {
  @ParameterizedTest
  @EnumSource(EducationalHashTable.Strategy.class)
  void randomizedHashOperationsMatchASet(EducationalHashTable.Strategy strategy) {
    Random random = new Random(0xA55 + strategy.ordinal());
    for (int trial = 0; trial < 30; trial++) {
      EducationalHashTable table = new EducationalHashTable(503, strategy);
      Set<Integer> reference = new HashSet<>();
      for (int operation = 0; operation < 250; operation++) {
        int key = random.nextInt(401) - 200;
        double choice = random.nextDouble();
        if (choice < .45) assertEquals(reference.add(key), table.insert(key).success());
        else if (choice < .75) assertEquals(reference.remove(key), table.delete(key).success());
        else assertEquals(reference.contains(key), table.search(key).success());
      }
      for (int key = -200; key <= 200; key++)
        assertEquals(reference.contains(key), table.search(key).success());
      table.clear();
      assertTrue(
          table.snapshot().stream().allMatch(value -> value.equals("—") || value.equals("[]")));
    }
  }

  @ParameterizedTest
  @EnumSource(EducationalHashTable.Strategy.class)
  void collisionAndDuplicateStatisticsAreConsistent(EducationalHashTable.Strategy strategy) {
    EducationalHashTable table = new EducationalHashTable(11, strategy);
    assertTrue(table.insert(1).success());
    var collision = table.insert(12);
    assertTrue(collision.success());
    assertEquals(1, collision.hash());
    assertTrue(collision.collisions() >= 1);
    assertFalse(table.insert(12).success());
    assertTrue(table.delete(1).success());
    assertFalse(table.search(1).success());
    assertTrue(table.search(12).success(), "search must pass tombstones");
    assertThrows(IllegalArgumentException.class, () -> table.insert(Integer.MIN_VALUE));
  }

  @Test
  void randomizedKnapsackImplementationsAgree() {
    Random random = new Random(0xB0A0D);
    for (int trial = 0; trial < 100; trial++) {
      int count = 1 + random.nextInt(12), capacity = 1 + random.nextInt(45);
      int[] weights = random.ints(count, 1, 16).toArray();
      int[] values = random.ints(count, 0, 31).toArray();
      int dynamic = OptimizationAlgorithms.knapsack(weights, values, capacity).maximumValue();
      int bounded =
          OptimizationAlgorithms.branchAndBoundKnapsack(weights, values, capacity).maximumValue();
      assertEquals(dynamic, bounded, "trial " + trial);
    }
  }

  @Test
  void randomizedTspImplementationsAgree() {
    Random random = new Random(0x75A);
    for (int trial = 0; trial < 24; trial++) {
      int size = 2 + random.nextInt(7);
      int[][] distances = new int[size][size];
      for (int row = 0; row < size; row++)
        for (int column = row + 1; column < size; column++)
          distances[row][column] = distances[column][row] = 1 + random.nextInt(30);
      assertEquals(
          OptimizationAlgorithms.bruteForceTsp(distances).cost(),
          OptimizationAlgorithms.heldKarp(distances).cost(),
          "trial " + trial);
    }
  }

  @Test
  void randomizedStrassenMatchesOrdinaryMultiplication() {
    Random random = new Random(0x57AA55E);
    for (int size = 1; size <= 9; size++)
      for (int trial = 0; trial < 12; trial++) {
        int[][] first = matrix(random, size), second = matrix(random, size);
        assertArrayEquals(
            MatrixAlgorithms.ordinaryMultiply(first, second),
            MatrixAlgorithms.strassen(first, second));
      }
  }

  @Test
  void vertexCoverResultsCoverEveryEdgeAndRespectTheApproximationBound() {
    Random random = new Random(0xC0A3);
    for (int trial = 0; trial < 80; trial++) {
      int vertices = 2 + random.nextInt(9);
      List<GraphAlgorithms.Edge> edges = new ArrayList<>();
      for (int first = 0; first < vertices; first++)
        for (int second = first + 1; second < vertices; second++)
          if (random.nextDouble() < .3) edges.add(new GraphAlgorithms.Edge(first, second));
      var exact = OptimizationAlgorithms.exactVertexCover(vertices, edges);
      var approximate = OptimizationAlgorithms.approximateVertexCover(vertices, edges);
      assertCovers(edges, exact.vertices());
      assertCovers(edges, approximate.vertices());
      assertTrue(approximate.vertices().size() <= 2 * exact.vertices().size());
    }
  }

  private static int[][] matrix(Random random, int size) {
    int[][] matrix = new int[size][size];
    for (int[] row : matrix)
      for (int column = 0; column < size; column++) row[column] = random.nextInt(21) - 10;
    return matrix;
  }

  private static void assertCovers(List<GraphAlgorithms.Edge> edges, Set<Integer> vertices) {
    for (GraphAlgorithms.Edge edge : edges)
      assertTrue(vertices.contains(edge.from()) || vertices.contains(edge.to()), edge.toString());
  }
}
