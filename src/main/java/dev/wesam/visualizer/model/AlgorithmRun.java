package dev.wesam.visualizer.model;

import java.util.List;

public record AlgorithmRun(List<AlgorithmStep> steps, String result) {
  public AlgorithmRun {
    steps = List.copyOf(steps);
  }
}
