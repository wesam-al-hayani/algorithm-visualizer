package dev.wesam.visualizer.structures;

import java.util.ArrayList;
import java.util.List;

/** Textbook B-tree supporting insertion, search, and deletion at configurable minimum degree. */
public final class BTree {
  public static final class Node {
    public final List<Integer> keys = new ArrayList<>();
    public final List<Node> children = new ArrayList<>();
    public boolean leaf;

    private Node(boolean leaf) {
      this.leaf = leaf;
    }
  }

  private final int degree;
  private Node root = new Node(true);
  private int splits;
  private int borrows;
  private int merges;
  private final List<String> lastEvents = new ArrayList<>();

  public BTree(int minimumDegree) {
    if (minimumDegree < 2) throw new IllegalArgumentException("minimum degree must be at least 2");
    degree = minimumDegree;
  }

  public Node root() {
    return root;
  }

  public int splits() {
    return splits;
  }

  public int borrows() {
    return borrows;
  }

  public int merges() {
    return merges;
  }

  public List<String> lastEvents() {
    return List.copyOf(lastEvents);
  }

  public boolean contains(int key) {
    return contains(root, key);
  }

  private boolean contains(Node node, int key) {
    int i = 0;
    while (i < node.keys.size() && key > node.keys.get(i)) i++;
    if (i < node.keys.size() && key == node.keys.get(i)) return true;
    return !node.leaf && contains(node.children.get(i), key);
  }

  public boolean insert(int key) {
    lastEvents.clear();
    if (contains(key)) {
      lastEvents.add("Key " + key + " is already present");
      return false;
    }
    if (root.keys.size() == 2 * degree - 1) {
      Node newRoot = new Node(false);
      newRoot.children.add(root);
      splitChild(newRoot, 0);
      root = newRoot;
      lastEvents.add("Split full root");
    }
    insertNonFull(root, key);
    return true;
  }

  private void insertNonFull(Node node, int key) {
    int i = node.keys.size() - 1;
    if (node.leaf) {
      node.keys.add(0);
      while (i >= 0 && key < node.keys.get(i)) {
        node.keys.set(i + 1, node.keys.get(i));
        i--;
      }
      node.keys.set(i + 1, key);
    } else {
      while (i >= 0 && key < node.keys.get(i)) i--;
      i++;
      if (node.children.get(i).keys.size() == 2 * degree - 1) {
        splitChild(node, i);
        if (key > node.keys.get(i)) i++;
      }
      insertNonFull(node.children.get(i), key);
    }
  }

  private void splitChild(Node parent, int childIndex) {
    Node full = parent.children.get(childIndex), right = new Node(full.leaf);
    int median = full.keys.get(degree - 1);
    for (int j = degree; j < full.keys.size(); j++) right.keys.add(full.keys.get(j));
    while (full.keys.size() > degree - 1) full.keys.remove(full.keys.size() - 1);
    if (!full.leaf) {
      for (int j = degree; j < full.children.size(); j++) right.children.add(full.children.get(j));
      while (full.children.size() > degree) full.children.remove(full.children.size() - 1);
    }
    parent.keys.add(childIndex, median);
    parent.children.add(childIndex + 1, right);
    splits++;
    lastEvents.add("Split full child around median " + median);
  }

  public boolean delete(int key) {
    lastEvents.clear();
    if (!contains(key)) {
      lastEvents.add("Key " + key + " was not found");
      return false;
    }
    delete(root, key);
    if (!root.leaf && root.keys.isEmpty()) {
      root = root.children.get(0);
      lastEvents.add("Root shrinks to its only child");
    }
    return true;
  }

  private void delete(Node node, int key) {
    int index = firstAtLeast(node, key);
    if (index < node.keys.size() && node.keys.get(index) == key) {
      if (node.leaf) {
        node.keys.remove(index);
        lastEvents.add("Delete " + key + " from leaf");
      } else {
        deleteFromInternal(node, index);
      }
      return;
    }

    boolean wasLastChild = index == node.keys.size();
    if (node.children.get(index).keys.size() == degree - 1) fill(node, index);
    if (wasLastChild && index > node.keys.size()) index--;
    delete(node.children.get(index), key);
  }

  private void deleteFromInternal(Node node, int index) {
    int key = node.keys.get(index);
    Node left = node.children.get(index);
    Node right = node.children.get(index + 1);
    if (left.keys.size() >= degree) {
      int predecessor = predecessor(left);
      node.keys.set(index, predecessor);
      lastEvents.add("Replace internal key " + key + " with predecessor " + predecessor);
      delete(left, predecessor);
    } else if (right.keys.size() >= degree) {
      int successor = successor(right);
      node.keys.set(index, successor);
      lastEvents.add("Replace internal key " + key + " with successor " + successor);
      delete(right, successor);
    } else {
      merge(node, index);
      delete(left, key);
    }
  }

  private void fill(Node parent, int childIndex) {
    if (childIndex > 0 && parent.children.get(childIndex - 1).keys.size() >= degree)
      borrowFromPrevious(parent, childIndex);
    else if (childIndex < parent.keys.size()
        && parent.children.get(childIndex + 1).keys.size() >= degree)
      borrowFromNext(parent, childIndex);
    else if (childIndex < parent.keys.size()) merge(parent, childIndex);
    else merge(parent, childIndex - 1);
  }

  private void borrowFromPrevious(Node parent, int childIndex) {
    Node child = parent.children.get(childIndex);
    Node sibling = parent.children.get(childIndex - 1);
    child.keys.add(0, parent.keys.get(childIndex - 1));
    if (!child.leaf) child.children.add(0, sibling.children.remove(sibling.children.size() - 1));
    parent.keys.set(childIndex - 1, sibling.keys.remove(sibling.keys.size() - 1));
    borrows++;
    lastEvents.add("Borrow from left sibling before descent");
  }

  private void borrowFromNext(Node parent, int childIndex) {
    Node child = parent.children.get(childIndex);
    Node sibling = parent.children.get(childIndex + 1);
    child.keys.add(parent.keys.get(childIndex));
    if (!child.leaf) child.children.add(sibling.children.remove(0));
    parent.keys.set(childIndex, sibling.keys.remove(0));
    borrows++;
    lastEvents.add("Borrow from right sibling before descent");
  }

  private void merge(Node parent, int keyIndex) {
    Node left = parent.children.get(keyIndex);
    Node right = parent.children.remove(keyIndex + 1);
    int separator = parent.keys.remove(keyIndex);
    left.keys.add(separator);
    left.keys.addAll(right.keys);
    if (!left.leaf) left.children.addAll(right.children);
    merges++;
    lastEvents.add("Merge siblings around separator " + separator);
  }

  private static int firstAtLeast(Node node, int key) {
    int index = 0;
    while (index < node.keys.size() && node.keys.get(index) < key) index++;
    return index;
  }

  private static int predecessor(Node node) {
    while (!node.leaf) node = node.children.get(node.keys.size());
    return node.keys.get(node.keys.size() - 1);
  }

  private static int successor(Node node) {
    while (!node.leaf) node = node.children.get(0);
    return node.keys.get(0);
  }

  public List<Integer> inOrder() {
    List<Integer> out = new ArrayList<>();
    walk(root, out);
    return out;
  }

  private void walk(Node n, List<Integer> out) {
    for (int i = 0; i < n.keys.size(); i++) {
      if (!n.leaf) walk(n.children.get(i), out);
      out.add(n.keys.get(i));
    }
    if (!n.leaf) walk(n.children.get(n.keys.size()), out);
  }

  public boolean invariantsHold() {
    return validate(root, true, 0, new int[] {-1}, Long.MIN_VALUE, Long.MAX_VALUE);
  }

  private boolean validate(
      Node n, boolean isRoot, int depth, int[] leafDepth, long low, long high) {
    if (n.keys.size() > 2 * degree - 1) return false;
    if (isRoot && !n.leaf && n.keys.isEmpty()) return false;
    if (!isRoot && n.keys.size() < degree - 1) return false;
    for (int i = 0; i < n.keys.size(); i++) {
      int key = n.keys.get(i);
      if (key <= low || key >= high || (i > 0 && n.keys.get(i - 1) >= key)) return false;
    }
    if (n.leaf) {
      if (leafDepth[0] < 0) leafDepth[0] = depth;
      return leafDepth[0] == depth;
    }
    if (n.children.size() != n.keys.size() + 1) return false;
    for (int i = 0; i < n.children.size(); i++) {
      long childLow = i == 0 ? low : n.keys.get(i - 1),
          childHigh = i == n.keys.size() ? high : n.keys.get(i);
      if (!validate(n.children.get(i), false, depth + 1, leafDepth, childLow, childHigh))
        return false;
    }
    return true;
  }
}
