package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;

import dev.wesam.visualizer.algorithms.SearchAlgorithms;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class SearchingCatalog {
  private SearchingCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    demos.add(
        demo(
            "Searching & Selection",
            "Linear Search",
            "Checks entries from left to right until the target is found.",
            "for each index i\n  if values[i] = target return i\nreturn not found",
            "O(n)",
            "O(1)",
            "values ; target",
            "8,3,6,2,9,5 ; 9",
            SearchingCatalog::linearSearch));
    demos.add(
        demo(
            "Searching & Selection",
            "Binary Search",
            "Repeatedly halves a sorted search interval.",
            "while low ≤ high\n  middle ← (low + high) / 2\n  keep the half containing target",
            "O(log n)",
            "O(1)",
            "sorted values ; target",
            "2,5,8,12,16,23,38,56 ; 23",
            SearchingCatalog::binarySearch));
    demos.add(
        demo(
            "Searching & Selection",
            "Quickselect",
            "Partitions like quicksort but continues only in the side containing rank k.",
            "partition around pivot\nif pivot = k return\nselect left or right side",
            "Average O(n), worst O(n²)",
            "O(1)",
            "values ; zero-based k",
            "9,1,8,2,7,3,6,4,5 ; 4",
            input -> selection(input, false)));
    demos.add(
        demo(
            "Searching & Selection",
            "Median of Medians",
            "Deterministic selection chooses a robust pivot from medians of groups of five.",
            "split into groups of five\nselect median of group medians\npartition and recurse",
            "O(n)",
            "O(n)",
            "values ; zero-based k",
            "14,3,9,7,1,12,8,5,2,10,6,11,4,13 ; 6",
            input -> selection(input, true)));
    return List.copyOf(demos);
  }

  static AlgorithmRun linearSearch(String input) {
    Parts p = valuesAndParameter(input);
    List<AlgorithmStep> s = new ArrayList<>();
    int found = -1;
    for (int i = 0; i < p.values.length; i++) {
      if (p.values[i] == p.parameter) found = i;
      s.add(
          arrayStep(
              "Compare index " + i + " with target " + p.parameter,
              p.values,
              Set.of(i),
              Set.of(),
              prefix(i),
              Map.of("Comparisons", i + 1),
              "Target: " + p.parameter));
      if (found >= 0) break;
    }
    return new AlgorithmRun(s, found < 0 ? "Target not found" : "Found at index " + found);
  }

  static AlgorithmRun binarySearch(String input) {
    Parts p = valuesAndParameter(input);
    int[] a = p.values;
    List<AlgorithmStep> s = new ArrayList<>();
    int low = 0, high = a.length - 1, found = -1, comparisons = 0;
    while (low <= high) {
      int mid = (low + high) >>> 1;
      comparisons++;
      s.add(
          arrayStep(
              "Inspect middle index " + mid,
              a,
              Set.of(mid),
              rangeSet(low, high),
              outside(low, high, a.length),
              Map.of("Comparisons", comparisons, "Interval size", high - low + 1),
              "Target: " + p.parameter));
      if (a[mid] == p.parameter) {
        found = mid;
        break;
      }
      if (a[mid] < p.parameter) low = mid + 1;
      else high = mid - 1;
    }
    return new AlgorithmRun(s, found < 0 ? "Target not found" : "Found at index " + found);
  }

  static AlgorithmRun selection(String input, boolean deterministic) {
    Parts p = valuesAndParameter(input);
    int result =
        deterministic
            ? SearchAlgorithms.medianOfMediansSelect(p.values, p.parameter)
            : SearchAlgorithms.quickselect(p.values, p.parameter);
    List<AlgorithmStep> s =
        List.of(
            arrayStep(
                "Partitioning identifies rank " + p.parameter,
                p.values,
                Set.of(p.parameter),
                Set.of(),
                Set.of(),
                Map.of("Requested rank", p.parameter),
                "Selected value: " + result));
    return new AlgorithmRun(s, "Rank " + p.parameter + " contains " + result);
  }
}
