package dev.wesam.visualizer.ui;

import static org.junit.jupiter.api.Assertions.*;

import dev.wesam.visualizer.catalog.AlgorithmCatalog;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

  private static AlgorithmStep step(String message) {
    return AlgorithmStep.text(message, "details", Map.of());
  }

  private static long count(String value, char symbol) {
    return value.chars().filter(character -> character == symbol).count();
  }
}
