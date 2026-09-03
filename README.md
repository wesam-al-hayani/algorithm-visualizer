# Algorithm & Data Structure Visualizer

A broad, step-driven JavaFX study tool for algorithms and data structures covered in TU Dortmund's DAP1, DAP2, and Efficient Algorithms courses. It is designed as a readable university portfolio project: algorithm logic is independent from JavaFX, every visible demo runs real code, and the UI exposes the state that changes during execution.

The application contains **57 interactive demonstrations** across nine sections. It is intentionally much more than a sorting visualizer.

## Highlights

- One consistent playback model with **Start, Pause, Resume, Step, Reset, Generate, and Speed** controls
- Exactly one logical frame per press of **Step** while paused
- Safe cancellation when changing algorithms or categories
- Array bars, graph layouts, tree/heap layouts, text alignment, hash/DP tables, set partitions, and grid pathfinding views
- Live statistics produced by the actual execution: comparisons, swaps, writes, visited nodes, distances, MST weight, flow, collisions, probes, rotations, and more
- Exponential demonstrations have explicit input limits
- Algorithm logic is unit-tested without launching JavaFX
- Repository-local Maven launcher and interruption-safe Git checkpoint helper

## Requirements

- Java 17 or newer (Java 21 recommended)
- macOS, Linux, or Windows with a desktop environment
- `curl` and `tar` for the first run of the repository-local Maven launcher

No global Maven installation is needed.

## Run

```bash
./mvnw javafx:run
```

The first invocation downloads Apache Maven 3.9.11 into the ignored `.mvn/` directory. JavaFX is resolved by Maven for the current platform.

## Test and Build

```bash
./mvnw clean test
./mvnw package
```

GitHub Actions runs the same clean test suite on pushes to `main` and `codex-work` and on pull requests.

## Using the Visualizer

1. Choose a category in the left sidebar.
2. Choose an algorithm from the category dropdown.
3. Edit the documented example input or press **Generate**.
4. Press **Start**, or press **Step** to move one logical operation at a time.
5. Use **Pause** and **Resume** during playback and adjust the speed slider at any time.

Graph edges use `0-1:4` for an undirected weighted edge or `0>1:4` for a directed one. Hash-table input accepts ordinary integers for insert, `?34` for search, and `-23` for delete. Grid rows are separated with `/` and use `.`, `#`, `S`, and `T`; the grid toolbar also supports direct wall/start/target editing, clearing, and maze generation.

## Algorithms Implemented

### Sorting

Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, Quick Sort, Randomized Quick Sort, Heap Sort, Counting Sort, and Radix Sort.

### Searching & Selection

Linear Search, Binary Search, Quickselect, and deterministic Median-of-Medians selection.

### Strings & Hashing

Naive String Matching, Knuth–Morris–Pratt with prefix table, Rabin–Karp, Separate Chaining, Linear Probing, Quadratic Probing, and Double Hashing.

### Graph Algorithms

Breadth-First Search, Depth-First Search, Connected Components, Topological Sort, Kosaraju Strongly Connected Components, Dijkstra, Bellman–Ford with negative-cycle detection, grid BFS/DFS/Dijkstra/A*, Kruskal, Prim, Union-Find with rank and path compression, Edmonds–Karp Max Flow with its Minimum Cut, and Maximum Bipartite Matching.

### Trees

Preorder, Inorder, Postorder, and Level-order traversal; Binary Search Tree insert/search/delete; Red-Black Tree insert/search with rotations and recoloring; B-Tree insert/search with node splitting.

### Heaps & Advanced Structures

Binary Min Heap, Binary Max Heap, Priority Queue operations, Binomial Heap, and Fibonacci Heap including decrease-key in the tested core.

### Dynamic Programming & Optimization

0/1 Knapsack, Branch-and-Bound Knapsack, Bellman–Held–Karp TSP, brute-force TSP, exact Vertex Cover, the standard 2-approximation for Vertex Cover, exact Max Cut, and exact MaxSAT.

### Divide & Conquer / Matrix

Strassen Matrix Multiplication, plus Merge Sort and Quick Sort in the sorting section.

### Algorithm Analysis

Runtime-growth comparison, Master Theorem explorer, amortized dynamic-array simulation, and repeated randomized-quicksort experiments.

The detailed implementation/visualization/test matrix is in [docs/ALGORITHM_COVERAGE.md](docs/ALGORITHM_COVERAGE.md).

## Project Structure

```text
src/main/java/dev/wesam/visualizer/
├── algorithms/    # UI-independent algorithm implementations
├── structures/    # Trees and heaps with invariant checks
├── catalog/       # 57 demos and their logical visualization steps
├── model/         # Immutable AlgorithmStep / AlgorithmRun records
├── ui/            # Responsive canvas renderer
└── AlgorithmVisualizerApp.java
src/test/java/     # JUnit 5 correctness and coverage tests
docs/              # Course-aligned algorithm coverage
scripts/           # Safe checkpoint helper
```

## Checkpoint Workflow

Development happens on `codex-work`; `main` is kept stable. To preserve a checkpoint and attempt a non-destructive push:

```bash
scripts/checkpoint.sh "feat: describe this checkpoint"
```

The helper stages changes, commits them, and pushes the current branch. If the push fails, the local commit remains intact and the script never rolls work back.

## Design Notes and Limits

- TSP dynamic programming is limited to 18 cities; brute-force TSP to 10.
- Exact Vertex Cover, Max Cut, and core MaxSAT are limited to 24 variables/vertices; the UI keeps MaxSAT at 20.
- Counting Sort rejects impractically large key ranges.
- Red-Black Tree deletion and B-Tree deletion are intentionally omitted; insertion/search and all structural invariants are implemented and tested.
- Visualizations favor understandable logical steps over micro-animation.

## Technology

Java 17 language level, JavaFX 21, Maven, and JUnit 5. There are no application libraries beyond JavaFX; the implementations use the Java standard library.

