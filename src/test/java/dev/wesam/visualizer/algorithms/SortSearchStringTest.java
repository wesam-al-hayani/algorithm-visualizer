package dev.wesam.visualizer.algorithms;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortSearchStringTest {
    @Test void everySortHandlesRepresentativeInputs() {
        int[][] cases = {{}, {1}, {1, 2, 3}, {5, 4, 3, 2, 1}, {3, -1, 3, 0, -1}, {9, 2, 7, 2, 5}};
        for (SortAlgorithms.Kind kind : SortAlgorithms.Kind.values()) {
            for (int[] input : cases) {
                int[] sorted = SortAlgorithms.sort(input, kind);
                for (int i = 1; i < sorted.length; i++) assertTrue(sorted[i - 1] <= sorted[i], kind.toString());
                assertEquals(input.length, sorted.length);
            }
        }
    }

    @Test void searchesFoundMissingAndBoundaries() {
        int[] values = {2, 4, 6, 8, 10};
        assertEquals(0, SearchAlgorithms.linearSearch(values, 2));
        assertEquals(4, SearchAlgorithms.binarySearch(values, 10));
        assertEquals(-1, SearchAlgorithms.binarySearch(values, 7));
        assertEquals(-1, SearchAlgorithms.linearSearch(new int[0], 1));
    }

    @Test void selectionVariantsFindEveryRankWithDuplicates() {
        int[] values = {8, 2, 5, 2, 9, 1, 7};
        int[] expected = {1, 2, 2, 5, 7, 8, 9};
        for (int k = 0; k < values.length; k++) {
            assertEquals(expected[k], SearchAlgorithms.quickselect(values, k));
            assertEquals(expected[k], SearchAlgorithms.medianOfMediansSelect(values, k));
        }
    }

    @Test void stringAlgorithmsFindOverlappingMatches() {
        for (List<Integer> actual : List.of(
                StringAlgorithms.naive("ababa", "aba"),
                StringAlgorithms.kmp("ababa", "aba"),
                StringAlgorithms.rabinKarp("ababa", "aba"))) {
            assertEquals(List.of(0, 2), actual);
        }
        assertArrayEquals(new int[]{0, 0, 1, 2, 3, 0, 1}, StringAlgorithms.prefixTable("ababaca"));
    }
}

