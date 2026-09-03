package dev.wesam.visualizer.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class TreeTest {
  @Test
  void binarySearchTreeOperationsAndTraversals() {
    BinarySearchTree tree = new BinarySearchTree();
    for (int v : new int[] {8, 3, 10, 1, 6, 14, 4, 7, 13}) assertTrue(tree.insert(v));
    assertEquals(List.of(1, 3, 4, 6, 7, 8, 10, 13, 14), tree.inorder());
    assertTrue(tree.contains(7));
    assertFalse(tree.contains(9));
    assertTrue(tree.delete(3));
    assertFalse(tree.contains(3));
    assertEquals(8, tree.preorder().size());
    assertEquals(8, tree.postorder().size());
    assertEquals(8, tree.levelOrder().size());
  }

  @Test
  void redBlackPropertiesSurviveManyInsertions() {
    RedBlackTree tree = new RedBlackTree();
    for (int v : new int[] {41, 38, 31, 12, 19, 8, 50, 60, 1, 2, 3, 4}) {
      assertTrue(tree.insert(v));
      assertTrue(tree.invariantsHold());
    }
    assertTrue(tree.contains(19));
    assertFalse(tree.contains(20));
    assertTrue(tree.rotations() > 0);
  }

  @Test
  void avlPerformsEveryRotationShapeAndDeletes() {
    int[][] insertions = {{30, 20, 10}, {10, 20, 30}, {30, 10, 20}, {10, 30, 20}};
    String[] expectedEvents = {"LL", "RR", "LR", "RL"};
    for (int i = 0; i < insertions.length; i++) {
      String expectedEvent = expectedEvents[i];
      AvlTree tree = new AvlTree();
      for (int value : insertions[i]) assertTrue(tree.insert(value));
      assertEquals(20, tree.root().key);
      assertTrue(tree.lastEvents().stream().anyMatch(event -> event.startsWith(expectedEvent)));
      assertTrue(tree.invariantsHold());
      assertEquals(2, tree.height());
      assertTrue(tree.delete(20));
      assertFalse(tree.delete(20));
      assertFalse(tree.contains(20));
      assertTrue(tree.invariantsHold());
    }
  }

  @Test
  void bTreeSplitsAndRemainsBalanced() {
    BTree tree = new BTree(2);
    for (int i = 1; i <= 30; i++) {
      assertTrue(tree.insert(i));
      assertTrue(tree.invariantsHold());
    }
    assertEquals(30, tree.inOrder().size());
    assertTrue(tree.contains(17));
    assertFalse(tree.contains(31));
    assertTrue(tree.splits() > 0);
  }
}
