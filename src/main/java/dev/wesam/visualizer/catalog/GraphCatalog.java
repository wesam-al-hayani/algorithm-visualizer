package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;
import static dev.wesam.visualizer.model.AlgorithmStep.VisualKind.*;

import dev.wesam.visualizer.algorithms.GraphAlgorithms;
import dev.wesam.visualizer.algorithms.GridPathfinding;
import dev.wesam.visualizer.algorithms.UnionFind;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class GraphCatalog {
  private GraphCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    demos.add(
        graphDemo(
            "Breadth-First Search",
            "A FIFO queue explores vertices in layers.",
            "enqueue start\nwhile queue not empty\n  visit front and enqueue unseen neighbors",
            "O(V + E)",
            false,
            "0-1,0-2,1-3,2-4,4-5",
            "bfs"));
    demos.add(
        graphDemo(
            "Depth-First Search",
            "Recursion/stack follows one branch before backtracking.",
            "visit vertex\nfor each unseen neighbor\n  recursively visit neighbor",
            "O(V + E)",
            false,
            "0-1,0-2,1-3,2-4,4-5",
            "dfs"));
    demos.add(
        graphDemo(
            "Connected Components",
            "Repeated BFS groups every vertex of an undirected graph.",
            "for each unvisited vertex\n  start BFS\n  emit one component",
            "O(V + E)",
            false,
            "0-1,1-2,3-4,5-6",
            "components"));
    demos.add(
        graphDemo(
            "Topological Sort",
            "Kahn's algorithm repeatedly removes vertices with indegree zero.",
            "compute indegrees\n"
                + "enqueue zero-indegree vertices\n"
                + "remove and decrease successor indegrees",
            "O(V + E)",
            true,
            "0>1,0>2,1>3,2>3,3>4",
            "topological"));
    demos.add(
        graphDemo(
            "Kosaraju SCC",
            "Two DFS passes group mutually reachable vertices.",
            "DFS to get finish order\nreverse every edge\nDFS in reverse finish order",
            "O(V + E)",
            true,
            "0>1,1>0,1>2,2>3,3>2,3>4",
            "scc"));
    demos.add(
        graphDemo(
            "Dijkstra",
            "A priority queue settles the nearest unsettled vertex and relaxes outgoing edges.",
            "distance[start] ← 0\nextract nearest unsettled vertex\nrelax each outgoing edge",
            "O((V + E) log V)",
            true,
            "0>1:4,0>2:1,2>1:2,1>3:1,2>3:5,3>4:3",
            "dijkstra"));
    demos.add(
        graphDemo(
            "Bellman–Ford",
            "Repeated edge relaxation supports negative weights and detects reachable negative"
                + " cycles.",
            "repeat V−1 times\n  relax every edge\none more pass detects negative cycle",
            "O(VE)",
            true,
            "0>1:4,0>2:5,1>2:-2,2>3:3,3>4:1",
            "bellman"));
    demos.add(
        graphDemo(
            "Kruskal Minimum Spanning Tree",
            "Takes edges by weight while Union-Find rejects cycles.",
            "sort edges by weight\n"
                + "if endpoints are in different sets\n"
                + "  choose edge and union sets",
            "O(E log E)",
            false,
            "0-1:1,0-2:4,1-2:2,1-3:5,2-3:3",
            "kruskal"));
    demos.add(
        graphDemo(
            "Prim Minimum Spanning Tree",
            "Grows one tree with the cheapest edge leaving it.",
            "start with one vertex\nqueue crossing edges\nchoose cheapest edge to a new vertex",
            "O(E log V)",
            false,
            "0-1:1,0-2:4,1-2:2,1-3:5,2-3:3",
            "prim"));
    demos.add(
        demo(
            "Graph Algorithms",
            "Disjoint Set / Union-Find",
            "Union by rank and path compression maintain disjoint components.",
            "make each item its own parent\n"
                + "find compresses parent paths\n"
                + "union attaches lower rank root",
            "Almost O(1) amortized",
            "O(n)",
            "Pairs to union",
            "0-1,2-3,1-2,4-5,3-5",
            GraphCatalog::unionFind));
    demos.add(
        demo(
            "Graph Algorithms",
            "Edmonds–Karp Max Flow / Min Cut",
            "BFS finds augmenting paths in the residual network; unreachable residual vertices"
                + " define the minimum cut.",
            "while BFS finds source-to-sink path\n"
                + "  find bottleneck\n"
                + "  update forward and reverse residual edges",
            "O(VE²)",
            "O(V²)",
            "Uses the classic six-vertex network",
            "classic network",
            GraphCatalog::maxFlow));
    demos.add(
        demo(
            "Graph Algorithms",
            "Maximum Bipartite Matching",
            "An augmenting-path algorithm reassigns matches to make room for each left vertex.",
            "for each left vertex\n  search an augmenting path\n  flip matching along path",
            "O(VE)",
            "O(V)",
            "Rows separated by /; 1 means an edge",
            "110/010/011",
            GraphCatalog::matching));
    for (GridPathfinding.Method method : GridPathfinding.Method.values()) {
      demos.add(
          demo(
              "Graph Algorithms",
              "Grid " + title(method.name()),
              "Explores a wall grid and reconstructs a path. A* uses Manhattan distance.",
              "put start in frontier\n"
                  + "expand the next cell\n"
                  + "add passable unseen neighbors\n"
                  + "reconstruct parents at target",
              method == GridPathfinding.Method.DFS ? "O(RC)" : "O(RC log RC)",
              "O(RC)",
              "Rows of ., #, S, T separated by /",
              "S...../.##.../...#../.#..../.....T",
              input -> grid(input, method)));
    }
    return List.copyOf(demos);
  }

  static AlgorithmDemo graphDemo(
      String name,
      String explanation,
      String pseudo,
      String time,
      boolean directed,
      String input,
      String operation) {
    return demo(
        "Graph Algorithms",
        name,
        explanation,
        pseudo,
        time,
        "O(V)",
        directed
            ? "Directed edges (>, optional :weight)"
            : "Undirected edges (-, optional :weight)",
        input,
        value -> graph(value, directed, operation));
  }

  static AlgorithmRun graph(String input, boolean directed, String op) {
    GraphAlgorithms.Graph g = parseGraph(input, directed);
    List<Integer> order = new ArrayList<>();
    String result;
    Set<Integer> special = Set.of();
    Map<String, Number> stats = new LinkedHashMap<>();
    switch (op) {
      case "bfs" -> {
        order = GraphAlgorithms.bfs(g, 0);
        result = "BFS order: " + order;
      }
      case "dfs" -> {
        order = GraphAlgorithms.dfs(g, 0);
        result = "DFS order: " + order;
      }
      case "components" -> {
        var c = GraphAlgorithms.connectedComponents(g);
        for (Set<Integer> x : c) order.addAll(x);
        result = "Components: " + c;
        stats.put("Components", c.size());
      }
      case "topological" -> {
        order = GraphAlgorithms.topologicalSort(g);
        result = "Topological order: " + order;
      }
      case "scc" -> {
        var c = GraphAlgorithms.stronglyConnectedComponents(g);
        for (Set<Integer> x : c) order.addAll(x);
        result = "Strong components: " + c;
        stats.put("Components", c.size());
      }
      case "dijkstra" -> {
        var sp = GraphAlgorithms.dijkstra(g, 0);
        for (int i = 0; i < g.vertices(); i++)
          if (sp.distance()[i] < GraphAlgorithms.INF) order.add(i);
        result = "Distances: " + distanceText(sp.distance());
        stats.put("Relaxed/reachable", order.size());
      }
      case "bellman" -> {
        var sp = GraphAlgorithms.bellmanFord(g, 0);
        for (int i = 0; i < g.vertices(); i++)
          if (sp.distance()[i] < GraphAlgorithms.INF) order.add(i);
        result =
            "Distances: "
                + distanceText(sp.distance())
                + (sp.negativeCycle() ? "; negative cycle detected" : "");
        stats.put("Negative cycle", sp.negativeCycle() ? 1 : 0);
      }
      case "kruskal" -> {
        var mst = GraphAlgorithms.kruskal(g);
        order =
            mst.edges().stream()
                .flatMap(e -> java.util.stream.Stream.of(e.from(), e.to()))
                .distinct()
                .toList();
        special = Set.copyOf(order);
        result = "MST weight: " + mst.totalWeight() + ", edges: " + mst.edges();
        stats.put("MST weight", mst.totalWeight());
      }
      case "prim" -> {
        var mst = GraphAlgorithms.prim(g, 0);
        order =
            mst.edges().stream()
                .flatMap(e -> java.util.stream.Stream.of(e.from(), e.to()))
                .distinct()
                .toList();
        special = Set.copyOf(order);
        result = "MST weight: " + mst.totalWeight() + ", edges: " + mst.edges();
        stats.put("MST weight", mst.totalWeight());
      }
      default -> throw new IllegalArgumentException(op);
    }
    List<AlgorithmStep> s = new ArrayList<>();
    Set<Integer> visited = new LinkedHashSet<>();
    for (int i = 0; i < Math.max(1, order.size()); i++) {
      if (!order.isEmpty()) visited.add(order.get(i));
      Map<String, Number> frameStats = new LinkedHashMap<>(stats);
      frameStats.put("Visited", visited.size());
      s.add(
          graphStep(
              (order.isEmpty() ? "No reachable vertex" : "Process vertex " + order.get(i)),
              g,
              order.isEmpty() ? Set.of() : Set.of(order.get(i)),
              special,
              visited,
              frameStats,
              result));
    }
    return new AlgorithmRun(s, result);
  }

  static AlgorithmRun unionFind(String input) {
    List<int[]> pairs = new ArrayList<>();
    int max = 0;
    for (String t : input.split(",")) {
      String[] p = t.trim().split("-");
      int a = Integer.parseInt(p[0]), b = Integer.parseInt(p[1]);
      pairs.add(new int[] {a, b});
      max = Math.max(max, Math.max(a, b));
    }
    UnionFind uf = new UnionFind(max + 1);
    List<AlgorithmStep> s = new ArrayList<>();
    for (int[] p : pairs) {
      uf.union(p[0], p[1]);
      int[] parents = uf.parents();
      s.add(
          new AlgorithmStep(
              "Union(" + p[0] + ", " + p[1] + ")",
              "find both roots\nattach lower rank root\ncompress paths",
              1,
              TREE,
              Arrays.stream(parents).boxed().toList(),
              labels(parents.length),
              Set.of(p[0], p[1]),
              Set.of(),
              Set.of(),
              List.of(),
              Map.of("Sets", countRoots(uf, parents.length)),
              uf.toString()));
    }
    return new AlgorithmRun(s, uf.toString());
  }

  static AlgorithmRun maxFlow(String ignored) {
    int[][] c = {
      {0, 16, 13, 0, 0, 0},
      {0, 0, 10, 12, 0, 0},
      {0, 4, 0, 0, 14, 0},
      {0, 0, 9, 0, 0, 20},
      {0, 0, 0, 7, 0, 4},
      {0, 0, 0, 0, 0, 0}
    };
    var r = GraphAlgorithms.edmondsKarp(c, 0, 5);
    GraphAlgorithms.Graph g = capacityGraph(c);
    Set<Integer> source = new LinkedHashSet<>();
    for (int i = 0; i < r.sourceSideOfMinCut().length; i++)
      if (r.sourceSideOfMinCut()[i]) source.add(i);
    return new AlgorithmRun(
        List.of(
            graphStep(
                "Maximum flow complete; partitions show the minimum cut",
                g,
                Set.of(0, 5),
                source,
                rangeSet(0, 5),
                Map.of("Maximum flow", r.maximumFlow(), "Source-side vertices", source.size()),
                "Residual network computed by Edmonds–Karp")),
        "Maximum flow = minimum cut capacity = " + r.maximumFlow());
  }

  static AlgorithmRun matching(String input) {
    String[] rows = input.trim().split("/");
    boolean[][] e = new boolean[rows.length][];
    for (int i = 0; i < rows.length; i++) {
      e[i] = new boolean[rows[i].length()];
      for (int j = 0; j < e[i].length; j++) e[i][j] = rows[i].charAt(j) == '1';
    }
    int count = GraphAlgorithms.maximumBipartiteMatching(e);
    List<String> labels = new ArrayList<>();
    for (int i = 0; i < rows.length; i++) labels.add("L" + i);
    for (int j = 0; j < (rows.length == 0 ? 0 : e[0].length); j++) labels.add("R" + j);
    List<AlgorithmStep.VisualEdge> edges = new ArrayList<>();
    for (int i = 0; i < e.length; i++)
      for (int j = 0; j < e[i].length; j++)
        if (e[i][j]) edges.add(new AlgorithmStep.VisualEdge(i, rows.length + j, 1, false, ""));
    return new AlgorithmRun(
        List.of(
            new AlgorithmStep(
                "Search augmenting paths",
                "for each left vertex\nfind augmenting path\nflip matching",
                1,
                GRAPH,
                List.of(),
                labels,
                rangeSet(0, rows.length - 1),
                rangeSet(rows.length, labels.size() - 1),
                Set.of(),
                edges,
                Map.of("Matching size", count),
                "Left and right partitions")),
        "Maximum matching size: " + count);
  }

  static AlgorithmRun grid(String input, GridPathfinding.Method method) {
    String[] rows = input.trim().split("/");
    if (rows.length == 0) throw new IllegalArgumentException("empty grid");
    if ((long) rows.length * rows[0].length() > 2_500)
      throw new IllegalArgumentException("Grid visualization is limited to 2,500 cells");
    boolean[][] walls = new boolean[rows.length][rows[0].length()];
    GridPathfinding.Cell start = null, target = null;
    for (int r = 0; r < rows.length; r++) {
      if (rows[r].length() != rows[0].length())
        throw new IllegalArgumentException("grid rows differ");
      for (int c = 0; c < rows[r].length(); c++) {
        char symbol = rows[r].charAt(c);
        if (symbol != '.' && symbol != '#' && symbol != 'S' && symbol != 'T')
          throw new IllegalArgumentException("grid uses only ., #, S, and T");
        walls[r][c] = symbol == '#';
        if (symbol == 'S') start = new GridPathfinding.Cell(r, c);
        if (symbol == 'T') target = new GridPathfinding.Cell(r, c);
      }
    }
    if (start == null) start = new GridPathfinding.Cell(0, 0);
    if (target == null) target = new GridPathfinding.Cell(rows.length - 1, rows[0].length() - 1);
    var result = GridPathfinding.find(walls, start, target, method);
    List<Integer> values = new ArrayList<>();
    List<String> labels = new ArrayList<>();
    for (String row : rows)
      for (char c : row.toCharArray()) {
        values.add(c == '#' ? 1 : 0);
        labels.add(String.valueOf(c));
      }
    int startId = start.row() * rows[0].length() + start.column(),
        targetId = target.row() * rows[0].length() + target.column();
    List<AlgorithmStep> s = new ArrayList<>();
    Set<Integer> visited = new LinkedHashSet<>();
    for (var cell : result.visited()) {
      visited.add(cell.row() * rows[0].length() + cell.column());
      s.add(
          new AlgorithmStep(
              "Visit cell (" + cell.row() + ", " + cell.column() + ")",
              "take frontier cell\nvisit passable neighbors\nrecord parent",
              1,
              GRID,
              values,
              labels,
              Set.of(cell.row() * rows[0].length() + cell.column()),
              Set.of(startId, targetId),
              visited,
              List.of(),
              Map.of("Visited", visited.size(), "Path length", Math.max(0, result.cost())),
              rows.length + " × " + rows[0].length() + " grid"));
    }
    Set<Integer> path = new LinkedHashSet<>();
    for (var cell : result.path()) path.add(cell.row() * rows[0].length() + cell.column());
    if (!s.isEmpty()) {
      AlgorithmStep last = s.get(s.size() - 1);
      s.add(
          new AlgorithmStep(
              "Final path reconstructed",
              last.pseudocode(),
              2,
              GRID,
              values,
              labels,
              path,
              Set.of(startId, targetId),
              visited,
              List.of(),
              Map.of("Visited", visited.size(), "Path length", Math.max(0, result.cost())),
              "Path: " + result.path()));
    }
    return new AlgorithmRun(s, result.cost() < 0 ? "No path" : "Path length: " + result.cost());
  }
}
