package dev.wesam.visualizer.ui;

import dev.wesam.visualizer.catalog.AlgorithmDemo;

/**
 * Converts parser failures into concise UI guidance without exposing exception implementation
 * details.
 */
public final class InputFeedback {
  private InputFeedback() {}

  public static String message(Exception exception, AlgorithmDemo demo) {
    String guidance = "Expected: " + demo.inputHint() + "\nExample: " + demo.defaultInput();
    if (containsNumberFormat(exception))
      return "A number could not be read. Check separators and numeric values.\n\n" + guidance;
    String detail = exception.getMessage();
    if (detail == null || detail.isBlank()) detail = "The input could not be parsed.";
    return detail + "\n\n" + guidance;
  }

  private static boolean containsNumberFormat(Throwable error) {
    for (Throwable current = error; current != null; current = current.getCause())
      if (current instanceof NumberFormatException) return true;
    return false;
  }
}
