/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int[][] open = Graphs.open(w, broken);
        int n = w.cells(), cols = w.cols();
        int deadEnds = 0;
        for (int[] neighbours : open) {
            if (neighbours.length == 1) deadEnds++;
        }
        if (n == 0) return new Result(new int[0], 0, deadEnds);

        boolean[] seen = new boolean[n];
        int[] stack = new int[n];
        byte[] nextDirection = new byte[n];
        int top = 0, visited = 1;
        stack[0] = 0;
        seen[0] = true;

        while (top >= 0 && stack[top] != n - 1) {
            int cell = stack[top];
            if (nextDirection[top] == 4) {
                top--;
                continue;
            }
            int direction = nextDirection[top]++;
            int neighbour = switch (direction) {
                case 0 -> cell >= cols ? cell - cols : -1;
                case 1 -> cell % cols + 1 < cols ? cell + 1 : -1;
                case 2 -> cell + cols < n ? cell + cols : -1;
                default -> cell % cols > 0 ? cell - 1 : -1;
            };
            if (neighbour < 0 || seen[neighbour]) continue;
            for (int door : open[cell]) {
                if (door == neighbour) {
                    seen[neighbour] = true;
                    visited++;
                    stack[++top] = neighbour;
                    nextDirection[top] = 0;
                    break;
                }
            }
        }

        int[] path = top < 0 ? new int[0] : java.util.Arrays.copyOf(stack, top + 1);
        return new Result(path, visited, deadEnds);
    }
}
