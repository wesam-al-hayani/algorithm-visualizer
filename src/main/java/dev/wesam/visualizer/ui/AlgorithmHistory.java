package dev.wesam.visualizer.ui;

import dev.wesam.visualizer.catalog.AlgorithmDemo;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

/** Stores only stable demo identifiers; no algorithm input or execution data leaves the machine. */
public final class AlgorithmHistory {
  private static final String FAVORITES = "favorites";
  private static final String RECENT = "recent";
  private static final String DELIMITER = "\\n";
  private static final int RECENT_LIMIT = 8;

  private final Preferences preferences;

  public AlgorithmHistory() {
    this(Preferences.userNodeForPackage(AlgorithmHistory.class));
  }

  AlgorithmHistory(Preferences preferences) {
    this.preferences = preferences;
  }

  public boolean isFavorite(AlgorithmDemo demo) {
    return read(FAVORITES).contains(id(demo));
  }

  public void setFavorite(AlgorithmDemo demo, boolean favorite) {
    LinkedHashSet<String> ids = read(FAVORITES);
    if (favorite) ids.add(id(demo));
    else ids.remove(id(demo));
    write(FAVORITES, ids);
  }

  public void recordViewed(AlgorithmDemo demo) {
    LinkedHashSet<String> updated = new LinkedHashSet<>();
    updated.add(id(demo));
    updated.addAll(read(RECENT));
    while (updated.size() > RECENT_LIMIT)
      updated.remove(updated.toArray(String[]::new)[RECENT_LIMIT]);
    write(RECENT, updated);
  }

  public List<AlgorithmDemo> favorites(List<AlgorithmDemo> catalog) {
    return resolve(read(FAVORITES), catalog);
  }

  public List<AlgorithmDemo> recentlyViewed(List<AlgorithmDemo> catalog) {
    return resolve(read(RECENT), catalog);
  }

  private List<AlgorithmDemo> resolve(Set<String> ids, List<AlgorithmDemo> catalog) {
    List<AlgorithmDemo> result = new ArrayList<>();
    for (String id : ids)
      catalog.stream().filter(demo -> id(demo).equals(id)).findFirst().ifPresent(result::add);
    return result;
  }

  private LinkedHashSet<String> read(String key) {
    LinkedHashSet<String> values = new LinkedHashSet<>();
    String stored = preferences.get(key, "");
    if (!stored.isBlank())
      for (String value : stored.split(DELIMITER)) if (!value.isBlank()) values.add(value);
    return values;
  }

  private void write(String key, Set<String> values) {
    preferences.put(key, String.join("\n", values));
  }

  private String id(AlgorithmDemo demo) {
    return demo.category() + "::" + demo.name();
  }
}
