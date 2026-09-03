package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;
import static dev.wesam.visualizer.model.AlgorithmStep.VisualKind.*;

import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import dev.wesam.visualizer.structures.BTree;
import dev.wesam.visualizer.structures.BinarySearchTree;
import dev.wesam.visualizer.structures.RedBlackTree;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class TreeCatalog {
  private TreeCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    for (String traversal : List.of("Preorder", "Inorder", "Postorder", "Level-order"))
      demos.add(
          demo(
              "Trees",
              "Binary Tree " + traversal,
              "Visits an example binary tree in " + traversal.toLowerCase() + " order.",
              traversalPseudocode(traversal),
              "O(n)",
              traversal.equals("Level-order") ? "O(n)" : "O(h)",
              "Values inserted into a BST",
              "8,3,10,1,6,14,4,7,13",
              input -> treeTraversal(input, traversal)));
    demos.add(
        demo(
            "Trees",
            "Binary Search Tree",
            "Inserts keys, searches paths, and performs textbook deletion.",
            "compare with current key\ngo left or right\ninsert/search; delete via successor",
            "Average O(log n), worst O(n)",
            "O(h)",
            "Keys; optional ?search or -delete",
            "8,3,10,1,6,14,4,7,?6,-3",
            TreeCatalog::bst));
    demos.add(
        demo(
            "Trees",
            "Red-Black Tree",
            "Balanced BST insertion repairs red-parent violations using recoloring and rotations.",
            "BST insert red node\nwhile parent is red\n  recolor or rotate\ncolor root black",
            "O(log n)",
            "O(log n)",
            "Keys to insert",
            "41,38,31,12,19,8,50,60",
            TreeCatalog::redBlack));
    demos.add(
        demo(
            "Trees",
            "B-Tree",
            "Multi-key balanced tree insertion splits full nodes on the descent.",
            "if root full, split it\ndescend toward key\nsplit any full child before entering",
            "O(log n)",
            "O(log n)",
            "Keys to insert (minimum degree 2)",
            "10,20,5,6,12,30,7,17,3,4",
            TreeCatalog::bTree));
    return List.copyOf(demos);
  }

  private static String traversalPseudocode(String traversal) {
    return switch (traversal) {
      case "Preorder" -> "visit node\ntraverse left subtree\ntraverse right subtree";
      case "Inorder" -> "traverse left subtree\nvisit node\ntraverse right subtree";
      case "Postorder" -> "traverse left subtree\ntraverse right subtree\nvisit node";
      default -> "enqueue root\nremove and visit queue front\nenqueue its existing children";
    };
  }

  static AlgorithmRun treeTraversal(String input, String traversal) {
    BinarySearchTree tree = makeBst(numbersLimited(input, 100, "tree"));
    List<Integer> order =
        switch (traversal) {
          case "Preorder" -> tree.preorder();
          case "Inorder" -> tree.inorder();
          case "Postorder" -> tree.postorder();
          default -> tree.levelOrder();
        };
    return treeFrames(tree, order, traversal + " traversal: " + order);
  }

  static AlgorithmRun bst(String input) {
    BinarySearchTree tree = new BinarySearchTree();
    List<AlgorithmStep> s = new ArrayList<>();
    String result = "";
    String[] operations = input.split(",");
    if (operations.length > 100)
      throw new IllegalArgumentException("Tree visualization is limited to 100 operations");
    for (String raw : operations) {
      String t = raw.trim();
      if (t.startsWith("?")) {
        int key = Integer.parseInt(t.substring(1));
        result = "Search " + key + ": " + tree.contains(key);
        s.add(
            treeStep(
                "Search for " + key,
                tree.levelOrder(),
                Set.of(key),
                Map.of("Nodes", tree.inorder().size()),
                result));
      } else if (t.startsWith("-")) {
        int key = Integer.parseInt(t.substring(1));
        tree.delete(key);
        result = "Deleted " + key;
        s.add(
            treeStep(
                result,
                tree.levelOrder(),
                Set.of(),
                Map.of("Nodes", tree.inorder().size()),
                "Inorder: " + tree.inorder()));
      } else {
        int key = Integer.parseInt(t);
        tree.insert(key);
        result = "Inserted " + key;
        s.add(
            treeStep(
                result,
                tree.levelOrder(),
                Set.of(key),
                Map.of("Nodes", tree.inorder().size()),
                "Inorder: " + tree.inorder()));
      }
    }
    return new AlgorithmRun(s, result + "; inorder " + tree.inorder());
  }

  static AlgorithmRun redBlack(String input) {
    RedBlackTree tree = new RedBlackTree();
    List<AlgorithmStep> s = new ArrayList<>();
    for (int key : numbersLimited(input, 100, "red-black tree")) {
      tree.insert(key);
      List<RedBlackTree.NodeView> nodes = tree.levelOrder();
      List<Integer> values = nodes.stream().map(RedBlackTree.NodeView::key).toList();
      Set<Integer> red = new LinkedHashSet<>(), black = new LinkedHashSet<>();
      for (int i = 0; i < nodes.size(); i++)
        if (nodes.get(i).color() == RedBlackTree.Color.RED) red.add(i);
        else black.add(i);
      s.add(
          new AlgorithmStep(
              "Insert " + key + "; repair colors/rotations",
              "BST insert red node\nrecolor red uncle or rotate\ncolor root black",
              1,
              TREE,
              values,
              List.of(),
              Set.of(values.indexOf(key)),
              red,
              black,
              List.of(),
              Map.of(
                  "Nodes", values.size(), "Rotations", tree.rotations(), "Red nodes", red.size()),
              "red-black tree\nInvariants: " + tree.invariantsHold()));
    }
    return new AlgorithmRun(
        s, "Inorder: " + tree.inorder() + "; invariants hold: " + tree.invariantsHold());
  }

  static AlgorithmRun bTree(String input) {
    BTree tree = new BTree(2);
    List<AlgorithmStep> s = new ArrayList<>();
    for (int key : numbersLimited(input, 100, "B-tree")) {
      tree.insert(key);
      s.add(
          new AlgorithmStep(
              "Insert " + key + (tree.splits() > 0 ? "; split full nodes as required" : ""),
              "split full root\ndescend and split full child\ninsert key in leaf",
              1,
              TREE,
              tree.inOrder(),
              List.of(),
              Set.of(key),
              Set.of(),
              Set.of(),
              List.of(),
              Map.of("Keys", tree.inOrder().size(), "Splits", tree.splits()),
              "Balanced invariants: " + tree.invariantsHold()));
    }
    return new AlgorithmRun(s, "Sorted keys: " + tree.inOrder());
  }
}
