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
  private static final String CLASSIC_FLOW_NETWORK =
      "0,16,13,0,0,0/0,0,10,12,0,0/0,4,0,0,14,0/0,0,9,0,0,20/0,0,0,7,0,4/0,0,0,0,0,0 ; 0,5";

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
        demo(
            "Graph Algorithms",
            "Tarjan SCC",
            "One DFS uses discovery indices, low-link values, and a stack to emit strong"
                + " components.",
            "initialize every index as unvisited\n"
                + "DFS each unvisited vertex\n"
                + "assign index and low-link; push vertex\n"
                + "update low-link through tree and stack edges\n"
                + "if low-link equals index, pop one component",
            "O(V + E)",
            "O(V)",
            "Directed edges (>, optional :weight)",
            "0>1,1>0,1>2,2>3,3>2,3>4",
            GraphCatalog::tarjan));
    demos.add(
        demo(
            "Graph Algorithms",
            "Bridge Finding",
            "Undirected DFS marks an edge as a bridge when its child cannot reach an ancestor.",
            "DFS each unvisited vertex\n"
                + "assign discovery and low times\n"
                + "update low from back edges and child low values\n"
                + "if child.low > vertex.discovery, record bridge",
            "O(V + E)",
            "O(V)",
            "Undirected edges (-, optional :weight)",
            "0-1,1-2,2-0,1-3,3-4,4-5,5-3,4-6",
            input -> lowLinks(input, false)));
    demos.add(
        demo(
            "Graph Algorithms",
            "Articulation Points",
            "Low-link values identify vertices whose removal separates an undirected component.",
            "DFS each unvisited vertex\n"
                + "assign discovery and low times\n"
                + "update low from back edges and child low values\n"
                + "root is a cut vertex with multiple DFS children\n"
                + "non-root is a cut vertex when child.low ≥ discovery",
            "O(V + E)",
            "O(V)",
            "Undirected edges (-, optional :weight)",
            "0-1,1-2,2-0,1-3,3-4,4-5,5-3,4-6",
            input -> lowLinks(input, true)));
    demos.add(
        demo(
            "Graph Algorithms",
            "Euler Path / Circuit",
            "Hierholzer consumes every edge exactly once when degree and connectivity conditions"
                + " permit.",
            "check nonzero-degree connectivity\n"
                + "check undirected odd degrees or directed in/out balance\n"
                + "choose the required start vertex\n"
                + "while the stack is not empty\n"
                + "  use an unused edge, or backtrack into the trail\n"
                + "reverse the completed trail",
            "O(V + E)",
            "O(V + E)",
            "Use - for undirected edges or > for directed edges",
            "0-1,1-2,2-0,0-3,3-4,4-0",
            GraphCatalog::euler));
    demos.add(
        demo(
            "Graph Algorithms",
            "Bipartite Check",
            "BFS assigns alternating colors and reports the first same-color conflict edge.",
            "for each uncolored component\n"
                + "color its start and enqueue it\n"
                + "remove a vertex and inspect each edge\n"
                + "color unvisited neighbors oppositely\n"
                + "if endpoint colors match, report conflict",
            "O(V + E)",
            "O(V)",
            "Undirected edges (-, optional :weight)",
            "0-3,0-4,1-3,1-5,2-4,2-5",
            GraphCatalog::bipartite));
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
        demo(
            "Graph Algorithms",
            "Grid Dijkstra vs A*",
            "Runs Dijkstra and A* on the same non-negative unit-weight grid. Both must return the"
                + " same optimal cost; A* uses Manhattan distance to guide expansion.",
            "parse one shared non-negative grid\n"
                + "run Dijkstra without a heuristic\n"
                + "run A* with admissible Manhattan distance\n"
                + "compare path cost, visited cells, and visualization steps\n"
                + "require both optimal path costs to agree",
            "O(RC log(RC))",
            "O(RC)",
            "Rows of ., #, S, T separated by /",
            "S...../.##.../...#../.#..../.....T",
            GraphCatalog::gridComparison));
    demos.add(
        demo(
            "Graph Algorithms",
            "Shortest-Path Algorithm Comparison",
            "Compares single-source and all-pairs algorithms on one directed graph while stating"
                + " each algorithm's edge-weight requirements and result scope.",
            "parse one shared directed weighted graph\n"
                + "run Dijkstra and Bellman–Ford from the selected source\n"
                + "run Floyd–Warshall and Johnson for all pairs\n"
                + "read the same selected source-target cost from every result\n"
                + "compare requirements, scope, and agreement",
            "O((V+E)logV) to O(V³)",
            "O(V) to O(V²)",
            "Non-negative directed weighted edges; then ; source,target",
            "0>1:4,0>2:1,2>1:2,1>3:1,2>3:5,3>4:3 ; 0,4",
            GraphCatalog::shortestPathComparison));
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
            "Capacity rows separated by /; then ; source,sink",
            CLASSIC_FLOW_NETWORK,
            GraphCatalog::maxFlow));
    demos.add(
        demo(
            "Graph Algorithms",
            "Dinic's Maximum Flow Algorithm",
            "BFS creates a level graph and DFS sends a blocking flow through admissible residual"
                + " edges.",
            "initialize the residual graph\n"
                + "while BFS reaches the sink and assigns levels\n"
                + "  reset current-edge pointers\n"
                + "  DFS along level-increasing residual edges\n"
                + "  update forward/reverse residual capacity and total flow\n"
                + "  finish the blocking flow for this level graph\n"
                + "residual reachability gives the minimum cut",
            "O(V²E)",
            "O(V²)",
            "Capacity rows separated by /; then ; source,sink",
            CLASSIC_FLOW_NETWORK,
            GraphCatalog::dinic));
    demos.add(
        demo(
            "Graph Algorithms",
            "Edmonds–Karp vs Dinic",
            "Runs both maximum-flow algorithms on the same network and compares flow and search"
                + " metrics.",
            "parse one shared capacity network\n"
                + "run Edmonds–Karp BFS augmentations\n"
                + "run Dinic level graphs and blocking flows\n"
                + "compare maximum flow, phases, augmentations, and steps\n"
                + "require both maximum-flow values to agree",
            "O(VE²) vs O(V²E)",
            "O(V²)",
            "Capacity rows separated by /; then ; source,sink",
            CLASSIC_FLOW_NETWORK,
            GraphCatalog::flowComparison));
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
    demos.add(
        demo(
            "Graph Algorithms",
            "Hopcroft–Karp Matching",
            "BFS layers many shortest augmenting paths, then DFS augments a batch per phase; the"
                + " result is compared with the simple matcher.",
            "start BFS from every free left vertex\n"
                + "build alternating shortest-path layers\n"
                + "for each free left vertex in the layered graph\n"
                + "  DFS one shortest augmenting path\n"
                + "  flip matched and unmatched edges\n"
                + "repeat phases until no augmenting path exists",
            "O(E√V)",
            "O(V)",
            "Rows separated by /; 1 means an edge",
            "1100/0110/0011/1001",
            GraphCatalog::hopcroftKarp));
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

  static AlgorithmRun tarjan(String input) {
    GraphAlgorithms.Graph graph = parseGraph(input, true);
    GraphAlgorithms.TarjanResult result = GraphAlgorithms.tarjanStronglyConnectedComponents(graph);
    List<AlgorithmStep> steps = new ArrayList<>();
    Set<Integer> completed = new LinkedHashSet<>();
    int componentCount = 0;
    for (GraphAlgorithms.TarjanFrame frame : result.frames()) {
      completed.addAll(frame.completedComponent());
      if (!frame.completedComponent().isEmpty()) componentCount++;
      int current = frame.current();
      steps.add(
          graphStep(
              frame.event(),
              graph,
              Set.of(current),
              Set.copyOf(frame.stack()),
              completed,
              !frame.completedComponent().isEmpty()
                  ? 4
                  : frame.event().startsWith("Discover") ? 2 : 3,
              Map.of(
                  "DFS index",
                  frame.index()[current],
                  "Low-link",
                  frame.lowLink()[current],
                  "Stack",
                  frame.stack().size(),
                  "Components",
                  componentCount),
              "Index: "
                  + Arrays.toString(frame.index())
                  + "\nLow-link: "
                  + Arrays.toString(frame.lowLink())
                  + "\nStack: "
                  + frame.stack()));
    }
    return new AlgorithmRun(steps, "Strong components: " + result.components());
  }

  static AlgorithmRun lowLinks(String input, boolean articulationMode) {
    GraphAlgorithms.Graph graph = parseGraph(input, false);
    GraphAlgorithms.LowLinkResult result = GraphAlgorithms.undirectedLowLinks(graph);
    Set<Integer> bridgeVertices = new LinkedHashSet<>();
    result
        .bridges()
        .forEach(
            edge -> {
              bridgeVertices.add(edge.from());
              bridgeVertices.add(edge.to());
            });
    List<AlgorithmStep> steps = new ArrayList<>();
    for (GraphAlgorithms.LowLinkFrame frame : result.frames()) {
      Set<Integer> active = new LinkedHashSet<>();
      active.add(frame.current());
      if (frame.neighbor() >= 0) active.add(frame.neighbor());
      steps.add(
          graphStep(
              frame.event(),
              graph,
              active,
              articulationMode ? frame.articulationPoints() : bridgeVertices,
              Set.of(),
              articulationMode && frame.articulationPoints().contains(frame.current())
                  ? 4
                  : !articulationMode && !frame.bridges().isEmpty() ? 3 : 2,
              Map.of(
                  "Discovery",
                  frame.discovery()[frame.current()],
                  "Low-link",
                  frame.lowLink()[frame.current()],
                  "Bridges",
                  frame.bridges().size(),
                  "Cut vertices",
                  frame.articulationPoints().size()),
              "Discovery: "
                  + Arrays.toString(frame.discovery())
                  + "\nLow-link: "
                  + Arrays.toString(frame.lowLink())
                  + "\nBridges: "
                  + frame.bridges()
                  + "\nArticulation points: "
                  + frame.articulationPoints()));
    }
    String summary =
        articulationMode
            ? "Articulation points: " + result.articulationPoints()
            : "Bridges: " + result.bridges();
    return new AlgorithmRun(steps, summary);
  }

  static AlgorithmRun euler(String input) {
    boolean directed = input.contains(">");
    GraphAlgorithms.Graph graph = parseGraph(input, directed);
    GraphAlgorithms.EulerResult result = GraphAlgorithms.eulerTrail(graph);
    List<AlgorithmStep> steps = new ArrayList<>();
    for (GraphAlgorithms.EulerFrame frame : result.frames())
      steps.add(
          graphStep(
              frame.event(),
              graph,
              Set.of(frame.current()),
              Set.copyOf(frame.stack()),
              Set.copyOf(frame.reverseTrail()),
              4,
              Map.of(
                  "Used edges",
                  frame.usedEdges(),
                  "Stack",
                  frame.stack().size(),
                  "Trail vertices",
                  frame.reverseTrail().size()),
              "Stack: "
                  + frame.stack()
                  + "\nReverse trail: "
                  + frame.reverseTrail()
                  + "\n"
                  + result.reason()));
    if (steps.isEmpty())
      steps.add(
          graphStep(
              result.reason(),
              graph,
              Set.of(),
              Set.of(),
              Set.copyOf(result.trail()),
              Map.of("Euler trail exists", result.exists() ? 1 : 0),
              result.reason()));
    return new AlgorithmRun(
        steps,
        result.exists()
            ? (result.circuit() ? "Euler circuit: " : "Euler path: ") + result.trail()
            : "No Euler path: " + result.reason());
  }

  static AlgorithmRun bipartite(String input) {
    GraphAlgorithms.Graph graph = parseGraph(input, false);
    GraphAlgorithms.BipartiteResult result = GraphAlgorithms.bipartiteCheck(graph);
    List<AlgorithmStep> steps = new ArrayList<>();
    for (GraphAlgorithms.BipartiteFrame frame : result.frames()) {
      Set<Integer> firstColor = new LinkedHashSet<>(), secondColor = new LinkedHashSet<>();
      for (int vertex = 0; vertex < frame.color().length; vertex++) {
        if (frame.color()[vertex] == 0) firstColor.add(vertex);
        if (frame.color()[vertex] == 1) secondColor.add(vertex);
      }
      Set<Integer> active = Set.of(frame.current(), frame.neighbor());
      steps.add(
          graphStep(
              frame.event(),
              graph,
              active,
              firstColor,
              secondColor,
              frame.event().startsWith("Conflict") ? 4 : 3,
              Map.of(
                  "Color 0",
                  firstColor.size(),
                  "Color 1",
                  secondColor.size(),
                  "Frontier",
                  frame.frontier().size()),
              "Colors: "
                  + Arrays.toString(frame.color())
                  + (result.conflict() == null ? "" : "\nConflict: " + result.conflict())));
    }
    if (steps.isEmpty())
      steps.add(
          graphStep(
              "No coloring conflict",
              graph,
              Set.of(),
              Set.of(),
              Set.of(),
              Map.of("Bipartite", result.bipartite() ? 1 : 0),
              "Colors: " + Arrays.toString(result.color())));
    return new AlgorithmRun(steps, result.bipartite() ? "Bipartite" : "Not Bipartite");
  }

  static AlgorithmRun hopcroftKarp(String input) {
    boolean[][] edges = bipartiteMatrix(input);
    GraphAlgorithms.MatchingResult result = GraphAlgorithms.hopcroftKarp(edges);
    int simple = GraphAlgorithms.maximumBipartiteMatching(edges);
    List<String> labels = bipartiteLabels(edges);
    List<AlgorithmStep.VisualEdge> visualEdges = bipartiteEdges(edges);
    List<AlgorithmStep> steps = new ArrayList<>();
    for (GraphAlgorithms.MatchingFrame frame : result.frames()) {
      Set<Integer> active = frame.augmentedLeft() < 0 ? Set.of() : Set.of(frame.augmentedLeft());
      Set<Integer> free = new LinkedHashSet<>(), matched = new LinkedHashSet<>();
      for (int left = 0; left < frame.leftMatch().length; left++) {
        if (frame.leftMatch()[left] < 0) free.add(left);
        else {
          matched.add(left);
          matched.add(edges.length + frame.leftMatch()[left]);
        }
      }
      steps.add(
          new AlgorithmStep(
              frame.event(),
              "BFS layers free left vertices\n"
                  + "DFS augments a batch of shortest paths\n"
                  + "repeat until no augmenting path exists",
              frame.augmentedLeft() < 0 ? 1 : 3,
              GRAPH,
              List.of(),
              labels,
              active,
              free,
              matched,
              visualEdges,
              Map.of(
                  "BFS phase",
                  frame.phase(),
                  "Matching",
                  countMatches(frame.leftMatch()),
                  "Free left",
                  free.size()),
              "Layers: "
                  + Arrays.toString(frame.layers())
                  + "\nLeft matches: "
                  + Arrays.toString(frame.leftMatch())));
    }
    if (steps.isEmpty())
      steps.add(
          new AlgorithmStep(
              "No augmenting path exists",
              "BFS layers free left vertices\n"
                  + "DFS augments a batch of shortest paths\n"
                  + "repeat until no augmenting path exists",
              2,
              GRAPH,
              List.of(),
              labels,
              Set.of(),
              Set.of(),
              Set.of(),
              visualEdges,
              Map.of("Matching", result.size()),
              "No matchable edge"));
    return new AlgorithmRun(
        steps,
        "Hopcroft–Karp matching: "
            + result.size()
            + "; simple augmenting-path matching: "
            + simple
            + "; agree: "
            + (result.size() == simple));
  }

  private static int countMatches(int[] leftMatch) {
    return (int) Arrays.stream(leftMatch).filter(value -> value >= 0).count();
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

  static AlgorithmRun gridComparison(String input) {
    AlgorithmRun dijkstra = grid(input, GridPathfinding.Method.DIJKSTRA);
    AlgorithmRun aStar = grid(input, GridPathfinding.Method.A_STAR);
    long dijkstraCost = finalStatistic(dijkstra, "Path length");
    long aStarCost = finalStatistic(aStar, "Path length");
    boolean agree =
        dijkstra.result().equals("No path")
            ? aStar.result().equals("No path")
            : dijkstraCost == aStarCost;
    List<String> table =
        new ArrayList<>(
            List.of("Algorithm", "Requirement", "Path Cost", "Visited Cells", "Visual Steps"));
    addPathfinderRow(table, "Dijkstra", "non-negative weights", dijkstraCost, dijkstra);
    addPathfinderRow(table, "A*", "non-negative + admissible h", aStarCost, aStar);
    AlgorithmStep step =
        new AlgorithmStep(
            agree ? "Both pathfinders agree on the optimal cost" : "Path-cost mismatch detected",
            "parse shared grid\nrun Dijkstra\nrun A*\ncompare work\nverify cost",
            4,
            TABLE,
            List.of(),
            table,
            rangeSet(5, table.size() - 1),
            Set.of(),
            Set.of(),
            List.of(),
            Map.of(
                "Algorithms agree", agree ? 1 : 0,
                "Dijkstra visited", finalStatistic(dijkstra, "Visited"),
                "A* visited", finalStatistic(aStar, "Visited")),
            "columns=5\nBoth algorithms used the exact same unit-weight grid. A* uses Manhattan"
                + " h; Dijkstra uses h=0.");
    return new AlgorithmRun(
        List.of(step), "Dijkstra=" + dijkstra.result() + "; A*=" + aStar.result());
  }

  static AlgorithmRun shortestPathComparison(String input) {
    GraphQuery query = graphQuery(input, true, true);
    GraphAlgorithms.ShortestPaths dijkstra = GraphAlgorithms.dijkstra(query.graph, query.source);
    GraphAlgorithms.ShortestPaths bellman = GraphAlgorithms.bellmanFord(query.graph, query.source);
    GraphAlgorithms.AllPairsShortestPaths floyd = GraphAlgorithms.floydWarshall(query.graph);
    GraphAlgorithms.AllPairsShortestPaths johnson = GraphAlgorithms.johnson(query.graph);
    long[] costs = {
      dijkstra.distance()[query.target],
      bellman.distance()[query.target],
      floyd.distance()[query.source][query.target],
      johnson.distance()[query.source][query.target]
    };
    boolean agree = Arrays.stream(costs).allMatch(cost -> cost == costs[0]);
    List<String> table =
        new ArrayList<>(
            List.of("Algorithm", "Scope", "Edge Requirement", "Selected Cost", "Work Profile"));
    addShortestPathRow(
        table, "Dijkstra", "single source", "no negative weights", costs[0], "sparse: E log V");
    addShortestPathRow(
        table, "Bellman–Ford", "single source", "negative allowed; no neg cycle", costs[1], "VE");
    addShortestPathRow(
        table,
        "Floyd–Warshall",
        "all pairs",
        "negative allowed; no neg cycle",
        costs[2],
        "V³ dense");
    addShortestPathRow(
        table,
        "Johnson",
        "all pairs",
        "negative allowed; no neg cycle",
        costs[3],
        "sparse: VE log V");
    AlgorithmStep step =
        new AlgorithmStep(
            agree
                ? "All four algorithms agree on the selected route cost"
                : "Shortest-path mismatch detected",
            "parse shared graph\n"
                + "run single-source algorithms\n"
                + "run all-pairs algorithms\n"
                + "read selected route\n"
                + "compare requirements",
            4,
            TABLE,
            List.of(),
            table,
            rangeSet(5, table.size() - 1),
            Set.of(),
            Set.of(),
            List.of(),
            Map.of(
                "Algorithms agree", agree ? 1 : 0,
                "Vertices", query.graph.vertices(),
                "Edges", query.graph.edges().size()),
            "columns=5\nSelected route "
                + query.source
                + " → "
                + query.target
                + ". Dijkstra is intentionally rejected for negative-edge comparison inputs.");
    return new AlgorithmRun(
        List.of(step),
        "Selected cost " + distanceValue(costs[0]) + "; all four algorithms agree=" + agree);
  }

  private static long finalStatistic(AlgorithmRun run, String name) {
    if (run.steps().isEmpty()) return 0;
    return run.steps().get(run.steps().size() - 1).statistics().getOrDefault(name, 0).longValue();
  }

  private static void addPathfinderRow(
      List<String> table, String name, String requirement, long cost, AlgorithmRun run) {
    table.add(name);
    table.add(requirement);
    table.add(run.result().equals("No path") ? "unreachable" : Long.toString(cost));
    table.add(Long.toString(finalStatistic(run, "Visited")));
    table.add(Integer.toString(run.steps().size()));
  }

  private static void addShortestPathRow(
      List<String> table, String name, String scope, String requirement, long cost, String work) {
    table.add(name);
    table.add(scope);
    table.add(requirement);
    table.add(distanceValue(cost));
    table.add(work);
  }

  private static String distanceValue(long distance) {
    return distance >= GraphAlgorithms.INF ? "∞" : Long.toString(distance);
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

  static AlgorithmRun maxFlow(String input) {
    FlowNetwork network = flowNetwork(input);
    int[][] capacity = network.capacity;
    var r = GraphAlgorithms.edmondsKarp(capacity, network.source, network.sink);
    GraphAlgorithms.Graph g = capacityGraph(capacity);
    List<AlgorithmStep> steps = new ArrayList<>();
    for (int index = 0; index < r.frames().size(); index++) {
      GraphAlgorithms.FlowFrame frame = r.frames().get(index);
      Set<Integer> sourceSide = new LinkedHashSet<>();
      if (index == r.frames().size() - 1)
        for (int vertex = 0; vertex < r.sourceSideOfMinCut().length; vertex++)
          if (r.sourceSideOfMinCut()[vertex]) sourceSide.add(vertex);
      steps.add(
          graphStep(
              frame.event(),
              g,
              Set.copyOf(frame.augmentingPath()),
              sourceSide,
              index == r.frames().size() - 1 ? rangeSet(0, capacity.length - 1) : Set.of(),
              Map.of(
                  "Maximum flow", r.maximumFlow(),
                  "Current flow", frame.totalFlow(),
                  "BFS phases", r.bfsPhases(),
                  "Augmentations", r.augmentations(),
                  "Bottleneck", frame.bottleneck()),
              "Path: "
                  + frame.augmentingPath()
                  + "\nResidual network: "
                  + Arrays.deepToString(frame.residual())));
    }
    return new AlgorithmRun(steps, "Maximum flow = minimum cut capacity = " + r.maximumFlow());
  }

  static AlgorithmRun dinic(String input) {
    FlowNetwork network = flowNetwork(input);
    GraphAlgorithms.DinicResult result =
        GraphAlgorithms.dinic(network.capacity, network.source, network.sink);
    GraphAlgorithms.Graph graph = capacityGraph(network.capacity);
    List<AlgorithmStep> steps = new ArrayList<>();
    for (int index = 0; index < result.frames().size(); index++) {
      GraphAlgorithms.DinicFrame frame = result.frames().get(index);
      Set<Integer> levelVertices = new LinkedHashSet<>();
      for (int vertex = 0; vertex < frame.level().length; vertex++)
        if (frame.level()[vertex] >= 0) levelVertices.add(vertex);
      Set<Integer> sourceSide = new LinkedHashSet<>();
      if (index == result.frames().size() - 1)
        for (int vertex = 0; vertex < result.sourceSideOfMinCut().length; vertex++)
          if (result.sourceSideOfMinCut()[vertex]) sourceSide.add(vertex);
      steps.add(
          new AlgorithmStep(
              frame.event(),
              "initialize residual graph\n"
                  + "build BFS level graph\n"
                  + "reset current edges\n"
                  + "DFS admissible edges\n"
                  + "update residual capacity\n"
                  + "complete blocking flow\n"
                  + "extract minimum cut",
              frame.event().startsWith("BFS") ? 1 : frame.event().startsWith("DFS") ? 4 : 5,
              GRAPH,
              List.of(),
              labels(network.capacity.length),
              Set.copyOf(frame.augmentingPath()),
              levelVertices,
              sourceSide,
              flowVisualEdges(network.capacity, frame.residual()),
              Map.of(
                  "BFS phases",
                  result.bfsPhases(),
                  "DFS pushes",
                  result.dfsPushes(),
                  "Augmentations",
                  result.augmentations(),
                  "Current flow",
                  frame.totalFlow(),
                  "Maximum flow",
                  result.maximumFlow()),
              "Levels: "
                  + Arrays.toString(frame.level())
                  + "\nAdmissible edges: "
                  + admissibleEdges(frame.level(), frame.residual())
                  + "\nDFS path: "
                  + frame.augmentingPath()
                  + "; pushed "
                  + frame.pushed()
                  + "\nCapacity: "
                  + Arrays.deepToString(network.capacity)
                  + "\nCurrent net flow: "
                  + Arrays.deepToString(flowMatrix(network.capacity, frame.residual()))
                  + "\nResidual: "
                  + Arrays.deepToString(frame.residual())));
    }
    return new AlgorithmRun(
        steps,
        "Dinic maximum flow: "
            + result.maximumFlow()
            + "; BFS phases "
            + result.bfsPhases()
            + "; DFS pushes "
            + result.dfsPushes()
            + "; augmentations "
            + result.augmentations());
  }

  static AlgorithmRun flowComparison(String input) {
    FlowNetwork network = flowNetwork(input);
    GraphAlgorithms.FlowResult edmonds =
        GraphAlgorithms.edmondsKarp(network.capacity, network.source, network.sink);
    GraphAlgorithms.DinicResult dinic =
        GraphAlgorithms.dinic(network.capacity, network.source, network.sink);
    boolean agree = edmonds.maximumFlow() == dinic.maximumFlow();
    List<String> table =
        List.of(
            "Algorithm",
            "Maximum Flow",
            "BFS Phases",
            "Flow Operations",
            "Visual Steps",
            "Edmonds–Karp",
            Integer.toString(edmonds.maximumFlow()),
            Integer.toString(edmonds.bfsPhases()),
            Integer.toString(edmonds.augmentations()),
            Integer.toString(edmonds.frames().size()),
            "Dinic",
            Integer.toString(dinic.maximumFlow()),
            Integer.toString(dinic.bfsPhases()),
            Integer.toString(dinic.augmentations()),
            Integer.toString(dinic.frames().size()));
    AlgorithmStep step =
        new AlgorithmStep(
            agree ? "Both algorithms agree" : "Maximum-flow mismatch detected",
            "parse network\nrun Edmonds–Karp\nrun Dinic\ncompare metrics\nverify equal flow",
            4,
            TABLE,
            List.of(),
            table,
            rangeSet(5, 14),
            Set.of(),
            Set.of(),
            List.of(),
            Map.of("Maximum flow", dinic.maximumFlow(), "Algorithms agree", agree ? 1 : 0),
            "columns=5\nEdmonds–Karp and Dinic ran on the same capacity matrix. Flow operations"
                + " are augmentations; visual steps are actual recorded frames.");
    return new AlgorithmRun(
        List.of(step),
        "Edmonds–Karp="
            + edmonds.maximumFlow()
            + ", Dinic="
            + dinic.maximumFlow()
            + ", agree="
            + agree);
  }

  private static FlowNetwork flowNetwork(String input) {
    String[] parts = input.split(";", 2);
    if (parts.length != 2) throw new IllegalArgumentException("Use capacity rows ; source,sink");
    String[] rows = parts[0].trim().split("/");
    if (rows.length < 2 || rows.length > 30)
      throw new IllegalArgumentException("Flow networks require 2 to 30 vertices");
    int[][] capacity = new int[rows.length][];
    for (int row = 0; row < rows.length; row++) {
      capacity[row] = numbers(rows[row]);
      if (capacity[row].length != rows.length)
        throw new IllegalArgumentException("Capacity matrix must be square");
    }
    int[] endpoints = numbers(parts[1]);
    if (endpoints.length != 2)
      throw new IllegalArgumentException("Choose exactly one source and sink");
    return new FlowNetwork(capacity, endpoints[0], endpoints[1]);
  }

  private static List<AlgorithmStep.VisualEdge> flowVisualEdges(
      int[][] capacity, int[][] residual) {
    List<AlgorithmStep.VisualEdge> edges = new ArrayList<>();
    for (int from = 0; from < capacity.length; from++)
      for (int to = 0; to < capacity.length; to++)
        if (capacity[from][to] > 0)
          edges.add(
              new AlgorithmStep.VisualEdge(
                  from,
                  to,
                  capacity[from][to],
                  true,
                  (capacity[from][to] - residual[from][to])
                      + "/"
                      + capacity[from][to]
                      + " r="
                      + residual[from][to]));
    return edges;
  }

  private static List<String> admissibleEdges(int[] level, int[][] residual) {
    List<String> edges = new ArrayList<>();
    for (int from = 0; from < residual.length; from++)
      for (int to = 0; to < residual.length; to++)
        if (level[from] >= 0 && residual[from][to] > 0 && level[to] == level[from] + 1)
          edges.add(from + "→" + to);
    return edges;
  }

  private static int[][] flowMatrix(int[][] capacity, int[][] residual) {
    int[][] flow = new int[capacity.length][capacity.length];
    for (int from = 0; from < capacity.length; from++)
      for (int to = 0; to < capacity.length; to++)
        flow[from][to] = capacity[from][to] - residual[from][to];
    return flow;
  }

  private record FlowNetwork(int[][] capacity, int source, int sink) {}

  static AlgorithmRun matching(String input) {
    boolean[][] e = bipartiteMatrix(input);
    int count = GraphAlgorithms.maximumBipartiteMatching(e);
    List<String> labels = bipartiteLabels(e);
    List<AlgorithmStep.VisualEdge> edges = bipartiteEdges(e);
    return new AlgorithmRun(
        List.of(
            new AlgorithmStep(
                "Search augmenting paths",
                "for each left vertex\nfind augmenting path\nflip matching",
                1,
                GRAPH,
                List.of(),
                labels,
                rangeSet(0, e.length - 1),
                rangeSet(e.length, labels.size() - 1),
                Set.of(),
                edges,
                Map.of("Matching size", count),
                "Left and right partitions")),
        "Maximum matching size: " + count);
  }

  private static boolean[][] bipartiteMatrix(String input) {
    String[] rows = input.trim().split("/");
    if (rows.length == 0 || rows[0].isEmpty())
      throw new IllegalArgumentException("bipartite matrix cannot be empty");
    int columns = rows[0].length();
    boolean[][] edges = new boolean[rows.length][columns];
    for (int row = 0; row < rows.length; row++) {
      if (rows[row].length() != columns)
        throw new IllegalArgumentException("bipartite matrix rows differ");
      for (int column = 0; column < columns; column++) {
        char value = rows[row].charAt(column);
        if (value != '0' && value != '1')
          throw new IllegalArgumentException("bipartite matrix uses only 0 and 1");
        edges[row][column] = value == '1';
      }
    }
    return edges;
  }

  private static List<String> bipartiteLabels(boolean[][] edges) {
    List<String> labels = new ArrayList<>();
    for (int left = 0; left < edges.length; left++) labels.add("L" + left);
    for (int right = 0; right < edges[0].length; right++) labels.add("R" + right);
    return labels;
  }

  private static List<AlgorithmStep.VisualEdge> bipartiteEdges(boolean[][] edges) {
    List<AlgorithmStep.VisualEdge> visualEdges = new ArrayList<>();
    for (int left = 0; left < edges.length; left++)
      for (int right = 0; right < edges[left].length; right++)
        if (edges[left][right])
          visualEdges.add(new AlgorithmStep.VisualEdge(left, edges.length + right, 1, false, ""));
    return visualEdges;
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
