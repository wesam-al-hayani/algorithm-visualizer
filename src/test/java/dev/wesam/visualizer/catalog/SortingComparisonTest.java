package dev.wesam.visualizer.catalog;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class SortingComparisonTest {
  @Test
  void compareModeUsesTheExactSameInputAndEndsWithMetricsTable() {
    var run = SortingCatalog.sortingComparison("MERGE,QUICK ; 9,3,7,1,5", 2, 2);
    var first = run.steps().get(0);
    assertTrue(first.details().startsWith("comparison-arrays"));
    assertEquals(first.values().subList(0, 5), first.values().subList(5, 10));
    var table = run.steps().get(run.steps().size() - 1);
    assertEquals(
        List.of("Algorithm", "Comparisons", "Swaps", "Writes", "Visualization Steps"),
        table.labels().subList(0, 5));
    assertTrue(run.result().contains("[1, 3, 5, 7, 9]"));
  }

  @Test
  void raceAcceptsTwoToSixUniqueAlgorithmsAndRejectsInvalidSelections() {
    var run =
        SortingCatalog.sortingComparison("BUBBLE,INSERTION,MERGE,QUICK,HEAP,RADIX ; 4,1,3,2", 2, 6);
    assertFalse(run.steps().isEmpty());
    assertThrows(
        IllegalArgumentException.class,
        () -> SortingCatalog.sortingComparison("MERGE ; 3,2,1", 2, 6));
    assertThrows(
        IllegalArgumentException.class,
        () -> SortingCatalog.sortingComparison("MERGE,MERGE ; 3,2,1", 2, 6));
    assertThrows(
        IllegalArgumentException.class,
        () -> SortingCatalog.sortingComparison("MERGE,NOPE ; 3,2,1", 2, 6));
  }
}
