# Assignment 3. Maze Escape

- Student ID: 250780
- Name: Muratkhanov Bauyrzhan
- Group: SE-2519
- Maze size (rows x columns): 8 x 10

I generated my input files with `java tools/MakeVariant.java 250780 data`.
My maze has 80 cells, 142 inner walls and 109 one-way slides.

## Program output

I obtained these 13 lines with `java -cp out Main`:

    Broken: 79
    Kept: 63
    Last broken wall: 90
    MST cost: 24566
    Heaviest broken wall: 969
    Path length: 16
    DFS visited: 42
    Dead ends: 27
    Zones: 10
    Largest zone: 12
    Is DAG: 0
    Flow stages: 7
    Fastest route cost: 20

My maze:

![maze](output/maze.png)

I can also open [the interactive maze](output/maze.html) to inspect walls, the path, zones and slides.

## Questions

These are my draft answers. I will review and rewrite them in my own words before submission.

Mission 0. `find` returns the root of the set containing a cell, while `union` joins two different sets and reports whether a join happened. I use path compression in `find` and attach the smaller set below the larger one in `union`, so a sequence of operations is fast.

Mission 1. I break a wall only when it connects two previously separate groups of cells. The resulting connected maze has no cycles, so it is a tree with exactly `R*C - 1` broken walls. The grid starts with `R*(C-1) + C*(R-1)` inner walls; subtracting the broken walls leaves `(R-1)*(C-1)`.

Mission 2. On my input, Kruskal and Prim both give an MST cost of **24566**. Both algorithms build a minimum spanning tree, although they choose edges in different ways; the distinct wall costs also make the minimum spanning tree unique.

Mission 3. There is exactly one path between any two cells because the open doors form a tree: a second path would create a cycle. I used an explicit stack for DFS instead of recursive calls, so even a very long path does not consume the Java call stack. I count a cell when DFS first marks it and stop the count as soon as it marks the exit.

Mission 4. In the second pass of Kosaraju, I follow reversed slides in decreasing order of the first pass's finish times; this collects the cells of each strongly connected zone. A directed cycle puts at least two cells in one zone, while a graph without cycles leaves every cell in its own zone. Because the input has no slide from a cell to itself, `Zones == number of cells` is equivalent to `Is DAG == 1`.

Mission 5. The second pass numbers zones in topological order, so every slide between different zones goes from a smaller number to a larger one. I process zones in that order to update the longest chain and the cheapest route; moving within a zone is free, and repeated transitions keep the cheapest cost. This pass takes `O(z + s)` time for `z` zones and `s` slides.

## AI use

I used ChatGPT to discuss the theory behind the algorithms and to help write and review the Java implementation. I checked the results against the three supplied examples and additional tests.

## Timing (Mission 7)

I ran the supplied `Bench` with Java 27. Each cell shows the average in milliseconds,
followed by the five measured runs in parentheses.

| maze | Kruskal, ms | Prim, ms |
|---|---:|---:|
| 100 x 100 | 5 (12, 5, 4, 2, 2) | 7 (14, 6, 4, 4, 6) |
| 300 x 300 | 15 (25, 13, 12, 12, 13) | 57 (69, 59, 47, 53, 57) |
| 1000 x 1000 | 269 (241, 231, 291, 300, 282) | 1341 (1182, 1252, 1474, 1377, 1418) |

The larger grids have many more walls, so sorting them in Kruskal and handling them
in Prim's priority queue take more time. The 100 x 100 measurements are especially
sensitive to JVM warm-up; memory allocation and other runtime effects also influence
the measured ratios. These numbers describe my runs on this computer, not fixed
execution times.

## Complexity

Here `n` is the number of cells, `m` the number of walls, `s` the number of slides,
and `z` the number of zones. Space means additional memory used by the algorithm,
including its result but excluding the input arrays.

| algorithm | time | space | assumptions |
|---|---|---|---|
| DSU `find` and `union` | `O(α(n))` amortized per operation | `O(n)` | Initialization takes `O(n)`; union by size and path compression are used. |
| Kruskal | `O(m log m + m α(n))` | `O(m + n)` | Walls are sorted by cost; DSU checks which ones join components. |
| Prim | `O(m log m)` | `O(m + n)` | Adjacency lists and a priority queue hold candidate walls. |
| DFS | `O(n + m)` | `O(n + m)` | The open maze is built from broken walls; each cell and door is examined a constant number of times. |
| Kosaraju | `O(n + s)` | `O(n + s)` | Both DFS passes use arrays as stacks; slide lists are built for both directions. |
| Route in the zone graph | `O(z + s)` | `O(z + s)` | Zone numbers are topological; each inter-zone slide is relaxed once. |

## Before submitting

I ran `java tools/Check.java`: all 13 lines matched on each of the three supplied
examples, and the DSU checks passed. I also ran `EscapeTest` and `ZonesTest` for
large inputs and additional edge cases. Before submission, I will rewrite the
draft answers in my own words, check public access to the repository, and submit
its link in Moodle myself.
