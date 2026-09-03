package dev.wesam.visualizer.structures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class BinarySearchTree {
  public static final class Node {
    public int key;
    public Node left, right;

    private Node(int key) {
      this.key = key;
    }
  }

  private Node root;

  public Node root() {
    return root;
  }

  public boolean insert(int key) {
    if (root == null) {
      root = new Node(key);
      return true;
    }
    Node current = root;
    while (true) {
      if (key == current.key) return false;
      if (key < current.key) {
        if (current.left == null) {
          current.left = new Node(key);
          return true;
        }
        current = current.left;
      } else {
        if (current.right == null) {
          current.right = new Node(key);
          return true;
        }
        current = current.right;
      }
    }
  }

  public boolean contains(int key) {
    Node current = root;
    while (current != null) {
      if (key == current.key) return true;
      current = key < current.key ? current.left : current.right;
    }
    return false;
  }

  public boolean delete(int key) {
    if (!contains(key)) return false;
    root = delete(root, key);
    return true;
  }

  private Node delete(Node node, int key) {
    if (node == null) return null;
    if (key < node.key) node.left = delete(node.left, key);
    else if (key > node.key) node.right = delete(node.right, key);
    else {
      if (node.left == null) return node.right;
      if (node.right == null) return node.left;
      Node successor = node.right;
      while (successor.left != null) successor = successor.left;
      node.key = successor.key;
      node.right = delete(node.right, successor.key);
    }
    return node;
  }

  public List<Integer> inorder() {
    List<Integer> out = new ArrayList<>();
    inorder(root, out);
    return out;
  }

  public List<Integer> preorder() {
    List<Integer> out = new ArrayList<>();
    preorder(root, out);
    return out;
  }

  public List<Integer> postorder() {
    List<Integer> out = new ArrayList<>();
    postorder(root, out);
    return out;
  }

  public List<Integer> levelOrder() {
    List<Integer> out = new ArrayList<>();
    if (root == null) return out;
    Queue<Node> queue = new ArrayDeque<>();
    queue.add(root);
    while (!queue.isEmpty()) {
      Node n = queue.remove();
      out.add(n.key);
      if (n.left != null) queue.add(n.left);
      if (n.right != null) queue.add(n.right);
    }
    return out;
  }

  private void inorder(Node n, List<Integer> out) {
    if (n != null) {
      inorder(n.left, out);
      out.add(n.key);
      inorder(n.right, out);
    }
  }

  private void preorder(Node n, List<Integer> out) {
    if (n != null) {
      out.add(n.key);
      preorder(n.left, out);
      preorder(n.right, out);
    }
  }

  private void postorder(Node n, List<Integer> out) {
    if (n != null) {
      postorder(n.left, out);
      postorder(n.right, out);
      out.add(n.key);
    }
  }
}
