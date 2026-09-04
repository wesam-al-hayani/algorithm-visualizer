package dev.wesam.visualizer.algorithms;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/** Repeatable operation-count experiments kept independent from JavaFX and wall-clock timing. */
public final class AnalysisExperiments {
  public enum Subject {
    BUBBLE_SORT("Bubble Sort", "n²"),
    MERGE_SORT("Merge Sort", "n log₂ n"),
    BINARY_SEARCH("Binary Search", "log₂ n + 1");

    private final String label;
    private final String theory;

    Subject(String label, String theory) {
      this.label = label;
      this.theory = theory;
    }

    public String theory() {
      return theory;
    }

    @Override
    public String toString() {
      return label;
    }
  }

  public record ComplexityPoint(int size, long measured, long theoretical) {}

  public record ComplexityResult(Subject subject, ComplexityPoint[] points, long seed) {
    public ComplexityResult {
      points = points.clone();
    }

    @Override
    public ComplexityPoint[] points() {
      return points.clone();
    }
  }

  public record Summary(long minimum, long maximum, double average, double median) {}

  public record QuickSortExperiment(
      int size,
      int trials,
      long seed,
      int[] baseArray,
      long[] deterministicComparisons,
      long[] randomizedComparisons,
      Summary deterministic,
      Summary randomized) {
    public QuickSortExperiment {
      baseArray = baseArray.clone();
      deterministicComparisons = deterministicComparisons.clone();
      randomizedComparisons = randomizedComparisons.clone();
    }

    @Override
    public int[] baseArray() {
      return baseArray.clone();
    }

    @Override
    public long[] deterministicComparisons() {
      return deterministicComparisons.clone();
    }

    @Override
    public long[] randomizedComparisons() {
      return randomizedComparisons.clone();
    }
  }

  private AnalysisExperiments() {}

  public static ComplexityResult complexity(Subject subject, int[] sizes, long seed) {
    Objects.requireNonNull(subject, "subject");
    if (sizes == null || sizes.length < 2 || sizes.length > 12)
      throw new IllegalArgumentException("Choose 2 to 12 input sizes");
    int previous = 0;
    ComplexityPoint[] points = new ComplexityPoint[sizes.length];
    for (int index = 0; index < sizes.length; index++) {
      int size = sizes[index];
      if (size < 2 || size > 2_000 || size <= previous)
        throw new IllegalArgumentException("Sizes must increase from 2 through 2,000");
      previous = size;
      long measured;
      if (subject == Subject.BINARY_SEARCH) {
        measured = binarySearchComparisons(size);
      } else {
        int[] values = shuffledRange(size, new Random(seed + 104_729L * index));
        SortAlgorithms.Kind kind =
            subject == Subject.BUBBLE_SORT ? SortAlgorithms.Kind.BUBBLE : SortAlgorithms.Kind.MERGE;
        measured = SortAlgorithms.measure(values, kind, seed).comparisons();
      }
      long theoretical =
          switch (subject) {
            case BUBBLE_SORT -> (long) size * size;
            case MERGE_SORT -> Math.round(size * log2(size));
            case BINARY_SEARCH -> (long) Math.floor(log2(size)) + 1;
          };
      points[index] = new ComplexityPoint(size, measured, theoretical);
    }
    return new ComplexityResult(subject, points, seed);
  }

  public static QuickSortExperiment quickSort(int trials, int size, long seed) {
    if (trials < 1 || trials > 1_000)
      throw new IllegalArgumentException("Trials must be from 1 through 1,000");
    if (size < 2 || size > 2_000)
      throw new IllegalArgumentException("Array size must be from 2 through 2,000");
    if ((long) trials * size > 500_000)
      throw new IllegalArgumentException("trials × size is limited to 500,000");
    int[] base = shuffledRange(size, new Random(seed));
    long[] deterministic = new long[trials], randomized = new long[trials];
    for (int trial = 0; trial < trials; trial++) {
      deterministic[trial] =
          SortAlgorithms.measure(base, SortAlgorithms.Kind.QUICK, seed).comparisons();
      randomized[trial] =
          SortAlgorithms.measure(
                  base, SortAlgorithms.Kind.RANDOMIZED_QUICK, seed + 7_919L * trial + 17)
              .comparisons();
    }
    return new QuickSortExperiment(
        size,
        trials,
        seed,
        base,
        deterministic,
        randomized,
        summary(deterministic),
        summary(randomized));
  }

  private static long binarySearchComparisons(int size) {
    int low = 0, high = size - 1, comparisons = 0;
    int missingTarget = size;
    while (low <= high) {
      comparisons++;
      int middle = low + (high - low) / 2;
      if (middle < missingTarget) low = middle + 1;
      else high = middle - 1;
    }
    return comparisons;
  }

  private static int[] shuffledRange(int size, Random random) {
    int[] values = new int[size];
    for (int index = 0; index < size; index++) values[index] = index;
    for (int index = size - 1; index > 0; index--) {
      int selected = random.nextInt(index + 1);
      int swap = values[index];
      values[index] = values[selected];
      values[selected] = swap;
    }
    return values;
  }

  private static Summary summary(long[] observations) {
    long[] sorted = observations.clone();
    Arrays.sort(sorted);
    long total = 0;
    for (long observation : sorted) total = Math.addExact(total, observation);
    double median =
        sorted.length % 2 == 1
            ? sorted[sorted.length / 2]
            : (sorted[sorted.length / 2 - 1] + sorted[sorted.length / 2]) / 2.0;
    return new Summary(
        sorted[0], sorted[sorted.length - 1], total / (double) sorted.length, median);
  }

  private static double log2(int value) {
    return Math.log(value) / Math.log(2);
  }
}
