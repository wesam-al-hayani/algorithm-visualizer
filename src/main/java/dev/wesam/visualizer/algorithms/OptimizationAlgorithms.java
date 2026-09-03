package dev.wesam.visualizer.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public final class OptimizationAlgorithms {
  private OptimizationAlgorithms() {}

  public record KnapsackResult(int maximumValue, List<Integer> selectedItems, int[][] table) {
    public KnapsackResult {
      selectedItems = List.copyOf(selectedItems);
    }
  }

  public record TourResult(long cost, List<Integer> tour, int statesExamined) {
    public TourResult {
      tour = List.copyOf(tour);
    }
  }

  public record VertexCoverResult(Set<Integer> vertices, boolean exact) {
    public VertexCoverResult {
      vertices = Set.copyOf(vertices);
    }
  }

  public record CutResult(Set<Integer> left, Set<Integer> right, long weight) {
    public CutResult {
      left = Set.copyOf(left);
      right = Set.copyOf(right);
    }
  }

  public record MaxSatResult(boolean[] assignment, int satisfiedClauses) {
    public MaxSatResult {
      assignment = assignment.clone();
    }
  }

  public static KnapsackResult knapsack(int[] weights, int[] values, int capacity) {
    if (weights.length != values.length || capacity < 0)
      throw new IllegalArgumentException("invalid knapsack input");
    int[][] table = new int[weights.length + 1][capacity + 1];
    for (int item = 1; item <= weights.length; item++) {
      if (weights[item - 1] <= 0) throw new IllegalArgumentException("weights must be positive");
      for (int cap = 0; cap <= capacity; cap++) {
        table[item][cap] = table[item - 1][cap];
        if (weights[item - 1] <= cap)
          table[item][cap] =
              Math.max(
                  table[item][cap], values[item - 1] + table[item - 1][cap - weights[item - 1]]);
      }
    }
    List<Integer> chosen = new ArrayList<>();
    int cap = capacity;
    for (int item = weights.length; item > 0; item--)
      if (table[item][cap] != table[item - 1][cap]) {
        chosen.add(item - 1);
        cap -= weights[item - 1];
      }
    java.util.Collections.reverse(chosen);
    return new KnapsackResult(table[weights.length][capacity], chosen, table);
  }

  /** Best-first branch and bound for 0/1 knapsack. */
  public static KnapsackResult branchAndBoundKnapsack(int[] weights, int[] values, int capacity) {
    if (weights.length != values.length || weights.length > 30 || capacity < 0)
      throw new IllegalArgumentException("invalid input or too many items");
    List<Item> items = new ArrayList<>();
    for (int i = 0; i < weights.length; i++) {
      if (weights[i] <= 0) throw new IllegalArgumentException("weights must be positive");
      items.add(new Item(i, weights[i], values[i], values[i] / (double) weights[i]));
    }
    items.sort(Comparator.comparingDouble(Item::ratio).reversed());
    PriorityQueue<BoundNode> queue =
        new PriorityQueue<>(Comparator.comparingDouble(BoundNode::bound).reversed());
    queue.add(new BoundNode(0, 0, 0, bound(items, 0, 0, 0, capacity), 0L));
    int best = 0;
    long bestMask = 0;
    while (!queue.isEmpty()) {
      BoundNode node = queue.remove();
      if (node.bound <= best || node.level >= items.size()) continue;
      Item item = items.get(node.level);
      int withWeight = node.weight + item.weight, withValue = node.value + item.value;
      long withMask = node.mask | (1L << item.originalIndex);
      if (withWeight <= capacity && withValue > best) {
        best = withValue;
        bestMask = withMask;
      }
      double withBound = bound(items, node.level + 1, withWeight, withValue, capacity);
      if (withWeight <= capacity && withBound > best)
        queue.add(new BoundNode(node.level + 1, withWeight, withValue, withBound, withMask));
      double withoutBound = bound(items, node.level + 1, node.weight, node.value, capacity);
      if (withoutBound > best)
        queue.add(new BoundNode(node.level + 1, node.weight, node.value, withoutBound, node.mask));
    }
    List<Integer> selected = new ArrayList<>();
    for (int i = 0; i < weights.length; i++) if ((bestMask & (1L << i)) != 0) selected.add(i);
    return new KnapsackResult(best, selected, new int[0][0]);
  }

  private static double bound(List<Item> items, int level, int weight, int value, int capacity) {
    if (weight > capacity) return 0;
    double result = value;
    int remaining = capacity - weight;
    for (int i = level; i < items.size() && remaining > 0; i++) {
      Item item = items.get(i);
      int take = Math.min(remaining, item.weight);
      result += take * item.ratio;
      remaining -= take;
    }
    return result;
  }

  private record Item(int originalIndex, int weight, int value, double ratio) {}

  private record BoundNode(int level, int weight, int value, double bound, long mask) {}

  /** Bellman-Held-Karp dynamic programming. City 0 is the fixed start. */
  public static TourResult heldKarp(int[][] distance) {
    int n = validateDistances(distance, 18);
    if (n == 1) return new TourResult(0, List.of(0, 0), 1);
    int subsets = 1 << (n - 1);
    long[][] dp = new long[subsets][n];
    int[][] parent = new int[subsets][n];
    for (long[] row : dp) Arrays.fill(row, GraphAlgorithms.INF);
    for (int[] row : parent) Arrays.fill(row, -1);
    for (int city = 1; city < n; city++) dp[1 << (city - 1)][city] = distance[0][city];
    int states = n - 1;
    for (int mask = 1; mask < subsets; mask++)
      for (int last = 1; last < n; last++)
        if ((mask & (1 << (last - 1))) != 0) {
          int previousMask = mask ^ (1 << (last - 1));
          if (previousMask == 0) continue;
          for (int previous = 1; previous < n; previous++)
            if ((previousMask & (1 << (previous - 1))) != 0) {
              long candidate = dp[previousMask][previous] + distance[previous][last];
              states++;
              if (candidate < dp[mask][last]) {
                dp[mask][last] = candidate;
                parent[mask][last] = previous;
              }
            }
        }
    int full = subsets - 1, last = -1;
    long best = GraphAlgorithms.INF;
    for (int city = 1; city < n; city++)
      if (dp[full][city] + distance[city][0] < best) {
        best = dp[full][city] + distance[city][0];
        last = city;
      }
    List<Integer> reverse = new ArrayList<>();
    int mask = full;
    while (last != -1) {
      reverse.add(last);
      int next = parent[mask][last];
      mask ^= 1 << (last - 1);
      last = next;
    }
    java.util.Collections.reverse(reverse);
    List<Integer> tour = new ArrayList<>();
    tour.add(0);
    tour.addAll(reverse);
    tour.add(0);
    return new TourResult(best, tour, states);
  }

  public static TourResult bruteForceTsp(int[][] distance) {
    int n = validateDistances(distance, 10);
    boolean[] used = new boolean[n];
    used[0] = true;
    List<Integer> route = new ArrayList<>();
    route.add(0);
    TourSearch search = new TourSearch();
    permute(distance, used, route, 0, search);
    return new TourResult(search.best, search.bestRoute, search.states);
  }

  private static void permute(
      int[][] d, boolean[] used, List<Integer> route, long cost, TourSearch search) {
    if (route.size() == d.length) {
      search.states++;
      long total = cost + d[route.get(route.size() - 1)][0];
      if (total < search.best) {
        search.best = total;
        search.bestRoute = new ArrayList<>(route);
        search.bestRoute.add(0);
      }
      return;
    }
    int last = route.get(route.size() - 1);
    for (int city = 1; city < d.length; city++)
      if (!used[city]) {
        used[city] = true;
        route.add(city);
        permute(d, used, route, cost + d[last][city], search);
        route.remove(route.size() - 1);
        used[city] = false;
      }
  }

  private static final class TourSearch {
    long best = GraphAlgorithms.INF;
    List<Integer> bestRoute = List.of();
    int states;
  }

  public static VertexCoverResult exactVertexCover(int vertices, List<GraphAlgorithms.Edge> edges) {
    if (vertices > 24 || vertices < 0)
      throw new IllegalArgumentException("exact cover is limited to 24 vertices");
    long limit = 1L << vertices;
    for (int size = 0; size <= vertices; size++)
      for (long mask = 0; mask < limit; mask++)
        if (Long.bitCount(mask) == size && covers(mask, edges)) {
          Set<Integer> result = new LinkedHashSet<>();
          for (int v = 0; v < vertices; v++) if ((mask & (1L << v)) != 0) result.add(v);
          return new VertexCoverResult(result, true);
        }
    return new VertexCoverResult(Set.of(), true);
  }

  public static VertexCoverResult approximateVertexCover(
      int vertices, List<GraphAlgorithms.Edge> edges) {
    boolean[] covered = new boolean[edges.size()];
    Set<Integer> result = new LinkedHashSet<>();
    for (int i = 0; i < edges.size(); i++)
      if (!covered[i]) {
        GraphAlgorithms.Edge picked = edges.get(i);
        result.add(picked.from());
        result.add(picked.to());
        for (int j = 0; j < edges.size(); j++) {
          GraphAlgorithms.Edge e = edges.get(j);
          if (e.from() == picked.from()
              || e.to() == picked.from()
              || e.from() == picked.to()
              || e.to() == picked.to()) covered[j] = true;
        }
      }
    return new VertexCoverResult(result, false);
  }

  private static boolean covers(long mask, List<GraphAlgorithms.Edge> edges) {
    for (GraphAlgorithms.Edge e : edges)
      if ((mask & (1L << e.from())) == 0 && (mask & (1L << e.to())) == 0) return false;
    return true;
  }

  public static CutResult exactMaxCut(int vertices, List<GraphAlgorithms.Edge> edges) {
    if (vertices < 1 || vertices > 24)
      throw new IllegalArgumentException("exact cut is limited to 24 vertices");
    long bestMask = 0, bestWeight = Long.MIN_VALUE, limit = 1L << (vertices - 1);
    for (long mask = 0; mask < limit; mask++) {
      long fullMask = mask << 1, weight = 0;
      for (GraphAlgorithms.Edge e : edges)
        if (((fullMask >>> e.from()) & 1) != ((fullMask >>> e.to()) & 1)) weight += e.weight();
      if (weight > bestWeight) {
        bestWeight = weight;
        bestMask = fullMask;
      }
    }
    Set<Integer> left = new LinkedHashSet<>(), right = new LinkedHashSet<>();
    for (int v = 0; v < vertices; v++)
      if (((bestMask >>> v) & 1) == 1) left.add(v);
      else right.add(v);
    return new CutResult(left, right, bestWeight);
  }

  /** Literals are encoded as ±(variable index + 1). */
  public static MaxSatResult exactMaxSat(int variables, List<int[]> clauses) {
    if (variables < 0 || variables > 24)
      throw new IllegalArgumentException("MaxSAT is limited to 24 variables");
    int best = -1;
    long bestMask = 0, limit = 1L << variables;
    for (long mask = 0; mask < limit; mask++) {
      int satisfied = 0;
      for (int[] clause : clauses) {
        boolean ok = false;
        for (int literal : clause) {
          int variable = Math.abs(literal) - 1;
          if (variable < 0 || variable >= variables)
            throw new IllegalArgumentException("invalid literal");
          boolean value = ((mask >>> variable) & 1) == 1;
          if (literal < 0) value = !value;
          ok |= value;
        }
        if (ok) satisfied++;
      }
      if (satisfied > best) {
        best = satisfied;
        bestMask = mask;
      }
    }
    boolean[] assignment = new boolean[variables];
    for (int i = 0; i < variables; i++) assignment[i] = ((bestMask >>> i) & 1) == 1;
    return new MaxSatResult(assignment, best);
  }

  private static int validateDistances(int[][] distance, int limit) {
    int n = distance.length;
    if (n == 0 || n > limit) throw new IllegalArgumentException("TSP size must be 1.." + limit);
    for (int[] row : distance)
      if (row.length != n) throw new IllegalArgumentException("distance matrix must be square");
    return n;
  }
}
