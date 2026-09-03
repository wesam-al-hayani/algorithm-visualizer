package dev.wesam.visualizer.catalog;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class AlgorithmCatalogTest {
  @Test
  void catalogCoversAllCategoriesAndEveryDefaultRunWorks() {
    var catalog = AlgorithmCatalog.create();
    assertEquals(61, catalog.size(), "every documented demo must remain wired");
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
      assertFalse(demo.pseudocode().isBlank(), demo.name() + " needs real pseudocode");
      int lineCount = demo.pseudocode().split("\\R", -1).length;
      run.steps()
          .forEach(
              step -> {
                assertEquals(demo.pseudocode(), step.pseudocode(), demo.name());
                assertTrue(
                    step.activeLine() < lineCount,
                    demo.name() + " active line " + step.activeLine() + " exceeds pseudocode");
              });
    }
  }
}
