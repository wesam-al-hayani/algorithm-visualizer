package dev.wesam.visualizer.ui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/** Bounded text snapshots are sufficient for readable graph and grid undo/redo. */
public final class EditHistory {
  private static final int LIMIT = 50;
  private final Deque<String> undo = new ArrayDeque<>();
  private final Deque<String> redo = new ArrayDeque<>();

  public String apply(String current, String replacement) {
    if (current.equals(replacement)) return current;
    undo.push(current);
    trim(undo);
    redo.clear();
    return replacement;
  }

  public Optional<String> undo(String current) {
    if (undo.isEmpty()) return Optional.empty();
    redo.push(current);
    trim(redo);
    return Optional.of(undo.pop());
  }

  public Optional<String> redo(String current) {
    if (redo.isEmpty()) return Optional.empty();
    undo.push(current);
    trim(undo);
    return Optional.of(redo.pop());
  }

  public void clear() {
    undo.clear();
    redo.clear();
  }

  private void trim(Deque<String> stack) {
    while (stack.size() > LIMIT) stack.removeLast();
  }
}
