package dev.wesam.visualizer.catalog;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class AlgorithmCatalogTest {
  @Test
  void catalogCoversAllCategoriesAndEveryDefaultRunWorks() {
    var catalog = AlgorithmCatalog.create();
    assertTrue(catalog.size() >= 55, "broad algorithm coverage");
    Set<String> categories =
        catalog.stream().map(AlgorithmDemo::category).collect(Collectors.toSet());
    assertEquals(
        Set.of(
            "Sorting",
            "Searching & Selection",
            "Strings & Hashing",
            "Graph Algorithms",
            "Trees",
            "Heaps & Advanced Structures",
            "Dynamic Programming & Optimization",
            "Divide & Conquer / Matrix",
            "Algorithm Analysis"),
        categories);
    for (AlgorithmDemo demo : catalog) {
      var run = assertDoesNotThrow(() -> demo.runner().apply(demo.defaultInput()), demo.name());
      assertNotNull(run.result(), demo.name());
      assertFalse(run.steps().isEmpty(), demo.name() + " should visualize at least one step");
    }
  }
}
