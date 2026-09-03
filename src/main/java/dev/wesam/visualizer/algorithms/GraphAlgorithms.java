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
    int rightSize = edges.length == 0 ? 0 : edges[0].length;
    for (boolean[] row : edges)
      if (row.length != rightSize) throw new IllegalArgumentException("ragged matrix");
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
    int maxFlow = 0;
    int[] parent = new int[n];
    while (flowBfs(residual, source, sink, parent)) {
      int bottleneck = Integer.MAX_VALUE;
      for (int v = sink; v != source; v = parent[v])
        bottleneck = Math.min(bottleneck, residual[parent[v]][v]);
      for (int v = sink; v != source; v = parent[v]) {
        int u = parent[v];
        residual[u][v] -= bottleneck;
        residual[v][u] += bottleneck;
      }
      maxFlow += bottleneck;
    }
    boolean[] sourceSide = new boolean[n];
    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(source);
    sourceSide[source] = true;
    while (!queue.isEmpty()) {
      int u = queue.remove();
      for (int v = 0; v < n; v++)
        if (!sourceSide[v] && residual[u][v] > 0) {
          sourceSide[v] = true;
          queue.add(v);
        }
    }
    return new FlowResult(maxFlow, residual, sourceSide);
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

  public record FlowResult(int maximumFlow, int[][] residual, boolean[] sourceSideOfMinCut) {
    public FlowResult {
      int[][] copy = new int[residual.length][];
      for (int i = 0; i < residual.length; i++) copy[i] = residual[i].clone();
      residual = copy;
      sourceSideOfMinCut = sourceSideOfMinCut.clone();
    }
  }

  private record NodeDistance(int node, long distance) {}

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
