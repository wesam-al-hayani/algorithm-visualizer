package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.demo;
import static dev.wesam.visualizer.model.AlgorithmStep.VisualKind.GRID;

import dev.wesam.visualizer.algorithms.MazeAlgorithms;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Visualizable real maze-generation algorithms and the explicitly named random-wall baseline. */
final class MazeCatalog {
  private MazeCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    demos.add(
        mazeDemo(
            MazeAlgorithms.Method.RANDOM_WALLS,
            "Independently scatters walls, then preserves a simple guaranteed route for"
                + " pathfinding.",
            "create an empty grid\n"
                + "for each cell\n"
                + "  place a wall with fixed probability\n"
                + "open a guaranteed start-to-target route\n"
                + "place exactly one start and target\n"
                + "emit the finished wall field",
            "O(RC)",
            "O(RC)"));
    demos.add(
        mazeDemo(
            MazeAlgorithms.Method.RECURSIVE_BACKTRACKING,
            "A randomized depth-first search carves a perfect maze and backtracks at dead ends.",
            "choose a start cell and mark it visited\n"
                + "while the DFS stack is not empty\n"
                + "  collect unvisited two-step neighbors\n"
                + "  carve through one random neighbor and push it\n"
                + "  otherwise pop and backtrack\n"
                + "place start and target in the connected maze",
            "O(RC)",
            "O(RC)"));
    demos.add(
        mazeDemo(
            MazeAlgorithms.Method.RECURSIVE_DIVISION,
            "Starts from an open chamber and recursively adds horizontal or vertical walls with one"
                + " passage.",
            "start with one bounded open chamber\n"
                + "choose a horizontal or vertical division\n"
                + "choose a random passage in the division\n"
                + "build the wall while leaving that passage open\n"
                + "recursively divide both new chambers\n"
                + "place start and target in the connected maze",
            "O(RC log(RC))",
            "O(log(RC))"));
    demos.add(
        mazeDemo(
            MazeAlgorithms.Method.RANDOMIZED_PRIM,
            "Grows a perfect maze by repeatedly connecting one random frontier cell.",
            "fill the grid with walls\n"
                + "carve one seed and collect its frontier\n"
                + "choose a random frontier wall\n"
                + "connect its unvisited cell and add new frontier walls\n"
                + "repeat until the frontier is empty\n"
                + "place start and target in the connected maze",
            "O(RC)",
            "O(RC)"));
    demos.add(
        mazeDemo(
            MazeAlgorithms.Method.RANDOMIZED_KRUSKAL,
            "Treats cells as disjoint sets and removes shuffled walls only when they join two"
                + " sets.",
            "carve cells and create one set per cell\n"
                + "shuffle every wall between adjacent cells\n"
                + "find the sets on both sides of the next wall\n"
                + "remove the wall and union different sets\n"
                + "skip walls whose cells are already connected\n"
                + "place start and target in the connected maze",
            "O(RC α(RC))",
            "O(RC)"));
    return List.copyOf(demos);
  }

  private static AlgorithmDemo mazeDemo(
      MazeAlgorithms.Method method,
      String explanation,
      String pseudocode,
      String time,
      String space) {
    return demo(
        "Maze Generation",
        method.toString(),
        explanation,
        pseudocode,
        time,
        space,
        "rows,columns,seed (5+ per side; at most 2,500 cells)",
        "15,21,2026",
        input -> mazeRun(method, input));
  }

  static AlgorithmRun mazeRun(MazeAlgorithms.Method method, String input) {
    String[] values = input.trim().split("\\s*,\\s*");
    if (values.length < 2 || values.length > 3)
      throw new IllegalArgumentException("Use rows,columns or rows,columns,seed");
    int rows = Integer.parseInt(values[0]);
    int columns = Integer.parseInt(values[1]);
    long seed = values.length == 3 ? Long.parseLong(values[2]) : 2026L;
    MazeAlgorithms.MazeResult maze =
        MazeAlgorithms.generate(method, rows, columns, new Random(seed));
    List<AlgorithmStep> steps = new ArrayList<>();
    for (int index = 0; index < maze.frames().size(); index++) {
      MazeAlgorithms.MazeFrame frame = maze.frames().get(index);
      List<Integer> cells = new ArrayList<>(rows * columns);
      List<String> labels = new ArrayList<>(rows * columns);
      Set<Integer> passages = new LinkedHashSet<>();
      String compact = frame.grid().replace("/", "");
      for (int cell = 0; cell < compact.length(); cell++) {
        char symbol = compact.charAt(cell);
        cells.add(symbol == '#' ? 1 : 0);
        labels.add(String.valueOf(symbol));
        if (symbol != '#') passages.add(cell);
      }
      boolean finished = index == maze.frames().size() - 1;
      steps.add(
          new AlgorithmStep(
              frame.message(),
              "initialize maze\n"
                  + "choose next region or edge\n"
                  + "inspect connectivity\n"
                  + "carve or divide\n"
                  + "continue generation\n"
                  + "finalize endpoints",
              frame.activeLine(),
              GRID,
              cells,
              labels,
              frame.active(),
              frame.frontier(),
              finished ? passages : Set.of(),
              List.of(),
              Map.of(
                  "Rows",
                  rows,
                  "Columns",
                  columns,
                  "Open cells",
                  passages.size(),
                  "Generation step",
                  index + 1),
              method + "\ncolumns=" + columns));
    }
    return new AlgorithmRun(
        steps,
        method
            + " generated a connected "
            + rows
            + " × "
            + columns
            + " grid in "
            + maze.frames().size()
            + " visual steps");
  }
}
