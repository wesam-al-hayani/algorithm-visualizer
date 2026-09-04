package dev.wesam.visualizer.catalog;

import java.util.ArrayList;
import java.util.List;

/** Builds the complete catalog in the order shown by the application. */
public final class AlgorithmCatalog {
  private AlgorithmCatalog() {}

  public static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    demos.addAll(SortingCatalog.create());
    demos.addAll(SearchingCatalog.create());
    demos.addAll(StringHashCatalog.create());
    demos.addAll(GraphCatalog.create());
    demos.addAll(MazeCatalog.create());
    demos.addAll(TreeCatalog.create());
    demos.addAll(HeapCatalog.create());
    demos.addAll(OptimizationCatalog.create());
    demos.addAll(MatrixCatalog.create());
    demos.addAll(AnalysisCatalog.create());
    return List.copyOf(demos);
  }
}
