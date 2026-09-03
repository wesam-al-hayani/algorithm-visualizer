package dev.wesam.visualizer.catalog;

import static dev.wesam.visualizer.catalog.CatalogSupport.*;
import static dev.wesam.visualizer.model.AlgorithmStep.VisualKind.*;

import dev.wesam.visualizer.algorithms.EducationalHashTable;
import dev.wesam.visualizer.algorithms.StringAlgorithms;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class StringHashCatalog {
  private StringHashCatalog() {}

  static List<AlgorithmDemo> create() {
    List<AlgorithmDemo> demos = new ArrayList<>();
    demos.add(
        demo(
            "Strings & Hashing",
            "Naive String Matching",
            "Aligns the pattern at every possible text position.",
            "for every alignment s\n  compare pattern left to right\n  report complete matches",
            "O(nm)",
            "O(1)",
            "text ; pattern",
            "ABABACABABA ; ABA",
            input -> stringMatch(input, "naive")));
    demos.add(
        demo(
            "Strings & Hashing",
            "Knuth–Morris–Pratt",
            "Uses a prefix table to avoid rechecking characters known to match.",
            "build longest-prefix-suffix table\n"
                + "compare text and pattern\n"
                + "on mismatch follow prefix link",
            "O(n + m)",
            "O(m)",
            "text ; pattern",
            "ABABACABABA ; ABABA",
            input -> stringMatch(input, "kmp")));
    demos.add(
        demo(
            "Strings & Hashing",
            "Rabin–Karp",
            "Compares rolling hashes before verifying candidate matches.",
            "hash pattern and first window\ncompare hashes\nroll hash to next alignment",
            "Expected O(n + m)",
            "O(1)",
            "text ; pattern",
            "THE QUICK BROWN FOX ; OWN",
            input -> stringMatch(input, "rabin")));
    for (EducationalHashTable.Strategy strategy : EducationalHashTable.Strategy.values()) {
      demos.add(
          demo(
              "Strings & Hashing",
              title(strategy.name()),
              "Interactive educational hash table; probes and collisions are taken from real"
                  + " insert/search/delete operations.",
              "bucket ← floorMod(key, capacity)\n"
                  + "follow chain or probing sequence\n"
                  + "insert, search, or mark deleted",
              "Expected O(1), worst O(n)",
              "O(n)",
              "Keys to insert; prefix ? to search, - to delete",
              "12,23,34,8,19,?34,-23",
              input -> hashDemo(input, strategy)));
    }
    return List.copyOf(demos);
  }

  static AlgorithmRun stringMatch(String input, String method) {
    String[] p = input.split(";", 2);
    if (p.length < 2) throw new IllegalArgumentException("Use: text ; pattern");
    String text = p[0].trim(), pattern = p[1].trim();
    if (text.length() > 500 || pattern.length() > 100)
      throw new IllegalArgumentException(
          "String visualization is limited to 500 text and 100 pattern characters");
    List<Integer> matches =
        switch (method) {
          case "naive" -> StringAlgorithms.naive(text, pattern);
          case "kmp" -> StringAlgorithms.kmp(text, pattern);
          default -> StringAlgorithms.rabinKarp(text, pattern);
        };
    List<AlgorithmStep> s = new ArrayList<>();
    for (int i = 0; i <= Math.max(0, text.length() - pattern.length()); i++)
      s.add(
          new AlgorithmStep(
              "Align pattern at index " + i,
              "align pattern\ncompare characters\nshift using algorithm rule",
              1,
              TEXT,
              List.of(),
              chars(text),
              rangeSet(i, Math.min(text.length() - 1, i + pattern.length() - 1)),
              matches.contains(i) ? rangeSet(i, i + pattern.length() - 1) : Set.of(),
              Set.copyOf(matches),
              List.of(),
              Map.of("Alignments", i + 1, "Matches", countThrough(matches, i)),
              "Pattern: "
                  + pattern
                  + (method.equals("kmp")
                      ? "\nPrefix table: " + Arrays.toString(StringAlgorithms.prefixTable(pattern))
                      : "")));
    return new AlgorithmRun(s, "Match positions: " + matches);
  }

  static AlgorithmRun hashDemo(String input, EducationalHashTable.Strategy strategy) {
    EducationalHashTable table = new EducationalHashTable(11, strategy);
    List<AlgorithmStep> s = new ArrayList<>();
    int collisions = 0;
    String[] operations = input.split(",");
    if (operations.length > 100)
      throw new IllegalArgumentException("Hash visualization is limited to 100 operations");
    for (String raw : operations) {
      String token = raw.trim();
      if (token.isEmpty()) continue;
      EducationalHashTable.OperationResult r;
      if (token.startsWith("?")) r = table.search(Integer.parseInt(token.substring(1)));
      else if (token.startsWith("-")) r = table.delete(Integer.parseInt(token.substring(1)));
      else r = table.insert(Integer.parseInt(token));
      collisions += r.collisions();
      s.add(
          new AlgorithmStep(
              r.message(),
              "hash key\nfollow collision strategy\nperform operation",
              1,
              TABLE,
              List.of(),
              table.snapshot(),
              Set.copyOf(r.probes()),
              Set.of(r.hash()),
              Set.of(),
              List.of(),
              Map.of("Hash", r.hash(), "Probes", r.probes().size(), "Collisions", collisions),
              "Operation: " + token));
    }
    return new AlgorithmRun(s, "Table: " + table.snapshot());
  }
}
