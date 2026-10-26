/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet sets = new DisjointSet(w.cells());
        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k = 0; k < w.count() && count < broken.length; k++) {
            if (sets.union(w.a()[k], w.b()[k])) broken[count++] = k;
        }
        return java.util.Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        long[] byCost = new long[w.count()];
        for (int k = 0; k < w.count(); k++) {
            byCost[k] = ((long) w.cost()[k] << 32) | (k & 0xffffffffL);
        }
        java.util.Arrays.sort(byCost);

        DisjointSet sets = new DisjointSet(w.cells());
        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (long edge : byCost) {
            if (count == broken.length) break;
            int k = (int) edge;
            if (sets.union(w.a()[k], w.b()[k])) broken[count++] = k;
        }
        return java.util.Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        if (n == 0) return 0;

        // Each wall has two linked adjacency entries: 2*k at a, 2*k+1 at b.
        int[] head = new int[n];
        java.util.Arrays.fill(head, -1);
        int[] next = new int[2 * w.count()];
        for (int k = 0; k < w.count(); k++) {
            int forward = 2 * k, backward = forward + 1;
            next[forward] = head[w.a()[k]];
            head[w.a()[k]] = forward;
            next[backward] = head[w.b()[k]];
            head[w.b()[k]] = backward;
        }

        boolean[] visited = new boolean[n];
        java.util.PriorityQueue<Long> frontier = new java.util.PriorityQueue<>();
        visited[0] = true;
        addFrontier(0, w, head, next, visited, frontier);
        long total = 0;
        int reached = 1;
        while (reached < n && !frontier.isEmpty()) {
            int k = frontier.remove().intValue();
            int newCell = visited[w.a()[k]] ? w.b()[k] : w.a()[k];
            if (visited[newCell]) continue;
            total += w.cost()[k];
            visited[newCell] = true;
            reached++;
            addFrontier(newCell, w, head, next, visited, frontier);
        }
        return total;
    }

    private static void addFrontier(int cell, Input.Walls w, int[] head, int[] next,
                                    boolean[] visited, java.util.PriorityQueue<Long> frontier) {
        for (int arc = head[cell]; arc != -1; arc = next[arc]) {
            int k = arc / 2;
            int other = (arc & 1) == 0 ? w.b()[k] : w.a()[k];
            if (!visited[other]) {
                frontier.add(((long) w.cost()[k] << 32) | (k & 0xffffffffL));
            }
        }
    }
}
