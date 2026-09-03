package dev.wesam.visualizer.algorithms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class UnionFindRandomizedTest {
  @Test
  void randomizedUnionsAndFindsMatchASimpleComponentModel() {
    Random random = new Random(0xD15C01A7);
    for (int trial = 0; trial < 80; trial++) {
      int size = 2 + random.nextInt(80);
      UnionFind sets = new UnionFind(size);
      int[] component = new int[size];
      for (int i = 0; i < size; i++) component[i] = i;
      for (int operation = 0; operation < 300; operation++) {
        int first = random.nextInt(size), second = random.nextInt(size);
        boolean different = component[first] != component[second];
        assertEquals(different, sets.union(first, second));
        if (different) {
          int replaced = component[second], replacement = component[first];
          for (int i = 0; i < size; i++) if (component[i] == replaced) component[i] = replacement;
        }
        int a = random.nextInt(size), b = random.nextInt(size);
        assertEquals(component[a] == component[b], sets.connected(a, b));
      }
      int[] parents = sets.parents();
      int[] ranks = sets.ranks();
      assertEquals(size, parents.length);
      assertEquals(size, ranks.length);
      for (int i = 0; i < size; i++) {
        assertTrue(parents[i] >= 0 && parents[i] < size);
        assertTrue(ranks[i] >= 0);
      }
    }
  }

  @Test
  void invalidSizesAndItemsAreRejected() {
    assertEquals(0, new UnionFind(0).parents().length);
    assertThrows(IllegalArgumentException.class, () -> new UnionFind(-1));
    UnionFind sets = new UnionFind(3);
    assertThrows(IndexOutOfBoundsException.class, () -> sets.find(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> sets.find(3));
  }
}
