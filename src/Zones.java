/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] outgoing = Graphs.slidesFrom(s);
        boolean[] seen = new boolean[n];
        int[] finished = new int[n];
        int[] stack = new int[n];
        int[] nextEdge = new int[n];
        int finishCount = 0;

        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0;
            stack[0] = start;
            nextEdge[0] = 0;
            seen[start] = true;
            while (top >= 0) {
                int cell = stack[top];
                if (nextEdge[top] == outgoing[cell].length) {
                    finished[finishCount++] = cell;
                    top--;
                    continue;
                }
                int slide = outgoing[cell][nextEdge[top]++];
                int neighbour = s.to()[slide];
                if (!seen[neighbour]) {
                    seen[neighbour] = true;
                    stack[++top] = neighbour;
                    nextEdge[top] = 0;
                }
            }
        }

        int[][] incoming = Graphs.slidesInto(s);
        int[] zoneOf = new int[n];
        java.util.Arrays.fill(zoneOf, -1);
        int zone = 0;
        for (int i = finishCount - 1; i >= 0; i--) {
            int start = finished[i];
            if (zoneOf[start] != -1) continue;
            int top = 0;
            stack[0] = start;
            zoneOf[start] = zone;
            while (top >= 0) {
                int cell = stack[top--];
                for (int slide : incoming[cell]) {
                    int neighbour = s.from()[slide];
                    if (zoneOf[neighbour] == -1) {
                        zoneOf[neighbour] = zone;
                        stack[++top] = neighbour;
                    }
                }
            }
            zone++;
        }
        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        if (zoneCount == 0) return new long[]{0, 0};

        int[] head = new int[zoneCount];
        java.util.Arrays.fill(head, -1);
        int[] next = new int[s.count()];
        for (int slide = 0; slide < s.count(); slide++) {
            int from = zoneOf[s.from()[slide]], to = zoneOf[s.to()[slide]];
            if (from == to) continue;
            next[slide] = head[from];
            head[from] = slide;
        }

        int[] stages = new int[zoneCount];
        java.util.Arrays.fill(stages, 1);
        long infinity = Long.MAX_VALUE / 4;
        long[] distance = new long[zoneCount];
        java.util.Arrays.fill(distance, infinity);
        distance[zoneOf[0]] = 0;

        for (int zone = 0; zone < zoneCount; zone++) {
            for (int slide = head[zone]; slide != -1; slide = next[slide]) {
                int to = zoneOf[s.to()[slide]];
                stages[to] = Math.max(stages[to], stages[zone] + 1);
                if (distance[zone] != infinity) {
                    // Parallel slides are relaxed separately, leaving their minimum cost.
                    distance[to] = Math.min(distance[to], distance[zone] + s.time()[slide]);
                }
            }
        }

        int longest = 0;
        for (int stage : stages) longest = Math.max(longest, stage);
        return new long[]{longest, distance[zoneOf[s.n() - 1]]};
    }
}
