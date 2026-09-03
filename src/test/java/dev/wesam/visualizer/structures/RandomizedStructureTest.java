package dev.wesam.visualizer.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RandomizedStructureTest {
  @ParameterizedTest
  @EnumSource(BinaryHeap.Type.class)
  void randomizedBinaryHeapOperationsMatchPriorityQueue(BinaryHeap.Type type) {
    Comparator<Integer> order =
        type == BinaryHeap.Type.MIN ? Comparator.naturalOrder() : Comparator.reverseOrder();
    Random random = new Random(0xB1A0 + type.ordinal());
    for (int trial = 0; trial < 35; trial++) {
      BinaryHeap heap = new BinaryHeap(type);
      PriorityQueue<Integer> reference = new PriorityQueue<>(order);
      for (int operation = 0; operation < 160; operation++) {
        if (reference.isEmpty() || random.nextDouble() < .68) {
          int value = random.nextInt(401) - 200;
          heap.insert(value);
          reference.add(value);
        } else {
          assertEquals(reference.remove(), heap.extract());
        }
        assertEquals(reference.size(), heap.size());
        assertTrue(heap.invariantHolds());
        if (!reference.isEmpty()) assertEquals(reference.peek(), heap.peek());
      }
      while (!reference.isEmpty()) assertEquals(reference.remove(), heap.extract());
      assertTrue(heap.isEmpty());
    }
  }

  @Test
  void randomizedBinomialHeapExtractsInSortedOrder() {
    Random random = new Random(0xB100);
    for (int trial = 0; trial < 40; trial++) {
      BinomialHeap heap = new BinomialHeap();
      List<Integer> expected =
          random.ints(1 + random.nextInt(80), -200, 201).boxed().sorted().toList();
      expected.forEach(heap::insert);
      assertTrue(heap.invariantHolds());
      List<Integer> actual = new ArrayList<>();
      while (!heap.isEmpty()) {
        actual.add(heap.extractMinimum());
        assertTrue(heap.invariantHolds());
      }
      assertEquals(expected, actual);
    }
  }

  @Test
  void randomizedFibonacciHeapExtractsInSortedOrder() {
    Random random = new Random(0xF1B0);
    for (int trial = 0; trial < 40; trial++) {
      FibonacciHeap heap = new FibonacciHeap();
      List<Integer> expected =
          random.ints(1 + random.nextInt(80), -200, 201).boxed().sorted().toList();
      expected.forEach(heap::insert);
      assertTrue(heap.invariantHolds());
      List<Integer> actual = new ArrayList<>();
      while (!heap.isEmpty()) {
        actual.add(heap.extractMinimum());
        assertTrue(heap.invariantHolds());
      }
      assertEquals(expected, actual);
    }
  }

  @Test
  void randomizedBstOperationsMatchTreeSet() {
    Random random = new Random(0xB57);
    for (int trial = 0; trial < 50; trial++) {
      BinarySearchTree tree = new BinarySearchTree();
      TreeSet<Integer> reference = new TreeSet<>();
      for (int operation = 0; operation < 200; operation++) {
        int value = random.nextInt(201) - 100;
        if (random.nextBoolean()) assertEquals(reference.add(value), tree.insert(value));
        else assertEquals(reference.remove(value), tree.delete(value));
        assertEquals(new ArrayList<>(reference), tree.inorder());
        assertEquals(reference.contains(value), tree.contains(value));
      }
    }
  }

  @Test
  void randomizedRedBlackInsertionsPreserveEveryInvariant() {
    Random random = new Random(0x8B7);
    for (int trial = 0; trial < 80; trial++) {
      RedBlackTree tree = new RedBlackTree();
      TreeSet<Integer> expected = new TreeSet<>();
      for (int operation = 0; operation < 120; operation++) {
        int value = random.nextInt(401) - 200;
        assertEquals(expected.add(value), tree.insert(value));
        assertEquals(new ArrayList<>(expected), tree.inorder());
        assertTrue(tree.invariantsHold(), "trial " + trial + ", operation " + operation);
      }
    }
  }

  @Test
  void randomizedAvlOperationsMatchTreeSetAndPreserveEveryInvariant() {
    Random random = new Random(0xA71);
    for (int trial = 0; trial < 70; trial++) {
      AvlTree tree = new AvlTree();
      TreeSet<Integer> expected = new TreeSet<>();
      for (int operation = 0; operation < 220; operation++) {
        int value = random.nextInt(601) - 300;
        if (random.nextBoolean()) assertEquals(expected.add(value), tree.insert(value));
        else assertEquals(expected.remove(value), tree.delete(value));
        assertEquals(new ArrayList<>(expected), tree.inorder());
        assertEquals(expected.contains(value), tree.contains(value));
        assertTrue(tree.invariantsHold(), "trial " + trial + ", operation " + operation);
      }
    }
  }

  @Test
  void randomizedBTreeInsertionsPreserveOrderingAndBalance() {
    Random random = new Random(0xB7EE);
    for (int degree : new int[] {2, 3, 4, 6}) {
      for (int trial = 0; trial < 30; trial++) {
        BTree tree = new BTree(degree);
        TreeSet<Integer> expected = new TreeSet<>();
        for (int operation = 0; operation < 180; operation++) {
          int value = random.nextInt(801) - 400;
          assertEquals(expected.add(value), tree.insert(value));
          assertEquals(new ArrayList<>(expected), tree.inOrder());
          assertTrue(tree.invariantsHold());
        }
      }
    }
  }
}
