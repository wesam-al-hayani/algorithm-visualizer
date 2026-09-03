package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;

import dev.wesam.visualizer.algorithms.SortAlgorithms;
import java.util.ArrayList;
import java.util.List;

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
              "choose the active range\ncompare keys\nmove or swap values\ngrow the sorted region",
              sortTime(kind),
              kind == SortAlgorithms.Kind.MERGE ? "O(n)" : "O(1)–O(n)",
              "Comma-separated integers",
              "38,12,27,6,19,44,3,31",
              input -> SortAlgorithms.visualize(numbersLimited(input, 80, "sorting"), kind)));
    }
    return List.copyOf(demos);
  }
}
