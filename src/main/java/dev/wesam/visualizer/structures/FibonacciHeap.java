package dev.wesam.visualizer.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/** Educational Fibonacci min-heap with circular root/child lists. */
public final class FibonacciHeap {
  public static final class Node {
    public int key, degree;
    public boolean marked;
    public Node parent, child, left = this, right = this;

    private Node(int key) {
      this.key = key;
    }
  }

  private Node minimum;
  private int size;

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return minimum == null;
  }

  public int findMinimum() {
    if (minimum == null) throw new NoSuchElementException();
    return minimum.key;
  }

  public Node insert(int key) {
    Node node = new Node(key);
    minimum = addToList(minimum, node);
    if (node.key < minimum.key) minimum = node;
    size++;
    return node;
  }

  public int extractMinimum() {
    if (minimum == null) throw new NoSuchElementException();
    Node removed = minimum;
    if (removed.child != null) {
      List<Node> children = members(removed.child);
      for (Node child : children) {
        detach(child);
        child.parent = null;
        child.marked = false;
        minimum = addToList(minimum, child);
      }
    }
    if (removed.right == removed) minimum = null;
    else {
      Node next = removed.right;
      detach(removed);
      minimum = next;
      consolidate();
    }
    size--;
    return removed.key;
  }

  public void decreaseKey(Node node, int newKey) {
    if (newKey > node.key) throw new IllegalArgumentException("new key is greater");
    node.key = newKey;
    Node parent = node.parent;
    if (parent != null && node.key < parent.key) {
      cut(node, parent);
      cascadingCut(parent);
    }
    if (minimum == null || node.key < minimum.key) minimum = node;
  }

  private void consolidate() {
    int maxDegree = (int) (Math.log(Math.max(1, size)) / Math.log(2)) + 3;
    Node[] degrees = new Node[maxDegree];
    for (Node current : members(minimum)) {
      Node x = current;
      int degree = x.degree;
      while (degrees[degree] != null) {
        Node y = degrees[degree];
        if (x.key > y.key) {
          Node t = x;
          x = y;
          y = t;
        }
        link(y, x);
        degrees[degree] = null;
        degree++;
        if (degree == degrees.length)
          degrees = java.util.Arrays.copyOf(degrees, degrees.length + 2);
      }
      degrees[degree] = x;
    }
    minimum = null;
    for (Node node : degrees)
      if (node != null) {
        node.left = node.right = node;
        minimum = addToList(minimum, node);
        if (node.key < minimum.key) minimum = node;
      }
  }

  private void link(Node child, Node parent) {
    detach(child);
    child.parent = parent;
    child.marked = false;
    parent.child = addToList(parent.child, child);
    parent.degree++;
  }

  private void cut(Node node, Node parent) {
    if (node.right == node) parent.child = null;
    else {
      if (parent.child == node) parent.child = node.right;
      detach(node);
    }
    parent.degree--;
    node.parent = null;
    node.marked = false;
    minimum = addToList(minimum, node);
  }

  private void cascadingCut(Node node) {
    Node parent = node.parent;
    if (parent != null) {
      if (!node.marked) node.marked = true;
      else {
        cut(node, parent);
        cascadingCut(parent);
      }
    }
  }

  public List<Integer> rootKeys() {
    List<Integer> out = new ArrayList<>();
    if (minimum != null) for (Node n : members(minimum)) out.add(n.key);
    return out;
  }

  public boolean invariantHolds() {
    if (minimum == null) return size == 0;
    int count = 0, min = Integer.MAX_VALUE;
    for (Node root : members(minimum)) {
      if (root.parent != null) return false;
      min = Math.min(min, root.key);
      int nodes = validate(root);
      if (nodes < 0) return false;
      count += nodes;
    }
    return count == size && minimum.key == min;
  }

  private int validate(Node node) {
    int count = 1, children = 0;
    if (node.child != null)
      for (Node child : members(node.child)) {
        if (child.parent != node || child.key < node.key) return -1;
        int sub = validate(child);
        if (sub < 0) return -1;
        count += sub;
        children++;
      }
    return children == node.degree ? count : -1;
  }

  private static Node addToList(Node head, Node node) {
    if (head == null) {
      node.left = node.right = node;
      return node;
    }
    node.right = head.right;
    node.left = head;
    head.right.left = node;
    head.right = node;
    return head;
  }

  private static void detach(Node node) {
    node.left.right = node.right;
    node.right.left = node.left;
    node.left = node.right = node;
  }

  private static List<Node> members(Node head) {
    List<Node> result = new ArrayList<>();
    if (head == null) return result;
    Node n = head;
    do {
      result.add(n);
      n = n.right;
    } while (n != head);
    return result;
  }
}
