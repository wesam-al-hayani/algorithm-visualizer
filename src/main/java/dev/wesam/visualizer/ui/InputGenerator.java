package dev.wesam.visualizer.ui;

import dev.wesam.visualizer.algorithms.SortAlgorithms;
import dev.wesam.visualizer.catalog.AlgorithmDemo;
import java.util.Arrays;
import java.util.Random;

/** Generates bounded example inputs without coupling the operation to JavaFX controls. */
public final class InputGenerator {
  private InputGenerator() {}

  public static String generate(AlgorithmDemo demo, String currentInput, Random random) {
    String name = demo.name();
    if (demo.category().equals("Sorting")
        || demo.category().equals("Trees")
        || demo.category().equals("Heaps & Advanced Structures")) {
      return randomCsv(random, 10);
    }
    if (name.equals("Linear Search") || name.equals("Binary Search")) {
      int[] values = randomValues(random, 10);
      if (name.equals("Binary Search"))
        values = SortAlgorithms.sort(values, SortAlgorithms.Kind.MERGE);
      return csv(values) + " ; " + values[random.nextInt(values.length)];
    }
    if (name.equals("Quickselect") || name.equals("Median of Medians"))
      return randomCsv(random, 11) + " ; " + random.nextInt(11);
    if (demo.category().equals("Strings & Hashing")
        && (name.contains("Probing")
            || name.equals("Separate Chaining")
            || name.equals("Double Hashing"))) {
      int[] keys = positiveRandomValues(random, 9);
      return csv(keys) + ",?" + keys[random.nextInt(keys.length)];
    }
    if (name.contains("Knapsack")) return "2,3,4,5,6 ; 3,5,6,8,9 ; 12";
    if (name.startsWith("Grid ")) return GridEditor.randomWalls(currentInput, random);
    return demo.defaultInput();
  }

  private static int[] randomValues(Random random, int count) {
    int[] values = new int[count];
    for (int i = 0; i < count; i++) values[i] = random.nextInt(91) - 20;
    return values;
  }

  private static int[] positiveRandomValues(Random random, int count) {
    int[] values = new int[count];
    for (int i = 0; i < count; i++) values[i] = random.nextInt(90) + 1;
    return values;
  }

  private static String randomCsv(Random random, int count) {
    return csv(randomValues(random, count));
  }

  private static String csv(int[] values) {
    return Arrays.toString(values).replace("[", "").replace("]", "");
  }
}
