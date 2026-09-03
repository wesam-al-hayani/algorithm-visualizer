package dev.wesam.visualizer.algorithms;

import java.util.ArrayList;
import java.util.List;

public final class StringAlgorithms {
  private StringAlgorithms() {}

  public static List<Integer> naive(String text, String pattern) {
    List<Integer> matches = new ArrayList<>();
    if (pattern.isEmpty()) {
      for (int i = 0; i <= text.length(); i++) matches.add(i);
      return matches;
    }
    for (int start = 0; start + pattern.length() <= text.length(); start++) {
      int j = 0;
      while (j < pattern.length() && text.charAt(start + j) == pattern.charAt(j)) j++;
      if (j == pattern.length()) matches.add(start);
    }
    return matches;
  }

  public static int[] prefixTable(String pattern) {
    int[] table = new int[pattern.length()];
    for (int i = 1, length = 0; i < pattern.length(); ) {
      if (pattern.charAt(i) == pattern.charAt(length)) table[i++] = ++length;
      else if (length > 0) length = table[length - 1];
      else table[i++] = 0;
    }
    return table;
  }

  public static List<Integer> kmp(String text, String pattern) {
    if (pattern.isEmpty()) return naive(text, pattern);
    int[] table = prefixTable(pattern);
    List<Integer> matches = new ArrayList<>();
    int i = 0, j = 0;
    while (i < text.length()) {
      if (text.charAt(i) == pattern.charAt(j)) {
        i++;
        j++;
      }
      if (j == pattern.length()) {
        matches.add(i - j);
        j = table[j - 1];
      } else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
        if (j > 0) j = table[j - 1];
        else i++;
      }
    }
    return matches;
  }

  public static List<Integer> rabinKarp(String text, String pattern) {
    List<Integer> matches = new ArrayList<>();
    if (pattern.isEmpty()) return naive(text, pattern);
    if (pattern.length() > text.length()) return matches;
    long modulus = 1_000_000_007L, base = 257, power = 1, patternHash = 0, windowHash = 0;
    for (int i = 0; i < pattern.length(); i++) {
      patternHash = (patternHash * base + pattern.charAt(i)) % modulus;
      windowHash = (windowHash * base + text.charAt(i)) % modulus;
      if (i + 1 < pattern.length()) power = power * base % modulus;
    }
    for (int start = 0; start + pattern.length() <= text.length(); start++) {
      if (patternHash == windowHash && text.regionMatches(start, pattern, 0, pattern.length()))
        matches.add(start);
      if (start + pattern.length() < text.length()) {
        windowHash = (windowHash - text.charAt(start) * power % modulus + modulus) % modulus;
        windowHash = (windowHash * base + text.charAt(start + pattern.length())) % modulus;
      }
    }
    return matches;
  }
}
