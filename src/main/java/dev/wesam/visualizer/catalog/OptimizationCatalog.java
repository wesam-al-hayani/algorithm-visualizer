package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;
import static dev.wesam.visualizer.model.AlgorithmStep.VisualKind.*;

import dev.wesam.visualizer.algorithms.GraphAlgorithms;
import dev.wesam.visualizer.algorithms.OptimizationAlgorithms;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class OptimizationCatalog {
  private OptimizationCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    demos.add(
        demo(
            "Dynamic Programming & Optimization",
            "0/1 Knapsack",
            "A table records the best value for every item prefix and capacity.",
            "for each item and capacity\n"
                + "  skip item\n"
                + "  or take it if it fits\n"
                + "backtrack selected items",
            "O(nC)",
            "O(nC)",
            "weights ; values ; capacity",
            "2,3,4,5 ; 3,4,5,8 ; 8",
            OptimizationCatalog::knapsack));
    demos.add(
        demo(
            "Dynamic Programming & Optimization",
            "Branch-and-Bound Knapsack",
            "Best-first search uses the fractional-knapsack bound to prune states that cannot beat"
                + " the incumbent.",
            "order by value/weight\nexpand best bound\nprune bound ≤ current best",
            "Worst O(2ⁿ)",
            "O(2ⁿ)",
            "weights ; values ; capacity (≤30 items)",
            "2,3,4,5 ; 3,4,5,8 ; 8",
            OptimizationCatalog::branchBound));
    demos.add(
        demo(
            "Dynamic Programming & Optimization",
            "Held–Karp TSP",
            "Subset dynamic programming stores the cheapest path ending at each city. Limited to 18"
                + " cities.",
            "dp[subset,last] ← min previous\nclose tour back to city 0\nreconstruct parents",
            "O(n²2ⁿ)",
            "O(n2ⁿ)",
            "Semicolon-separated distance rows",
            "0,10,15,20;10,0,35,25;15,35,0,30;20,25,30,0",
            input -> tsp(input, false)));
    demos.add(
        demo(
            "Dynamic Programming & Optimization",
            "Brute-Force TSP",
            "Enumerates all tours for comparison. Limited to 10 cities.",
            "permute cities after city 0\nscore closed tour\nretain the cheapest",
            "O(n!)",
            "O(n)",
            "Semicolon-separated distance rows",
            "0,10,15,20;10,0,35,25;15,35,0,30;20,25,30,0",
            input -> tsp(input, true)));
    demos.add(
        demo(
            "Dynamic Programming & Optimization",
            "Exact Vertex Cover",
            "Enumerates subsets from smallest to largest. Limited to 24 vertices.",
            "for subsets by increasing size\n  test every edge is covered\nreturn first cover",
            "O(2ⁿE)",
            "O(n)",
            "Undirected edges",
            "0-1,1-2,2-0,2-3",
            input -> vertexCover(input, true)));
    demos.add(
        demo(
            "Dynamic Programming & Optimization",
            "2-Approximation Vertex Cover",
            "Picks both endpoints of an uncovered edge, guaranteeing at most twice optimum.",
            "pick an uncovered edge\nadd both endpoints\nremove incident edges",
            "O(E²) educational version",
            "O(E)",
            "Undirected edges",
            "0-1,1-2,2-0,2-3",
            input -> vertexCover(input, false)));
    demos.add(
        demo(
            "Dynamic Programming & Optimization",
            "Exact Max Cut",
            "Enumerates both-side assignments and scores crossing edges. Limited to 24 vertices.",
            "for each partition\n  sum weights of crossing edges\nretain best",
            "O(2ⁿE)",
            "O(n)",
            "Weighted undirected edges",
            "0-1:2,1-2:3,2-0:1,2-3:4",
            OptimizationCatalog::maxCut));
    demos.add(
        demo(
            "Dynamic Programming & Optimization",
            "Exact MaxSAT",
            "Enumerates truth assignments and retains the one satisfying most clauses. Limited to"
                + " 20 UI variables.",
            "for every assignment\n  evaluate all clauses\nretain best assignment",
            "O(2ⁿ · clauses)",
            "O(n)",
            "Clauses separated by ;, literals like 1,-2",
            "1,2;-1,2;1,-2;-1,-2,3",
            OptimizationCatalog::maxSat));
    return List.copyOf(demos);
  }

  static AlgorithmRun knapsack(String input) {
    String[] p = input.split(";");
    if (p.length != 3) throw new IllegalArgumentException("Use weights ; values ; capacity");
    int[] w = numbers(p[0]), v = numbers(p[1]);
    int c = Integer.parseInt(p[2].trim());
    if (w.length > 100 || c > 500)
      throw new IllegalArgumentException(
          "Knapsack visualization is limited to 100 items and capacity 500");
    var r = OptimizationAlgorithms.knapsack(w, v, c);
    List<AlgorithmStep> s = new ArrayList<>();
    for (int item = 1; item < r.table().length; item++)
      s.add(
          tableStep(
              "Compute DP row for item " + item,
              r.table(),
              item,
              Map.of("Items considered", item, "Best value", r.table()[item][c]),
              "Capacity " + c));
    return new AlgorithmRun(
        s, "Maximum value " + r.maximumValue() + "; selected item indices " + r.selectedItems());
  }

  static AlgorithmRun branchBound(String input) {
    String[] p = input.split(";");
    if (p.length != 3) throw new IllegalArgumentException("Use weights ; values ; capacity");
    int[] weights = numbers(p[0]);
    if (weights.length > 20)
      throw new IllegalArgumentException("Branch-and-bound visualization is limited to 20 items");
    var r =
        OptimizationAlgorithms.branchAndBoundKnapsack(
            weights, numbers(p[1]), Integer.parseInt(p[2].trim()));
    return new AlgorithmRun(
        List.of(
            AlgorithmStep.text(
                "Best-first search completed",
                "Selected items: " + r.selectedItems(),
                Map.of("Best value", r.maximumValue()))),
        "Maximum value " + r.maximumValue());
  }

  static AlgorithmRun tsp(String input, boolean brute) {
    int[][] d = matrix(input);
    int limit = brute ? 9 : 12;
    if (d.length > limit)
      throw new IllegalArgumentException(
          "This TSP visualization is limited to " + limit + " cities");
    var r = brute ? OptimizationAlgorithms.bruteForceTsp(d) : OptimizationAlgorithms.heldKarp(d);
    GraphAlgorithms.Graph g = completeGraph(d);
    List<AlgorithmStep> s = new ArrayList<>();
    Set<Integer> visited = new LinkedHashSet<>();
    for (int city : r.tour()) {
      visited.add(city);
      s.add(
          graphStep(
              "Extend tour to city " + city,
              g,
              Set.of(city),
              Set.of(),
              visited,
              Map.of("States examined", r.statesExamined(), "Tour cost", r.cost()),
              "Tour: " + r.tour()));
    }
    return new AlgorithmRun(s, "Tour " + r.tour() + " costs " + r.cost());
  }

  static AlgorithmRun vertexCover(String input, boolean exact) {
    GraphAlgorithms.Graph g = parseGraph(input, false);
    var r =
        exact
            ? OptimizationAlgorithms.exactVertexCover(g.vertices(), g.edges())
            : OptimizationAlgorithms.approximateVertexCover(g.vertices(), g.edges());
    var optimal = exact ? r : OptimizationAlgorithms.exactVertexCover(g.vertices(), g.edges());
    double ratio =
        optimal.vertices().isEmpty() ? 1 : r.vertices().size() / (double) optimal.vertices().size();
    return new AlgorithmRun(
        List.of(
            graphStep(
                "Selected cover vertices",
                g,
                r.vertices(),
                Set.of(),
                r.vertices(),
                Map.of(
                    "Cover size",
                    r.vertices().size(),
                    "Optimum",
                    optimal.vertices().size(),
                    "Ratio",
                    ratio),
                "Every edge touches a selected vertex")),
        "Cover " + r.vertices() + "; ratio " + String.format("%.2f", ratio));
  }

  static AlgorithmRun maxCut(String input) {
    GraphAlgorithms.Graph g = parseGraph(input, false);
    var r = OptimizationAlgorithms.exactMaxCut(g.vertices(), g.edges());
    return new AlgorithmRun(
        List.of(
            graphStep(
                "Best partition found",
                g,
                r.left(),
                r.right(),
                rangeSet(0, g.vertices() - 1),
                Map.of("Crossing weight", r.weight()),
                "Left " + r.left() + " | Right " + r.right())),
        "Maximum cut weight: " + r.weight());
  }

  static AlgorithmRun maxSat(String input) {
    String[] parts = input.split(";");
    List<int[]> clauses = new ArrayList<>();
    int variables = 0;
    for (String part : parts) {
      int[] c = numbers(part);
      for (int x : c) variables = Math.max(variables, Math.abs(x));
      clauses.add(c);
    }
    if (variables > 18) throw new IllegalArgumentException("UI MaxSAT limit is 18 variables");
    var r = OptimizationAlgorithms.exactMaxSat(variables, clauses);
    List<String> labels = new ArrayList<>();
    for (int i = 0; i < variables; i++) labels.add("x" + (i + 1) + " = " + r.assignment()[i]);
    return new AlgorithmRun(
        List.of(
            new AlgorithmStep(
                "Best assignment",
                "enumerate assignments\nevaluate clauses\nretain best",
                1,
                SETS,
                List.of(),
                labels,
                rangeSet(0, variables - 1),
                Set.of(),
                Set.of(),
                List.of(),
                Map.of("Satisfied clauses", r.satisfiedClauses(), "Total clauses", clauses.size()),
                "Assignment: " + Arrays.toString(r.assignment()))),
        r.satisfiedClauses() + " / " + clauses.size() + " clauses satisfied");
  }
}
