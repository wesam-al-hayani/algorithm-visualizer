package dev.wesam.visualizer.ui;

import static org.junit.jupiter.api.Assertions.*;

import dev.wesam.visualizer.catalog.AlgorithmCatalog;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ExportAndFeedbackTest {
  @Test
  void textAndCsvExportsContainMetadataResultAndStableStatistics() {
    var demo = AlgorithmCatalog.create().get(0);
    var step =
        AlgorithmStep.text(
            "Compare values", "line one\nline two", Map.of("Swaps", 2, "Comparisons", 5));
    String text = ResultExporter.text(demo, "3,2,1", step, "[1, 2, 3]");
    assertTrue(text.contains("Algorithm Lab 2.0.0"));
    assertTrue(text.contains("Comparisons: 5"));
    assertTrue(text.contains("Result: [1, 2, 3]"));
    String csv = ResultExporter.csv(demo, "3,2,1", step, "[1, 2, 3]");
    assertTrue(csv.startsWith("section,name,value"));
    assertTrue(csv.contains("\"statistics\",\"Swaps\",\"2\""));
    assertTrue(csv.contains("\"line one\nline two\""));
    assertEquals("algorithm-lab-bubble", ResultExporter.suggestedBaseName(demo));
  }

  @Test
  void parserFeedbackUsesTheDocumentedHintWithoutLeakingExceptionNames() {
    var demo = AlgorithmCatalog.create().get(0);
    String numeric = InputFeedback.message(new NumberFormatException("secret parser detail"), demo);
    assertTrue(numeric.contains(demo.inputHint()));
    assertTrue(numeric.contains(demo.defaultInput()));
    assertFalse(numeric.contains("NumberFormatException"));
    String constrained =
        InputFeedback.message(new IllegalArgumentException("At most 80 values"), demo);
    assertTrue(constrained.startsWith("At most 80 values"));
    assertFalse(
        InputFeedback.message(new IllegalStateException(), demo).contains("IllegalStateException"));
  }
}
