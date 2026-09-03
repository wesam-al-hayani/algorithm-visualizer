package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;
import static dev.wesam.visualizer.model.AlgorithmStep.VisualKind.*;

import dev.wesam.visualizer.algorithms.SortAlgorithms;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class AnalysisCatalog {
  private AnalysisCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    demos.add(
        demo(
            "Algorithm Analysis",
            "Runtime Growth",
            "Compares common asymptotic growth classes on the same n values.",
            "evaluate 1, log n, n, n log n, n², 2ⁿ",
            "Interactive",
            "O(1)",
            "Maximum n (5–50)",
            "20",
            AnalysisCatalog::growth));
    demos.add(
        demo(
            "Algorithm Analysis",
            "Master Theorem Explorer",
            "Evaluates representative T(n)=aT(n/b)+f(n) recurrences and identifies their case.",
            "compare f(n) with n^(log_b a)\nselect case 1, 2, or 3",
            "Analysis tool",
            "O(1)",
            "a,b,k for f(n)=n^k",
            "2,2,1",
            AnalysisCatalog::masterTheorem));
    demos.add(
        demo(
            "Algorithm Analysis",
            "Amortized Dynamic Array",
            "Simulates doubling; expensive resize operations are spread across many constant-time"
                + " appends.",
            "if full, allocate double capacity\ncopy existing items\nappend one item",
            "O(1) amortized append",
            "O(n)",
            "Number of appends",
            "20",
            AnalysisCatalog::amortized));
    demos.add(
        demo(
            "Algorithm Analysis",
            "Randomized Quicksort Experiment",
            "Repeats quicksort with random pivots and reports the observed average comparison"
                + " count.",
            "repeat trials\nshuffle pivot choices\nsum comparisons / trials",
            "Expected O(n log n)",
            "O(log n) expected",
            "values ; trials",
            "9,1,8,2,7,3,6,4,5,0 ; 50",
            AnalysisCatalog::randomizedExperiment));
    return List.copyOf(demos);
  }

  static AlgorithmRun growth(String input) {
    int n = Math.max(5, Math.min(50, Integer.parseInt(input.trim())));
    List<Integer> values = new ArrayList<>();
    List<String> labels = new ArrayList<>();
    for (int i = 1; i <= n; i++) {
      values.add((int) Math.min(10000, i * i));
      labels.add(Integer.toString(i));
    }
    return new AlgorithmRun(
        List.of(
            new AlgorithmStep(
                "Growth curves sampled through n = " + n,
                "1, log n, n, n log n, n², 2ⁿ",
                0,
                ARRAY,
                values,
                labels,
                Set.of(),
                Set.of(),
                Set.of(),
                List.of(),
                Map.of("n", n, "n log₂ n", (int) (n * Math.log(n) / Math.log(2)), "n²", n * n),
                "Exponential growth quickly leaves the visible scale.")),
        "Compared six common growth classes through n=" + n);
  }

  static AlgorithmRun masterTheorem(String input) {
    int[] p = numbers(input);
    if (p.length != 3 || p[0] <= 0 || p[1] <= 1) throw new IllegalArgumentException("Use a,b,k");
    double critical = Math.log(p[0]) / Math.log(p[1]);
    String result =
        Math.abs(p[2] - critical) < 1e-9
            ? "Case 2: Θ(n^" + p[2] + " log n)"
            : p[2] < critical
                ? "Case 1: Θ(n^" + String.format("%.2f", critical) + ")"
                : "Case 3: Θ(n^" + p[2] + ") (regularity assumed)";
    return new AlgorithmRun(
        List.of(
            AlgorithmStep.text(
                "Compare k with log_b(a)",
                "k = "
                    + p[2]
                    + ", log_"
                    + p[1]
                    + "("
                    + p[0]
                    + ") = "
                    + String.format("%.3f", critical),
                Map.of("a", p[0], "b", p[1], "k", p[2])),
            AlgorithmStep.text("Master Theorem result", result, Map.of())),
        result);
  }

  static AlgorithmRun amortized(String input) {
    int count = Math.max(1, Math.min(200, Integer.parseInt(input.trim()))),
        capacity = 1,
        copies = 0;
    List<AlgorithmStep> s = new ArrayList<>();
    List<Integer> a = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      boolean resized = i == capacity;
      if (resized) {
        copies += i;
        capacity *= 2;
      }
      a.add(i);
      s.add(
          arrayStep(
              (resized
                  ? "Capacity full: copy " + i + " entries, then append"
                  : "Append without resizing"),
              a.stream().mapToInt(Integer::intValue).toArray(),
              Set.of(i),
              Set.of(),
              Set.of(),
              Map.of("Size", i + 1, "Capacity", capacity, "Total copies", copies),
              "Amortized work per append: "
                  + String.format("%.2f", (copies + count) / (double) (i + 1))));
    }
    return new AlgorithmRun(s, "Total append writes + copies: " + (count + copies));
  }

  static AlgorithmRun randomizedExperiment(String input) {
    Parts p = valuesAndParameter(input);
    int trials = Math.max(1, Math.min(1000, p.parameter));
    long total = 0, min = Long.MAX_VALUE, max = 0;
    List<AlgorithmStep> s = new ArrayList<>();
    for (int i = 0; i < trials; i++) {
      long c =
          SortAlgorithms.run(p.values, SortAlgorithms.Kind.RANDOMIZED_QUICK, i * 7919L + 17)
              .comparisons();
      total += c;
      min = Math.min(min, c);
      max = Math.max(max, c);
      if (i < 50 || i == trials - 1)
        s.add(
            arrayStep(
                "Trial " + (i + 1) + " uses new random pivots",
                p.values,
                Set.of(),
                Set.of(),
                Set.of(),
                Map.of("Trial", i + 1, "Comparisons", c, "Running average", total / (i + 1)),
                "Deterministic input; randomized pivot choices"));
    }
    return new AlgorithmRun(
        s,
        "Average comparisons: "
            + String.format("%.2f", total / (double) trials)
            + " (min "
            + min
            + ", max "
            + max
            + ")");
  }
}
