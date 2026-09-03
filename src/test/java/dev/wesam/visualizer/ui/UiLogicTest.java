package dev.wesam.visualizer.ui;

import static org.junit.jupiter.api.Assertions.*;

import dev.wesam.visualizer.catalog.AlgorithmCatalog;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.prefs.Preferences;
import org.junit.jupiter.api.Test;

class UiLogicTest {
  @Test
  void playbackMovesThroughStartPauseResumeAndFinishStates() {
    PlaybackController playback = new PlaybackController();
    AlgorithmRun run =
        new AlgorithmRun(List.of(step("first"), step("second"), step("third")), "finished result");

    assertEquals(PlaybackController.State.READY, playback.state());
    playback.load(run);
    playback.start();
    assertEquals(PlaybackController.State.RUNNING, playback.state());
    assertEquals("first", playback.advanceOne().orElseThrow().message());
    assertEquals(1, playback.position());

    playback.pause();
    assertEquals(PlaybackController.State.PAUSED, playback.state());
    playback.resume();
    assertEquals(PlaybackController.State.RUNNING, playback.state());
    assertEquals("second", playback.advanceOne().orElseThrow().message());
    assertEquals("third", playback.advanceOne().orElseThrow().message());
    assertEquals(PlaybackController.State.FINISHED, playback.state());
    assertTrue(playback.advanceOne().isEmpty());
    assertEquals("finished result", playback.result());
  }

  @Test
  void playbackResetAndEmptyRunHavePredictableState() {
    PlaybackController playback = new PlaybackController();
    playback.load(new AlgorithmRun(List.of(), "nothing to do"));
    playback.start();
    assertEquals(PlaybackController.State.FINISHED, playback.state());
    assertEquals(0, playback.totalSteps());
    playback.reset();
    assertFalse(playback.hasRun());
    assertEquals(0, playback.position());
    assertEquals(PlaybackController.State.READY, playback.state());
  }

  @Test
  void gridEditingPreservesSingleStartAndTarget() {
    String grid = "S.../..../...T";
    grid = GridEditor.edit(grid, 1, 1, GridEditor.Mode.DRAW_WALLS);
    assertEquals("S.../.#../...T", grid);
    grid = GridEditor.edit(grid, 1, 2, GridEditor.Mode.SET_START);
    assertEquals(1, count(grid, 'S'));
    grid = GridEditor.edit(grid, 0, 3, GridEditor.Mode.SET_TARGET);
    assertEquals(1, count(grid, 'T'));
    assertEquals(grid, GridEditor.edit(grid, 99, 99, GridEditor.Mode.DRAW_WALLS));
  }

  @Test
  void gridClearAndRandomWallsAreValidAndDeterministicWithASeed() {
    assertEquals("S.../..../...T", GridEditor.clear("####/####/####"));
    String first = GridEditor.randomWalls("S...../....../.....T", new Random(42));
    String second = GridEditor.randomWalls("S...../....../.....T", new Random(42));
    assertEquals(first, second);
    assertEquals(1, count(first, 'S'));
    assertEquals(1, count(first, 'T'));
    assertTrue(first.contains("#"));
  }

  @Test
  void generatedInputsAreDeterministicAndRunnable() {
    for (var demo : AlgorithmCatalog.create()) {
      String generated = InputGenerator.generate(demo, demo.defaultInput(), new Random(1234));
      assertNotNull(generated);
      assertDoesNotThrow(() -> demo.runner().apply(generated), demo.name());
    }
  }

  @Test
  void editHistorySupportsBoundedUndoAndRedo() {
    EditHistory history = new EditHistory();
    String value = "initial";
    for (int i = 0; i < 60; i++) value = history.apply(value, "edit-" + i);
    assertEquals("edit-58", history.undo(value).orElseThrow());
    assertEquals("edit-59", history.redo("edit-58").orElseThrow());
    history.clear();
    assertTrue(history.undo(value).isEmpty());
    assertTrue(history.redo(value).isEmpty());
  }

  @Test
  void favoritesAndRecentAlgorithmsUseStableCatalogIdentifiers() throws Exception {
    Preferences preferences =
        Preferences.userRoot().node("algorithm-lab-tests/" + UUID.randomUUID());
    try {
      AlgorithmHistory history = new AlgorithmHistory(preferences);
      var catalog = AlgorithmCatalog.create();
      var first = catalog.get(0);
      var second = catalog.get(1);
      history.setFavorite(first, true);
      assertTrue(history.isFavorite(first));
      assertEquals(List.of(first), history.favorites(catalog));
      history.setFavorite(first, false);
      assertFalse(history.isFavorite(first));

      history.recordViewed(first);
      history.recordViewed(second);
      history.recordViewed(first);
      assertEquals(List.of(first, second), history.recentlyViewed(catalog));
      for (int index = 2; index < 14; index++) history.recordViewed(catalog.get(index));
      assertEquals(8, history.recentlyViewed(catalog).size());
    } finally {
      preferences.removeNode();
    }
  }

  @Test
  void treeLabBuildsPersistentOperationHistoryAndDeterministicRandomTrees() {
    assertTrue(TreeLabInput.supports("AVL Tree"));
    assertFalse(TreeLabInput.supports("Binary Tree Inorder"));
    String history = TreeLabInput.append("", TreeLabInput.Operation.INSERT, 20);
    history = TreeLabInput.append(history, TreeLabInput.Operation.SEARCH, 20);
    history = TreeLabInput.append(history, TreeLabInput.Operation.DELETE, 20);
    assertEquals("20,?20,-20", history);
    assertThrows(
        IllegalArgumentException.class,
        () -> TreeLabInput.append("", TreeLabInput.Operation.INSERT, -1));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            TreeLabInput.append(
                java.util.stream.IntStream.range(0, 100)
                    .mapToObj(String::valueOf)
                    .collect(java.util.stream.Collectors.joining(",")),
                TreeLabInput.Operation.INSERT,
                101));
    String first = TreeLabInput.randomTree(new Random(19), 12);
    assertEquals(first, TreeLabInput.randomTree(new Random(19), 12));
    assertEquals(12, first.split(",").length);
    assertThrows(IllegalArgumentException.class, () -> TreeLabInput.randomTree(new Random(), 0));
  }

  private static AlgorithmStep step(String message) {
    return AlgorithmStep.text(message, "details", Map.of());
  }

  private static long count(String value, char symbol) {
    return value.chars().filter(character -> character == symbol).count();
  }
}
