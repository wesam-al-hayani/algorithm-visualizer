package dev.wesam.visualizer.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/** Deterministic, UI-independent maze generators with snapshots for educational playback. */
public final class MazeAlgorithms {
  public enum Method {
    RANDOM_WALLS("Random Walls"),
    RECURSIVE_BACKTRACKING("Recursive Backtracking Maze"),
    RECURSIVE_DIVISION("Recursive Division Maze"),
    RANDOMIZED_PRIM("Randomized Prim Maze"),
    RANDOMIZED_KRUSKAL("Randomized Kruskal Maze");

    private final String label;

    Method(String label) {
      this.label = label;
    }

    @Override
    public String toString() {
      return label;
    }
  }

  public record MazeFrame(
      String grid, String message, int activeLine, Set<Integer> active, Set<Integer> frontier) {
    public MazeFrame {
      active = Set.copyOf(active);
      frontier = Set.copyOf(frontier);
    }
  }

  public record MazeResult(
      Method method, int rows, int columns, String grid, List<MazeFrame> frames) {
    public MazeResult {
      frames = List.copyOf(frames);
    }
  }

  private record Cell(int row, int column) {}

  private record Passage(Cell from, Cell to, Cell wall) {}

  private MazeAlgorithms() {}

  /** Generates a maze with the dimensions of a compact slash-separated grid template. */
  public static MazeResult generate(Method method, String template, Random random) {
    int[] dimensions = dimensions(template);
    return generate(method, dimensions[0], dimensions[1], random);
  }

  /** Generates a maze. Proper maze methods guarantee that their start and target are connected. */
  public static MazeResult generate(Method method, int rows, int columns, Random random) {
    Objects.requireNonNull(method, "method");
    Objects.requireNonNull(random, "random");
    validateDimensions(rows, columns);
    return switch (method) {
      case RANDOM_WALLS -> randomWalls(rows, columns, random);
      case RECURSIVE_BACKTRACKING -> recursiveBacktracking(rows, columns, random);
      case RECURSIVE_DIVISION -> recursiveDivision(rows, columns, random);
      case RANDOMIZED_PRIM -> randomizedPrim(rows, columns, random);
      case RANDOMIZED_KRUSKAL -> randomizedKruskal(rows, columns, random);
    };
  }

  private static MazeResult randomWalls(int rows, int columns, Random random) {
    char[][] grid = filled(rows, columns, '.');
    for (int row = 0; row < rows; row++)
      for (int column = 0; column < columns; column++)
        grid[row][column] = random.nextDouble() < .27 ? '#' : '.';
    // Keep a guaranteed route so every pathfinder has a useful generated input.
    for (int row = 0; row < rows; row++) grid[row][0] = '.';
    for (int column = 0; column < columns; column++) grid[rows - 1][column] = '.';
    Cell start = new Cell(0, 0), target = new Cell(rows - 1, columns - 1);
    List<MazeFrame> frames = new ArrayList<>();
    addFrame(
        grid, start, target, frames, "Scatter independent random walls", 1, Set.of(), Set.of());
    return result(Method.RANDOM_WALLS, grid, start, target, frames);
  }

  private static MazeResult recursiveBacktracking(int rows, int columns, Random random) {
    char[][] grid = filled(rows, columns, '#');
    List<Cell> cells = logicalCells(rows, columns);
    Cell start = cells.get(0), target = cells.get(cells.size() - 1);
    boolean[][] visited = new boolean[rows][columns];
    Deque<Cell> stack = new ArrayDeque<>();
    List<MazeFrame> frames = new ArrayList<>();
    grid[start.row][start.column] = '.';
    visited[start.row][start.column] = true;
    stack.push(start);
    addFrame(
        grid, start, target, frames, "Choose the first cell", 0, ids(start, columns), Set.of());
    while (!stack.isEmpty()) {
      Cell current = stack.peek();
      List<Cell> neighbors = unvisitedTwoStepNeighbors(current, visited, rows, columns);
      if (neighbors.isEmpty()) {
        stack.pop();
        addFrame(
            grid,
            start,
            target,
            frames,
            "Backtrack from a cell with no unvisited neighbors",
            4,
            ids(current, columns),
            cellIds(stack, columns));
        continue;
      }
      Cell next = neighbors.get(random.nextInt(neighbors.size()));
      Cell wall = between(current, next);
      grid[wall.row][wall.column] = '.';
      grid[next.row][next.column] = '.';
      visited[next.row][next.column] = true;
      stack.push(next);
      addFrame(
          grid,
          start,
          target,
          frames,
          "Carve to a random unvisited neighbor",
          3,
          ids(columns, current, wall, next),
          cellIds(stack, columns));
    }
    return result(Method.RECURSIVE_BACKTRACKING, grid, start, target, frames);
  }

  private static MazeResult randomizedPrim(int rows, int columns, Random random) {
    char[][] grid = filled(rows, columns, '#');
    List<Cell> cells = logicalCells(rows, columns);
    Cell start = cells.get(0), target = cells.get(cells.size() - 1);
    boolean[][] visited = new boolean[rows][columns];
    List<Passage> frontier = new ArrayList<>();
    List<MazeFrame> frames = new ArrayList<>();
    visited[start.row][start.column] = true;
    grid[start.row][start.column] = '.';
    addFrontier(start, visited, rows, columns, frontier);
    addFrame(
        grid,
        start,
        target,
        frames,
        "Seed the maze and collect frontier walls",
        1,
        ids(start, columns),
        frontierIds(frontier, columns));
    while (!frontier.isEmpty()) {
      Passage passage = frontier.remove(random.nextInt(frontier.size()));
      if (visited[passage.to.row][passage.to.column]) continue;
      visited[passage.to.row][passage.to.column] = true;
      grid[passage.wall.row][passage.wall.column] = '.';
      grid[passage.to.row][passage.to.column] = '.';
      addFrontier(passage.to, visited, rows, columns, frontier);
      addFrame(
          grid,
          start,
          target,
          frames,
          "Connect a random frontier cell to the maze",
          3,
          ids(columns, passage.from, passage.wall, passage.to),
          frontierIds(frontier, columns));
    }
    return result(Method.RANDOMIZED_PRIM, grid, start, target, frames);
  }

  private static MazeResult randomizedKruskal(int rows, int columns, Random random) {
    char[][] grid = filled(rows, columns, '#');
    List<Cell> cells = logicalCells(rows, columns);
    Cell start = cells.get(0), target = cells.get(cells.size() - 1);
    for (Cell cell : cells) grid[cell.row][cell.column] = '.';
    List<Passage> walls = new ArrayList<>();
    for (Cell cell : cells) {
      Cell right = new Cell(cell.row, cell.column + 2);
      Cell down = new Cell(cell.row + 2, cell.column);
      if (right.column < columns - 1) walls.add(new Passage(cell, right, between(cell, right)));
      if (down.row < rows - 1) walls.add(new Passage(cell, down, between(cell, down)));
    }
    Collections.shuffle(walls, random);
    int[] parent = new int[rows * columns];
    Arrays.fill(parent, -1);
    List<MazeFrame> frames = new ArrayList<>();
    addFrame(
        grid,
        start,
        target,
        frames,
        "Start with one set per cell and shuffled separating walls",
        1,
        Set.of(),
        frontierIds(walls, columns));
    for (Passage wall : walls) {
      int first = wall.from.row * columns + wall.from.column;
      int second = wall.to.row * columns + wall.to.column;
      int firstRoot = find(parent, first), secondRoot = find(parent, second);
      if (firstRoot == secondRoot) continue;
      union(parent, firstRoot, secondRoot);
      grid[wall.wall.row][wall.wall.column] = '.';
      addFrame(
          grid,
          start,
          target,
          frames,
          "Remove a wall that joins two different sets",
          3,
          ids(columns, wall.from, wall.wall, wall.to),
          Set.of());
    }
    return result(Method.RANDOMIZED_KRUSKAL, grid, start, target, frames);
  }

  private static MazeResult recursiveDivision(int rows, int columns, Random random) {
    char[][] grid = filled(rows, columns, '#');
    int cellRows = logicalCount(rows), cellColumns = logicalCount(columns);
    Cell start = new Cell(1, 1);
    Cell target = new Cell(1 + 2 * (cellRows - 1), 1 + 2 * (cellColumns - 1));
    for (int row = 1; row <= target.row; row++)
      for (int column = 1; column <= target.column; column++) grid[row][column] = '.';
    List<MazeFrame> frames = new ArrayList<>();
    addFrame(grid, start, target, frames, "Begin with one open chamber", 0, Set.of(), Set.of());
    divide(grid, 0, cellRows - 1, 0, cellColumns - 1, start, target, random, frames, columns);
    return result(Method.RECURSIVE_DIVISION, grid, start, target, frames);
  }

  private static void divide(
      char[][] grid,
      int top,
      int bottom,
      int left,
      int right,
      Cell start,
      Cell target,
      Random random,
      List<MazeFrame> frames,
      int columns) {
    int height = bottom - top, width = right - left;
    if (height <= 0 && width <= 0) return;
    boolean horizontal =
        height > 0 && (width <= 0 || height > width || height == width && random.nextBoolean());
    Set<Integer> wallCells = new LinkedHashSet<>();
    if (horizontal) {
      int split = top + random.nextInt(height);
      int wallRow = 2 * split + 2;
      int opening = left + random.nextInt(right - left + 1);
      for (int cellColumn = left; cellColumn <= right; cellColumn++) {
        int column = 2 * cellColumn + 1;
        if (cellColumn != opening) {
          grid[wallRow][column] = '#';
          wallCells.add(wallRow * columns + column);
        }
        if (cellColumn < right) {
          grid[wallRow][column + 1] = '#';
          wallCells.add(wallRow * columns + column + 1);
        }
      }
      grid[wallRow][2 * opening + 1] = '.';
      wallCells.remove(wallRow * columns + 2 * opening + 1);
      addFrame(
          grid,
          start,
          target,
          frames,
          "Divide horizontally and leave one passage",
          3,
          wallCells,
          Set.of());
      divide(grid, top, split, left, right, start, target, random, frames, columns);
      divide(grid, split + 1, bottom, left, right, start, target, random, frames, columns);
    } else {
      int split = left + random.nextInt(width);
      int wallColumn = 2 * split + 2;
      int opening = top + random.nextInt(bottom - top + 1);
      for (int cellRow = top; cellRow <= bottom; cellRow++) {
        int row = 2 * cellRow + 1;
        if (cellRow != opening) {
          grid[row][wallColumn] = '#';
          wallCells.add(row * columns + wallColumn);
        }
        if (cellRow < bottom) {
          grid[row + 1][wallColumn] = '#';
          wallCells.add((row + 1) * columns + wallColumn);
        }
      }
      grid[2 * opening + 1][wallColumn] = '.';
      wallCells.remove((2 * opening + 1) * columns + wallColumn);
      addFrame(
          grid,
          start,
          target,
          frames,
          "Divide vertically and leave one passage",
          3,
          wallCells,
          Set.of());
      divide(grid, top, bottom, left, split, start, target, random, frames, columns);
      divide(grid, top, bottom, split + 1, right, start, target, random, frames, columns);
    }
  }

  private static List<Cell> logicalCells(int rows, int columns) {
    List<Cell> cells = new ArrayList<>();
    for (int row = 1; row < rows - 1; row += 2)
      for (int column = 1; column < columns - 1; column += 2) cells.add(new Cell(row, column));
    return cells;
  }

  private static int logicalCount(int size) {
    return (size - 1) / 2;
  }

  private static List<Cell> unvisitedTwoStepNeighbors(
      Cell cell, boolean[][] visited, int rows, int columns) {
    List<Cell> neighbors = new ArrayList<>();
    int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
    for (int[] direction : directions) {
      int row = cell.row + direction[0], column = cell.column + direction[1];
      if (row > 0 && row < rows - 1 && column > 0 && column < columns - 1 && !visited[row][column])
        neighbors.add(new Cell(row, column));
    }
    return neighbors;
  }

  private static void addFrontier(
      Cell cell, boolean[][] visited, int rows, int columns, List<Passage> frontier) {
    for (Cell neighbor : unvisitedTwoStepNeighbors(cell, visited, rows, columns))
      frontier.add(new Passage(cell, neighbor, between(cell, neighbor)));
  }

  private static Cell between(Cell first, Cell second) {
    return new Cell((first.row + second.row) / 2, (first.column + second.column) / 2);
  }

  private static Set<Integer> cellIds(Iterable<Cell> cells, int columns) {
    Set<Integer> ids = new LinkedHashSet<>();
    for (Cell cell : cells) ids.add(cell.row * columns + cell.column);
    return ids;
  }

  private static Set<Integer> frontierIds(List<Passage> passages, int columns) {
    Set<Integer> ids = new LinkedHashSet<>();
    for (Passage passage : passages) ids.add(passage.to.row * columns + passage.to.column);
    return ids;
  }

  private static Set<Integer> ids(Cell cell, int columns) {
    return Set.of(cell.row * columns + cell.column);
  }

  private static Set<Integer> ids(int columns, Cell... cells) {
    Set<Integer> result = new LinkedHashSet<>();
    for (Cell cell : cells) result.add(cell.row * columns + cell.column);
    return result;
  }

  private static int find(int[] parent, int value) {
    int root = value;
    while (parent[root] >= 0) root = parent[root];
    while (value != root) {
      int next = parent[value];
      parent[value] = root;
      value = next;
    }
    return root;
  }

  private static void union(int[] parent, int first, int second) {
    if (parent[first] > parent[second]) {
      int swap = first;
      first = second;
      second = swap;
    }
    parent[first] += parent[second];
    parent[second] = first;
  }

  private static MazeResult result(
      Method method, char[][] grid, Cell start, Cell target, List<MazeFrame> frames) {
    grid[start.row][start.column] = 'S';
    grid[target.row][target.column] = 'T';
    String compact = join(grid);
    if (frames.isEmpty() || !frames.get(frames.size() - 1).grid.equals(compact))
      addFrame(grid, start, target, frames, "Maze generation complete", 5, Set.of(), Set.of());
    return new MazeResult(method, grid.length, grid[0].length, compact, frames);
  }

  private static void addFrame(
      char[][] grid,
      Cell start,
      Cell target,
      List<MazeFrame> frames,
      String message,
      int activeLine,
      Set<Integer> active,
      Set<Integer> frontier) {
    char startValue = grid[start.row][start.column];
    char targetValue = grid[target.row][target.column];
    grid[start.row][start.column] = 'S';
    grid[target.row][target.column] = 'T';
    frames.add(new MazeFrame(join(grid), message, activeLine, active, frontier));
    grid[start.row][start.column] = startValue;
    grid[target.row][target.column] = targetValue;
  }

  private static int[] dimensions(String template) {
    if (template == null || template.isBlank()) return new int[] {15, 21};
    String[] rows = template.trim().split("/");
    int columns = rows[0].length();
    for (String row : rows)
      if (row.length() != columns)
        throw new IllegalArgumentException("Grid rows must have the same length");
    return new int[] {Math.max(5, rows.length), Math.max(5, columns)};
  }

  private static void validateDimensions(int rows, int columns) {
    if (rows < 5 || columns < 5)
      throw new IllegalArgumentException("Maze dimensions must both be at least 5");
    if ((long) rows * columns > 2_500)
      throw new IllegalArgumentException("Maze visualization is limited to 2,500 cells");
  }

  private static char[][] filled(int rows, int columns, char value) {
    char[][] grid = new char[rows][columns];
    for (char[] row : grid) Arrays.fill(row, value);
    return grid;
  }

  private static String join(char[][] grid) {
    StringBuilder result = new StringBuilder();
    for (int row = 0; row < grid.length; row++) {
      if (row > 0) result.append('/');
      result.append(grid[row]);
    }
    return result.toString();
  }
}
