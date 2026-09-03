package dev.wesam.visualizer.structures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/** Textbook red-black tree with insertion, search, rotations, and recoloring. */
public final class RedBlackTree {
  public enum Color {
    RED,
    BLACK
  }

  public final class Node {
    public int key;
    public Color color;
    public Node left, right, parent;

    private Node(int key, Color color) {
      this.key = key;
      this.color = color;
    }

    public boolean isNil() {
      return this == nil;
    }
  }

  private final Node nil = new Node(0, Color.BLACK);
  private Node root = nil;
  private int rotations;

  public RedBlackTree() {
    nil.left = nil.right = nil.parent = nil;
  }

  public Node root() {
    return root;
  }

  public int rotations() {
    return rotations;
  }

  public boolean contains(int key) {
    return find(key) != nil;
  }

  private Node find(int key) {
    Node n = root;
    while (n != nil && n.key != key) n = key < n.key ? n.left : n.right;
    return n;
  }

  public boolean insert(int key) {
    Node parent = nil, current = root;
    while (current != nil) {
      parent = current;
      if (key == current.key) return false;
      current = key < current.key ? current.left : current.right;
    }
    Node node = new Node(key, Color.RED);
    node.left = node.right = nil;
    node.parent = parent;
    if (parent == nil) root = node;
    else if (key < parent.key) parent.left = node;
    else parent.right = node;
    fixAfterInsert(node);
    return true;
  }

  private void fixAfterInsert(Node node) {
    while (node.parent.color == Color.RED) {
      if (node.parent == node.parent.parent.left) {
        Node uncle = node.parent.parent.right;
        if (uncle.color == Color.RED) {
          node.parent.color = Color.BLACK;
          uncle.color = Color.BLACK;
          node.parent.parent.color = Color.RED;
          node = node.parent.parent;
        } else {
          if (node == node.parent.right) {
            node = node.parent;
            rotateLeft(node);
          }
          node.parent.color = Color.BLACK;
          node.parent.parent.color = Color.RED;
          rotateRight(node.parent.parent);
        }
      } else {
        Node uncle = node.parent.parent.left;
        if (uncle.color == Color.RED) {
          node.parent.color = Color.BLACK;
          uncle.color = Color.BLACK;
          node.parent.parent.color = Color.RED;
          node = node.parent.parent;
        } else {
          if (node == node.parent.left) {
            node = node.parent;
            rotateRight(node);
          }
          node.parent.color = Color.BLACK;
          node.parent.parent.color = Color.RED;
          rotateLeft(node.parent.parent);
        }
      }
    }
    root.color = Color.BLACK;
  }

  private void rotateLeft(Node x) {
    Node y = x.right;
    x.right = y.left;
    if (y.left != nil) y.left.parent = x;
    y.parent = x.parent;
    if (x.parent == nil) root = y;
    else if (x == x.parent.left) x.parent.left = y;
    else x.parent.right = y;
    y.left = x;
    x.parent = y;
    rotations++;
  }

  private void rotateRight(Node y) {
    Node x = y.left;
    y.left = x.right;
    if (x.right != nil) x.right.parent = y;
    x.parent = y.parent;
    if (y.parent == nil) root = x;
    else if (y == y.parent.right) y.parent.right = x;
    else y.parent.left = x;
    x.right = y;
    y.parent = x;
    rotations++;
  }

  public List<Integer> inorder() {
    List<Integer> out = new ArrayList<>();
    inorder(root, out);
    return out;
  }

  private void inorder(Node n, List<Integer> out) {
    if (n != nil) {
      inorder(n.left, out);
      out.add(n.key);
      inorder(n.right, out);
    }
  }

  public record NodeView(int key, Color color) {}

  public List<NodeView> levelOrder() {
    List<NodeView> out = new ArrayList<>();
    if (root == nil) return out;
    Queue<Node> queue = new ArrayDeque<>();
    queue.add(root);
    while (!queue.isEmpty()) {
      Node node = queue.remove();
      out.add(new NodeView(node.key, node.color));
      if (node.left != nil) queue.add(node.left);
      if (node.right != nil) queue.add(node.right);
    }
    return out;
  }

  /** Verifies root color, ordering, red-parent, and equal black-height properties. */
  public boolean invariantsHold() {
    return root == nil
        || root.color == Color.BLACK && validate(root, Long.MIN_VALUE, Long.MAX_VALUE) > 0;
  }

  private int validate(Node n, long low, long high) {
    if (n == nil) return 1;
    if (n.key <= low || n.key >= high) return -1;
    if (n.color == Color.RED && (n.left.color == Color.RED || n.right.color == Color.RED))
      return -1;
    int left = validate(n.left, low, n.key), right = validate(n.right, n.key, high);
    if (left < 0 || left != right) return -1;
    return left + (n.color == Color.BLACK ? 1 : 0);
  }
}
