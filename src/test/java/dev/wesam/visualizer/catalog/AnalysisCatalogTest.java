package dev.wesam.visualizer.catalog;

import static org.junit.jupiter.api.Assertions.*;

import dev.wesam.visualizer.model.AlgorithmStep;
import org.junit.jupiter.api.Test;

class AnalysisCatalogTest {
  @Test
  void complexityLabProducesAJavaFxChartFrame() {
    var run = AnalysisCatalog.experimentalComplexity("MERGE_SORT ; 10,25,50,100,250 ; 77");
    assertEquals(AlgorithmStep.VisualKind.CHART, run.steps().get(0).kind());
    assertTrue(run.steps().get(0).details().contains("Measured comparisons|Theoretical"));
  }

  @Test
  void quickSortLabProvidesChartAndCompleteDistributionTable() {
    var run = AnalysisCatalog.randomizedExperiment("100,75,2026");
    assertEquals(AlgorithmStep.VisualKind.CHART, run.steps().get(0).kind());
    assertEquals(AlgorithmStep.VisualKind.TABLE, run.steps().get(1).kind());
    assertTrue(run.steps().get(1).labels().contains("Minimum"));
    assertTrue(run.steps().get(1).labels().contains("Median"));
    assertThrows(
        IllegalArgumentException.class, () -> AnalysisCatalog.randomizedExperiment("100,75"));
  }
}
