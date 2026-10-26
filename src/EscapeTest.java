import java.util.Arrays;

/** Additional checks for the DFS visit order and a million-cell path. */
public class EscapeTest {
    public static void main(String[] args) {
        Input.Walls branched = new Input.Walls(2, 2,
                new int[]{0, 0, 1}, new int[]{1, 2, 3}, new int[]{1, 2, 3});
        Escape.Result small = Escape.escape(branched, new int[]{0, 1, 2});
        check(Arrays.equals(small.path(), new int[]{0, 1, 3}), "right door before down door");
        check(small.visited() == 3, "stop counting when the exit is marked");
        check(small.deadEnds() == 2, "count all one-door cells");

        int rows = 1000, cols = 1000, n = rows * cols;
        int[] a = new int[n - 1], b = new int[n - 1], cost = new int[n - 1];
        int[] broken = new int[n - 1];
        int previous = -1, edge = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int cell = row * cols + (row % 2 == 0 ? col : cols - 1 - col);
                if (previous != -1) {
                    a[edge] = previous;
                    b[edge] = cell;
                    broken[edge] = edge;
                    edge++;
                }
                previous = cell;
            }
        }
        Escape.Result large = Escape.escape(new Input.Walls(rows, cols, a, b, cost), broken);
        check(large.path().length == 999_001 && large.path()[0] == 0
                && large.path()[large.path().length - 1] == n - 1,
                "1000 by 1000 path without recursive calls");
        check(large.visited() == 999_001 && large.deadEnds() == 2,
                "large path visit and dead-end counts");
        System.out.println("All Escape checks passed.");
    }

    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        System.out.println("ok    " + label);
    }
}
