package dev.wesam.visualizer.algorithms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class RandomizedSortSearchTest {
  @ParameterizedTest(name = "{0} representative case {1}")
  @MethodSource("sortCases")
  void everySortMatchesTheJdkOracle(SortAlgorithms.Kind kind, int[] input) {
    int[] expected = input.clone();
    Arrays.sort(expected);
    assertArrayEquals(expected, SortAlgorithms.sort(input, kind));
  }

  static Stream<Arguments> sortCases() {
    List<int[]> cases =
        List.of(
            new int[] {},
            new int[] {7},
            new int[] {-3, -1, -2, 0},
            new int[] {4, 4, 4, 4},
            new int[] {1, 2, 3, 4, 5},
            new int[] {5, 4, 3, 2, 1},
            new int[] {9, -2, 7, 0, -2, 5});
    List<Arguments> arguments = new ArrayList<>();
    for (SortAlgorithms.Kind kind : SortAlgorithms.Kind.values())
      for (int[] input : cases) arguments.add(Arguments.of(kind, input));
    for (SortAlgorithms.Kind kind : SortAlgorithms.Kind.values())
      if (kind != SortAlgorithms.Kind.COUNTING)
        arguments.add(
            Arguments.of(
                kind,
                new int[] {Integer.MAX_VALUE, 0, Integer.MIN_VALUE, -1, 1, Integer.MAX_VALUE}));
    return arguments.stream();
  }

  @ParameterizedTest
  @EnumSource(SortAlgorithms.Kind.class)
  void randomizedSortsMatchTheJdkOracle(SortAlgorithms.Kind kind) {
    Random random = new Random(0x51A7 + kind.ordinal());
    for (int trial = 0; trial < 80; trial++) {
      int[] input = random.ints(random.nextInt(45), -100, 101).toArray();
      int[] expected = input.clone();
      Arrays.sort(expected);
      assertArrayEquals(expected, SortAlgorithms.sort(input, kind), "trial " + trial);
    }
  }

  @Test
  void randomizedSelectionMatchesASortedCopy() {
    Random random = new Random(0x5E1EC7);
    for (int trial = 0; trial < 120; trial++) {
      int[] values = random.ints(1 + random.nextInt(60), -40, 41).toArray();
      int[] expected = values.clone();
      Arrays.sort(expected);
      for (int k : new int[] {0, expected.length / 2, expected.length - 1}) {
        assertEquals(expected[k], SearchAlgorithms.quickselect(values, k));
        assertEquals(expected[k], SearchAlgorithms.medianOfMediansSelect(values, k));
      }
    }
  }

  @Test
  void randomizedSearchesReturnValidIndices() {
    Random random = new Random(0xB1A2);
    for (int trial = 0; trial < 150; trial++) {
      int[] values = random.ints(random.nextInt(80), -30, 31).toArray();
      int[] sorted = values.clone();
      Arrays.sort(sorted);
      int target = random.nextInt(81) - 40;
      int linear = SearchAlgorithms.linearSearch(values, target);
      int binary = SearchAlgorithms.binarySearch(sorted, target);
      assertEquals(firstIndex(values, target), linear);
      assertEquals(contains(sorted, target), binary >= 0);
      if (binary >= 0) assertEquals(target, sorted[binary]);
    }
  }

  @Test
  void selectionRejectsRanksOutsideTheArray() {
    assertThrows(IllegalArgumentException.class, () -> SearchAlgorithms.quickselect(new int[0], 0));
    assertThrows(
        IllegalArgumentException.class,
        () -> SearchAlgorithms.medianOfMediansSelect(new int[] {1, 2}, -1));
    assertThrows(
        IllegalArgumentException.class,
        () -> SearchAlgorithms.medianOfMediansSelect(new int[] {1, 2}, 2));
  }

  @ParameterizedTest
  @MethodSource("stringCases")
  void allStringMatchersAgree(String text, String pattern) {
    List<Integer> expected = StringAlgorithms.naive(text, pattern);
    assertEquals(expected, StringAlgorithms.kmp(text, pattern));
    assertEquals(expected, StringAlgorithms.rabinKarp(text, pattern));
  }

  static Stream<Arguments> stringCases() {
    return Stream.of(
        Arguments.of("", ""),
        Arguments.of("abc", ""),
        Arguments.of("aaaaa", "aa"),
        Arguments.of("mississippi", "issi"),
        Arguments.of("algorithm", "rhythm"),
        Arguments.of("a", "longer"),
        Arguments.of("A B A B", "A B"));
  }

  private static int firstIndex(int[] values, int target) {
    for (int i = 0; i < values.length; i++) if (values[i] == target) return i;
    return -1;
  }

  private static boolean contains(int[] values, int target) {
    for (int value : values) if (value == target) return true;
    return false;
  }
}
