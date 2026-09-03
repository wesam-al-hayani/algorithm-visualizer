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
        demo(
            "Graph Algorithms",
            "Floyd–Warshall",
            "Dynamic programming permits each vertex in turn as an intermediate for every"
                + " source-target pair.",
            "initialize the distance and next matrices\n"
                + "for each intermediate vertex k\n"
                + "  for each source i and target j\n"
                + "    candidate ← distance[i,k] + distance[k,j]\n"
                + "    if candidate improves distance[i,j]\n"
                + "      update distance[i,j] and next[i,j]\n"
                + "check distance[v,v] for negative cycles\n"
                + "reconstruct the selected path through next",
            "O(V³)",
            "O(V²)",
            "Directed weighted edges; then ; source,target",
            "0>1:3,0>3:7,1>0:8,1>2:2,2>0:5,2>3:1,3>0:2 ; 0,2",
            input -> allPairs(input, false)));
    demos.add(
        demo(
            "Graph Algorithms",
            "Johnson's Algorithm",
            "Sparse all-pairs shortest paths combine Bellman–Ford reweighting with Dijkstra from"
                + " every vertex.",
            "add a zero-edge super-source\n"
                + "run Bellman–Ford to detect a negative cycle\n"
                + "use its distances as vertex potentials\n"
                + "reweight every edge to be non-negative\n"
                + "run Dijkstra from every source\n"
                + "restore the original all-pairs distances",
            "O(VE log V)",
            "O(V² + E)",
            "Directed weighted edges; then ; source,target",
            "0>1:1,0>2:4,1>2:-2,1>3:5,2>3:2,3>0:3 ; 0,3",
            input -> allPairs(input, true)));
    demos.add(
        demo(
            "Graph Algorithms",
            "A* on Weighted Graph",
            "A priority queue combines path cost with a Euclidean coordinate heuristic to reach one"
                + " target.",
            "gScore[start] ← 0; put start in open set\n"
                + "remove vertex with smallest fScore\n"
                + "move it from open set to closed set\n"
                + "for each outgoing edge, compute tentative gScore\n"
                + "if improved, record parent and gScore\n"
                + "hScore ← Euclidean distance to target\n"
                + "fScore ← gScore + hScore\n"
                + "reconstruct the path when target closes",
            "O((V + E) log V)",
            "O(V)",
            "Non-negative directed edges; then ; target (source is 0)",
            "0>1:2,0>2:5,1>2:1,1>3:4,2>3:1,3>4:2,2>4:6 ; 4",
            GraphCatalog::graphAStar));
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

  static AlgorithmRun allPairs(String input, boolean useJohnson) {
    GraphQuery query = graphQuery(input, true, true);
    GraphAlgorithms.AllPairsShortestPaths result =
        useJohnson
            ? GraphAlgorithms.johnson(query.graph)
            : GraphAlgorithms.floydWarshall(query.graph);
    if (useJohnson) return johnsonFrames(query, result);
    return floydWarshallFrames(query, result);
  }

  private static AlgorithmRun floydWarshallFrames(
      GraphQuery query, GraphAlgorithms.AllPairsShortestPaths expected) {
    GraphAlgorithms.Graph graph = query.graph;
    int n = graph.vertices();
    long[][] distance = new long[n][n];
    for (int row = 0; row < n; row++) {
      Arrays.fill(distance[row], GraphAlgorithms.INF);
      distance[row][row] = 0;
    }
    for (GraphAlgorithms.Edge edge : graph.edges())
      distance[edge.from()][edge.to()] = Math.min(distance[edge.from()][edge.to()], edge.weight());
    List<AlgorithmStep> steps = new ArrayList<>();
    for (int intermediate = 0; intermediate < n; intermediate++) {
      Set<Integer> updated = new LinkedHashSet<>();
      Set<Integer> compared = new LinkedHashSet<>();
      for (int from = 0; from < n; from++) {
        compared.add(from * n + intermediate);
        for (int to = 0; to < n; to++) {
          compared.add(intermediate * n + to);
          if (distance[from][intermediate] < GraphAlgorithms.INF
              && distance[intermediate][to] < GraphAlgorithms.INF
              && distance[from][to] > distance[from][intermediate] + distance[intermediate][to]) {
            distance[from][to] = distance[from][intermediate] + distance[intermediate][to];
            updated.add(from * n + to);
          }
        }
      }
      steps.add(
          distanceMatrixStep(
              "Allow vertex " + intermediate + " as an intermediate",
              distance,
              updated.isEmpty() ? 3 : 5,
              updated,
              compared,
              Map.of(
                  "Intermediate k",
                  intermediate,
                  "Updated cells",
                  updated.size(),
                  "Negative cycle",
                  expected.negativeCycle() ? 1 : 0),
              "Compared row and column for k=" + intermediate));
    }
    String path = selectedPath(expected, query.source, query.target);
    if (!steps.isEmpty()) {
      long[][] verified = expected.distance();
      steps.add(
          distanceMatrixStep(
              expected.negativeCycle()
                  ? "Negative cycle detected on the matrix diagonal"
                  : "Reconstruct selected path " + query.source + " → " + query.target,
              verified,
              expected.negativeCycle() ? 6 : 7,
              Set.of(query.source * n + query.target),
              Set.of(),
              Map.of("Negative cycle", expected.negativeCycle() ? 1 : 0),
              "Next matrix: " + Arrays.deepToString(expected.next()) + "\n" + path));
    }
    return new AlgorithmRun(steps, path);
  }

  private static AlgorithmRun johnsonFrames(
      GraphQuery query, GraphAlgorithms.AllPairsShortestPaths result) {
    List<String> phases =
        List.of(
            "Add a zero-edge super-source",
            "Run Bellman–Ford",
            "Compute vertex potentials",
            "Reweight edges to non-negative costs",
            "Run Dijkstra from every source",
            "Restore original distances");
    List<AlgorithmStep> steps = new ArrayList<>();
    for (int phase = 0; phase < phases.size(); phase++)
      steps.add(
          distanceMatrixStep(
              phases.get(phase),
              result.distance(),
              phase,
              phase == phases.size() - 1
                  ? Set.of(query.source * query.graph.vertices() + query.target)
                  : Set.of(),
              Set.of(),
              Map.of(
                  "Phase",
                  phase + 1,
                  "Dijkstra runs",
                  phase >= 4 ? query.graph.vertices() : 0,
                  "Negative cycle",
                  result.negativeCycle() ? 1 : 0),
              phase == phases.size() - 1
                  ? selectedPath(result, query.source, query.target)
                  : "Major phase " + (phase + 1) + " of " + phases.size()));
    return new AlgorithmRun(steps, selectedPath(result, query.source, query.target));
  }

  static AlgorithmRun graphAStar(String input) {
    GraphQuery query = graphQuery(input, true, false);
    List<GraphAlgorithms.Point> coordinates = new ArrayList<>();
    for (int vertex = 0; vertex < query.graph.vertices(); vertex++) {
      double angle = 2 * Math.PI * vertex / Math.max(1, query.graph.vertices());
      coordinates.add(new GraphAlgorithms.Point(.4 * Math.cos(angle), .4 * Math.sin(angle)));
    }
    var search = GraphAlgorithms.aStar(query.graph, coordinates, 0, query.target);
    List<AlgorithmStep> steps = new ArrayList<>();
    for (GraphAlgorithms.AStarFrame frame : search.frames()) {
      int current = frame.current();
      steps.add(
          graphStep(
              "Expand vertex " + current,
              query.graph,
              Set.of(current),
              frame.open(),
              frame.closed(),
              Map.of(
                  "gScore",
                  frame.gScore()[current],
                  "hScore",
                  frame.hScore()[current],
                  "fScore",
                  frame.fScore()[current],
                  "Open set",
                  frame.open().size(),
                  "Closed set",
                  frame.closed().size()),
              "Open: "
                  + frame.open()
                  + "\nClosed: "
                  + frame.closed()
                  + "\nPath: "
                  + search.path()));
    }
    String summary =
        search.found()
            ? "Path " + search.path() + "; cost " + search.cost()
            : "No path from 0 to " + query.target;
    return new AlgorithmRun(steps, summary);
  }

  private static AlgorithmStep distanceMatrixStep(
      String message,
      long[][] distance,
      int activeLine,
      Set<Integer> active,
      Set<Integer> compared,
      Map<String, Number> statistics,
      String details) {
    List<String> cells = new ArrayList<>();
    for (long[] row : distance)
      for (long value : row) cells.add(value >= GraphAlgorithms.INF ? "∞" : Long.toString(value));
    return new AlgorithmStep(
        message,
        "initialize matrix\n"
            + "choose intermediate or phase\n"
            + "compare candidate routes\n"
            + "update distance and reconstruction data",
        activeLine,
        TABLE,
        List.of(),
        cells,
        active,
        compared,
        Set.of(),
        List.of(),
        statistics,
        details + "\ncolumns=" + distance.length);
  }

  private static GraphQuery graphQuery(
      String input, boolean directed, boolean sourceAndTargetRequired) {
    String[] parts = input.split(";", 2);
    if (parts.length != 2)
      throw new IllegalArgumentException(
          sourceAndTargetRequired ? "Use edges ; source,target" : "Use edges ; target");
    GraphAlgorithms.Graph graph = parseGraph(parts[0], directed);
    int[] selection = numbers(parts[1]);
    int source = sourceAndTargetRequired && selection.length > 0 ? selection[0] : 0;
    int target =
        sourceAndTargetRequired && selection.length > 1
            ? selection[1]
            : selection.length > 0 ? selection[0] : -1;
    if (target < 0
        || source < 0
        || target >= graph.vertices()
        || source >= graph.vertices()
        || sourceAndTargetRequired && selection.length != 2
        || !sourceAndTargetRequired && selection.length != 1)
      throw new IllegalArgumentException("Selected source/target is outside the graph");
    return new GraphQuery(graph, source, target);
  }

  private static String selectedPath(
      GraphAlgorithms.AllPairsShortestPaths result, int source, int target) {
    if (result.negativeCycle()) return "Negative cycle detected; shortest paths are undefined";
    List<Integer> path = result.path(source, target);
    return path.isEmpty()
        ? "No path from " + source + " to " + target
        : "Path " + path + "; cost " + result.distance()[source][target];
  }

  private record GraphQuery(GraphAlgorithms.Graph graph, int source, int target) {}

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
