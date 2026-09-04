package dev.wesam.visualizer.ui;

import dev.wesam.visualizer.catalog.AlgorithmDemo;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

/** Produces dependency-free text and CSV exports for the currently visible algorithm state. */
public final class ResultExporter {
  private ResultExporter() {}

  public static String text(
      AlgorithmDemo demo, String input, AlgorithmStep step, String finalResult) {
    Objects.requireNonNull(demo, "demo");
    Objects.requireNonNull(step, "step");
    StringBuilder output = new StringBuilder();
    output.append("Algorithm Lab 2.0.0\n");
    output.append("Algorithm: ").append(demo.name()).append('\n');
    output.append("Category: ").append(demo.category()).append('\n');
    output.append("Input: ").append(input).append('\n');
    output.append("Current operation: ").append(step.message()).append('\n');
    output.append("Result: ").append(finalResult).append("\n\nStatistics\n");
    ordered(step.statistics())
        .forEach(
            entry ->
                output.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n'));
    if (!step.details().isBlank()) output.append("\nDetails\n").append(step.details()).append('\n');
    return output.toString();
  }

  public static String csv(
      AlgorithmDemo demo, String input, AlgorithmStep step, String finalResult) {
    Objects.requireNonNull(demo, "demo");
    Objects.requireNonNull(step, "step");
    StringBuilder output = new StringBuilder("section,name,value\n");
    row(output, "metadata", "version", "2.0.0");
    row(output, "metadata", "algorithm", demo.name());
    row(output, "metadata", "category", demo.category());
    row(output, "metadata", "input", input);
    row(output, "state", "operation", step.message());
    row(output, "state", "result", finalResult);
    ordered(step.statistics())
        .forEach(entry -> row(output, "statistics", entry.getKey(), entry.getValue().toString()));
    if (!step.details().isBlank()) row(output, "state", "details", step.details());
    return output.toString();
  }

  public static String suggestedBaseName(AlgorithmDemo demo) {
    String slug =
        demo.name()
            .toLowerCase(java.util.Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("(^-|-$)", "");
    return "algorithm-lab-" + (slug.isBlank() ? "result" : slug);
  }

  private static java.util.List<Map.Entry<String, Number>> ordered(Map<String, Number> values) {
    return values.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).toList();
  }

  private static void row(StringBuilder output, String section, String name, String value) {
    output
        .append(escape(section))
        .append(',')
        .append(escape(name))
        .append(',')
        .append(escape(value))
        .append('\n');
  }

  private static String escape(String value) {
    String safe = value == null ? "" : value;
    return '"' + safe.replace("\"", "\"\"") + '"';
  }
}
