package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;

import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import dev.wesam.visualizer.structures.BinaryHeap;
import dev.wesam.visualizer.structures.BinomialHeap;
import dev.wesam.visualizer.structures.FibonacciHeap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class HeapCatalog {
  private HeapCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    demos.add(
        demo(
            "Heaps & Advanced Structures",
            "Binary Min Heap",
            "Array-backed complete tree maintaining the minimum at its root.",
            "insert at end and sift up\nextract root and sift down",
            "O(log n) per update",
            "O(n)",
            "Values",
            "9,3,7,1,6,2,8",
            input -> binaryHeap(input, BinaryHeap.Type.MIN, false)));
    demos.add(
        demo(
            "Heaps & Advanced Structures",
            "Binary Max Heap",
            "Array-backed complete tree maintaining the maximum at its root.",
            "insert at end and sift up\nextract root and sift down",
            "O(log n) per update",
            "O(n)",
            "Values",
            "9,3,7,1,6,2,8",
            input -> binaryHeap(input, BinaryHeap.Type.MAX, false)));
    demos.add(
        demo(
            "Heaps & Advanced Structures",
            "Priority Queue",
            "Uses a binary min-heap to insert priorities and repeatedly extract the smallest.",
            "insert priorities\npeek minimum\nextract in priority order",
            "O(log n) per update",
            "O(n)",
            "Priorities",
            "7,2,9,1,5,3",
            input -> binaryHeap(input, BinaryHeap.Type.MIN, true)));
    demos.add(
        demo(
            "Heaps & Advanced Structures",
            "Binomial Heap",
            "A forest with at most one binomial tree of each degree; union links equal-degree"
                + " roots.",
            "merge root lists by degree\nlink equal-degree trees\nextract minimum root",
            "O(log n) update",
            "O(n)",
            "Values",
            "12,7,25,3,18,1,9",
            HeapCatalog::binomialHeap));
    demos.add(
        demo(
            "Heaps & Advanced Structures",
            "Fibonacci Heap",
            "Lazy root-list insertion and consolidation on extract-min; decrease-key cuts violating"
                + " children.",
            "insert into root list\nextract minimum and consolidate\ndecrease key, cut, cascade",
            "Amortized O(1) insert/decrease; O(log n) extract",
            "O(n)",
            "Values",
            "12,7,25,3,18,1,9",
            HeapCatalog::fibonacciHeap));
    return List.copyOf(demos);
  }

  static AlgorithmRun binaryHeap(String input, BinaryHeap.Type type, boolean drain) {
    BinaryHeap heap = new BinaryHeap(type);
    List<AlgorithmStep> s = new ArrayList<>();
    int[] source = numbersLimited(input, 100, "heap");
    if (type == BinaryHeap.Type.MAX && !drain) {
      heap.heapify(Arrays.stream(source).boxed().toList());
      s.add(
          treeStep(
              "Bottom-up heapify",
              heap.array(),
              Set.of(),
              Map.of("Heap size", heap.size()),
              "Array representation: " + heap.array()));
    } else
      for (int value : source) {
        heap.insert(value);
        s.add(
            treeStep(
                "Insert " + value + " and sift up",
                heap.array(),
                Set.of(value),
                Map.of("Heap size", heap.size()),
                "Array representation: " + heap.array()));
      }
    if (!drain && !heap.isEmpty()) {
      int index = heap.size() - 1, newPriority = type == BinaryHeap.Type.MIN ? -50 : 150;
      heap.changePriority(index, newPriority);
      s.add(
          treeStep(
              "Change priority at index " + index + " to " + newPriority,
              heap.array(),
              Set.of(newPriority),
              Map.of("Heap size", heap.size()),
              "Array representation: " + heap.array()));
    }
    if (drain)
      while (!heap.isEmpty()) {
        int v = heap.extract();
        s.add(
            treeStep(
                "Extract priority " + v + " and sift down",
                heap.array(),
                Set.of(),
                Map.of("Heap size", heap.size()),
                "Extracted: " + v + "\nArray representation: " + heap.array()));
      }
    return new AlgorithmRun(
        s, drain ? "Priority queue drained in sorted order" : "Heap array: " + heap.array());
  }

  static AlgorithmRun binomialHeap(String input) {
    BinomialHeap heap = new BinomialHeap();
    List<AlgorithmStep> s = new ArrayList<>();
    for (int v : numbersLimited(input, 100, "binomial heap")) {
      heap.insert(v);
      s.add(
          treeStep(
              "Insert " + v + "; link trees of equal degree",
              heap.rootDegrees(),
              Set.of(),
              Map.of("Size", heap.size(), "Root trees", heap.rootDegrees().size()),
              "Root degrees: " + heap.rootDegrees() + "; minimum: " + heap.findMinimum()));
    }
    int min = heap.extractMinimum();
    s.add(
        treeStep(
            "Extract minimum " + min + " and union child forest",
            heap.rootDegrees(),
            Set.of(),
            Map.of("Size", heap.size()),
            "Invariant: " + heap.invariantHolds()));
    return new AlgorithmRun(s, "Extracted " + min + "; remaining size " + heap.size());
  }

  static AlgorithmRun fibonacciHeap(String input) {
    FibonacciHeap heap = new FibonacciHeap();
    List<AlgorithmStep> s = new ArrayList<>();
    FibonacciHeap.Node decrease = null;
    for (int v : numbersLimited(input, 100, "Fibonacci heap")) {
      FibonacciHeap.Node node = heap.insert(v);
      if (decrease == null) decrease = node;
      s.add(
          treeStep(
              "Insert " + v + " into circular root list",
              heap.rootKeys(),
              Set.of(v),
              Map.of("Size", heap.size(), "Roots", heap.rootKeys().size()),
              "Minimum: " + heap.findMinimum()));
    }
    int min = heap.extractMinimum();
    s.add(
        treeStep(
            "Extract " + min + " and consolidate equal-degree roots",
            heap.rootKeys(),
            Set.of(),
            Map.of("Size", heap.size(), "Roots", heap.rootKeys().size()),
            "Invariant: " + heap.invariantHolds()));
    if (decrease != null && decrease.key != min) {
      int lowered = heap.findMinimum() - 5;
      heap.decreaseKey(decrease, lowered);
      s.add(
          treeStep(
              "Decrease key and cut if heap order is violated",
              heap.rootKeys(),
              Set.of(lowered),
              Map.of("Size", heap.size(), "Roots", heap.rootKeys().size()),
              "Minimum pointer: " + heap.findMinimum() + "; invariant: " + heap.invariantHolds()));
    }
    return new AlgorithmRun(s, "Extracted " + min + "; new minimum " + heap.findMinimum());
  }
}
