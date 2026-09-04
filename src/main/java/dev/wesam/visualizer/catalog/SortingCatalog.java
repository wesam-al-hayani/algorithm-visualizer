package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;

import dev.wesam.visualizer.algorithms.SortAlgorithms;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class SortingCatalog {
  private SortingCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    for (SortAlgorithms.Kind kind : SortAlgorithms.Kind.values()) {
      String name = title(kind.name());
      demos.add(
          demo(
              "Sorting",
              name,
              name
                  + " rearranges values into nondecreasing order. Colored bars show the active"
                  + " comparison, partition, and finished region.",
              SortAlgorithms.pseudocode(kind),
              sortTime(kind),
              kind == SortAlgorithms.Kind.MERGE ? "O(n)" : "O(1)–O(n)",
              "Comma-separated integers",
              "38,12,27,6,19,44,3,31",
              input -> SortAlgorithms.visualize(numbersLimited(input, 80, "sorting"), kind)));
    }
    demos.add(
        demo(
            "Sorting",
            "Sorting Compare Mode",
            "Runs two selected sorting algorithms on the exact same initial array with one"
                + " synchronized playback timeline.",
            "parse two distinct sorting algorithms and one shared array\n"
                + "run both instrumented algorithms on clones of that array\n"
                + "advance both visual traces one logical frame at a time\n"
                + "keep a finished algorithm visible while the other continues\n"
                + "compare comparisons, swaps, writes, and visualization steps",
            "Depends on selected algorithms",
            "O(n) plus selected algorithms",
            "ALGORITHM,ALGORITHM ; comma-separated integers",
            "MERGE,QUICK ; 38,12,27,6,19,44,3,31",
            input -> sortingComparison(input, 2, 2)));
    demos.add(
        demo(
            "Sorting",
            "Sorting Race",
            "Animates two to six selected sorting algorithms on identical input, then reports"
                + " operation counts without presenting animation time as a benchmark.",
            "select two to six distinct sorting algorithms\n"
                + "clone the exact same initial array for every algorithm\n"
                + "advance every visual trace on one synchronized timeline\n"
                + "leave completed algorithms visible while others continue\n"
                + "rank only comparisons, swaps, writes, and visualization steps",
            "Depends on selected algorithms",
            "O(kn) plus selected algorithms",
            "2–6 algorithm names ; comma-separated integers",
            "BUBBLE,INSERTION,MERGE,QUICK,HEAP ; 38,12,27,6,19,44,3,31",
            input -> sortingComparison(input, 2, 6)));
    return List.copyOf(demos);
  }

  static AlgorithmRun sortingComparison(String input, int minimum, int maximum) {
    String[] sections = input.split(";", 2);
    if (sections.length != 2)
      throw new IllegalArgumentException("Use algorithm names ; comma-separated integers");
    List<SortAlgorithms.Kind> kinds = parseKinds(sections[0]);
    if (kinds.size() < minimum || kinds.size() > maximum)
      throw new IllegalArgumentException("Select " + minimum + " to " + maximum + " algorithms");
    int[] initial = numbersLimited(sections[1], 80, "sorting comparison");
    List<SortAlgorithms.SortResult> results =
        kinds.stream().map(kind -> SortAlgorithms.run(initial, kind, 42L)).toList();
    int visualFrames = results.stream().mapToInt(result -> result.steps().size()).max().orElse(0);
    List<AlgorithmStep> steps = new ArrayList<>();
    for (int frame = 0; frame < visualFrames; frame++)
      steps.add(comparisonFrame(kinds, results, initial.length, frame));
    steps.add(resultTable(kinds, results));
    boolean agree =
        results.stream()
            .allMatch(result -> Arrays.equals(results.get(0).values(), result.values()));
    return new AlgorithmRun(
        steps,
        agree
            ? kinds.size() + " algorithms produced " + Arrays.toString(results.get(0).values())
            : "Result mismatch detected between selected sorting algorithms");
  }

  private static List<SortAlgorithms.Kind> parseKinds(String input) {
    Set<SortAlgorithms.Kind> kinds = new LinkedHashSet<>();
    for (String token : input.trim().split("\\s*,\\s*")) {
      String normalized = token.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
      try {
        if (!kinds.add(SortAlgorithms.Kind.valueOf(normalized)))
          throw new IllegalArgumentException("Select each sorting algorithm only once");
      } catch (IllegalArgumentException exception) {
        if (exception.getMessage() != null && exception.getMessage().startsWith("Select each"))
          throw exception;
        throw new IllegalArgumentException("Unknown sorting algorithm: " + token, exception);
      }
    }
    return List.copyOf(kinds);
  }

  private static AlgorithmStep comparisonFrame(
      List<SortAlgorithms.Kind> kinds,
      List<SortAlgorithms.SortResult> results,
      int length,
      int frame) {
    List<Integer> values = new ArrayList<>(kinds.size() * length);
    Set<Integer> active = new LinkedHashSet<>(),
        secondary = new LinkedHashSet<>(),
        complete = new LinkedHashSet<>();
    Map<String, Number> statistics = new LinkedHashMap<>();
    List<String> messages = new ArrayList<>();
    for (int series = 0; series < kinds.size(); series++) {
      SortAlgorithms.SortResult result = results.get(series);
      AlgorithmStep source = result.steps().get(Math.min(frame, result.steps().size() - 1));
      values.addAll(source.values());
      int offset = series * length;
      source.active().forEach(index -> active.add(offset + index));
      source.secondary().forEach(index -> secondary.add(offset + index));
      source.complete().forEach(index -> complete.add(offset + index));
      String name = title(kinds.get(series).name());
      messages.add(name + ": " + source.message());
      statistics.put(name + " comparisons", source.statistics().getOrDefault("Comparisons", 0));
      statistics.put(name + " swaps", source.statistics().getOrDefault("Swaps", 0));
      statistics.put(name + " writes", source.statistics().getOrDefault("Writes", 0));
      statistics.put(name + " step", Math.min(frame + 1, result.steps().size()));
    }
    return new AlgorithmStep(
        "Synchronized step " + (frame + 1),
        "prepare shared input\n"
            + "run instrumented sorts\n"
            + "advance synchronized frames\n"
            + "hold completed traces\n"
            + "compare metrics",
        2,
        AlgorithmStep.VisualKind.ARRAY,
        values,
        List.of(),
        active,
        secondary,
        complete,
        List.of(),
        statistics,
        "comparison-arrays\nseries="
            + kinds.size()
            + "\nlength="
            + length
            + "\nnames="
            + kinds.stream()
                .map(kind -> title(kind.name()))
                .collect(java.util.stream.Collectors.joining("|"))
            + "\n"
            + String.join("\n", messages));
  }

  private static AlgorithmStep resultTable(
      List<SortAlgorithms.Kind> kinds, List<SortAlgorithms.SortResult> results) {
    List<String> cells =
        new ArrayList<>(
            List.of("Algorithm", "Comparisons", "Swaps", "Writes", "Visualization Steps"));
    for (int index = 0; index < kinds.size(); index++) {
      SortAlgorithms.SortResult result = results.get(index);
      cells.add(title(kinds.get(index).name()));
      cells.add(Long.toString(result.comparisons()));
      cells.add(Long.toString(result.swaps()));
      cells.add(Long.toString(result.writes()));
      cells.add(Integer.toString(result.steps().size()));
    }
    return new AlgorithmStep(
        "Operation-count results — animation time is not an algorithmic benchmark",
        "prepare shared input\n"
            + "run instrumented sorts\n"
            + "advance synchronized frames\n"
            + "hold completed traces\n"
            + "compare metrics",
        4,
        AlgorithmStep.VisualKind.TABLE,
        List.of(),
        cells,
        rangeSet(5, cells.size() - 1),
        Set.of(),
        Set.of(),
        List.of(),
        Map.of("Algorithms", kinds.size()),
        "columns=5\nCounts come from algorithm operations, not wall-clock animation time.");
  }
}
