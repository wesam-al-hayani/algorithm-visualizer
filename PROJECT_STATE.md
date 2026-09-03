# Project State

## Current Phase

Algorithm Lab V2.0 — Phase 5: advanced graph algorithms

## Completed

- [x] Inspect workspace and GitHub authentication
- [x] Confirm repository names are available
- [x] Create Maven/JavaFX project metadata
- [x] Add interruption-safe checkpoint script
- [x] Create JavaFX application shell
- [x] Implement 61 algorithm/data-structure demonstrations
- [x] Add responsive step-based visualizers and playback controls
- [x] Add direct grid editing and maze generation
- [x] Add JUnit correctness, invariant, and catalog coverage tests
- [x] Add algorithm coverage and project documentation
- [x] Complete automated and manual verification
- [x] Verify GitHub Actions on development checkpoints
- [x] Recover and verify the V1 repository before V2 changes
- [x] Confirm `main` remains clean at `3685d94cf251eeef236e04a209203dc4ac5f4005`
- [x] Switch to the existing `codex-work` development branch
- [x] Re-run the complete V1 test suite as the V2 baseline
- [x] Split the 1,782-line `AlgorithmCatalog` into nine category catalogs and shared helpers
- [x] Extract a UI-independent `PlaybackController` state machine
- [x] Extract `InputGenerator` and pure `GridEditor` transformations
- [x] Rewire the JavaFX entry point without changing the step-based playback model
- [x] Add parameterized and deterministic randomized tests for every V1 core area
- [x] Add direct tests for playback state, grid editing, and generated inputs
- [x] Add JaCoCo 0.8.15 with an enforced 80% core line-coverage floor
- [x] Expand GitHub Actions to Ubuntu, macOS, and Windows
- [x] Make CI run clean tests, coverage verification, and packaging
- [x] Replace shared placeholder text with algorithm-specific pseudocode for all 61 demonstrations
- [x] Highlight the active pseudocode line during playback
- [x] Add immediate algorithm search across names, categories, and explanations
- [x] Add persistent favorites and an eight-item recently viewed history
- [x] Add complete light/dark themes, focus mode, and keyboard shortcuts
- [x] Add bounded undo/redo history for interactive grid edits
- [x] Add an About dialog with project and runtime attribution
- [x] Add a full AVL tree with insert/search/delete, height and balance metadata, and all four rotation cases
- [x] Add deterministic AVL rotation tests and 15,400 randomized insert/delete invariant checks
- [x] Implement Red-Black Tree deletion with successor replacement and double-black repair
- [x] Expose recoloring, rotations, and all four sibling repair cases to the visualizer
- [x] Validate Red-Black parent links and run 20,000 randomized mixed operations across every repair case
- [x] Implement textbook B-Tree deletion for leaves and internal nodes
- [x] Expose predecessor/successor replacement, left/right borrowing, merging, and root shrinking
- [x] Verify B-Tree insert/delete invariants across minimum degrees 2, 3, 4, and 6
- [x] Add persistent Insert/Search/Delete/Clear/Random Tree controls for BST, AVL, Red-Black, and B-Tree
- [x] Keep the current tree, operation count, repair events, and invariant statistics visible after every operation
- [x] Fix re-entrant Favorites/Recently Viewed navigation uncovered by the packaged UI smoke test
- [x] Add Floyd-Warshall distance/next matrices, selected-path reconstruction, and negative-cycle detection
- [x] Add Johnson's Algorithm with Bellman-Ford potentials, edge reweighting, and repeated Dijkstra
- [x] Add weighted-graph A* with Euclidean heuristic plus g/h/f and open/closed trace frames
- [x] Cross-validate all-pairs results on random nonnegative graphs and negative-edge DAGs

## Last Completed Task

Phase 5 shortest paths: Floyd-Warshall, Johnson, and weighted-graph A* with randomized cross-validation

## Current Task

Implement Tarjan SCC, bridges, articulation points, Euler path/circuit, bipartite check, and Hopcroft-Karp with trace data and cross-validation.

## Next Task

Implement mandatory Dinic flow, visualize it, and cross-validate it against Edmonds-Karp.

## Tests

- 23 JUnit test methods: PASS
- Every one of 61 demo default inputs: PASS
- Final `./mvnw clean test`: PASS
- `./mvnw -DskipTests package`: PASS
- `./mvnw javafx:run` startup: PASS
- Manual UI: Start, Pause, Resume, one-step, Reset, Generate, speed, category switching, input-change cancellation, invalid-input alert, graph rendering, grid toolbar, maze generation, dropdown contrast, and resizing: PASS
- V2 baseline `./mvnw --batch-mode clean test`: PASS (2026-09-03)
- Catalog refactor `./mvnw --batch-mode test`: PASS (23 tests, 61 catalog demos)
- Application refactor `./mvnw --batch-mode clean test`: PASS (23 tests)
- Application refactor `./mvnw javafx:run` startup: PASS
- Phase 2 `./mvnw --batch-mode clean test`: PASS (147 JUnit invocations)
- JaCoCo core line coverage: 97.74% (1,641 / 1,679 lines); required minimum: 80%
- JaCoCo core branch coverage: 87.14% (1,098 / 1,260 branches)
- Phase 2 `./mvnw --batch-mode -DskipTests package`: PASS
- Phase 3 `./mvnw --batch-mode clean test`: PASS (149 JUnit invocations)
- Phase 3 JaCoCo core line coverage: 97.70% (1,702 / 1,742 lines); required minimum: 80%
- Phase 3 JaCoCo core branch coverage: 87.29% (1,126 / 1,290 branches)
- Phase 3 `./mvnw --batch-mode -DskipTests package`: PASS
- Phase 3 packaged UI smoke test: search, result navigation, favorites, recents, real pseudocode, active-line highlighting, light/dark themes, focus mode, and search/step shortcuts: PASS
- AVL `./mvnw --batch-mode clean test`: PASS (151 JUnit invocations; 62 catalog demos)
- AVL JaCoCo core line coverage: 97.83% (1,807 / 1,847 lines); required minimum: 80%
- AVL JaCoCo core branch coverage: 87.24% (1,183 / 1,356 branches)
- Red-Black deletion `./mvnw --batch-mode clean test`: PASS (153 JUnit invocations)
- Red-Black deletion JaCoCo core line coverage: 98.06% (1,916 / 1,954 lines); required minimum: 80%
- Red-Black deletion JaCoCo core branch coverage: 87.27% (1,241 / 1,422 branches)
- B-Tree deletion `./mvnw --batch-mode clean test`: PASS (155 JUnit invocations)
- B-Tree deletion JaCoCo core line coverage: 98.19% (2,006 / 2,043 lines); required minimum: 80%
- B-Tree deletion JaCoCo core branch coverage: 87.67% (1,294 / 1,476 branches)
- Phase 4 final `./mvnw --batch-mode clean test`: PASS (156 JUnit invocations; 62 catalog demos)
- Phase 4 final JaCoCo core line coverage: 98.21% (2,031 / 2,068 lines); required minimum: 80%
- Phase 4 final JaCoCo core branch coverage: 87.69% (1,311 / 1,495 branches)
- Phase 4 `./mvnw --batch-mode -DskipTests package`: PASS
- Phase 4 packaged UI smoke test: recent-item navigation; BST lab; AVL insert/search/delete/clear/random; Red-Black deletion and repair events; B-Tree deletion and merge events: PASS
- Phase 5 shortest paths `./mvnw --batch-mode clean test`: PASS (159 JUnit invocations; 65 catalog demos)
- Phase 5 shortest paths JaCoCo core line coverage: 98.35% (2,201 / 2,238 lines); required minimum: 80%
- Phase 5 shortest paths JaCoCo core branch coverage: 87.71% (1,413 / 1,611 branches)

## Known Problems

- Maven is not installed globally; the tested `./mvnw` launcher handles it.
- V2 implementation is in progress; `main` must not be merged until all mandatory V2 work is verified.

## GitHub

- Repository: `wesam-al-hayani/algorithm-visualizer`
- Development branch: `codex-work`
- Last push successful: yes
- Stable V1 main commit: `3685d94cf251eeef236e04a209203dc4ac5f4005`
- V2 work branch: `codex-work`
- Pending commits: Phase 5 shortest-path checkpoint ready to commit
