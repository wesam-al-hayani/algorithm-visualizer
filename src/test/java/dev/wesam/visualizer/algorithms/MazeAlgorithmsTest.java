package dev.wesam.visualizer.algorithms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MazeAlgorithmsTest {
  @Test
  void everyGeneratorIsDeterministicValidAndVisualizable() {
    for (MazeAlgorithms.Method method : MazeAlgorithms.Method.values()) {
      MazeAlgorithms.MazeResult first =
          MazeAlgorithms.generate(method, 15, 21, new Random(0xA11CE));
      MazeAlgorithms.MazeResult second =
          MazeAlgorithms.generate(method, 15, 21, new Random(0xA11CE));
      assertEquals(first.grid(), second.grid(), method.toString());
      assertEquals(15, first.grid().split("/").length);
      assertTrue(first.grid().chars().allMatch(value -> ".#ST/".indexOf(value) >= 0));
      assertEquals(1, count(first.grid(), 'S'));
      assertEquals(1, count(first.grid(), 'T'));
      assertFalse(first.frames().isEmpty());
      assertEquals(first.grid(), first.frames().get(first.frames().size() - 1).grid());
      assertTrue(reachable(first.grid()), method + " must connect its endpoints");
    }
  }

  @Test
  void properGeneratorsRemainConnectedAcrossSizesAndSeeds() {
    for (MazeAlgorithms.Method method : MazeAlgorithms.Method.values()) {
      if (method == MazeAlgorithms.Method.RANDOM_WALLS) continue;
      for (int seed = 0; seed < 40; seed++) {
        int rows = 5 + seed % 8;
        int columns = 5 + seed % 11;
        MazeAlgorithms.MazeResult maze =
            MazeAlgorithms.generate(method, rows, columns, new Random(seed));
        assertEquals(rows, maze.rows());
        assertEquals(columns, maze.columns());
        assertTrue(reachable(maze.grid()), method + " seed " + seed);
      }
    }
  }

  @Test
  void templateDimensionsAndInvalidBoundsAreHandled() {
    MazeAlgorithms.MazeResult maze =
        MazeAlgorithms.generate(
            MazeAlgorithms.Method.RECURSIVE_BACKTRACKING,
            "S...../....../....../....../.....T",
            new Random(2));
    assertEquals(5, maze.rows());
    assertEquals(6, maze.columns());
    assertThrows(
        IllegalArgumentException.class,
        () -> MazeAlgorithms.generate(MazeAlgorithms.Method.RANDOMIZED_PRIM, 4, 10, new Random()));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            MazeAlgorithms.generate(
                MazeAlgorithms.Method.RANDOMIZED_KRUSKAL, 51, 51, new Random()));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            MazeAlgorithms.generate(
                MazeAlgorithms.Method.RECURSIVE_DIVISION, "...../....", new Random()));
  }

  private static boolean reachable(String compact) {
    String[] rows = compact.split("/");
    int start = compact.replace("/", "").indexOf('S');
    int target = compact.replace("/", "").indexOf('T');
    int columns = rows[0].length();
    boolean[] visited = new boolean[rows.length * columns];
    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(start);
    visited[start] = true;
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    while (!queue.isEmpty()) {
      int cell = queue.remove();
      if (cell == target) return true;
      int row = cell / columns, column = cell % columns;
      for (int[] direction : directions) {
        int nextRow = row + direction[0], nextColumn = column + direction[1];
        if (nextRow < 0 || nextRow >= rows.length || nextColumn < 0 || nextColumn >= columns)
          continue;
        int next = nextRow * columns + nextColumn;
        if (!visited[next] && rows[nextRow].charAt(nextColumn) != '#') {
          visited[next] = true;
          queue.add(next);
        }
      }
    }
    return false;
  }

  private static long count(String value, char symbol) {
    return value.chars().filter(character -> character == symbol).count();
  }
}
