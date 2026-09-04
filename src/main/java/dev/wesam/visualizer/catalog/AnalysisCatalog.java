package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;
import static dev.wesam.visualizer.model.AlgorithmStep.VisualKind.*;

import dev.wesam.visualizer.algorithms.AnalysisExperiments;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
            "Experimental Complexity",
            "Runs one selected algorithm without visualization overhead at several input sizes and"
                + " charts measured comparisons against its theoretical growth curve.",
            "parse an algorithm, increasing sizes, and random seed\n"
                + "generate a repeatable input at each size\n"
                + "run the instrumented algorithm without retaining animation frames\n"
                + "record measured comparisons and theoretical growth\n"
                + "plot both curves on the same JavaFX chart",
            "Experimental operation counting",
            "O(max n) working input",
            "BUBBLE_SORT|MERGE_SORT|BINARY_SEARCH ; sizes ; seed",
            "BUBBLE_SORT ; 10,25,50,100,250,500 ; 2026",
            AnalysisCatalog::experimentalComplexity));
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
            "Compares fixed right-boundary pivots with randomized pivots on the exact same seeded"
                + " base array and reports a full comparison-count distribution.",
            "parse trial count, array size, and seed\n"
                + "generate one seeded base permutation\n"
                + "run deterministic and randomized pivots on identical clones\n"
                + "collect minimum, maximum, average, and median comparisons\n"
                + "chart both comparison-count series without animation overhead",
            "Expected O(n log n)",
            "O(log n) expected",
            "trials,array size,random seed",
            "200,100,2026",
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

  static AlgorithmRun experimentalComplexity(String input) {
    String[] parts = input.split(";", -1);
    if (parts.length != 3)
      throw new IllegalArgumentException("Use algorithm ; increasing sizes ; seed");
    AnalysisExperiments.Subject subject;
    try {
      subject =
          AnalysisExperiments.Subject.valueOf(
              parts[0].trim().toUpperCase(Locale.ROOT).replace(' ', '_'));
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException(
          "Choose BUBBLE_SORT, MERGE_SORT, or BINARY_SEARCH", exception);
    }
    int[] sizes = numbers(parts[1]);
    long seed = Long.parseLong(parts[2].trim());
    AnalysisExperiments.ComplexityResult experiment =
        AnalysisExperiments.complexity(subject, sizes, seed);
    List<Integer> values = new ArrayList<>();
    List<String> labels = new ArrayList<>();
    for (AnalysisExperiments.ComplexityPoint point : experiment.points()) {
      values.add(Math.toIntExact(point.measured()));
      labels.add(Integer.toString(point.size()));
    }
    for (AnalysisExperiments.ComplexityPoint point : experiment.points())
      values.add(Math.toIntExact(point.theoretical()));
    AnalysisExperiments.ComplexityPoint last = experiment.points()[experiment.points().length - 1];
    AlgorithmStep chart =
        new AlgorithmStep(
            "Measured comparisons vs " + subject.theory(),
            "parse experiment\ngenerate inputs\nmeasure operations\ncompute theory\nplot curves",
            4,
            CHART,
            values,
            labels,
            Set.of(),
            Set.of(),
            Set.of(),
            List.of(),
            Map.of(
                "Input sizes", sizes.length,
                "Largest n", last.size(),
                "Measured comparisons", last.measured(),
                "Theoretical value", last.theoretical()),
            "chart\nseries=2\npoints="
                + sizes.length
                + "\nnames=Measured comparisons|Theoretical "
                + subject.theory()
                + "\nxlabel=Input size n\nseed="
                + seed);
    return new AlgorithmRun(
        List.of(chart),
        subject + ": measured " + last.measured() + " comparisons at n=" + last.size());
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
    String[] values = input.trim().split("\\s*,\\s*");
    if (values.length != 3) throw new IllegalArgumentException("Use trials,array size,random seed");
    int trials = Integer.parseInt(values[0]);
    int size = Integer.parseInt(values[1]);
    long seed = Long.parseLong(values[2]);
    AnalysisExperiments.QuickSortExperiment experiment =
        AnalysisExperiments.quickSort(trials, size, seed);
    List<Integer> observations = new ArrayList<>(trials * 2);
    Arrays.stream(experiment.deterministicComparisons())
        .forEach(value -> observations.add(Math.toIntExact(value)));
    Arrays.stream(experiment.randomizedComparisons())
        .forEach(value -> observations.add(Math.toIntExact(value)));
    List<String> trialLabels =
        java.util.stream.IntStream.rangeClosed(1, trials).mapToObj(Integer::toString).toList();
    AlgorithmStep chart =
        new AlgorithmStep(
            "Deterministic vs randomized pivot comparisons",
            "parse experiment\n"
                + "generate base array\n"
                + "run both strategies\n"
                + "summarize distribution\n"
                + "plot trials",
            4,
            CHART,
            observations,
            trialLabels,
            Set.of(),
            Set.of(),
            Set.of(),
            List.of(),
            Map.of(
                "Trials",
                trials,
                "Array size",
                size,
                "Deterministic average",
                experiment.deterministic().average(),
                "Randomized average",
                experiment.randomized().average()),
            "chart\nseries=2\npoints="
                + trials
                + "\nnames=Right-boundary pivot|Randomized pivot\nxlabel=Trial\nseed="
                + seed);
    List<String> summary =
        new ArrayList<>(List.of("Pivot Strategy", "Minimum", "Maximum", "Average", "Median"));
    addSummary(summary, "Right boundary", experiment.deterministic());
    addSummary(summary, "Randomized", experiment.randomized());
    AlgorithmStep table =
        new AlgorithmStep(
            "Comparison-count distribution",
            "parse experiment\n"
                + "generate base array\n"
                + "run both strategies\n"
                + "summarize distribution\n"
                + "plot trials",
            3,
            TABLE,
            List.of(),
            summary,
            rangeSet(5, summary.size() - 1),
            Set.of(),
            Set.of(),
            List.of(),
            Map.of("Trials", trials, "Array size", size, "Seed", seed),
            "columns=5\nBoth strategies receive the exact same seeded base array. Counts exclude"
                + " visualization and wall-clock overhead.");
    return new AlgorithmRun(
        List.of(chart, table),
        "Deterministic average "
            + String.format("%.2f", experiment.deterministic().average())
            + "; randomized average "
            + String.format("%.2f", experiment.randomized().average()));
  }

  private static void addSummary(
      List<String> cells, String name, AnalysisExperiments.Summary summary) {
    cells.add(name);
    cells.add(Long.toString(summary.minimum()));
    cells.add(Long.toString(summary.maximum()));
    cells.add(String.format("%.2f", summary.average()));
    cells.add(String.format("%.2f", summary.median()));
  }
}
