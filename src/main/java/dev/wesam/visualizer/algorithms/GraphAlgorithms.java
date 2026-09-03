package dev.wesam.visualizer.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public final class GraphAlgorithms {
  public static final long INF = Long.MAX_VALUE / 4;

  private GraphAlgorithms() {}

  public record Edge(int from, int to, int weight) {
    public Edge(int from, int to) {
      this(from, to, 1);
    }
  }

  public record Graph(int vertices, List<Edge> edges, boolean directed) {
    public Graph {
      if (vertices < 0) throw new IllegalArgumentException("vertices cannot be negative");
      edges = List.copyOf(edges);
      for (Edge e : edges)
        if (e.from < 0 || e.to < 0 || e.from >= vertices || e.to >= vertices)
          throw new IllegalArgumentException("edge endpoint is outside graph");
    }

    public List<List<Edge>> adjacency() {
      List<List<Edge>> adjacency = new ArrayList<>();
      for (int i = 0; i < vertices; i++) adjacency.add(new ArrayList<>());
      for (Edge e : edges) {
        adjacency.get(e.from).add(e);
        if (!directed) adjacency.get(e.to).add(new Edge(e.to, e.from, e.weight));
      }
      return adjacency;
    }
  }

  public record ShortestPaths(long[] distance, int[] parent, boolean negativeCycle) {
    public ShortestPaths {
      distance = distance.clone();
      parent = parent.clone();
    }

    public List<Integer> pathTo(int target) {
      if (target < 0 || target >= distance.length || distance[target] >= INF) return List.of();
      List<Integer> path = new ArrayList<>();
      for (int at = target; at != -1; at = parent[at]) path.add(at);
      Collections.reverse(path);
      return path;
    }
  }

  public record MstResult(List<Edge> edges, long totalWeight, boolean spanning) {
    public MstResult {
      edges = List.copyOf(edges);
    }
  }

  public record AllPairsShortestPaths(long[][] distance, int[][] next, boolean negativeCycle) {
    public AllPairsShortestPaths {
      distance = copy(distance);
      next = copy(next);
    }

    public List<Integer> path(int source, int target) {
      if (negativeCycle
          || source < 0
          || target < 0
          || source >= next.length
          || target >= next.length
          || next[source][target] < 0) return List.of();
      List<Integer> path = new ArrayList<>();
      path.add(source);
      int current = source;
      while (current != target && path.size() <= next.length) {
        current = next[current][target];
        if (current < 0) return List.of();
        path.add(current);
      }
      return current == target ? List.copyOf(path) : List.of();
    }
  }

  public record Point(double x, double y) {}

  public record AStarFrame(
      int current,
      long[] gScore,
      double[] hScore,
      double[] fScore,
      Set<Integer> open,
      Set<Integer> closed) {
    public AStarFrame {
      gScore = gScore.clone();
      hScore = hScore.clone();
      fScore = fScore.clone();
      open = Set.copyOf(open);
      closed = Set.copyOf(closed);
    }
  }

  public record AStarResult(
      List<Integer> path,
      long cost,
      long[] gScore,
      double[] hScore,
      double[] fScore,
      int expanded,
      List<AStarFrame> frames) {
    public AStarResult {
      path = List.copyOf(path);
      gScore = gScore.clone();
      hScore = hScore.clone();
      fScore = fScore.clone();
      frames = List.copyOf(frames);
    }

    public boolean found() {
      return cost < INF;
    }
  }

  public record TarjanFrame(
      int current,
      int[] index,
      int[] lowLink,
      List<Integer> stack,
      Set<Integer> completedComponent,
      String event) {
    public TarjanFrame {
      index = index.clone();
      lowLink = lowLink.clone();
      stack = List.copyOf(stack);
      completedComponent = Set.copyOf(completedComponent);
    }
  }

  public record TarjanResult(
      List<Set<Integer>> components, int[] index, int[] lowLink, List<TarjanFrame> frames) {
    public TarjanResult {
      components = components.stream().map(Set::copyOf).toList();
      index = index.clone();
      lowLink = lowLink.clone();
      frames = List.copyOf(frames);
    }
  }

  public record LowLinkFrame(
      int current,
      int neighbor,
      int[] discovery,
      int[] lowLink,
      Set<Integer> articulationPoints,
      List<Edge> bridges,
      String event) {
    public LowLinkFrame {
      discovery = discovery.clone();
      lowLink = lowLink.clone();
      articulationPoints = Set.copyOf(articulationPoints);
      bridges = List.copyOf(bridges);
    }
  }

  public record LowLinkResult(
      List<Edge> bridges,
      Set<Integer> articulationPoints,
      int[] discovery,
      int[] lowLink,
      int[] parent,
      List<LowLinkFrame> frames) {
    public LowLinkResult {
      bridges = List.copyOf(bridges);
      articulationPoints = Set.copyOf(articulationPoints);
      discovery = discovery.clone();
      lowLink = lowLink.clone();
      parent = parent.clone();
      frames = List.copyOf(frames);
    }
  }

  public record EulerFrame(
      int current, List<Integer> stack, List<Integer> reverseTrail, int usedEdges, String event) {
    public EulerFrame {
      stack = List.copyOf(stack);
      reverseTrail = List.copyOf(reverseTrail);
    }
  }

  public record EulerResult(
      boolean exists,
      boolean circuit,
      List<Integer> trail,
      String reason,
      List<EulerFrame> frames) {
    public EulerResult {
      trail = List.copyOf(trail);
      frames = List.copyOf(frames);
    }
  }

  public record BipartiteFrame(
      int current, int neighbor, int[] color, Set<Integer> frontier, String event) {
    public BipartiteFrame {
      color = color.clone();
      frontier = Set.copyOf(frontier);
    }
  }

  public record BipartiteResult(
      boolean bipartite, int[] color, Edge conflict, List<BipartiteFrame> frames) {
    public BipartiteResult {
      color = color.clone();
      frames = List.copyOf(frames);
    }
  }

  public record MatchingFrame(
      int phase, int[] layers, int[] leftMatch, int[] rightMatch, int augmentedLeft, String event) {
    public MatchingFrame {
      layers = layers.clone();
      leftMatch = leftMatch.clone();
      rightMatch = rightMatch.clone();
    }
  }

  public record MatchingResult(
      int size,
      int[] leftMatch,
      int[] rightMatch,
      int bfsPhases,
      int augmentations,
      List<MatchingFrame> frames) {
    public MatchingResult {
      leftMatch = leftMatch.clone();
      rightMatch = rightMatch.clone();
      frames = List.copyOf(frames);
    }
  }

  public record DinicFrame(
      int phase,
      int[] level,
      List<Integer> augmentingPath,
      int pushed,
      int totalFlow,
      int[][] residual,
      String event) {
    public DinicFrame {
      level = level.clone();
      augmentingPath = List.copyOf(augmentingPath);
      residual = copy(residual);
    }
  }

  public record DinicResult(
      int maximumFlow,
      int[][] residual,
      boolean[] sourceSideOfMinCut,
      int bfsPhases,
      int dfsPushes,
      int augmentations,
      int steps,
      List<DinicFrame> frames) {
    public DinicResult {
      residual = copy(residual);
      sourceSideOfMinCut = sourceSideOfMinCut.clone();
      frames = List.copyOf(frames);
    }
  }

  public static List<Integer> bfs(Graph graph, int start) {
    checkVertex(graph, start);
    List<List<Edge>> adj = graph.adjacency();
    boolean[] seen = new boolean[graph.vertices];
    Queue<Integer> queue = new ArrayDeque<>();
    List<Integer> order = new ArrayList<>();
    seen[start] = true;
    queue.add(start);
    while (!queue.isEmpty()) {
      int current = queue.remove();
      order.add(current);
      for (Edge edge : adj.get(current))
        if (!seen[edge.to]) {
          seen[edge.to] = true;
          queue.add(edge.to);
        }
    }
    return order;
  }

  public static List<Integer> dfs(Graph graph, int start) {
    checkVertex(graph, start);
    boolean[] seen = new boolean[graph.vertices];
    List<Integer> order = new ArrayList<>();
    dfsVisit(start, graph.adjacency(), seen, order);
    return order;
  }

  private static void dfsVisit(
      int current, List<List<Edge>> adj, boolean[] seen, List<Integer> order) {
    seen[current] = true;
    order.add(current);
    for (Edge edge : adj.get(current)) if (!seen[edge.to]) dfsVisit(edge.to, adj, seen, order);
  }

  public static List<Set<Integer>> connectedComponents(Graph graph) {
    if (graph.directed)
      throw new IllegalArgumentException("connected components require an undirected graph");
    boolean[] seen = new boolean[graph.vertices];
    List<List<Edge>> adj = graph.adjacency();
    List<Set<Integer>> result = new ArrayList<>();
    for (int start = 0; start < graph.vertices; start++)
      if (!seen[start]) {
        Set<Integer> component = new LinkedHashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        seen[start] = true;
        while (!queue.isEmpty()) {
          int current = queue.remove();
          component.add(current);
          for (Edge e : adj.get(current))
            if (!seen[e.to]) {
              seen[e.to] = true;
              queue.add(e.to);
            }
        }
        result.add(component);
      }
    return result;
  }

  public static List<Integer> topologicalSort(Graph graph) {
    if (!graph.directed)
      throw new IllegalArgumentException("topological sort requires a directed graph");
    int[] indegree = new int[graph.vertices];
    for (Edge e : graph.edges) indegree[e.to]++;
    Queue<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < graph.vertices; i++) if (indegree[i] == 0) queue.add(i);
    List<Integer> result = new ArrayList<>();
    List<List<Edge>> adj = graph.adjacency();
    while (!queue.isEmpty()) {
      int current = queue.remove();
      result.add(current);
      for (Edge e : adj.get(current)) if (--indegree[e.to] == 0) queue.add(e.to);
    }
    if (result.size() != graph.vertices)
      throw new IllegalArgumentException("graph contains a directed cycle");
    return result;
  }

  /** Kosaraju's algorithm. */
  public static List<Set<Integer>> stronglyConnectedComponents(Graph graph) {
    if (!graph.directed) return connectedComponents(graph);
    List<List<Edge>> adj = graph.adjacency();
    boolean[] seen = new boolean[graph.vertices];
    Deque<Integer> finish = new ArrayDeque<>();
    for (int v = 0; v < graph.vertices; v++) if (!seen[v]) finishDfs(v, adj, seen, finish);
    List<Edge> reversedEdges =
        graph.edges.stream().map(e -> new Edge(e.to, e.from, e.weight)).toList();
    List<List<Edge>> reverse = new Graph(graph.vertices, reversedEdges, true).adjacency();
    Arrays.fill(seen, false);
    List<Set<Integer>> components = new ArrayList<>();
    while (!finish.isEmpty()) {
      int v = finish.pop();
      if (!seen[v]) {
        Set<Integer> part = new LinkedHashSet<>();
        collect(v, reverse, seen, part);
        components.add(part);
      }
    }
    return components;
  }

  private static void finishDfs(
      int v, List<List<Edge>> adj, boolean[] seen, Deque<Integer> finish) {
    seen[v] = true;
    for (Edge e : adj.get(v)) if (!seen[e.to]) finishDfs(e.to, adj, seen, finish);
    finish.push(v);
  }

  private static void collect(int v, List<List<Edge>> adj, boolean[] seen, Set<Integer> result) {
    seen[v] = true;
    result.add(v);
    for (Edge e : adj.get(v)) if (!seen[e.to]) collect(e.to, adj, seen, result);
  }

  /** Tarjan's one-pass strongly connected components algorithm. */
  public static TarjanResult tarjanStronglyConnectedComponents(Graph graph) {
    int[] index = new int[graph.vertices];
    int[] lowLink = new int[graph.vertices];
    Arrays.fill(index, -1);
    Arrays.fill(lowLink, -1);
    boolean[] onStack = new boolean[graph.vertices];
    Deque<Integer> stack = new ArrayDeque<>();
    List<Set<Integer>> components = new ArrayList<>();
    List<TarjanFrame> frames = new ArrayList<>();
    int[] nextIndex = {0};
    List<List<Edge>> adjacency = graph.adjacency();
    for (int vertex = 0; vertex < graph.vertices; vertex++)
      if (index[vertex] < 0)
        tarjanVisit(
            vertex, adjacency, index, lowLink, onStack, stack, nextIndex, components, frames);
    return new TarjanResult(components, index, lowLink, frames);
  }

  private static void tarjanVisit(
      int vertex,
      List<List<Edge>> adjacency,
      int[] index,
      int[] lowLink,
      boolean[] onStack,
      Deque<Integer> stack,
      int[] nextIndex,
      List<Set<Integer>> components,
      List<TarjanFrame> frames) {
    index[vertex] = lowLink[vertex] = nextIndex[0]++;
    stack.push(vertex);
    onStack[vertex] = true;
    frames.add(
        new TarjanFrame(
            vertex, index, lowLink, new ArrayList<>(stack), Set.of(), "Discover and push vertex"));
    for (Edge edge : adjacency.get(vertex)) {
      if (index[edge.to] < 0) {
        tarjanVisit(
            edge.to, adjacency, index, lowLink, onStack, stack, nextIndex, components, frames);
        lowLink[vertex] = Math.min(lowLink[vertex], lowLink[edge.to]);
      } else if (onStack[edge.to]) {
        lowLink[vertex] = Math.min(lowLink[vertex], index[edge.to]);
      }
      frames.add(
          new TarjanFrame(
              vertex,
              index,
              lowLink,
              new ArrayList<>(stack),
              Set.of(),
              "Update low-link through edge " + vertex + " → " + edge.to));
    }
    if (lowLink[vertex] == index[vertex]) {
      Set<Integer> component = new LinkedHashSet<>();
      int member;
      do {
        member = stack.pop();
        onStack[member] = false;
        component.add(member);
      } while (member != vertex);
      components.add(component);
      frames.add(
          new TarjanFrame(
              vertex,
              index,
              lowLink,
              new ArrayList<>(stack),
              component,
              "Pop one strongly connected component"));
    }
  }

  /** Computes bridges and articulation points together with one undirected low-link DFS. */
  public static LowLinkResult undirectedLowLinks(Graph graph) {
    if (graph.directed)
      throw new IllegalArgumentException(
          "bridges and articulation points require an undirected graph");
    List<List<UndirectedArc>> adjacency = new ArrayList<>();
    for (int vertex = 0; vertex < graph.vertices; vertex++) adjacency.add(new ArrayList<>());
    for (int id = 0; id < graph.edges.size(); id++) {
      Edge edge = graph.edges.get(id);
      adjacency.get(edge.from).add(new UndirectedArc(edge.to, id));
      adjacency.get(edge.to).add(new UndirectedArc(edge.from, id));
    }
    int[] discovery = new int[graph.vertices];
    int[] lowLink = new int[graph.vertices];
    int[] parent = new int[graph.vertices];
    Arrays.fill(discovery, -1);
    Arrays.fill(lowLink, -1);
    Arrays.fill(parent, -1);
    int[] time = {0};
    List<Edge> bridges = new ArrayList<>();
    Set<Integer> articulation = new LinkedHashSet<>();
    List<LowLinkFrame> frames = new ArrayList<>();
    for (int vertex = 0; vertex < graph.vertices; vertex++)
      if (discovery[vertex] < 0)
        lowLinkVisit(
            vertex,
            -1,
            adjacency,
            graph,
            discovery,
            lowLink,
            parent,
            time,
            bridges,
            articulation,
            frames);
    return new LowLinkResult(bridges, articulation, discovery, lowLink, parent, frames);
  }

  private static void lowLinkVisit(
      int vertex,
      int parentEdge,
      List<List<UndirectedArc>> adjacency,
      Graph graph,
      int[] discovery,
      int[] lowLink,
      int[] parent,
      int[] time,
      List<Edge> bridges,
      Set<Integer> articulation,
      List<LowLinkFrame> frames) {
    discovery[vertex] = lowLink[vertex] = time[0]++;
    int children = 0;
    frames.add(
        new LowLinkFrame(
            vertex, -1, discovery, lowLink, articulation, bridges, "Discover vertex " + vertex));
    for (UndirectedArc arc : adjacency.get(vertex)) {
      if (arc.edgeId == parentEdge) continue;
      if (discovery[arc.to] < 0) {
        children++;
        parent[arc.to] = vertex;
        lowLinkVisit(
            arc.to,
            arc.edgeId,
            adjacency,
            graph,
            discovery,
            lowLink,
            parent,
            time,
            bridges,
            articulation,
            frames);
        lowLink[vertex] = Math.min(lowLink[vertex], lowLink[arc.to]);
        if (lowLink[arc.to] > discovery[vertex]) bridges.add(graph.edges.get(arc.edgeId));
        if (parentEdge >= 0 && lowLink[arc.to] >= discovery[vertex]) articulation.add(vertex);
      } else {
        lowLink[vertex] = Math.min(lowLink[vertex], discovery[arc.to]);
      }
      if (parentEdge < 0 && children > 1) articulation.add(vertex);
      frames.add(
          new LowLinkFrame(
              vertex,
              arc.to,
              discovery,
              lowLink,
              articulation,
              bridges,
              "Inspect edge " + vertex + " — " + arc.to));
    }
  }

  /** Hierholzer's algorithm for directed or undirected multigraphs. */
  public static EulerResult eulerTrail(Graph graph) {
    if (graph.vertices == 0)
      return new EulerResult(true, true, List.of(), "Empty graph has a trivial circuit", List.of());
    int start = -1;
    boolean circuit;
    if (graph.directed) {
      int[] in = new int[graph.vertices], out = new int[graph.vertices];
      for (Edge edge : graph.edges) {
        out[edge.from]++;
        in[edge.to]++;
      }
      int starts = 0, ends = 0;
      for (int vertex = 0; vertex < graph.vertices; vertex++) {
        int difference = out[vertex] - in[vertex];
        if (difference == 1) {
          starts++;
          start = vertex;
        } else if (difference == -1) ends++;
        else if (difference != 0) return noEuler("Directed in/out-degree differences exceed one");
        if (start < 0 && out[vertex] > 0) start = vertex;
      }
      if (!((starts == 0 && ends == 0) || (starts == 1 && ends == 1)))
        return noEuler("Directed graph needs zero or one start/end imbalance pair");
      circuit = starts == 0;
    } else {
      int[] degree = new int[graph.vertices];
      for (Edge edge : graph.edges) {
        degree[edge.from]++;
        degree[edge.to]++;
      }
      List<Integer> odd = new ArrayList<>();
      for (int vertex = 0; vertex < graph.vertices; vertex++) {
        if ((degree[vertex] & 1) == 1) odd.add(vertex);
        if (start < 0 && degree[vertex] > 0) start = vertex;
      }
      if (odd.size() != 0 && odd.size() != 2)
        return noEuler("Undirected graph needs exactly zero or two odd-degree vertices");
      if (odd.size() == 2) start = odd.get(0);
      circuit = odd.isEmpty();
    }
    if (start < 0)
      return new EulerResult(
          true, true, List.of(0), "Graph with no edges has a trivial circuit", List.of());
    if (!nonzeroVerticesConnected(graph, start))
      return noEuler("Vertices incident to edges are disconnected");

    List<List<TraversalArc>> adjacency = new ArrayList<>();
    for (int vertex = 0; vertex < graph.vertices; vertex++) adjacency.add(new ArrayList<>());
    for (int id = 0; id < graph.edges.size(); id++) {
      Edge edge = graph.edges.get(id);
      adjacency.get(edge.from).add(new TraversalArc(edge.to, id));
      if (!graph.directed) adjacency.get(edge.to).add(new TraversalArc(edge.from, id));
    }
    boolean[] used = new boolean[graph.edges.size()];
    int[] cursor = new int[graph.vertices];
    int usedCount = 0;
    Deque<Integer> stack = new ArrayDeque<>();
    List<Integer> reverseTrail = new ArrayList<>();
    List<EulerFrame> frames = new ArrayList<>();
    stack.push(start);
    while (!stack.isEmpty()) {
      int vertex = stack.peek();
      List<TraversalArc> outgoing = adjacency.get(vertex);
      while (cursor[vertex] < outgoing.size() && used[outgoing.get(cursor[vertex]).edgeId])
        cursor[vertex]++;
      if (cursor[vertex] == outgoing.size()) {
        reverseTrail.add(stack.pop());
        frames.add(
            new EulerFrame(
                vertex,
                new ArrayList<>(stack),
                reverseTrail,
                usedCount,
                "Backtrack and prepend vertex to trail"));
      } else {
        TraversalArc arc = outgoing.get(cursor[vertex]++);
        if (used[arc.edgeId]) continue;
        used[arc.edgeId] = true;
        usedCount++;
        stack.push(arc.to);
        frames.add(
            new EulerFrame(
                arc.to,
                new ArrayList<>(stack),
                reverseTrail,
                usedCount,
                "Use edge and extend current trail"));
      }
    }
    if (usedCount != graph.edges.size()) return noEuler("Not every edge belongs to one trail");
    Collections.reverse(reverseTrail);
    return new EulerResult(
        true, circuit, reverseTrail, circuit ? "Euler circuit found" : "Euler path found", frames);
  }

  private static EulerResult noEuler(String reason) {
    return new EulerResult(false, false, List.of(), reason, List.of());
  }

  private static boolean nonzeroVerticesConnected(Graph graph, int start) {
    List<List<Integer>> adjacency = new ArrayList<>();
    int[] degree = new int[graph.vertices];
    for (int vertex = 0; vertex < graph.vertices; vertex++) adjacency.add(new ArrayList<>());
    for (Edge edge : graph.edges) {
      adjacency.get(edge.from).add(edge.to);
      adjacency.get(edge.to).add(edge.from);
      degree[edge.from]++;
      degree[edge.to]++;
    }
    boolean[] seen = new boolean[graph.vertices];
    Queue<Integer> queue = new ArrayDeque<>();
    seen[start] = true;
    queue.add(start);
    while (!queue.isEmpty()) {
      int vertex = queue.remove();
      for (int next : adjacency.get(vertex))
        if (!seen[next]) {
          seen[next] = true;
          queue.add(next);
        }
    }
    for (int vertex = 0; vertex < graph.vertices; vertex++)
      if (degree[vertex] > 0 && !seen[vertex]) return false;
    return true;
  }

  /** BFS two-coloring for an undirected graph. */
  public static BipartiteResult bipartiteCheck(Graph graph) {
    if (graph.directed)
      throw new IllegalArgumentException("bipartite check requires an undirected graph");
    int[] color = new int[graph.vertices];
    Arrays.fill(color, -1);
    List<BipartiteFrame> frames = new ArrayList<>();
    List<List<Edge>> adjacency = graph.adjacency();
    for (int start = 0; start < graph.vertices; start++) {
      if (color[start] >= 0) continue;
      Queue<Integer> queue = new ArrayDeque<>();
      color[start] = 0;
      queue.add(start);
      while (!queue.isEmpty()) {
        int current = queue.remove();
        for (Edge edge : adjacency.get(current)) {
          if (color[edge.to] < 0) {
            color[edge.to] = 1 - color[current];
            queue.add(edge.to);
            frames.add(
                new BipartiteFrame(
                    current,
                    edge.to,
                    color,
                    new LinkedHashSet<>(queue),
                    "Assign opposite color across edge"));
          } else if (color[edge.to] == color[current]) {
            frames.add(
                new BipartiteFrame(
                    current,
                    edge.to,
                    color,
                    new LinkedHashSet<>(queue),
                    "Conflict: adjacent vertices have the same color"));
            return new BipartiteResult(false, color, edge, frames);
          }
        }
      }
    }
    return new BipartiteResult(true, color, null, frames);
  }

  /** Hopcroft-Karp maximum cardinality matching for a rectangular bipartite adjacency matrix. */
  public static MatchingResult hopcroftKarp(boolean[][] edges) {
    int rightSize = validateBipartiteMatrix(edges);
    int[] leftMatch = new int[edges.length];
    int[] rightMatch = new int[rightSize];
    int[] distance = new int[edges.length];
    Arrays.fill(leftMatch, -1);
    Arrays.fill(rightMatch, -1);
    List<MatchingFrame> frames = new ArrayList<>();
    int size = 0, phases = 0, augmentations = 0;
    while (hopcroftBfs(edges, leftMatch, rightMatch, distance)) {
      phases++;
      frames.add(
          new MatchingFrame(
              phases, distance, leftMatch, rightMatch, -1, "BFS builds layers from free vertices"));
      for (int left = 0; left < edges.length; left++)
        if (leftMatch[left] < 0 && hopcroftDfs(left, edges, leftMatch, rightMatch, distance)) {
          size++;
          augmentations++;
          frames.add(
              new MatchingFrame(
                  phases,
                  distance,
                  leftMatch,
                  rightMatch,
                  left,
                  "DFS augments along a shortest layered path"));
        }
    }
    return new MatchingResult(size, leftMatch, rightMatch, phases, augmentations, frames);
  }

  private static boolean hopcroftBfs(
      boolean[][] edges, int[] leftMatch, int[] rightMatch, int[] distance) {
    int unreachable = Integer.MAX_VALUE;
    Queue<Integer> queue = new ArrayDeque<>();
    for (int left = 0; left < edges.length; left++) {
      distance[left] = leftMatch[left] < 0 ? 0 : unreachable;
      if (leftMatch[left] < 0) queue.add(left);
    }
    boolean augmentingPathExists = false;
    while (!queue.isEmpty()) {
      int left = queue.remove();
      for (int right = 0; right < edges[left].length; right++)
        if (edges[left][right]) {
          int pairedLeft = rightMatch[right];
          if (pairedLeft < 0) augmentingPathExists = true;
          else if (distance[pairedLeft] == unreachable) {
            distance[pairedLeft] = distance[left] + 1;
            queue.add(pairedLeft);
          }
        }
    }
    return augmentingPathExists;
  }

  private static boolean hopcroftDfs(
      int left, boolean[][] edges, int[] leftMatch, int[] rightMatch, int[] distance) {
    for (int right = 0; right < edges[left].length; right++)
      if (edges[left][right]) {
        int pairedLeft = rightMatch[right];
        if (pairedLeft < 0
            || distance[pairedLeft] == distance[left] + 1
                && hopcroftDfs(pairedLeft, edges, leftMatch, rightMatch, distance)) {
          leftMatch[left] = right;
          rightMatch[right] = left;
          return true;
        }
      }
    distance[left] = Integer.MAX_VALUE;
    return false;
  }

  private static int validateBipartiteMatrix(boolean[][] edges) {
    int rightSize = edges.length == 0 ? 0 : edges[0].length;
    for (boolean[] row : edges)
      if (row.length != rightSize) throw new IllegalArgumentException("ragged matrix");
    return rightSize;
  }

  public static ShortestPaths dijkstra(Graph graph, int source) {
    checkVertex(graph, source);
    for (Edge e : graph.edges)
      if (e.weight < 0)
        throw new IllegalArgumentException("Dijkstra requires non-negative weights");
    long[] distance = new long[graph.vertices];
    Arrays.fill(distance, INF);
    distance[source] = 0;
    int[] parent = new int[graph.vertices];
    Arrays.fill(parent, -1);
    boolean[] settled = new boolean[graph.vertices];
    PriorityQueue<NodeDistance> queue =
        new PriorityQueue<>(Comparator.comparingLong(NodeDistance::distance));
    queue.add(new NodeDistance(source, 0));
    List<List<Edge>> adj = graph.adjacency();
    while (!queue.isEmpty()) {
      NodeDistance item = queue.remove();
      int current = item.node;
      if (settled[current]) continue;
      settled[current] = true;
      for (Edge e : adj.get(current))
        if (distance[e.to] > distance[current] + e.weight) {
          distance[e.to] = distance[current] + e.weight;
          parent[e.to] = current;
          queue.add(new NodeDistance(e.to, distance[e.to]));
        }
    }
    return new ShortestPaths(distance, parent, false);
  }

  public static ShortestPaths bellmanFord(Graph graph, int source) {
    checkVertex(graph, source);
    List<Edge> edges = expandedEdges(graph);
    long[] distance = new long[graph.vertices];
    Arrays.fill(distance, INF);
    distance[source] = 0;
    int[] parent = new int[graph.vertices];
    Arrays.fill(parent, -1);
    for (int pass = 1; pass < graph.vertices; pass++) {
      boolean changed = false;
      for (Edge e : edges)
        if (distance[e.from] < INF && distance[e.to] > distance[e.from] + e.weight) {
          distance[e.to] = distance[e.from] + e.weight;
          parent[e.to] = e.from;
          changed = true;
        }
      if (!changed) break;
    }
    boolean negativeCycle = false;
    for (Edge e : edges)
      if (distance[e.from] < INF && distance[e.to] > distance[e.from] + e.weight)
        negativeCycle = true;
    return new ShortestPaths(distance, parent, negativeCycle);
  }

  public static AllPairsShortestPaths floydWarshall(Graph graph) {
    int n = graph.vertices;
    long[][] distance = new long[n][n];
    int[][] next = new int[n][n];
    for (int i = 0; i < n; i++) {
      Arrays.fill(distance[i], INF);
      Arrays.fill(next[i], -1);
      distance[i][i] = 0;
      next[i][i] = i;
    }
    for (Edge edge : expandedEdges(graph))
      if (edge.weight < distance[edge.from][edge.to]) {
        distance[edge.from][edge.to] = edge.weight;
        next[edge.from][edge.to] = edge.to;
      }
    for (int intermediate = 0; intermediate < n; intermediate++)
      for (int from = 0; from < n; from++)
        for (int to = 0; to < n; to++)
          if (distance[from][intermediate] < INF
              && distance[intermediate][to] < INF
              && distance[from][to] > distance[from][intermediate] + distance[intermediate][to]) {
            distance[from][to] = distance[from][intermediate] + distance[intermediate][to];
            next[from][to] = next[from][intermediate];
          }
    boolean negativeCycle = false;
    for (int vertex = 0; vertex < n; vertex++)
      if (distance[vertex][vertex] < 0) negativeCycle = true;
    return new AllPairsShortestPaths(distance, next, negativeCycle);
  }

  /** Johnson's sparse all-pairs algorithm using Bellman-Ford potentials and repeated Dijkstra. */
  public static AllPairsShortestPaths johnson(Graph graph) {
    int n = graph.vertices;
    List<Edge> original = expandedEdges(graph);
    List<Edge> augmented = new ArrayList<>(original);
    for (int vertex = 0; vertex < n; vertex++) augmented.add(new Edge(n, vertex, 0));
    ShortestPaths potentials = bellmanFord(new Graph(n + 1, augmented, true), n);
    if (potentials.negativeCycle()) {
      long[][] distance = new long[n][n];
      int[][] next = new int[n][n];
      for (int i = 0; i < n; i++) {
        Arrays.fill(distance[i], INF);
        Arrays.fill(next[i], -1);
      }
      return new AllPairsShortestPaths(distance, next, true);
    }

    List<List<Edge>> adjacency = new ArrayList<>();
    for (int vertex = 0; vertex < n; vertex++) adjacency.add(new ArrayList<>());
    for (Edge edge : original) adjacency.get(edge.from).add(edge);
    long[][] distance = new long[n][n];
    int[][] next = new int[n][n];
    for (int source = 0; source < n; source++) {
      Arrays.fill(distance[source], INF);
      Arrays.fill(next[source], -1);
      DijkstraRow row = dijkstraReweighted(adjacency, potentials.distance(), source);
      for (int target = 0; target < n; target++) {
        if (row.distance[target] >= INF) continue;
        distance[source][target] =
            row.distance[target] - potentials.distance()[source] + potentials.distance()[target];
        if (source == target) next[source][target] = source;
        else {
          int first = target;
          while (row.parent[first] != -1 && row.parent[first] != source) first = row.parent[first];
          if (row.parent[first] == source) next[source][target] = first;
        }
      }
    }
    return new AllPairsShortestPaths(distance, next, false);
  }

  private static DijkstraRow dijkstraReweighted(
      List<List<Edge>> adjacency, long[] potential, int source) {
    long[] distance = new long[adjacency.size()];
    Arrays.fill(distance, INF);
    distance[source] = 0;
    int[] parent = new int[adjacency.size()];
    Arrays.fill(parent, -1);
    PriorityQueue<NodeDistance> queue =
        new PriorityQueue<>(Comparator.comparingLong(NodeDistance::distance));
    queue.add(new NodeDistance(source, 0));
    while (!queue.isEmpty()) {
      NodeDistance item = queue.remove();
      if (item.distance != distance[item.node]) continue;
      for (Edge edge : adjacency.get(item.node)) {
        long weight = edge.weight + potential[edge.from] - potential[edge.to];
        long candidate = item.distance + weight;
        if (candidate < distance[edge.to]) {
          distance[edge.to] = candidate;
          parent[edge.to] = edge.from;
          queue.add(new NodeDistance(edge.to, candidate));
        }
      }
    }
    return new DijkstraRow(distance, parent);
  }

  /** A* for a weighted graph. Euclidean distance between supplied coordinates is the heuristic. */
  public static AStarResult aStar(Graph graph, List<Point> coordinates, int source, int target) {
    checkVertex(graph, source);
    checkVertex(graph, target);
    if (coordinates.size() != graph.vertices)
      throw new IllegalArgumentException("one coordinate is required for every vertex");
    for (Edge edge : graph.edges)
      if (edge.weight < 0) throw new IllegalArgumentException("A* requires non-negative weights");

    long[] gScore = new long[graph.vertices];
    double[] hScore = new double[graph.vertices];
    double[] fScore = new double[graph.vertices];
    int[] parent = new int[graph.vertices];
    boolean[] open = new boolean[graph.vertices];
    boolean[] closed = new boolean[graph.vertices];
    Arrays.fill(gScore, INF);
    Arrays.fill(fScore, Double.POSITIVE_INFINITY);
    Arrays.fill(parent, -1);
    double heuristicScale = admissibleEuclideanScale(graph, coordinates);
    for (int vertex = 0; vertex < graph.vertices; vertex++)
      hScore[vertex] = heuristicScale * euclidean(coordinates.get(vertex), coordinates.get(target));
    gScore[source] = 0;
    fScore[source] = hScore[source];
    open[source] = true;
    PriorityQueue<AStarQueueNode> queue =
        new PriorityQueue<>(Comparator.comparingDouble(AStarQueueNode::score));
    queue.add(new AStarQueueNode(source, fScore[source]));
    List<AStarFrame> frames = new ArrayList<>();
    List<List<Edge>> adjacency = graph.adjacency();
    int expanded = 0;
    while (!queue.isEmpty()) {
      AStarQueueNode item = queue.remove();
      int current = item.node;
      if (closed[current] || item.score > fScore[current]) continue;
      open[current] = false;
      closed[current] = true;
      expanded++;
      if (current != target) {
        for (Edge edge : adjacency.get(current)) {
          long candidate = gScore[current] + edge.weight;
          if (candidate < gScore[edge.to]) {
            parent[edge.to] = current;
            gScore[edge.to] = candidate;
            fScore[edge.to] = candidate + hScore[edge.to];
            closed[edge.to] = false;
            open[edge.to] = true;
            queue.add(new AStarQueueNode(edge.to, fScore[edge.to]));
          }
        }
      }
      frames.add(new AStarFrame(current, gScore, hScore, fScore, indexes(open), indexes(closed)));
      if (current == target) break;
    }
    List<Integer> path =
        gScore[target] >= INF ? List.of() : reconstructPath(parent, source, target);
    return new AStarResult(path, gScore[target], gScore, hScore, fScore, expanded, frames);
  }

  private static double euclidean(Point first, Point second) {
    return Math.hypot(first.x - second.x, first.y - second.y);
  }

  private static double admissibleEuclideanScale(Graph graph, List<Point> coordinates) {
    double scale = 1;
    for (Edge edge : graph.edges) {
      double geometricLength = euclidean(coordinates.get(edge.from), coordinates.get(edge.to));
      if (geometricLength > 0) scale = Math.min(scale, edge.weight / geometricLength);
    }
    return Math.max(0, scale);
  }

  private static Set<Integer> indexes(boolean[] flags) {
    Set<Integer> result = new LinkedHashSet<>();
    for (int i = 0; i < flags.length; i++) if (flags[i]) result.add(i);
    return result;
  }

  private static List<Integer> reconstructPath(int[] parent, int source, int target) {
    List<Integer> path = new ArrayList<>();
    for (int current = target; current != -1; current = parent[current]) {
      path.add(current);
      if (current == source) break;
    }
    Collections.reverse(path);
    return path.isEmpty() || path.get(0) != source ? List.of() : List.copyOf(path);
  }

  public static MstResult kruskal(Graph graph) {
    requireUndirected(graph);
    List<Edge> sorted = new ArrayList<>(graph.edges);
    sorted.sort(Comparator.comparingInt(Edge::weight));
    UnionFind sets = new UnionFind(graph.vertices);
    List<Edge> chosen = new ArrayList<>();
    long total = 0;
    for (Edge e : sorted)
      if (sets.union(e.from, e.to)) {
        chosen.add(e);
        total += e.weight;
        if (chosen.size() == graph.vertices - 1) break;
      }
    return new MstResult(chosen, total, graph.vertices == 0 || chosen.size() == graph.vertices - 1);
  }

  public static MstResult prim(Graph graph, int start) {
    requireUndirected(graph);
    checkVertex(graph, start);
    boolean[] inTree = new boolean[graph.vertices];
    inTree[start] = true;
    PriorityQueue<Edge> candidates = new PriorityQueue<>(Comparator.comparingInt(Edge::weight));
    candidates.addAll(graph.adjacency().get(start));
    List<Edge> chosen = new ArrayList<>();
    long total = 0;
    List<List<Edge>> adj = graph.adjacency();
    while (!candidates.isEmpty() && chosen.size() < graph.vertices - 1) {
      Edge edge = candidates.remove();
      if (inTree[edge.to]) continue;
      inTree[edge.to] = true;
      chosen.add(edge);
      total += edge.weight;
      for (Edge next : adj.get(edge.to)) if (!inTree[next.to]) candidates.add(next);
    }
    return new MstResult(chosen, total, chosen.size() == graph.vertices - 1);
  }

  public static int maximumBipartiteMatching(boolean[][] edges) {
    int rightSize = validateBipartiteMatrix(edges);
    int[] rightMatch = new int[rightSize];
    Arrays.fill(rightMatch, -1);
    int matches = 0;
    for (int left = 0; left < edges.length; left++) {
      boolean[] seen = new boolean[rightSize];
      if (augment(left, edges, seen, rightMatch)) matches++;
    }
    return matches;
  }

  private static boolean augment(int left, boolean[][] edges, boolean[] seen, int[] rightMatch) {
    for (int right = 0; right < edges[left].length; right++)
      if (edges[left][right] && !seen[right]) {
        seen[right] = true;
        if (rightMatch[right] == -1 || augment(rightMatch[right], edges, seen, rightMatch)) {
          rightMatch[right] = left;
          return true;
        }
      }
    return false;
  }

  /** Edmonds-Karp. Returns max flow, residual matrix and source side of the minimum cut. */
  public static FlowResult edmondsKarp(int[][] capacity, int source, int sink) {
    int[][] residual = validatedCapacityCopy(capacity, source, sink);
    int n = capacity.length;
    int maxFlow = 0;
    int bfsPhases = 0, augmentations = 0, steps = 0;
    int[] parent = new int[n];
    while (true) {
      steps++;
      if (!flowBfs(residual, source, sink, parent)) break;
      bfsPhases++;
      augmentations++;
      int bottleneck = Integer.MAX_VALUE;
      for (int v = sink; v != source; v = parent[v])
        bottleneck = Math.min(bottleneck, residual[parent[v]][v]);
      for (int v = sink; v != source; v = parent[v]) {
        int u = parent[v];
        residual[u][v] -= bottleneck;
        residual[v][u] += bottleneck;
        steps++;
      }
      maxFlow = Math.addExact(maxFlow, bottleneck);
    }
    boolean[] sourceSide = residualReachable(residual, source);
    return new FlowResult(maxFlow, residual, sourceSide, bfsPhases, augmentations, steps);
  }

  /** Dinic's algorithm: repeated BFS level graphs followed by DFS blocking flows. */
  public static DinicResult dinic(int[][] capacity, int source, int sink) {
    int[][] residual = validatedCapacityCopy(capacity, source, sink);
    int n = capacity.length;
    int[] level = new int[n];
    int totalFlow = 0, phase = 0, augmentations = 0;
    DinicMetrics metrics = new DinicMetrics();
    List<DinicFrame> frames = new ArrayList<>();
    while (dinicBfs(residual, source, sink, level, metrics)) {
      phase++;
      frames.add(
          new DinicFrame(
              phase, level, List.of(), 0, totalFlow, residual, "BFS builds the level graph"));
      int[] next = new int[n];
      while (true) {
        List<Integer> path = new ArrayList<>();
        path.add(source);
        int pushed =
            dinicDfs(source, sink, Integer.MAX_VALUE, residual, level, next, path, metrics);
        if (pushed == 0) break;
        totalFlow = Math.addExact(totalFlow, pushed);
        augmentations++;
        frames.add(
            new DinicFrame(
                phase,
                level,
                path,
                pushed,
                totalFlow,
                residual,
                "DFS pushes flow along an admissible path"));
      }
      frames.add(
          new DinicFrame(
              phase,
              level,
              List.of(),
              0,
              totalFlow,
              residual,
              "Blocking flow completes this level graph"));
    }
    return new DinicResult(
        totalFlow,
        residual,
        residualReachable(residual, source),
        phase,
        metrics.dfsPushes,
        augmentations,
        metrics.steps,
        frames);
  }

  private static boolean dinicBfs(
      int[][] residual, int source, int sink, int[] level, DinicMetrics metrics) {
    Arrays.fill(level, -1);
    level[source] = 0;
    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(source);
    while (!queue.isEmpty()) {
      int u = queue.remove();
      for (int v = 0; v < residual.length; v++) {
        metrics.steps++;
        if (level[v] < 0 && residual[u][v] > 0) {
          level[v] = level[u] + 1;
          queue.add(v);
        }
      }
    }
    return level[sink] >= 0;
  }

  private static int dinicDfs(
      int current,
      int sink,
      int available,
      int[][] residual,
      int[] level,
      int[] next,
      List<Integer> path,
      DinicMetrics metrics) {
    if (current == sink) return available;
    for (; next[current] < residual.length; next[current]++) {
      int target = next[current];
      metrics.steps++;
      if (residual[current][target] <= 0 || level[target] != level[current] + 1) continue;
      path.add(target);
      int pushed =
          dinicDfs(
              target,
              sink,
              Math.min(available, residual[current][target]),
              residual,
              level,
              next,
              path,
              metrics);
      if (pushed > 0) {
        residual[current][target] -= pushed;
        residual[target][current] += pushed;
        metrics.dfsPushes++;
        return pushed;
      }
      path.remove(path.size() - 1);
    }
    return 0;
  }

  private static int[][] validatedCapacityCopy(int[][] capacity, int source, int sink) {
    int n = capacity.length;
    if (n == 0 || source < 0 || sink < 0 || source >= n || sink >= n || source == sink)
      throw new IllegalArgumentException("invalid source or sink");
    int[][] residual = new int[n][n];
    for (int i = 0; i < n; i++) {
      if (capacity[i].length != n)
        throw new IllegalArgumentException("capacity matrix must be square");
      for (int j = 0; j < n; j++) {
        if (capacity[i][j] < 0) throw new IllegalArgumentException("capacity cannot be negative");
        residual[i][j] = capacity[i][j];
      }
    }
    return residual;
  }

  private static boolean[] residualReachable(int[][] residual, int source) {
    boolean[] sourceSide = new boolean[residual.length];
    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(source);
    sourceSide[source] = true;
    while (!queue.isEmpty()) {
      int current = queue.remove();
      for (int target = 0; target < residual.length; target++)
        if (!sourceSide[target] && residual[current][target] > 0) {
          sourceSide[target] = true;
          queue.add(target);
        }
    }
    return sourceSide;
  }

  private static boolean flowBfs(int[][] residual, int source, int sink, int[] parent) {
    Arrays.fill(parent, -1);
    parent[source] = source;
    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(source);
    while (!queue.isEmpty()) {
      int u = queue.remove();
      for (int v = 0; v < residual.length; v++)
        if (parent[v] == -1 && residual[u][v] > 0) {
          parent[v] = u;
          if (v == sink) return true;
          queue.add(v);
        }
    }
    return false;
  }

  public record FlowResult(
      int maximumFlow,
      int[][] residual,
      boolean[] sourceSideOfMinCut,
      int bfsPhases,
      int augmentations,
      int steps) {
    public FlowResult {
      int[][] copy = new int[residual.length][];
      for (int i = 0; i < residual.length; i++) copy[i] = residual[i].clone();
      residual = copy;
      sourceSideOfMinCut = sourceSideOfMinCut.clone();
    }
  }

  private record NodeDistance(int node, long distance) {}

  private record DijkstraRow(long[] distance, int[] parent) {}

  private record AStarQueueNode(int node, double score) {}

  private record UndirectedArc(int to, int edgeId) {}

  private record TraversalArc(int to, int edgeId) {}

  private static final class DinicMetrics {
    private int dfsPushes;
    private int steps;
  }

  private static long[][] copy(long[][] source) {
    long[][] result = new long[source.length][];
    for (int i = 0; i < source.length; i++) result[i] = source[i].clone();
    return result;
  }

  private static int[][] copy(int[][] source) {
    int[][] result = new int[source.length][];
    for (int i = 0; i < source.length; i++) result[i] = source[i].clone();
    return result;
  }

  private static List<Edge> expandedEdges(Graph graph) {
    if (graph.directed) return graph.edges;
    List<Edge> result = new ArrayList<>();
    for (Edge e : graph.edges) {
      result.add(e);
      result.add(new Edge(e.to, e.from, e.weight));
    }
    return result;
  }

  private static void checkVertex(Graph graph, int vertex) {
    if (vertex < 0 || vertex >= graph.vertices)
      throw new IllegalArgumentException("vertex outside graph");
  }

  private static void requireUndirected(Graph graph) {
    if (graph.directed)
      throw new IllegalArgumentException("minimum spanning trees require an undirected graph");
  }
}
