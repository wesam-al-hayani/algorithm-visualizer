package dev.wesam.visualizer.algorithms;

public final class MatrixAlgorithms {
  private MatrixAlgorithms() {}

  public static int[][] ordinaryMultiply(int[][] first, int[][] second) {
    checkSquarePair(first, second);
    int n = first.length;
    int[][] result = new int[n][n];
    for (int i = 0; i < n; i++)
      for (int k = 0; k < n; k++)
        for (int j = 0; j < n; j++) result[i][j] += first[i][k] * second[k][j];
    return result;
  }

  public static int[][] strassen(int[][] first, int[][] second) {
    checkSquarePair(first, second);
    int n = first.length;
    if (n == 0) return new int[0][0];
    int size = 1;
    while (size < n) size <<= 1;
    int[][] a = new int[size][size], b = new int[size][size];
    for (int i = 0; i < n; i++) {
      System.arraycopy(first[i], 0, a[i], 0, n);
      System.arraycopy(second[i], 0, b[i], 0, n);
    }
    int[][] padded = multiply(a, b);
    int[][] result = new int[n][n];
    for (int i = 0; i < n; i++) System.arraycopy(padded[i], 0, result[i], 0, n);
    return result;
  }

  private static int[][] multiply(int[][] a, int[][] b) {
    int n = a.length;
    if (n <= 2) return ordinaryMultiply(a, b);
    int h = n / 2;
    int[][] a11 = part(a, 0, 0, h),
        a12 = part(a, 0, h, h),
        a21 = part(a, h, 0, h),
        a22 = part(a, h, h, h);
    int[][] b11 = part(b, 0, 0, h),
        b12 = part(b, 0, h, h),
        b21 = part(b, h, 0, h),
        b22 = part(b, h, h, h);
    int[][] m1 = multiply(add(a11, a22), add(b11, b22));
    int[][] m2 = multiply(add(a21, a22), b11);
    int[][] m3 = multiply(a11, subtract(b12, b22));
    int[][] m4 = multiply(a22, subtract(b21, b11));
    int[][] m5 = multiply(add(a11, a12), b22);
    int[][] m6 = multiply(subtract(a21, a11), add(b11, b12));
    int[][] m7 = multiply(subtract(a12, a22), add(b21, b22));
    int[][] result = new int[n][n];
    join(add(subtract(add(m1, m4), m5), m7), result, 0, 0);
    join(add(m3, m5), result, 0, h);
    join(add(m2, m4), result, h, 0);
    join(add(subtract(add(m1, m3), m2), m6), result, h, h);
    return result;
  }

  private static int[][] part(int[][] source, int row, int col, int size) {
    int[][] r = new int[size][size];
    for (int i = 0; i < size; i++) System.arraycopy(source[row + i], col, r[i], 0, size);
    return r;
  }

  private static void join(int[][] source, int[][] target, int row, int col) {
    for (int i = 0; i < source.length; i++)
      System.arraycopy(source[i], 0, target[row + i], col, source.length);
  }

  private static int[][] add(int[][] a, int[][] b) {
    int n = a.length;
    int[][] r = new int[n][n];
    for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) r[i][j] = a[i][j] + b[i][j];
    return r;
  }

  private static int[][] subtract(int[][] a, int[][] b) {
    int n = a.length;
    int[][] r = new int[n][n];
    for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) r[i][j] = a[i][j] - b[i][j];
    return r;
  }

  private static void checkSquarePair(int[][] a, int[][] b) {
    if (a.length != b.length)
      throw new IllegalArgumentException("matrices must have equal square size");
    for (int[] r : a)
      if (r.length != a.length) throw new IllegalArgumentException("matrix must be square");
    for (int[] r : b)
      if (r.length != b.length) throw new IllegalArgumentException("matrix must be square");
  }
}
