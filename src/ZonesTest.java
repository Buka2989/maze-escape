/** Checks component membership, topological numbering and an iterative long chain. */
public class ZonesTest {
    public static void main(String[] args) {
        Input.Slides sample = new Input.Slides(7,
                new int[]{0, 1, 2, 3, 5, 6, 1, 0, 3, 4},
                new int[]{1, 0, 3, 2, 6, 5, 2, 4, 5, 6},
                new int[10]);
        int[] zone = Zones.kosaraju(sample);
        check(zone[0] == zone[1] && zone[2] == zone[3] && zone[5] == zone[6],
                "mutually reachable cells share a zone");
        check(zone[0] != zone[2] && zone[0] != zone[4] && zone[2] != zone[5]
                && zone[4] != zone[5], "four separate zones");
        boolean ordered = true;
        for (int i = 0; i < sample.count(); i++) {
            int from = zone[sample.from()[i]], to = zone[sample.to()[i]];
            if (from != to && from >= to) ordered = false;
        }
        check(ordered, "all inter-zone slides go to a higher number");

        int n = 200_000;
        int[] from = new int[n - 1], to = new int[n - 1];
        for (int i = 0; i < n - 1; i++) {
            from[i] = i;
            to[i] = i + 1;
        }
        int[] chain = Zones.kosaraju(new Input.Slides(n, from, to, new int[n - 1]));
        boolean correct = true;
        for (int i = 0; i < n; i++) {
            if (chain[i] != i) correct = false;
        }
        check(correct, "200,000-cell chain is numbered without recursion");
        System.out.println("All Zones checks passed.");
    }

    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        System.out.println("ok    " + label);
    }
}
