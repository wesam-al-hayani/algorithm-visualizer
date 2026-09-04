# Architecture

Algorithm Lab deliberately uses a small architecture. Algorithm correctness does not depend on JavaFX, while the JavaFX layer consumes immutable snapshots instead of reaching into algorithm internals.

```text
Algorithm or data structure logic
              ↓
         AlgorithmRun
              ↓
         AlgorithmStep
              ↓
     PlaybackController
              ↓
   VisualizationCanvas
```

## Execution Model

An algorithm adapter parses input and calls a real implementation. The resulting `AlgorithmRun` contains the full ordered list of immutable `AlgorithmStep` snapshots plus final statistics and explanatory details. Each step identifies a `VisualKind`, the active pseudocode line, a human-readable operation, and the state needed by its renderer.

`PlaybackController` owns a small state machine: ready, playing, paused, and completed. A JavaFX `Timeline` asks it for the next frame during continuous playback. The same transition serves manual **Step**, so one press advances exactly one logical frame. Selection and input changes cancel the previous timeline before they create another run.

`VisualizationCanvas` renders the current snapshot. It contains renderers for arrays, graphs, trees, grids, text alignment, tables, sets, and charts. Rendering is a projection of state; it does not perform the algorithm.

## Package Responsibilities

| Package | Responsibility |
| --- | --- |
| `dev.wesam.visualizer.algorithms` | UI-independent implementations for arrays, strings, graphs, flow, matching, mazes, optimization, matrix operations, and empirical measurements |
| `dev.wesam.visualizer.structures` | Teaching-oriented trees and heaps with operations, event traces, and invariant validation |
| `dev.wesam.visualizer.catalog` | Category-specific demo definitions: metadata, input grammar, example, parser, execution adapter, explanation, complexity, and pseudocode |
| `dev.wesam.visualizer.model` | Immutable shared values such as `AlgorithmRun`, `AlgorithmStep`, statistics, and `VisualKind` |
| `dev.wesam.visualizer.ui` | Playback, editing, history, generation, export, input feedback, preferences, and canvas rendering |
| `dev.wesam.visualizer` | JavaFX composition root and application lifecycle |

Catalogs are split by subject rather than collected in one large registry. `AlgorithmCatalog` combines them into the 83-demo list used by navigation, search, default-input smoke tests, and documentation counts.

## Data and Control Flow

```text
category / search / favorite / recent selection
                         ↓
                 AlgorithmDefinition
                         ↓
                  parse user input
                         ↓
      real algorithm produces snapshots + statistics
                         ↓
                    AlgorithmRun
                         ↓
 Start / Pause / Resume / Step / Reset / speed control
                         ↓
                 PlaybackController
                         ↓
 operation text + pseudocode highlight + statistics + canvas
```

Invalid input stops before execution. `InputFeedback` converts parser failures into a short message that includes the selected demo's expected format and default example; normal validation never exposes a stack trace or exception class.

## Why Runs Are Precomputed

Precomputing a bounded teaching run makes playback deterministic, makes stepping reversible by frame index, keeps JavaFX work on the application thread, and lets tests inspect every snapshot without launching a window. It also separates logical visualization steps from measurement-only execution: sorting experiments use count-only paths through the same algorithm choices so frame allocation does not distort results.

The tradeoff is memory proportional to the number and size of snapshots. UI input limits bound this cost, especially for exponential algorithms and large matrices.

## Interactive Labs

### Trees

BST, AVL, Red-Black, and B-Tree demos keep a structure instance across toolbar operations. Insert, search, delete, clear, and random-tree actions produce a new run from the current tree state. Repair events—rotations, recoloring, double-black cases, borrowing, merging, and root shrinking—are preserved in the visual trace. The core structures expose invariant validators used by randomized tests.

### Grids and Mazes

`GridEditor` is a pure transformation layer for walls, erasing, start/target moves, and clearing. `EditHistory` keeps a bounded undo/redo sequence. Drag gestures are grouped into coherent edits, and maze generation participates in the same history. `MazeAlgorithms` produces seeded frame sequences and guarantees endpoint connectivity.

### Comparisons and Charts

Comparison adapters run algorithms against one shared input and merge their snapshots into side-by-side state. They report algorithmic operations separately from visualization frames. `VisualKind.CHART` renders measured and theoretical series on responsive axes for complexity and randomized trials.

## Persistent and Exported State

`Preferences` stores theme, favorites, and recent selections; no algorithm state or sensitive data leaves the machine. `ResultExporter` serializes only the currently visible demo name, input, operation, statistics, and details into dependency-free text or RFC-style CSV content. The JavaFX application lets the user choose the destination with a native file chooser.

## Threading

JavaFX scene changes occur on the JavaFX application thread. The playback timeline never launches overlapping algorithm tasks: runs are computed synchronously within the deliberately bounded interactive limits, and selecting another demo stops current playback first. Tests exercise algorithm and controller code without a JavaFX toolkit.

## Adding a Demonstration

1. Put reusable correctness logic in `algorithms` or `structures`; do not embed it in a canvas renderer.
2. Add focused unit tests, including an independent oracle or invariant where practical.
3. Add a category-catalog definition with a precise input grammar, safe example, explanation, complexity, and algorithm-specific pseudocode.
4. Convert the implementation trace to immutable `AlgorithmStep` values and choose the closest existing `VisualKind`.
5. If a new visual form is genuinely needed, add one `VisualKind` and a renderer that only reads snapshot data.
6. Ensure the catalog-default test executes the new definition and update the coverage matrix.

This path keeps new algorithms testable from the command line and prevents the UI from becoming a second, divergent implementation.
