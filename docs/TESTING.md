# Testing

Algorithm Lab tests correctness below the JavaFX layer, then uses catalog and packaged-application checks to catch integration defects. The suite favors deterministic seeds, explicit invariants, and independent cross-validation instead of asserting only that code completes.

## Commands

Run the complete clean suite and coverage gate:

```bash
./mvnw --batch-mode clean test
```

Build the application module without rerunning tests:

```bash
./mvnw --batch-mode -DskipTests package
```

Run one focused class or selected classes:

```bash
./mvnw --batch-mode -Dtest=MazeAlgorithmsTest test
./mvnw --batch-mode -Dtest=AlgorithmCatalogTest,UiLogicTest test
```

After a test run, open `target/site/jacoco/index.html` to inspect the JaCoCo report. The Maven build fails if the configured core bundle drops below 80% line coverage.

## Test Layers

| Layer | What it verifies | Representative checks |
| --- | --- | --- |
| Focused unit tests | Expected outputs and edge cases | Sorting, selection, strings, shortest paths, flow, matching, maze, matrix, optimization |
| Invariant tests | Structural validity after each mutation | AVL balance/heights; Red-Black colors, parents, ordering, black-height; B-Tree occupancy/order/leaf depth; heap order |
| Randomized property tests | Behavior over many generated inputs with fixed seeds | Mixed tree operations, maze connectivity, graph analyses, flow networks, sorting arrays |
| Cross-validation | Agreement between independent algorithms or simple definitions | Sorts vs `Arrays.sort`; Floyd–Warshall vs Johnson/Dijkstra; Tarjan vs Kosaraju; matching implementations; Edmonds–Karp vs Dinic |
| State-machine tests | UI-independent interaction semantics | Start/pause/resume/step/reset, history undo/redo, input generation, selection history |
| Catalog smoke tests | Every visible demo remains runnable | Execute all 83 default inputs, verify category membership, pseudocode, and nonempty frames |
| Packaged UI checks | Real JavaFX integration | Navigation, keyboard shortcuts, themes, focus mode, comparison controls, chart/table rendering, tree/grid editing, export |

## Oracle and Property Strategy

### Arrays, Selection, and Strings

Generated arrays are compared with `Arrays.sort` in tests only. Search and selection outputs are checked against sorted positions or simple scans. String matching is compared with expected occurrence positions, including overlaps and no-match cases. The application implementations do not call these test oracles.

### Trees and Heaps

Randomized insert/delete sequences are checked after every operation rather than only at the end. Tree validators cover ordering and structure-specific rules. Traversals are compared with sorted reference sets where appropriate. Heap extraction order is checked against a simple ordered reference.

### Graphs

Small seeded graphs allow independent definitions that would be too slow for production use. SCC partitions from Tarjan and Kosaraju are normalized and compared. A reported bridge or articulation point is checked by removing the edge or vertex and measuring connectivity. Shortest-path implementations are cross-checked only when their input assumptions overlap; negative-cycle cases are tested explicitly.

For maximum flow, hundreds of deterministic random capacity networks are sent through both Edmonds–Karp and Dinic. They must agree on maximum-flow value, residual feasibility, and cut capacity. Matching algorithms similarly agree on cardinality across random bipartite graphs.

### Mazes and Grids

Each generator is deterministic for a given method, size, and seed. Tests assert rectangular cells, exactly one start and target, valid symbols, stable frame sequences, and BFS connectivity. Multi-size/multi-seed cases guard against rare disconnected outputs. Pure grid edits and bounded undo/redo are tested without JavaFX.

### Experimental Analysis

Count-only sorting paths are checked against the animated implementation's final result and known complexity relationships. Randomized Quick Sort experiments hold the base array and seed constant so min/max/average/median summaries are repeatable. Tests assert measured series structure without treating timing noise as correctness data.

## Adding a Test

- Use a fixed seed and include it in a failure message.
- Prefer a property or independent oracle over a copy of the implementation's control flow.
- Check boundaries: empty/singleton input, duplicates, disconnected graphs, invalid weights, full/empty structures, and parser limits.
- For a new catalog demo, test core logic directly and let `AlgorithmCatalogTest` cover its default integration path.
- Do not require a display for unit tests. Reserve real JavaFX interaction for the packaged manual checklist.

## Manual Release Checklist

After automated tests pass, launch the packaged application and verify:

1. Search and category navigation reach representative demos in all 10 categories.
2. Start, pause, resume, one-step, reset, generate, and speed changes behave consistently.
3. The active pseudocode line and operation text change with the canvas state.
4. Light/dark theme, focus mode, favorites, recent history, and keyboard shortcuts work after navigation.
5. Tree insert/search/delete/clear/random actions preserve visible invariants.
6. Grid drawing, erase, start/target movement, all maze generators, undo, and redo work.
7. Sorting, shortest-path, and flow comparison selectors produce shared-input results.
8. Complexity and Quick Sort experiment charts remain legible after resizing.
9. Invalid inputs show friendly format guidance without exception internals.
10. TXT and CSV exports contain the selected demo, input, operation, statistics, and details.

The release gate additionally requires clean package and jlink builds, platform-native packaging configuration, a clean Git status, and successful GitHub Actions checks.
