package dev.wesam.visualizer.algorithms;

import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class SortAlgorithms {
  private SortAlgorithms() {}

  public enum Kind {
    BUBBLE,
    SELECTION,
    INSERTION,
    MERGE,
    QUICK,
    RANDOMIZED_QUICK,
    HEAP,
    COUNTING,
    RADIX
  }

  public static int[] sort(int[] input, Kind kind) {
    return run(input, kind, 42L).values();
  }

  public static SortResult run(int[] input, Kind kind, long seed) {
    int[] values = input.clone();
    Recorder r = new Recorder(values);
    switch (kind) {
      case BUBBLE -> bubble(values, r);
      case SELECTION -> selection(values, r);
      case INSERTION -> insertion(values, r);
      case MERGE -> mergeSort(values, 0, values.length - 1, r, 0);
      case QUICK -> quickSort(values, 0, values.length - 1, r, null, 0);
      case RANDOMIZED_QUICK -> quickSort(values, 0, values.length - 1, r, new Random(seed), 0);
      case HEAP -> heapSort(values, r);
      case COUNTING -> countingSort(values, r);
      case RADIX -> radixSort(values, r);
    }
    r.frame("Finished — the array is sorted", Set.of(), Set.of(), range(values.length), 0);
    return new SortResult(values, r.steps, r.comparisons, r.swaps, r.writes);
  }

  public static AlgorithmRun visualize(int[] input, Kind kind) {
    SortResult result = run(input, kind, System.nanoTime());
    return new AlgorithmRun(result.steps(), Arrays.toString(result.values()));
  }

  private static void bubble(int[] a, Recorder r) {
    for (int end = a.length - 1; end > 0; end--) {
      boolean changed = false;
      for (int i = 0; i < end; i++) {
        r.compare(i, i + 1, "Compare adjacent values", 1);
        if (a[i] > a[i + 1]) {
          r.swap(i, i + 1, "Swap the out-of-order pair", 2);
          changed = true;
        }
      }
      r.frame(
          "Largest remaining value is in position",
          Set.of(end),
          Set.of(),
          suffix(end, a.length),
          3);
      if (!changed) break;
    }
  }

  private static void selection(int[] a, Recorder r) {
    for (int i = 0; i < a.length; i++) {
      int min = i;
      for (int j = i + 1; j < a.length; j++) {
        r.compare(min, j, "Scan the unsorted suffix for its minimum", 1);
        if (a[j] < a[min]) min = j;
      }
      if (min != i) r.swap(i, min, "Place the minimum at the boundary", 2);
      r.frame("Sorted prefix grows by one", Set.of(i), Set.of(), prefix(i + 1), 3);
    }
  }

  private static void insertion(int[] a, Recorder r) {
    for (int i = 1; i < a.length; i++) {
      int key = a[i];
      int j = i - 1;
      while (j >= 0) {
        r.compare(j, j + 1, "Compare key with sorted prefix", 1);
        if (a[j] <= key) break;
        a[j + 1] = a[j];
        r.writes++;
        r.frame("Shift larger value right", Set.of(j, j + 1), Set.of(i), prefix(i), 2);
        j--;
      }
      a[j + 1] = key;
      r.writes++;
      r.frame("Insert key into its position", Set.of(j + 1), Set.of(), prefix(i + 1), 3);
    }
  }

  private static void mergeSort(int[] a, int lo, int hi, Recorder r, int depth) {
    if (lo >= hi) return;
    int mid = (lo + hi) >>> 1;
    r.maxDepth = Math.max(r.maxDepth, depth);
    r.frame("Divide range [" + lo + ", " + hi + "]", rangeSet(lo, hi), Set.of(mid), Set.of(), 1);
    mergeSort(a, lo, mid, r, depth + 1);
    mergeSort(a, mid + 1, hi, r, depth + 1);
    int[] copy = Arrays.copyOfRange(a, lo, hi + 1);
    int left = 0, right = mid - lo + 1;
    for (int target = lo; target <= hi; target++) {
      if (left <= mid - lo && right < copy.length) r.comparisons++;
      if (left > mid - lo) a[target] = copy[right++];
      else if (right >= copy.length) a[target] = copy[left++];
      else if (copy[left] <= copy[right]) a[target] = copy[left++];
      else a[target] = copy[right++];
      r.writes++;
      r.frame("Merge the two sorted halves", Set.of(target), rangeSet(lo, hi), Set.of(), 3);
    }
  }

  private static void quickSort(int[] a, int lo, int hi, Recorder r, Random random, int depth) {
    if (lo >= hi) return;
    r.maxDepth = Math.max(r.maxDepth, depth);
    if (random != null) {
      int chosen = lo + random.nextInt(hi - lo + 1);
      r.swap(chosen, hi, "Choose a random pivot", 1);
    }
    int pivot = a[hi];
    int boundary = lo;
    r.frame("Pivot is " + pivot, Set.of(hi), rangeSet(lo, hi), Set.of(), 2);
    for (int scan = lo; scan < hi; scan++) {
      r.compare(scan, hi, "Compare value with pivot", 3);
      if (a[scan] <= pivot) r.swap(boundary++, scan, "Move value to the left partition", 4);
    }
    r.swap(boundary, hi, "Put pivot between the partitions", 5);
    quickSort(a, lo, boundary - 1, r, random, depth + 1);
    quickSort(a, boundary + 1, hi, r, random, depth + 1);
  }

  private static void heapSort(int[] a, Recorder r) {
    for (int i = a.length / 2 - 1; i >= 0; i--) siftDown(a, a.length, i, r);
    for (int end = a.length - 1; end > 0; end--) {
      r.swap(0, end, "Move maximum to the sorted suffix", 2);
      siftDown(a, end, 0, r);
      r.frame("Restore max-heap", Set.of(0), rangeSet(0, end - 1), suffix(end, a.length), 3);
    }
  }

  private static void siftDown(int[] a, int size, int root, Recorder r) {
    while (true) {
      int largest = root, left = root * 2 + 1, right = left + 1;
      if (left < size) {
        r.comparisons++;
        if (a[left] > a[largest]) largest = left;
      }
      if (right < size) {
        r.comparisons++;
        if (a[right] > a[largest]) largest = right;
      }
      if (largest == root) return;
      r.swap(root, largest, "Sift the larger child upward", 1);
      root = largest;
    }
  }

  private static void countingSort(int[] a, Recorder r) {
    if (a.length == 0) return;
    int min = a[0], max = a[0];
    for (int value : a) {
      min = Math.min(min, value);
      max = Math.max(max, value);
    }
    if ((long) max - min > 100_000)
      throw new IllegalArgumentException("Counting sort range is too large");
    int[] counts = new int[max - min + 1];
    for (int i = 0; i < a.length; i++) {
      counts[a[i] - min]++;
      r.frame("Count occurrence of " + a[i], Set.of(i), Set.of(), Set.of(), 1);
    }
    int target = 0;
    for (int i = 0; i < counts.length; i++) {
      while (counts[i]-- > 0) {
        a[target] = i + min;
        r.writes++;
        r.frame("Write values back in key order", Set.of(target), Set.of(), prefix(target + 1), 2);
        target++;
      }
    }
  }

  private static void radixSort(int[] a, Recorder r) {
    if (a.length == 0) return;
    int[] output = new int[a.length];
    // Flipping the sign bit maps signed integer order to unsigned order.
    for (int shift = 0; shift < Integer.SIZE; shift += Byte.SIZE) {
      int[] counts = new int[256];
      for (int value : a) counts[((value ^ Integer.MIN_VALUE) >>> shift) & 0xff]++;
      for (int i = 1; i < counts.length; i++) counts[i] += counts[i - 1];
      for (int i = a.length - 1; i >= 0; i--) {
        int digit = ((a[i] ^ Integer.MIN_VALUE) >>> shift) & 0xff;
        output[--counts[digit]] = a[i];
      }
      System.arraycopy(output, 0, a, 0, a.length);
      r.writes += a.length;
      r.frame(
          "Stable sort by byte " + (shift / Byte.SIZE + 1), range(a.length), Set.of(), Set.of(), 1);
    }
  }

  private static Set<Integer> range(int length) {
    return rangeSet(0, length - 1);
  }

  private static Set<Integer> prefix(int end) {
    return rangeSet(0, end - 1);
  }

  private static Set<Integer> suffix(int start, int end) {
    return rangeSet(start, end - 1);
  }

  private static Set<Integer> rangeSet(int start, int end) {
    java.util.LinkedHashSet<Integer> values = new java.util.LinkedHashSet<>();
    for (int i = Math.max(0, start); i <= end; i++) values.add(i);
    return values;
  }

  public record SortResult(
      int[] values, List<AlgorithmStep> steps, long comparisons, long swaps, long writes) {
    public SortResult {
      values = values.clone();
      steps = List.copyOf(steps);
    }
  }

  private static final class Recorder {
    private final int[] values;
    private final List<AlgorithmStep> steps = new ArrayList<>();
    private long comparisons, swaps, writes, maxDepth;

    private Recorder(int[] values) {
      this.values = values;
      frame("Initial array", Set.of(), Set.of(), Set.of(), 0);
    }

    private void compare(int a, int b, String message, int line) {
      comparisons++;
      frame(message, indexSet(a, b), Set.of(), Set.of(), line);
    }

    private void swap(int a, int b, String message, int line) {
      if (a != b) {
        int temp = values[a];
        values[a] = values[b];
        values[b] = temp;
        swaps++;
        writes += 2;
      }
      frame(message, indexSet(a, b), Set.of(), Set.of(), line);
    }

    private Set<Integer> indexSet(int a, int b) {
      return a == b ? Set.of(a) : Set.of(a, b);
    }

    private void frame(
        String message,
        Set<Integer> active,
        Set<Integer> secondary,
        Set<Integer> complete,
        int line) {
      Map<String, Number> stats = new LinkedHashMap<>();
      stats.put("Comparisons", comparisons);
      stats.put("Swaps", swaps);
      stats.put("Writes", writes);
      stats.put("Recursion depth", maxDepth);
      steps.add(
          new AlgorithmStep(
              message,
              "1  choose current range / item\n"
                  + "2  compare or partition\n"
                  + "3  write or swap\n"
                  + "4  grow the solved region",
              line,
              AlgorithmStep.VisualKind.ARRAY,
              Arrays.stream(values).boxed().toList(),
              List.of(),
              active,
              secondary,
              complete,
              List.of(),
              stats,
              ""));
    }
  }
}
