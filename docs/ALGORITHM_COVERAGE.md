# Algorithm Coverage

Algorithm Lab V2.0 exposes **83 runnable demonstrations across 10 categories**. Every row below executes real logic, produces visual steps, includes algorithm-specific pseudocode, and is exercised by the catalog-wide default-input test. “Focused test” means the algorithm or its shared core also has targeted correctness, behavior, or invariant coverage. “Randomized / cross-check” identifies stronger property testing beyond one fixed example.

## Sorting — 11 demos

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| Bubble Sort | ✓ | ✓ | ✓ | `Arrays.sort` oracle | Playback, generated arrays |
| Selection Sort | ✓ | ✓ | ✓ | `Arrays.sort` oracle | Playback, generated arrays |
| Insertion Sort | ✓ | ✓ | ✓ | `Arrays.sort` oracle | Playback, generated arrays |
| Merge Sort | ✓ | ✓ | ✓ | `Arrays.sort` oracle | Playback, generated arrays |
| Quick Sort | ✓ | ✓ | ✓ | `Arrays.sort` oracle | Playback, generated arrays |
| Randomized Quick Sort | ✓ | ✓ | ✓ | Seeded trials + sort oracle | Playback, generated arrays |
| Heap Sort | ✓ | ✓ | ✓ | `Arrays.sort` oracle | Playback, generated arrays |
| Counting Sort | ✓ | ✓ | ✓ | `Arrays.sort` oracle | Playback, bounded key range |
| Radix Sort | ✓ | ✓ | ✓ | `Arrays.sort` oracle | Playback, generated arrays |
| Sorting Compare Mode | ✓ | ✓ | ✓ | Shared-input agreement | Two direct selectors |
| Sorting Race | ✓ | ✓ | ✓ | 2–6 shared-input agreement | Direct checkboxes, synchronized playback |

## Searching — 4 demos

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| Linear Search | ✓ | ✓ | ✓ | Simple-scan oracle | Target and generated array |
| Binary Search | ✓ | ✓ | ✓ | Linear-search oracle | Target and sorted array |
| Quickselect | ✓ | ✓ | ✓ | Sorted-rank oracle | Rank and generated array |
| Median of Medians | ✓ | ✓ | ✓ | Sorted-rank oracle | Rank and generated array |

## Strings / Hashing — 7 demos

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| Naive String Matching | ✓ | ✓ | ✓ | Expected occurrence set | Text and pattern input |
| Knuth–Morris–Pratt | ✓ | ✓ | ✓ | Naive-matcher oracle | Text and pattern input |
| Rabin–Karp | ✓ | ✓ | ✓ | Naive-matcher oracle | Text and pattern input |
| Separate Chaining | ✓ | ✓ | ✓ | Reference-map behavior | Insert, search, delete commands |
| Linear Probing | ✓ | ✓ | ✓ | Reference-set behavior | Insert, search, delete commands |
| Quadratic Probing | ✓ | ✓ | ✓ | Reference-set behavior | Insert, search, delete commands |
| Double Hashing | ✓ | ✓ | ✓ | Reference-set behavior | Insert, search, delete commands |

## Graphs — 29 demos

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| Breadth-First Search | ✓ | ✓ | ✓ | Traversal properties | Start vertex, graph generation |
| Depth-First Search | ✓ | ✓ | ✓ | Traversal properties | Start vertex, graph generation |
| Connected Components | ✓ | ✓ | ✓ | Reachability definition | Graph generation |
| Topological Sort | ✓ | ✓ | ✓ | Edge-order property | DAG generation / cycle feedback |
| Kosaraju SCC | ✓ | ✓ | ✓ | Tarjan cross-validation | Directed graph input |
| Tarjan SCC | ✓ | ✓ | ✓ | Kosaraju cross-validation | Directed graph input |
| Bridge Finding | ✓ | ✓ | ✓ | Edge-removal definition | Undirected graph input |
| Articulation Points | ✓ | ✓ | ✓ | Vertex-removal definition | Undirected graph input |
| Euler Path / Circuit | ✓ | ✓ | ✓ | Edge-use/degree properties | Directed or undirected input |
| Bipartite Check | ✓ | ✓ | ✓ | Coloring / conflict property | Graph generation |
| Dijkstra | ✓ | ✓ | ✓ | Bellman–Ford / all-pairs cross-check | Source and weighted graph |
| Bellman–Ford | ✓ | ✓ | ✓ | Floyd–Warshall cross-check | Negative edges/cycle reporting |
| Floyd–Warshall | ✓ | ✓ | ✓ | Johnson + repeated-source cross-check | Path query, matrices |
| Johnson's Algorithm | ✓ | ✓ | ✓ | Floyd–Warshall cross-check | Path query, reweighted graph |
| A* on Weighted Graph | ✓ | ✓ | ✓ | Dijkstra optimal-cost oracle | Source, target, coordinates |
| Grid Dijkstra vs A* | ✓ | ✓ | ✓ | Shared-grid cost agreement | Shared grid comparison |
| Shortest-Path Algorithm Comparison | ✓ | ✓ | ✓ | Four-algorithm agreement where applicable | Direct scope/weight comparison |
| Kruskal Minimum Spanning Tree | ✓ | ✓ | ✓ | Prim weight cross-check | Weighted graph generation |
| Prim Minimum Spanning Tree | ✓ | ✓ | ✓ | Kruskal weight cross-check | Start vertex, graph generation |
| Disjoint Set / Union-Find | ✓ | ✓ | ✓ | Reference partitions | Union/find command sequence |
| Edmonds–Karp Max Flow / Min Cut | ✓ | ✓ | ✓ | Dinic cross-validation | Capacity network input |
| Dinic's Maximum Flow Algorithm | ✓ | ✓ | ✓ | 500 seeded networks vs Edmonds–Karp | Capacity network input |
| Edmonds–Karp vs Dinic | ✓ | ✓ | ✓ | Flow and cut-capacity agreement | Shared network comparison |
| Maximum Bipartite Matching | ✓ | ✓ | ✓ | Hopcroft–Karp cross-validation | Bipartite graph input |
| Hopcroft–Karp Matching | ✓ | ✓ | ✓ | Simple matching cross-validation | Bipartite graph input |
| Grid BFS | ✓ | ✓ | ✓ | Grid reachability properties | Draw/erase/move endpoints, undo/redo |
| Grid DFS | ✓ | ✓ | ✓ | Grid reachability properties | Draw/erase/move endpoints, undo/redo |
| Grid Dijkstra | ✓ | ✓ | ✓ | BFS cost on unit grid | Draw/erase/move endpoints, undo/redo |
| Grid A Star | ✓ | ✓ | ✓ | Dijkstra optimal-cost oracle | Draw/erase/move endpoints, undo/redo |

## Maze Generation — 5 demos

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| Random Walls | ✓ | ✓ | ✓ | Multi-size/seed connectivity | Instant generation, grid undo/redo |
| Recursive Backtracking Maze | ✓ | ✓ | ✓ | Multi-size/seed connectivity | Instant generation, grid undo/redo |
| Recursive Division Maze | ✓ | ✓ | ✓ | Multi-size/seed connectivity | Instant generation, grid undo/redo |
| Randomized Prim Maze | ✓ | ✓ | ✓ | Multi-size/seed connectivity | Instant generation, grid undo/redo |
| Randomized Kruskal Maze | ✓ | ✓ | ✓ | Multi-size/seed connectivity | Instant generation, grid undo/redo |

## Trees — 8 demos

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| Binary Tree Preorder | ✓ | ✓ | ✓ | Traversal sequence oracle | Tree input and playback |
| Binary Tree Inorder | ✓ | ✓ | ✓ | Traversal sequence oracle | Tree input and playback |
| Binary Tree Postorder | ✓ | ✓ | ✓ | Traversal sequence oracle | Tree input and playback |
| Binary Tree Level-order | ✓ | ✓ | ✓ | Traversal sequence oracle | Tree input and playback |
| Binary Search Tree | ✓ | ✓ | ✓ | Ordered-set behavior | Persistent insert/search/delete/clear/random |
| AVL Tree | ✓ | ✓ | ✓ | 15,400 randomized invariant checks | Persistent insert/search/delete/clear/random |
| Red-Black Tree | ✓ | ✓ | ✓ | 20,000 randomized mixed operations | Persistent insert/search/delete/clear/random |
| B-Tree | ✓ | ✓ | ✓ | Degrees 2, 3, 4, 6 invariant runs | Persistent insert/search/delete/clear/random |

## Heaps — 5 demos

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| Binary Min Heap | ✓ | ✓ | ✓ | Extraction-order oracle | Command sequence and generated input |
| Binary Max Heap | ✓ | ✓ | ✓ | Extraction-order oracle | Command sequence and generated input |
| Priority Queue | ✓ | ✓ | ✓ | Reference priority ordering | Insert/peek/extract commands |
| Binomial Heap | ✓ | ✓ | ✓ | Heap invariants and ordering | Insert/union/extract sequence |
| Fibonacci Heap | ✓ | ✓ | ✓ | Heap invariants and ordering | Insert/extract/decrease-key core |

## Optimization — 8 demos

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| 0/1 Knapsack | ✓ | ✓ | ✓ | Brute-force oracle on small inputs | Items and capacity input |
| Branch-and-Bound Knapsack | ✓ | ✓ | ✓ | Dynamic-programming cross-check | Items and capacity input |
| Held–Karp TSP | ✓ | ✓ | ✓ | Brute-force TSP cross-check | Bounded distance matrix |
| Brute-Force TSP | ✓ | ✓ | ✓ | Held–Karp cross-check | Bounded distance matrix |
| Exact Vertex Cover | ✓ | ✓ | ✓ | Edge-coverage property | Bounded graph input |
| 2-Approximation Vertex Cover | ✓ | ✓ | ✓ | Exact optimum / ratio | Shared graph comparison |
| Exact Max Cut | ✓ | ✓ | ✓ | Partition weight oracle | Bounded graph input |
| Exact MaxSAT | ✓ | ✓ | ✓ | Assignment evaluation | Bounded CNF input |

## Matrix — 1 demo

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| Strassen Matrix Multiplication | ✓ | ✓ | ✓ | Classical multiplication oracle | Matrix input and recursion playback |

## Analysis — 5 demos

| Demonstration | Implemented | Visualized | Focused test | Randomized / cross-check | Interactive operations |
| --- | :---: | :---: | :---: | --- | --- |
| Runtime Growth | ✓ | ✓ | Catalog | Formula values | Size input and comparison table |
| Experimental Complexity | ✓ | ✓ | ✓ | Real count-only implementations vs theory | Algorithm and size range |
| Master Theorem Explorer | ✓ | ✓ | Catalog | Case/formula consistency | Recurrence parameters |
| Amortized Dynamic Array | ✓ | ✓ | ✓ | Aggregate-cost invariant | Operation count |
| Randomized Quicksort Experiment | ✓ | ✓ | ✓ | Seeded repeated trials and sort oracle | Trials, size, seed |

## Test Interpretation

- **Catalog coverage:** `AlgorithmCatalogTest` executes the default input for all 83 visible definitions, verifies the 10 known categories, and requires nonempty algorithm-specific pseudocode and frames.
- **Dedicated correctness:** focused suites assert results, edge cases, parser limits, statistics, and meaningful intermediate state.
- **Randomized and invariant testing:** fixed seeds make failures reproducible while exploring thousands of mutations or hundreds of graphs.
- **Cross-validation:** independent implementations agree only over their shared valid domain; for example, Dijkstra is never used as an oracle on negative edges.
- **UI verification:** packaged smoke checks cover navigation, playback, selectors, labs, charts, validation feedback, accessibility cues, and export in addition to headless core tests.

The course-oriented scope includes sorting, selection, hashing, balanced and external-memory trees, heaps, divide and conquer, dynamic programming, randomized and amortized analysis, SCCs, matching, flow/cut, TSP, Vertex Cover, Max Cut, and MaxSAT. Broader techniques such as linear programming or generic data-structure augmentation are discussed through related implemented algorithms rather than presented as misleading standalone menu entries.
