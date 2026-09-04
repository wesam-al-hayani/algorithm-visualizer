# Learning Guide

Algorithm Lab is most useful when you predict the next step before pressing **Step**, then explain why the highlighted pseudocode line preserves the algorithm's invariant. The sequence below moves from local array operations to graph structure, balanced trees, optimization, and experimental analysis.

## 1. Start with Search and Elementary Sorting

These demos make comparisons, writes, and invariants easy to see. Try the same short array in each sorting demo before using generated inputs.

| Topic | Core idea | Main data structures | Typical complexity | Continue with |
| --- | --- | --- | --- | --- |
| Linear Search | Inspect candidates until the target is found | Array | O(n) time | Binary Search |
| Binary Search | Eliminate half of a sorted range per comparison | Sorted array | O(log n) time | Binary-search complexity experiment |
| Bubble / Selection / Insertion Sort | Grow a sorted region through swaps, selection, or insertion | Array | O(n²) time, O(1) auxiliary space | Sorting Compare Mode |
| Merge Sort | Sort halves, then merge ordered runs | Array plus buffer | O(n log n) time, O(n) space | Master Theorem |
| Quick / Randomized Quick Sort | Partition around a pivot | Array, recursion stack | O(n log n) expected; O(n²) worst | Randomized Quick Sort Experiment |
| Heap Sort | Repeatedly extract from an implicit heap | Array-backed heap | O(n log n) time, O(1) extra array space | Binary heaps |
| Counting / Radix Sort | Use bounded keys or stable digit passes instead of comparisons | Count buckets / digit buckets | O(n + k) or O(d(n + k)) | Hashing |

Use **Sorting Compare Mode** for two algorithms on the same input. Use **Sorting Race** for two to six traces. Its comparison, swap, write, and visualization-step counts are deterministic work measures—not wall-clock benchmarks.

## 2. Selection, Strings, and Hashing

| Topic | Core idea | Main data structures | Typical complexity | Continue with |
| --- | --- | --- | --- | --- |
| Quickselect | Recurse only into the partition containing rank k | Array | O(n) expected; O(n²) worst | Median of Medians |
| Median of Medians | Choose a provably balanced pivot from grouped medians | Array, groups of five | O(n) worst-case | Quicksort pivot experiments |
| Naive matching | Test the pattern at every alignment | Text and pattern strings | O((n−m+1)m) | KMP, Rabin–Karp |
| KMP | Reuse matched-prefix information after a mismatch | Prefix-function table | O(n + m) | Automata and borders |
| Rabin–Karp | Compare rolling hashes before verifying characters | Rolling hash | O(n + m) expected | Hash tables |
| Chaining and open addressing | Map keys to slots and resolve collisions | Bucket lists or probe table | O(1) expected operations | Load factors, amortized analysis |

For open addressing, watch tombstones and probe sequences: deleting a slot by making it truly empty can incorrectly terminate a later search.

## 3. Graph Foundations

Begin with an unweighted graph. Predict the frontier, visited set, and final tree before advancing each frame.

| Topic | Core idea | Main data structures | Typical complexity | Continue with |
| --- | --- | --- | --- | --- |
| BFS | Expand vertices by distance layers | Queue, visited set | O(V + E) | Unweighted shortest paths, bipartite check |
| DFS | Fully explore one branch before backtracking | Recursion/stack | O(V + E) | Topological Sort, SCCs, low-link analysis |
| Connected Components | Restart traversal at each unvisited vertex | BFS/DFS forest | O(V + E) | Union-Find |
| Topological Sort | Order a DAG so every edge points forward | Indegrees/queue or DFS | O(V + E) | Dynamic programming on DAGs |
| Union-Find | Maintain merging components efficiently | Parent forest, rank, path compression | Near-constant amortized | Kruskal |
| Kruskal / Prim | Grow an MST by globally safe edges or a connected frontier | Union-Find / priority queue | O(E log E) / O(E log V) | Cut property |

## 4. Shortest Paths

The input's edge assumptions determine the correct algorithm. A low-weight result is meaningless if the preconditions are violated.

| Algorithm | Core idea | Allowed weights / scope | Typical complexity | Closely related |
| --- | --- | --- | --- | --- |
| Dijkstra | Settle the cheapest unsettled distance | Nonnegative, single source | O((V + E) log V) | Prim, A* |
| Bellman–Ford | Relax every edge repeatedly | Negative edges; detects reachable negative cycles | O(VE) | Johnson potentials |
| Floyd–Warshall | Admit intermediate vertices one by one | All pairs; negative edges, no negative cycles | O(V³), O(V²) space | Transitive closure |
| Johnson's Algorithm | Reweight once, then run Dijkstra per source | Sparse all-pairs; negative edges, no negative cycles | O(VE + V(E + V) log V) | Bellman–Ford + Dijkstra |
| Weighted-graph A* | Order the frontier by g + admissible h | Single pair; heuristic must not overestimate | Dijkstra-like worst case | Grid A* |

Use **Shortest-Path Algorithm Comparison** to see scope, weight requirements, cost agreement, and work metrics together. On grids, compare Dijkstra and A* on exactly the same walls: both must return the same optimal cost, while A* may explore fewer cells.

## 5. Structural Graph Algorithms

| Algorithm | Core idea | State worth watching | Typical complexity | Closely related |
| --- | --- | --- | --- | --- |
| Kosaraju SCC | DFS finishing order, transpose, DFS again | Finish order and component labels | O(V + E) | Tarjan SCC |
| Tarjan SCC | Pop a component when `low[v] == index[v]` | Index, low-link, stack membership | O(V + E) | Bridges, articulation points |
| Bridges | An edge is critical when a child cannot reach an ancestor | DFS parent and low values | O(V + E) | Articulation Points |
| Articulation Points | A vertex separates a child subtree under root/non-root rules | Child counts and low values | O(V + E) | Biconnected components |
| Euler Path / Circuit | Consume each edge once, then splice the walk | Degrees, edge IDs, traversal stack | O(V + E) | Hamiltonian path contrast |
| Bipartite Check | BFS two-coloring; same-color edge is a witness | Vertex colors and conflict edge | O(V + E) | Bipartite matching |

The low-link value is not simply a depth. It is the earliest discovery index reachable through the DFS subtree using tree edges plus at most one back edge.

## 6. Matching and Network Flow

| Algorithm | Core idea | Main state | Typical complexity | Closely related |
| --- | --- | --- | --- | --- |
| Simple bipartite matching | Search for one augmenting path per left vertex | Current pairs, visited right side | O(VE) | Hopcroft–Karp |
| Hopcroft–Karp | Augment a maximal set of shortest disjoint paths per phase | BFS layers and DFS paths | O(E√V) | Dinic |
| Edmonds–Karp | Choose every residual augmenting path with BFS | Residual graph, parent path | O(VE²) | Max-flow min-cut theorem |
| Dinic | Build a level graph, then send blocking flow | Levels, current-edge pointers, residual capacity | O(V²E) general bound | Hopcroft–Karp |

In **Edmonds–Karp vs Dinic**, distinguish augmentation or blocking-flow operations from visualization frames. Both implementations must agree on maximum flow and the source side of a valid minimum cut may differ while cut capacity remains equal.

## 7. Trees and Heaps

Use the persistent tree toolbar instead of rebuilding from a command list. Insert keys that trigger each rotation, then delete keys in a different order and watch the invariants remain valid.

| Structure | Core idea | Invariant | Typical operation cost | Closely related |
| --- | --- | --- | --- | --- |
| Binary Search Tree | Smaller keys left, larger keys right | Inorder traversal is sorted | O(h), worst O(n) | Tree traversals |
| AVL Tree | Repair the first unbalanced ancestors with rotations | Balance factor −1, 0, or 1 | O(log n) | Red-Black Tree |
| Red-Black Tree | Recolor and rotate to bound black-height | Root/leaf blackness, no red-red edge, equal black-height | O(log n) | 2–3–4 trees |
| B-Tree | Store many sorted keys per node and split/borrow/merge | Degree-dependent occupancy, equal leaf depth | O(log n) node visits | External-memory indexes |
| Binary Heap | Keep the minimum/maximum at an implicit-tree root | Parent dominates children | O(log n) insert/extract | Priority Queue, Heap Sort |
| Binomial Heap | Represent size as a forest of binomial trees | At most one tree of each degree | O(log n) union/extract | Binary representation |
| Fibonacci Heap | Delay consolidation until extraction | Heap order plus marked-node discipline | Amortized O(1) insert/decrease-key | Advanced priority queues |

Red-Black and B-Tree deletion are not simulated shortcuts: successor replacement, double-black repair, borrowing, merging, and root shrinking are surfaced as real events.

## 8. Dynamic Programming, Exact Search, and Approximation

| Topic | Core idea | State | Typical complexity | Closely related |
| --- | --- | --- | --- | --- |
| 0/1 Knapsack | Best value for each prefix and capacity | DP table | O(nW) | Branch and bound |
| Branch-and-Bound Knapsack | Prune subtrees whose optimistic bound cannot win | Search tree, fractional bound | Exponential worst case | Best-first search |
| Held–Karp TSP | Best path to endpoint for each visited subset | Subset DP | O(n²2ⁿ) | Brute-force TSP |
| Vertex Cover exact / 2-approx | Compare exhaustive optimum with edge-pair selection | Candidate cover and ratio | Exponential / O(V + E) | Approximation guarantees |
| Max Cut / MaxSAT | Evaluate assignments or partitions exactly | Bit mask and incumbent | Exponential | Local search |
| Strassen | Replace eight block products with seven combinations | Matrix quadrants | O(n^log₂7) | Divide and conquer |

Input limits are part of the lesson: exact exponential algorithms become unusable quickly even when their code is concise.

## 9. Grids and Maze Generation

Grid BFS, DFS, Dijkstra, and A* reuse the same traversal concepts with cells as vertices. Draw walls by dragging, switch to erase mode, move endpoints, then use undo/redo to compare layouts.

- **Random Walls** samples obstacles and repairs connectivity as a baseline.
- **Recursive Backtracking** carves a depth-first maze with long corridors.
- **Recursive Division** starts open and adds walls with passages.
- **Randomized Prim** grows the maze from a frontier.
- **Randomized Kruskal** removes walls that join different cell components.

The seed makes every frame sequence reproducible, and the generator guarantees exactly one start, one target, and a connecting path.

## 10. Analyze Rather Than Guess

Use **Experimental Complexity** to compare actual comparison counts with n², n log n, and log n reference curves. Use **Randomized Quicksort Experiment** to hold the base array constant while changing pivot choices over repeatable trials.

Three cautions matter:

1. An operation count is reproducible and explanatory; elapsed animation time is not a benchmark.
2. Big-O describes growth, not the exact value for a small input.
3. Randomized algorithms need a distribution of trials—minimum, maximum, average, and median—not one lucky run.

When you can explain the observed curve, return to **Runtime Growth**, **Master Theorem Explorer**, and **Amortized Dynamic Array** to connect the measurements to formal analysis.
