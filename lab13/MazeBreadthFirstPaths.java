import java.util.*;

/**
 *  @author Jeff Xiang
 */

public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */

    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs(int v, PriorityQueue<Integer> q, ArrayList<Integer> visited) {
        marked[v] = true;
        announce();
        visited.add(v);

        if (v == t) {
            targetFound = true;
        }

        if (targetFound) {
            return;
        }

         for (int w : maze.adj(v)) {
             if (!marked[w]) {
                 q.add(w);
                 marked[w] = true;
                 distTo[w] = distTo[v] + 1;
                 edgeTo[w] = v;
                 announce();
             }
             if (!visited.contains(w)) {
                 bfs(q.poll(), q, visited);
                 if (targetFound) {
                     return;
                 }
             }
         }
    }


    @Override
    public void solve() {
        ArrayList<Integer> visited = new ArrayList<>();
        PriorityQueue<Integer> q = new PriorityQueue<>();
        bfs(s, q, visited);
    }
}

