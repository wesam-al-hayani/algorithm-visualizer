package dev.wesam.visualizer.structures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/** Textbook red-black tree with insertion, deletion, rotations, and recoloring. */
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
  private final List<String> lastEvents = new ArrayList<>();

  public RedBlackTree() {
    nil.left = nil.right = nil.parent = nil;
  }

  public Node root() {
    return root;
  }

  public int rotations() {
    return rotations;
  }

  public List<String> lastEvents() {
    return List.copyOf(lastEvents);
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
    lastEvents.clear();
    Node parent = nil, current = root;
    while (current != nil) {
      parent = current;
      if (key == current.key) {
        lastEvents.add("Key " + key + " is already present");
        return false;
      }
      current = key < current.key ? current.left : current.right;
    }
    Node node = new Node(key, Color.RED);
    node.left = node.right = nil;
    node.parent = parent;
    if (parent == nil) root = node;
    else if (key < parent.key) parent.left = node;
    else parent.right = node;
    fixAfterInsert(node);
    lastEvents.add("Root restored to black");
    return true;
  }

  private void fixAfterInsert(Node node) {
    while (node.parent.color == Color.RED) {
      if (node.parent == node.parent.parent.left) {
        Node uncle = node.parent.parent.right;
        if (uncle.color == Color.RED) {
          lastEvents.add("Recolor red parent and uncle; move violation upward");
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
          lastEvents.add("Recolor red parent and uncle; move violation upward");
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
    lastEvents.add("Rotate left at " + x.key);
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
    lastEvents.add("Rotate right at " + y.key);
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

  public boolean delete(int key) {
    lastEvents.clear();
    Node target = find(key);
    if (target == nil) {
      lastEvents.add("Key " + key + " was not found");
      return false;
    }

    Node removed = target;
    Color removedColor = removed.color;
    Node repair;
    if (target.left == nil) {
      repair = target.right;
      transplant(target, target.right);
      lastEvents.add("Remove node with no left child");
    } else if (target.right == nil) {
      repair = target.left;
      transplant(target, target.left);
      lastEvents.add("Remove node with no right child");
    } else {
      removed = minimum(target.right);
      removedColor = removed.color;
      repair = removed.right;
      lastEvents.add("Replace " + target.key + " with successor " + removed.key);
      if (removed.parent == target) {
        repair.parent = removed;
      } else {
        transplant(removed, removed.right);
        removed.right = target.right;
        removed.right.parent = removed;
      }
      transplant(target, removed);
      removed.left = target.left;
      removed.left.parent = removed;
      removed.color = target.color;
    }

    if (removedColor == Color.BLACK) {
      lastEvents.add("Repair double-black at " + label(repair));
      fixAfterDelete(repair);
    }
    if (root != nil) root.parent = nil;
    nil.parent = nil;
    lastEvents.add("Red-black invariants restored");
    return true;
  }

  private void fixAfterDelete(Node node) {
    while (node != root && node.color == Color.BLACK) {
      if (node == node.parent.left) {
        Node sibling = node.parent.right;
        if (sibling.color == Color.RED) {
          lastEvents.add("Sibling case 1: red sibling; recolor and rotate left");
          sibling.color = Color.BLACK;
          node.parent.color = Color.RED;
          rotateLeft(node.parent);
          sibling = node.parent.right;
        }
        if (sibling.left.color == Color.BLACK && sibling.right.color == Color.BLACK) {
          lastEvents.add("Sibling case 2: black sibling with black children; move black upward");
          if (sibling != nil) sibling.color = Color.RED;
          node = node.parent;
        } else {
          if (sibling.right.color == Color.BLACK) {
            lastEvents.add("Sibling case 3: near red child; rotate right at sibling");
            sibling.left.color = Color.BLACK;
            sibling.color = Color.RED;
            rotateRight(sibling);
            sibling = node.parent.right;
          }
          lastEvents.add("Sibling case 4: far red child; recolor and rotate left");
          sibling.color = node.parent.color;
          node.parent.color = Color.BLACK;
          sibling.right.color = Color.BLACK;
          rotateLeft(node.parent);
          node = root;
        }
      } else {
        Node sibling = node.parent.left;
        if (sibling.color == Color.RED) {
          lastEvents.add("Sibling case 1 mirror: red sibling; recolor and rotate right");
          sibling.color = Color.BLACK;
          node.parent.color = Color.RED;
          rotateRight(node.parent);
          sibling = node.parent.left;
        }
        if (sibling.right.color == Color.BLACK && sibling.left.color == Color.BLACK) {
          lastEvents.add(
              "Sibling case 2 mirror: black sibling with black children; move black upward");
          if (sibling != nil) sibling.color = Color.RED;
          node = node.parent;
        } else {
          if (sibling.left.color == Color.BLACK) {
            lastEvents.add("Sibling case 3 mirror: near red child; rotate left at sibling");
            sibling.right.color = Color.BLACK;
            sibling.color = Color.RED;
            rotateLeft(sibling);
            sibling = node.parent.left;
          }
          lastEvents.add("Sibling case 4 mirror: far red child; recolor and rotate right");
          sibling.color = node.parent.color;
          node.parent.color = Color.BLACK;
          sibling.left.color = Color.BLACK;
          rotateRight(node.parent);
          node = root;
        }
      }
    }
    node.color = Color.BLACK;
  }

  private void transplant(Node replaced, Node replacement) {
    if (replaced.parent == nil) root = replacement;
    else if (replaced == replaced.parent.left) replaced.parent.left = replacement;
    else replaced.parent.right = replacement;
    replacement.parent = replaced.parent;
  }

  private Node minimum(Node node) {
    while (node.left != nil) node = node.left;
    return node;
  }

  private String label(Node node) {
    return node == nil ? "NIL" : Integer.toString(node.key);
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
    return nil.color == Color.BLACK
        && nil.left == nil
        && nil.right == nil
        && (root == nil
            || root.parent == nil
                && root.color == Color.BLACK
                && validate(root, Long.MIN_VALUE, Long.MAX_VALUE) > 0);
  }

  private int validate(Node n, long low, long high) {
    if (n == nil) return 1;
    if (n.key <= low || n.key >= high) return -1;
    if (n.left == null || n.right == null) return -1;
    if (n.left != nil && n.left.parent != n || n.right != nil && n.right.parent != n) return -1;
    if (n.color == Color.RED && (n.left.color == Color.RED || n.right.color == Color.RED))
      return -1;
    int left = validate(n.left, low, n.key), right = validate(n.right, n.key, high);
    if (left < 0 || left != right) return -1;
    return left + (n.color == Color.BLACK ? 1 : 0);
  }
}
