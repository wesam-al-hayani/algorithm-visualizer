package dev.wesam.visualizer.ui;

import java.util.Arrays;
import java.util.Random;

/** Pure transformations for the compact grid input format used by all grid demonstrations. */
public final class GridEditor {
  public enum Mode {
    DRAW_WALLS,
    SET_START,
    SET_TARGET
  }

  private GridEditor() {}

  public static String edit(String input, int row, int column, Mode mode) {
    char[][] grid = parse(input);
    if (row < 0 || row >= grid.length || column < 0 || column >= grid[0].length) return input;
    if (mode == Mode.SET_START) {
      replace(grid, 'S', '.');
      if (grid[row][column] != 'T') grid[row][column] = 'S';
    } else if (mode == Mode.SET_TARGET) {
      replace(grid, 'T', '.');
      if (grid[row][column] != 'S') grid[row][column] = 'T';
    } else if (grid[row][column] != 'S' && grid[row][column] != 'T') {
      grid[row][column] = grid[row][column] == '#' ? '.' : '#';
    }
    return join(grid);
  }

  public static String clear(String input) {
    int[] dimensions = dimensions(input);
    char[][] grid = new char[dimensions[0]][dimensions[1]];
    for (char[] row : grid) Arrays.fill(row, '.');
    grid[0][0] = 'S';
    grid[grid.length - 1][grid[0].length - 1] = 'T';
    return join(grid);
  }

  public static String randomWalls(String input, Random random) {
    int[] dimensions = dimensions(input);
    int rows = Math.max(5, dimensions[0]);
    int columns = Math.max(6, dimensions[1]);
    char[][] grid = new char[rows][columns];
    for (int row = 0; row < rows; row++)
      for (int column = 0; column < columns; column++)
        grid[row][column] = random.nextDouble() < .27 ? '#' : '.';
    for (int row = 0; row < rows; row++) grid[row][0] = '.';
    for (int column = 0; column < columns; column++) grid[rows - 1][column] = '.';
    grid[0][0] = 'S';
    grid[rows - 1][columns - 1] = 'T';
    return join(grid);
  }

  private static int[] dimensions(String input) {
    String[] rows = input == null || input.isBlank() ? new String[0] : input.split("/");
    int rowCount = Math.max(2, rows.length);
    int columnCount = Math.max(2, rows.length == 0 ? 0 : rows[0].length());
    return new int[] {rowCount, columnCount};
  }

  private static char[][] parse(String input) {
    String[] rows = input.split("/");
    if (rows.length == 0 || rows[0].isEmpty()) throw new IllegalArgumentException("Grid is empty");
    char[][] grid = new char[rows.length][rows[0].length()];
    for (int row = 0; row < rows.length; row++) {
      if (rows[row].length() != grid[0].length)
        throw new IllegalArgumentException("Grid rows must have the same length");
      grid[row] = rows[row].toCharArray();
    }
    return grid;
  }

  private static void replace(char[][] grid, char target, char replacement) {
    for (char[] row : grid)
      for (int column = 0; column < row.length; column++)
        if (row[column] == target) row[column] = replacement;
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
