package dev.wesam.visualizer.algorithms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class AnalysisExperimentsTest {
  @Test
  void countOnlySortUsesTheRealImplementationWithoutRetainingFrames() {
    int[] input = new Random(9).ints(100, -50, 51).toArray();
    var visual = SortAlgorithms.run(input, SortAlgorithms.Kind.MERGE, 4);
    var measured = SortAlgorithms.measure(input, SortAlgorithms.Kind.MERGE, 4);
    assertArrayEquals(visual.values(), measured.values());
    assertEquals(visual.comparisons(), measured.comparisons());
    assertEquals(visual.writes(), measured.writes());
    assertTrue(measured.steps().isEmpty());
  }

  @Test
  void complexityExperimentsMeasureEveryRequestedSizeAgainstTheory() {
    int[] sizes = {10, 25, 50, 100, 250, 500};
    for (AnalysisExperiments.Subject subject : AnalysisExperiments.Subject.values()) {
      var result = AnalysisExperiments.complexity(subject, sizes, 2026);
      assertEquals(sizes.length, result.points().length);
      assertEquals(500, result.points()[result.points().length - 1].size());
      assertTrue(result.points()[result.points().length - 1].measured() > 0);
      assertTrue(result.points()[result.points().length - 1].theoretical() > 0);
    }
    assertThrows(
        IllegalArgumentException.class,
        () ->
            AnalysisExperiments.complexity(
                AnalysisExperiments.Subject.BUBBLE_SORT, new int[] {10, 5}, 1));
  }

  @Test
  void quickSortExperimentIsSeededAndSummarizesBothStrategies() {
    var first = AnalysisExperiments.quickSort(80, 60, 12345);
    var second = AnalysisExperiments.quickSort(80, 60, 12345);
    assertArrayEquals(first.baseArray(), second.baseArray());
    assertArrayEquals(first.randomizedComparisons(), second.randomizedComparisons());
    assertEquals(first.deterministic(), second.deterministic());
    assertTrue(first.randomized().minimum() <= first.randomized().median());
    assertTrue(first.randomized().median() <= first.randomized().maximum());
    assertThrows(IllegalArgumentException.class, () -> AnalysisExperiments.quickSort(0, 10, 1));
    assertThrows(
        IllegalArgumentException.class, () -> AnalysisExperiments.quickSort(1_000, 2_000, 1));
  }
}
