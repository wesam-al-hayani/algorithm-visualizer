# Algorithm Coverage

This coverage baseline was checked against publicly accessible TU Dortmund material. The [Algorithm Engineering course overview](https://ae.cs.tu-dortmund.de/de/studium/kurse/) explicitly lists DAP2 sorting, selection, knapsack, graph algorithms, heaps, hash tables, balanced trees, divide-and-conquer, greedy methods, dynamic programming, O-notation, recurrences, and the Master Theorem. The same page lists Efficient Algorithms topics including string matching, SCC, matching, flows/cuts, TSP, Vertex Cover, MaxSAT, B-trees, disjoint sets, amortized analysis, randomized analysis, and approximation quality. The [TU Dortmund DAP1 overview](https://cs.tu-dortmund.de/studium/vor-dem-studium/einstieg/vorlesungen/dap-1/) and [module handbook index](https://cs.tu-dortmund.de/studium/poen-mhb-etc/details/bsc-anginf-mhb-details/modulhandbuch-bsc-anginf/) were also inspected.

“Tested” means either a dedicated correctness/invariant test or execution by the catalog-wide test, which runs the default input of every visible demonstration. All rows below are implemented and visualized.

| Topic | Algorithm/Data Structure | Implemented | Visualized | Tested |
| --- | --- | :---: | :---: | :---: |
| Sorting | Bubble Sort | ✓ | ✓ | ✓ |
| Sorting | Selection Sort | ✓ | ✓ | ✓ |
| Sorting | Insertion Sort | ✓ | ✓ | ✓ |
| Sorting | Merge Sort | ✓ | ✓ | ✓ |
| Sorting | Quick Sort | ✓ | ✓ | ✓ |
| Sorting | Randomized Quick Sort | ✓ | ✓ | ✓ |
| Sorting | Heap Sort | ✓ | ✓ | ✓ |
| Sorting | Counting Sort | ✓ | ✓ | ✓ |
| Sorting | Radix Sort | ✓ | ✓ | ✓ |
| Searching | Linear Search | ✓ | ✓ | ✓ |
| Searching | Binary Search | ✓ | ✓ | ✓ |
| Selection | Quickselect | ✓ | ✓ | ✓ |
| Selection | Median of Medians | ✓ | ✓ | ✓ |
| Strings | Naive String Matching | ✓ | ✓ | ✓ |
| Strings | Knuth–Morris–Pratt | ✓ | ✓ | ✓ |
| Strings | Rabin–Karp | ✓ | ✓ | ✓ |
| Hashing | Separate Chaining | ✓ | ✓ | ✓ |
| Hashing | Linear Probing | ✓ | ✓ | ✓ |
| Hashing | Quadratic Probing | ✓ | ✓ | ✓ |
| Hashing | Double Hashing | ✓ | ✓ | ✓ |
| Graph traversal | Breadth-First Search | ✓ | ✓ | ✓ |
| Graph traversal | Depth-First Search | ✓ | ✓ | ✓ |
| Graph traversal | Connected Components | ✓ | ✓ | ✓ |
| Directed graphs | Topological Sort | ✓ | ✓ | ✓ |
| Directed graphs | Kosaraju SCC | ✓ | ✓ | ✓ |
| Shortest paths | Dijkstra | ✓ | ✓ | ✓ |
| Shortest paths | Bellman–Ford | ✓ | ✓ | ✓ |
| Grid pathfinding | BFS | ✓ | ✓ | ✓ |
| Grid pathfinding | DFS | ✓ | ✓ | ✓ |
| Grid pathfinding | Dijkstra | ✓ | ✓ | ✓ |
| Grid pathfinding | A* with Manhattan heuristic | ✓ | ✓ | ✓ |
| Minimum spanning trees | Kruskal | ✓ | ✓ | ✓ |
| Minimum spanning trees | Prim | ✓ | ✓ | ✓ |
| Disjoint sets | Union-Find, rank + path compression | ✓ | ✓ | ✓ |
| Network flow | Edmonds–Karp Maximum Flow | ✓ | ✓ | ✓ |
| Network flow | Minimum Cut derivation | ✓ | ✓ | ✓ |
| Matching | Maximum Bipartite Matching | ✓ | ✓ | ✓ |
| Tree traversal | Preorder | ✓ | ✓ | ✓ |
| Tree traversal | Inorder | ✓ | ✓ | ✓ |
| Tree traversal | Postorder | ✓ | ✓ | ✓ |
| Tree traversal | Level-order | ✓ | ✓ | ✓ |
| Trees | Binary Search Tree | ✓ | ✓ | ✓ |
| Balanced trees | Red-Black Tree | ✓ | ✓ | ✓ |
| External-memory trees | B-Tree | ✓ | ✓ | ✓ |
| Heaps | Binary Min Heap | ✓ | ✓ | ✓ |
| Heaps | Binary Max Heap | ✓ | ✓ | ✓ |
| Heaps | Priority Queue operations | ✓ | ✓ | ✓ |
| Advanced heaps | Binomial Heap | ✓ | ✓ | ✓ |
| Advanced heaps | Fibonacci Heap | ✓ | ✓ | ✓ |
| Dynamic programming | 0/1 Knapsack | ✓ | ✓ | ✓ |
| Branch and bound | 0/1 Knapsack | ✓ | ✓ | ✓ |
| TSP | Bellman–Held–Karp | ✓ | ✓ | ✓ |
| TSP | Brute-force comparison | ✓ | ✓ | ✓ |
| Approximation | Exact Vertex Cover | ✓ | ✓ | ✓ |
| Approximation | Vertex Cover 2-approximation | ✓ | ✓ | ✓ |
| Cut problems | Exact Max Cut | ✓ | ✓ | ✓ |
| Satisfiability | Exact MaxSAT | ✓ | ✓ | ✓ |
| Divide and conquer | Strassen Matrix Multiplication | ✓ | ✓ | ✓ |
| Analysis | Runtime growth classes | ✓ | ✓ | ✓ |
| Analysis | Master Theorem explorer | ✓ | ✓ | ✓ |
| Analysis | Amortized dynamic-array simulation | ✓ | ✓ | ✓ |
| Randomized analysis | Quicksort repeated experiments | ✓ | ✓ | ✓ |

## Scope decisions

- The public course description mentions linear programming and data-structure augmentation as broad techniques rather than one prescribed visualizable algorithm. They are not presented as fake standalone menu items. Approximation quality is demonstrated with exact-vs-approximate Vertex Cover and a measured ratio.
- Red-Black Tree and B-Tree deletion are not in the visible UI. The specification explicitly allows omission where correctness/readability would be harmed; insert/search, recoloring/rotations, splitting, and invariants are fully present.

