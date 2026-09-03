package dev.wesam.visualizer.ui;

import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.Objects;
import java.util.Optional;

/**
 * Small UI-independent state machine for algorithm playback. JavaFX supplies the clock; this class
 * owns the current run and guarantees that one advance consumes exactly one logical step.
 */
public final class PlaybackController {
  public enum State {
    READY,
    RUNNING,
    PAUSED,
    FINISHED
  }

  private AlgorithmRun run;
  private int position;
  private State state = State.READY;

  public void load(AlgorithmRun newRun) {
    run = Objects.requireNonNull(newRun);
    position = 0;
    state = run.steps().isEmpty() ? State.FINISHED : State.READY;
  }

  public void start() {
    if (hasRemainingSteps()) state = State.RUNNING;
  }

  public void pause() {
    if (state == State.RUNNING) state = State.PAUSED;
  }

  public void resume() {
    if (state == State.PAUSED && hasRemainingSteps()) state = State.RUNNING;
  }

  public Optional<AlgorithmStep> advanceOne() {
    if (!hasRemainingSteps()) {
      if (run != null) state = State.FINISHED;
      return Optional.empty();
    }
    AlgorithmStep step = run.steps().get(position++);
    if (!hasRemainingSteps()) state = State.FINISHED;
    return Optional.of(step);
  }

  public void reset() {
    run = null;
    position = 0;
    state = State.READY;
  }

  public boolean hasRun() {
    return run != null;
  }

  public boolean hasRemainingSteps() {
    return run != null && position < run.steps().size();
  }

  public int position() {
    return position;
  }

  public int totalSteps() {
    return run == null ? 0 : run.steps().size();
  }

  public String result() {
    return run == null ? "" : run.result();
  }

  public State state() {
    return state;
  }
}
