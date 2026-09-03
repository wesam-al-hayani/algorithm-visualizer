package dev.wesam.visualizer.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class HeapTest {
  @Test
  void binaryMinMaxHeapAndChangePriority() {
    for (BinaryHeap.Type type : BinaryHeap.Type.values()) {
      BinaryHeap heap = new BinaryHeap(type);
      heap.heapify(List.of(7, 2, 9, 1, 5));
      assertTrue(heap.invariantHolds());
      int first = heap.extract();
      assertEquals(type == BinaryHeap.Type.MIN ? 1 : 9, first);
      heap.insert(3);
      heap.changePriority(0, type == BinaryHeap.Type.MIN ? 10 : -10);
      assertTrue(heap.invariantHolds());
    }
  }

  @Test
  void binomialHeapInsertUnionAndExtractSorted() {
    BinomialHeap first = new BinomialHeap(), second = new BinomialHeap();
    for (int v : new int[] {7, 1, 9}) first.insert(v);
    for (int v : new int[] {4, 2, 8}) second.insert(v);
    first.union(second);
    assertTrue(first.invariantHolds());
    assertEquals(0, second.size());
    List<Integer> output = new ArrayList<>();
    while (!first.isEmpty()) {
      output.add(first.extractMinimum());
      assertTrue(first.invariantHolds());
    }
    assertEquals(List.of(1, 2, 4, 7, 8, 9), output);
  }

  @Test
  void fibonacciHeapConsolidatesAndDecreasesKeys() {
    FibonacciHeap heap = new FibonacciHeap();
    FibonacciHeap.Node seven = null;
    for (int v : new int[] {9, 3, 12, 7, 1, 6}) {
      var n = heap.insert(v);
      if (v == 7) seven = n;
    }
    assertEquals(1, heap.extractMinimum());
    assertTrue(heap.invariantHolds());
    heap.decreaseKey(seven, 0);
    assertEquals(0, heap.findMinimum());
    assertTrue(heap.invariantHolds());
    List<Integer> output = new ArrayList<>();
    while (!heap.isEmpty()) {
      output.add(heap.extractMinimum());
      assertTrue(heap.invariantHolds());
    }
    assertEquals(List.of(0, 3, 6, 9, 12), output);
  }
}
