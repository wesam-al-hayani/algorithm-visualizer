# Project State

## Current Phase

Algorithm Lab V2.0 — Phase 1: safety and refactoring

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

## Last Completed Task

Application responsibility refactor; clean tests and JavaFX startup pass

## Current Task

Expand randomized/parameterized tests, add direct playback/grid tests, configure JaCoCo, and upgrade GitHub Actions to a multi-OS matrix.

## Next Task

Upgrade educational UI: algorithm search, highlighted pseudocode, themes, shortcuts, favorites/recent items, and focus mode.

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

## Known Problems

- Maven is not installed globally; the tested `./mvnw` launcher handles it.
- Red-Black Tree deletion and B-Tree deletion remain V1 limitations and are mandatory V2 work.
- V2 implementation is in progress; `main` must not be merged until all mandatory V2 work is verified.

## GitHub

- Repository: `wesam-al-hayani/algorithm-visualizer`
- Development branch: `codex-work`
- Last push successful: yes
- Stable V1 main commit: `3685d94cf251eeef236e04a209203dc4ac5f4005`
- V2 work branch: `codex-work`
- Pending commits: application responsibility refactor ready to commit
