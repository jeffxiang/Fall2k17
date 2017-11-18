import java.util.ArrayList;
import java.util.Observable;
/**
 *  @author Jeff Xiang
 */

public class MazeCycles extends MazeExplorer {

    private int s;
    private int t;
    private boolean targetFound = false;
    private boolean cycleFound = false;

    public MazeCycles(Maze m) {
        super(m);
        s = 0;
        t = maze.xyTo1D(maze.N(), maze.N());
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    @Override
    public void solve() {
        printMaze();
        dfs(s, -1);
    }

    public void printMaze() {
        for (int v = 0; v < t; v += 1) {
            System.out.printf("%d\n", v);
            for (int w : maze.adj(v)) {
                System.out.printf("  %d\n", w);
            }
        }
    }

    public void dfs(int v, int prev) {
        marked[v] = true;
        announce();

        if (v == t) {
            targetFound = true;
        }

        if (targetFound) {
            return;
        }
        System.out.printf("DFS Node: %d, Prev Node: %d\n", v, prev);

        for (int w : maze.adj(v)) {
            if (w == prev) continue;
            System.out.printf("  Adjacent Node to (%d): %d\n", v, w);
             if (marked[w]) {
                edgeTo[w] = v;
                distTo[w] = distTo[v] + 1;
                System.out.println("  Cycle Here");
                announce();
                cycleFound = true;
                return;
            } else {
                edgeTo[w] = v;
                announce();
                distTo[w] = distTo[v] + 1;
                dfs(w, v);
                if (targetFound || cycleFound) {
                    return;
                }
            }
        }
    }
}

