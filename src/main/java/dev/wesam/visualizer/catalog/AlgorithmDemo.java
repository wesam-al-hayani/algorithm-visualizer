package dev.wesam.visualizer.catalog;

import dev.wesam.visualizer.model.AlgorithmRun;
import java.util.function.Function;

public record AlgorithmDemo(
    String category,
    String name,
    String explanation,
    String pseudocode,
    String timeComplexity,
    String spaceComplexity,
    String inputHint,
    String defaultInput,
    Function<String, AlgorithmRun> runner) {

  @Override
  public String toString() {
    return name;
  }
}
