package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.model.AlgorithmStep.VisualKind.*;

import dev.wesam.visualizer.algorithms.GraphAlgorithms;
import dev.wesam.visualizer.algorithms.SortAlgorithms;
import dev.wesam.visualizer.algorithms.UnionFind;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import dev.wesam.visualizer.structures.BinarySearchTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class CatalogSupport {
  private CatalogSupport() {}

  static AlgorithmDemo demo(
      String category,
      String name,
      String explanation,
      String pseudo,
      String time,
      String space,
      String hint,
      String input,
      java.util.function.Function<String, AlgorithmRun> runner) {
    return new AlgorithmDemo(category, name, explanation, pseudo, time, space, hint, input, runner);
  }

  static AlgorithmStep arrayStep(
      String m,
      int[] a,
      Set<Integer> active,
      Set<Integer> secondary,
      Set<Integer> complete,
      Map<String, Number> stats,
      String details) {
    return new AlgorithmStep(
        m,
        "inspect active item\nupdate state\ncontinue",
        1,
        ARRAY,
        Arrays.stream(a).boxed().toList(),
        List.of(),
        active,
        secondary,
        complete,
        List.of(),
        stats,
        details);
  }

  static AlgorithmStep treeStep(
      String m,
      List<Integer> values,
      Set<Integer> activeValues,
      Map<String, Number> stats,
      String details) {
    Set<Integer> active = new LinkedHashSet<>();
    for (int i = 0; i < values.size(); i++) if (activeValues.contains(values.get(i))) active.add(i);
    return new AlgorithmStep(
        m,
        "compare / update structure\nrestore invariant",
        1,
        TREE,
        values,
        List.of(),
        active,
        Set.of(),
        Set.of(),
        List.of(),
        stats,
        details);
  }

  static AlgorithmStep graphStep(
      String m,
      GraphAlgorithms.Graph g,
      Set<Integer> a,
      Set<Integer> b,
      Set<Integer> done,
      Map<String, Number> stats,
      String details) {
    List<AlgorithmStep.VisualEdge> edges =
        g.edges().stream()
            .map(
                e ->
                    new AlgorithmStep.VisualEdge(
                        e.from(), e.to(), e.weight(), g.directed(), Integer.toString(e.weight())))
            .toList();
    return new AlgorithmStep(
        m,
        "select next vertex or edge\nupdate frontier and labels\nrecord completed state",
        1,
        GRAPH,
        List.of(),
        labels(g.vertices()),
        a,
        b,
        done,
        edges,
        stats,
        details);
  }

  static AlgorithmStep tableStep(
      String m, int[][] table, int activeRow, Map<String, Number> stats, String details) {
    List<Integer> values = new ArrayList<>();
    List<String> labels = new ArrayList<>();
    for (int r = 0; r < table.length; r++)
      for (int c = 0; c < table[r].length; c++) {
        values.add(table[r][c]);
        labels.add(Integer.toString(table[r][c]));
      }
    int cols = table.length == 0 ? 0 : table[0].length;
    return new AlgorithmStep(
        m,
        "skip item\nor take item if it fits\nstore the better value",
        1,
        TABLE,
        values,
        labels,
        rangeSet(activeRow * cols, (activeRow + 1) * cols - 1),
        Set.of(),
        Set.of(),
        List.of(),
        stats,
        details + "\ncolumns=" + cols);
  }

  static AlgorithmStep matrixStep(
      String m, int[][] matrix, Map<String, Number> stats, String details) {
    List<Integer> v = new ArrayList<>();
    List<String> l = new ArrayList<>();
    for (int[] row : matrix)
      for (int x : row) {
        v.add(x);
        l.add(Integer.toString(x));
      }
    return new AlgorithmStep(
        m,
        "split\nrecurse\ncombine",
        1,
        TABLE,
        v,
        l,
        Set.of(),
        Set.of(),
        Set.of(),
        List.of(),
        stats,
        details + "\ncolumns=" + matrix.length);
  }

  static AlgorithmRun treeFrames(BinarySearchTree tree, List<Integer> order, String result) {
    List<AlgorithmStep> s = new ArrayList<>();
    Set<Integer> doneValues = new LinkedHashSet<>();
    List<Integer> layout = tree.levelOrder();
    for (int key : order) {
      doneValues.add(key);
      Set<Integer> done = new LinkedHashSet<>();
      for (int i = 0; i < layout.size(); i++) if (doneValues.contains(layout.get(i))) done.add(i);
      s.add(
          new AlgorithmStep(
              "Visit node " + key,
              "visit nodes in traversal order",
              1,
              TREE,
              layout,
              List.of(),
              Set.of(layout.indexOf(key)),
              Set.of(),
              done,
              List.of(),
              Map.of("Visited", done.size()),
              result));
    }
    return new AlgorithmRun(s, result);
  }

  static GraphAlgorithms.Graph parseGraph(String input, boolean directed) {
    List<GraphAlgorithms.Edge> edges = new ArrayList<>();
    int max = -1;
    for (String raw : input.split(",")) {
      String token = raw.trim();
      if (token.isEmpty()) continue;
      String[] weight = token.split(":", 2);
      int w = weight.length == 2 ? Integer.parseInt(weight[1].trim()) : 1;
      String separator = token.contains(">") ? ">" : "-";
      String endpoints = weight[0];
      String[] p = endpoints.split(java.util.regex.Pattern.quote(separator));
      if (p.length != 2) throw new IllegalArgumentException("Invalid edge: " + token);
      int a = Integer.parseInt(p[0].trim()), b = Integer.parseInt(p[1].trim());
      max = Math.max(max, Math.max(a, b));
      edges.add(new GraphAlgorithms.Edge(a, b, w));
    }
    if (max >= 60)
      throw new IllegalArgumentException("Graph visualization is limited to 60 vertices");
    return new GraphAlgorithms.Graph(max + 1, edges, directed);
  }

  static GraphAlgorithms.Graph completeGraph(int[][] d) {
    List<GraphAlgorithms.Edge> e = new ArrayList<>();
    for (int i = 0; i < d.length; i++)
      for (int j = i + 1; j < d.length; j++) e.add(new GraphAlgorithms.Edge(i, j, d[i][j]));
    return new GraphAlgorithms.Graph(d.length, e, false);
  }

  static GraphAlgorithms.Graph capacityGraph(int[][] c) {
    List<GraphAlgorithms.Edge> e = new ArrayList<>();
    for (int i = 0; i < c.length; i++)
      for (int j = 0; j < c.length; j++)
        if (c[i][j] > 0) e.add(new GraphAlgorithms.Edge(i, j, c[i][j]));
    return new GraphAlgorithms.Graph(c.length, e, true);
  }

  static int[][] matrix(String input) {
    String[] rows = input.trim().split(";");
    int[][] m = new int[rows.length][];
    for (int i = 0; i < rows.length; i++) m[i] = numbers(rows[i]);
    for (int[] r : m)
      if (r.length != m.length) throw new IllegalArgumentException("matrix must be square");
    return m;
  }

  static BinarySearchTree makeBst(int[] values) {
    BinarySearchTree tree = new BinarySearchTree();
    for (int v : values) tree.insert(v);
    return tree;
  }

  static Parts valuesAndParameter(String input) {
    String[] p = input.split(";", 2);
    if (p.length != 2) throw new IllegalArgumentException("Use values ; parameter");
    return new Parts(numbers(p[0]), Integer.parseInt(p[1].trim()));
  }

  static final class Parts {
    final int[] values;
    final int parameter;

    Parts(int[] values, int parameter) {
      this.values = values;
      this.parameter = parameter;
    }
  }

  static int[] numbers(String input) {
    String clean = input.trim();
    if (clean.isEmpty()) return new int[0];
    String[] parts = clean.split("[ ,]+");
    int[] result = new int[parts.length];
    for (int i = 0; i < parts.length; i++) result[i] = Integer.parseInt(parts[i].trim());
    return result;
  }

  static int[] numbersLimited(String input, int limit, String subject) {
    int[] result = numbers(input);
    if (result.length > limit)
      throw new IllegalArgumentException(
          "The " + subject + " visualization is limited to " + limit + " values");
    return result;
  }

  static List<String> labels(int n) {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < n; i++) result.add(Integer.toString(i));
    return result;
  }

  static List<String> chars(String s) {
    return s.chars().mapToObj(c -> String.valueOf((char) c)).toList();
  }

  static Set<Integer> prefix(int end) {
    return rangeSet(0, end - 1);
  }

  static Set<Integer> outside(int low, int high, int n) {
    Set<Integer> r = new LinkedHashSet<>();
    r.addAll(rangeSet(0, low - 1));
    r.addAll(rangeSet(high + 1, n - 1));
    return r;
  }

  static Set<Integer> rangeSet(int a, int b) {
    Set<Integer> r = new LinkedHashSet<>();
    for (int i = Math.max(0, a); i <= b; i++) r.add(i);
    return r;
  }

  static int countRoots(UnionFind uf, int n) {
    Set<Integer> s = new LinkedHashSet<>();
    for (int i = 0; i < n; i++) s.add(uf.find(i));
    return s.size();
  }

  static String distanceText(long[] d) {
    List<String> r = new ArrayList<>();
    for (long x : d) r.add(x >= GraphAlgorithms.INF ? "∞" : Long.toString(x));
    return r.toString();
  }

  static long countThrough(List<Integer> values, int limit) {
    long count = 0;
    for (int value : values) if (value <= limit) count++;
    return count;
  }

  static String title(String value) {
    StringBuilder b = new StringBuilder();
    for (String part : value.toLowerCase().split("_")) {
      if (!b.isEmpty()) b.append(' ');
      b.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
    }
    return b.toString();
  }

  static String sortTime(SortAlgorithms.Kind kind) {
    return switch (kind) {
      case BUBBLE -> "Best O(n), average/worst O(n²)";
      case INSERTION -> "Best O(n), average/worst O(n²)";
      case SELECTION -> "Best/average/worst O(n²)";
      case MERGE, HEAP -> "Best/average/worst O(n log n)";
      case COUNTING -> "Best/average/worst O(n + k)";
      case RADIX -> "Best/average/worst O(d(n + b))";
      case QUICK -> "Best/average O(n log n), worst O(n²)";
      case RANDOMIZED_QUICK -> "Expected O(n log n), worst O(n²)";
    };
  }
}
