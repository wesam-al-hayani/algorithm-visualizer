package dev.wesam.visualizer.structures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/** Height-balanced binary search tree supporting insertion, search, and deletion. */
public final class AvlTree {
  public static final class Node {
    public int key;
    public int height = 1;
    public Node left, right;

    private Node(int key) {
      this.key = key;
    }
  }

  public record NodeView(int key, int height, int balanceFactor) {}

  private Node root;
  private int rotations;
  private final List<String> lastEvents = new ArrayList<>();

  public Node root() {
    return root;
  }

  public int height() {
    return height(root);
  }

  public int rotations() {
    return rotations;
  }

  public List<String> lastEvents() {
    return List.copyOf(lastEvents);
  }

  public boolean contains(int key) {
    Node current = root;
    while (current != null) {
      if (key == current.key) return true;
      current = key < current.key ? current.left : current.right;
    }
    return false;
  }

  public boolean insert(int key) {
    lastEvents.clear();
    if (contains(key)) {
      lastEvents.add("Key " + key + " is already present");
      return false;
    }
    root = insert(root, key);
    return true;
  }

  private Node insert(Node node, int key) {
    if (node == null) return new Node(key);
    if (key < node.key) node.left = insert(node.left, key);
    else node.right = insert(node.right, key);
    updateHeight(node);
    return rebalance(node);
  }

  public boolean delete(int key) {
    lastEvents.clear();
    if (!contains(key)) {
      lastEvents.add("Key " + key + " was not found");
      return false;
    }
    root = delete(root, key);
    return true;
  }

  private Node delete(Node node, int key) {
    if (key < node.key) node.left = delete(node.left, key);
    else if (key > node.key) node.right = delete(node.right, key);
    else if (node.left == null || node.right == null) {
      node = node.left != null ? node.left : node.right;
    } else {
      Node successor = minimum(node.right);
      lastEvents.add("Replace " + node.key + " with successor " + successor.key);
      node.key = successor.key;
      node.right = delete(node.right, successor.key);
    }
    if (node == null) return null;
    updateHeight(node);
    return rebalance(node);
  }

  private Node rebalance(Node node) {
    int balance = balance(node);
    if (balance > 1) {
      if (balance(node.left) < 0) {
        lastEvents.add("LR rotation at " + node.key);
        node.left = rotateLeft(node.left);
      } else {
        lastEvents.add("LL rotation at " + node.key);
      }
      return rotateRight(node);
    }
    if (balance < -1) {
      if (balance(node.right) > 0) {
        lastEvents.add("RL rotation at " + node.key);
        node.right = rotateRight(node.right);
      } else {
        lastEvents.add("RR rotation at " + node.key);
      }
      return rotateLeft(node);
    }
    return node;
  }

  private Node rotateLeft(Node node) {
    Node pivot = node.right;
    node.right = pivot.left;
    pivot.left = node;
    updateHeight(node);
    updateHeight(pivot);
    rotations++;
    return pivot;
  }

  private Node rotateRight(Node node) {
    Node pivot = node.left;
    node.left = pivot.right;
    pivot.right = node;
    updateHeight(node);
    updateHeight(pivot);
    rotations++;
    return pivot;
  }

  private static Node minimum(Node node) {
    while (node.left != null) node = node.left;
    return node;
  }

  private static int height(Node node) {
    return node == null ? 0 : node.height;
  }

  private static int balance(Node node) {
    return node == null ? 0 : height(node.left) - height(node.right);
  }

  private static void updateHeight(Node node) {
    node.height = 1 + Math.max(height(node.left), height(node.right));
  }

  public List<Integer> inorder() {
    List<Integer> out = new ArrayList<>();
    inorder(root, out);
    return out;
  }

  private static void inorder(Node node, List<Integer> out) {
    if (node == null) return;
    inorder(node.left, out);
    out.add(node.key);
    inorder(node.right, out);
  }

  public List<NodeView> levelOrder() {
    List<NodeView> out = new ArrayList<>();
    if (root == null) return out;
    Queue<Node> queue = new ArrayDeque<>();
    queue.add(root);
    while (!queue.isEmpty()) {
      Node node = queue.remove();
      out.add(new NodeView(node.key, node.height, balance(node)));
      if (node.left != null) queue.add(node.left);
      if (node.right != null) queue.add(node.right);
    }
    return out;
  }

  /** Verifies ordering, stored heights, and the AVL balance bound at every node. */
  public boolean invariantsHold() {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE) >= 0;
  }

  private static int validate(Node node, long low, long high) {
    if (node == null) return 0;
    if (node.key <= low || node.key >= high) return -1;
    int left = validate(node.left, low, node.key);
    int right = validate(node.right, node.key, high);
    if (left < 0 || right < 0 || Math.abs(left - right) > 1) return -1;
    int expectedHeight = 1 + Math.max(left, right);
    return node.height == expectedHeight ? expectedHeight : -1;
  }
}
