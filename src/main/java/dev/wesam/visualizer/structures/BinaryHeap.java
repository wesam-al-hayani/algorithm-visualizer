package dev.wesam.visualizer.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class BinaryHeap {
  public enum Type {
    MIN,
    MAX
  }

  private final Type type;
  private final List<Integer> values = new ArrayList<>();

  public BinaryHeap(Type type) {
    this.type = type;
  }

  public int size() {
    return values.size();
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  public List<Integer> array() {
    return List.copyOf(values);
  }

  public int peek() {
    if (values.isEmpty()) throw new NoSuchElementException();
    return values.get(0);
  }

  public void insert(int value) {
    values.add(value);
    siftUp(values.size() - 1);
  }

  public int extract() {
    int result = peek(), last = values.remove(values.size() - 1);
    if (!values.isEmpty()) {
      values.set(0, last);
      siftDown(0);
    }
    return result;
  }

  public void changePriority(int index, int value) {
    if (index < 0 || index >= values.size()) throw new IndexOutOfBoundsException(index);
    int old = values.set(index, value);
    if (preferred(value, old)) siftUp(index);
    else siftDown(index);
  }

  public void heapify(List<Integer> input) {
    values.clear();
    values.addAll(input);
    for (int i = values.size() / 2 - 1; i >= 0; i--) siftDown(i);
  }

  private void siftUp(int index) {
    while (index > 0) {
      int parent = (index - 1) / 2;
      if (!preferred(values.get(index), values.get(parent))) break;
      swap(index, parent);
      index = parent;
    }
  }

  private void siftDown(int index) {
    while (true) {
      int chosen = index, left = index * 2 + 1, right = left + 1;
      if (left < values.size() && preferred(values.get(left), values.get(chosen))) chosen = left;
      if (right < values.size() && preferred(values.get(right), values.get(chosen))) chosen = right;
      if (chosen == index) return;
      swap(index, chosen);
      index = chosen;
    }
  }

  private boolean preferred(int first, int second) {
    return type == Type.MIN ? first < second : first > second;
  }

  private void swap(int a, int b) {
    int value = values.get(a);
    values.set(a, values.get(b));
    values.set(b, value);
  }

  public boolean invariantHolds() {
    for (int i = 1; i < values.size(); i++)
      if (preferred(values.get(i), values.get((i - 1) / 2))) return false;
    return true;
  }
}
