package dev.wesam.visualizer.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** One immutable, logical frame of an algorithm execution. */
public record AlgorithmStep(
    String message,
    String pseudocode,
    int activeLine,
    VisualKind kind,
    List<Integer> values,
    List<String> labels,
    Set<Integer> active,
    Set<Integer> secondary,
    Set<Integer> complete,
    List<VisualEdge> edges,
    Map<String, Number> statistics,
    String details) {

  public AlgorithmStep {
    values = values == null ? List.of() : List.copyOf(values);
    labels = labels == null ? List.of() : List.copyOf(labels);
    active = active == null ? Set.of() : Set.copyOf(active);
    secondary = secondary == null ? Set.of() : Set.copyOf(secondary);
    complete = complete == null ? Set.of() : Set.copyOf(complete);
    edges = edges == null ? List.of() : List.copyOf(edges);
    statistics = statistics == null ? Map.of() : Map.copyOf(statistics);
    details = details == null ? "" : details;
  }

  public static AlgorithmStep text(String message, String details, Map<String, Number> statistics) {
    return new AlgorithmStep(
        message,
        "",
        -1,
        VisualKind.TEXT,
        List.of(),
        List.of(),
        Set.of(),
        Set.of(),
        Set.of(),
        List.of(),
        statistics,
        details);
  }

  public enum VisualKind {
    ARRAY,
    TEXT,
    GRAPH,
    TABLE,
    TREE,
    GRID,
    CHART,
    SETS
  }

  public record VisualEdge(int from, int to, int weight, boolean directed, String label) {}
}
