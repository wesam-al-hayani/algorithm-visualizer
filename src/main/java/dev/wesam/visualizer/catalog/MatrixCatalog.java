package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;

import dev.wesam.visualizer.algorithms.MatrixAlgorithms;
import dev.wesam.visualizer.model.AlgorithmRun;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class MatrixCatalog {
  private MatrixCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    demos.add(
        demo(
            "Divide & Conquer / Matrix",
            "Strassen Matrix Multiplication",
            "Recursively combines seven half-size products; padding supports non-power-of-two"
                + " square matrices.",
            "split A and B into quadrants\ncompute M1…M7 recursively\ncombine result quadrants",
            "O(n^log₂7)",
            "O(n²)",
            "A rows / B rows",
            "1,2;3,4 / 5,6;7,8",
            MatrixCatalog::strassen));
    return List.copyOf(demos);
  }

  static AlgorithmRun strassen(String input) {
    String[] p = input.split("/", 2);
    if (p.length != 2) throw new IllegalArgumentException("Use A / B");
    int[][] a = matrix(p[0]), b = matrix(p[1]), r = MatrixAlgorithms.strassen(a, b);
    return new AlgorithmRun(
        List.of(
            matrixStep(
                "Divide matrices into quadrants",
                a,
                Map.of("Matrix size", a.length),
                "Seven recursive products"),
            matrixStep(
                "Combine M1 through M7",
                r,
                Map.of("Scalar entries", r.length * r.length),
                "Result matches ordinary multiplication: "
                    + Arrays.deepEquals(r, MatrixAlgorithms.ordinaryMultiply(a, b)))),
        "Product: " + Arrays.deepToString(r));
  }
}
