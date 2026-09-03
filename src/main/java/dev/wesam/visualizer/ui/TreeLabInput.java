package dev.wesam.visualizer.ui;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/** Builds the bounded operation history consumed by the persistent tree demonstrations. */
public final class TreeLabInput {
  public enum Operation {
    INSERT,
    SEARCH,
    DELETE
  }

  private static final Set<String> SUPPORTED =
      Set.of("Binary Search Tree", "AVL Tree", "Red-Black Tree", "B-Tree");

  private TreeLabInput() {}

  public static boolean supports(String algorithmName) {
    return SUPPORTED.contains(algorithmName);
  }

  public static String append(String history, Operation operation, int key) {
    if (key < 0) throw new IllegalArgumentException("Tree Lab keys must be zero or greater");
    String clean = history == null ? "" : history.trim();
    int count = clean.isEmpty() ? 0 : clean.split(",").length;
    if (count >= 100) throw new IllegalArgumentException("Tree Lab is limited to 100 operations");
    String token =
        switch (operation) {
          case INSERT -> Integer.toString(key);
          case SEARCH -> "?" + key;
          case DELETE -> "-" + key;
        };
    return clean.isEmpty() ? token : clean + "," + token;
  }

  public static String randomTree(Random random, int count) {
    if (count < 1 || count > 100)
      throw new IllegalArgumentException("Random tree size must be between 1 and 100");
    LinkedHashSet<Integer> keys = new LinkedHashSet<>();
    while (keys.size() < count) keys.add(random.nextInt(100));
    return keys.stream()
        .map(String::valueOf)
        .reduce((left, right) -> left + "," + right)
        .orElse("");
  }
}
