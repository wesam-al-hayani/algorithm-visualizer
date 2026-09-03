package dev.wesam.visualizer.algorithms;

import java.util.Arrays;

public final class SearchAlgorithms {
    private SearchAlgorithms() { }

    public static int linearSearch(int[] values, int target) {
        for (int i = 0; i < values.length; i++) if (values[i] == target) return i;
        return -1;
    }

    public static int binarySearch(int[] sorted, int target) {
        int low = 0, high = sorted.length - 1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            if (sorted[middle] == target) return middle;
            if (sorted[middle] < target) low = middle + 1;
            else high = middle - 1;
        }
        return -1;
    }

    /** Returns the zero-based k-th smallest value. */
    public static int quickselect(int[] input, int k) {
        checkK(input, k);
        int[] a = input.clone();
        int low = 0, high = a.length - 1;
        while (true) {
            int pivot = partition(a, low, high, high);
            if (pivot == k) return a[pivot];
            if (pivot < k) low = pivot + 1;
            else high = pivot - 1;
        }
    }

    /** Deterministic linear-time selection using groups of five. */
    public static int medianOfMediansSelect(int[] input, int k) {
        checkK(input, k);
        return select(input.clone(), 0, input.length - 1, k);
    }

    private static int select(int[] a, int low, int high, int k) {
        if (low == high) return a[low];
        int pivotValue = choosePivot(a, low, high);
        int pivotIndex = low;
        while (a[pivotIndex] != pivotValue) pivotIndex++;
        pivotIndex = partition(a, low, high, pivotIndex);
        if (k == pivotIndex) return a[k];
        return k < pivotIndex ? select(a, low, pivotIndex - 1, k) : select(a, pivotIndex + 1, high, k);
    }

    private static int choosePivot(int[] a, int low, int high) {
        int length = high - low + 1;
        if (length <= 5) {
            insertionSortRange(a, low, high);
            return a[low + length / 2];
        }
        int medians = 0;
        for (int start = low; start <= high; start += 5) {
            int end = Math.min(start + 4, high);
            insertionSortRange(a, start, end);
            int median = start + (end - start) / 2;
            swap(a, low + medians++, median);
        }
        return select(a, low, low + medians - 1, low + medians / 2);
    }

    private static int partition(int[] a, int low, int high, int pivotIndex) {
        int pivot = a[pivotIndex];
        swap(a, pivotIndex, high);
        int boundary = low;
        for (int i = low; i < high; i++) if (a[i] < pivot) swap(a, boundary++, i);
        swap(a, boundary, high);
        return boundary;
    }

    private static void insertionSortRange(int[] a, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int value = a[i], j = i - 1;
            while (j >= low && a[j] > value) { a[j + 1] = a[j]; j--; }
            a[j + 1] = value;
        }
    }

    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }
    private static void checkK(int[] values, int k) {
        if (k < 0 || k >= values.length) throw new IllegalArgumentException("k must be between 0 and n - 1");
    }

    public static int[] sortedCopy(int[] values) {
        return SortAlgorithms.sort(values, SortAlgorithms.Kind.MERGE);
    }
}

