import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
/**
 *  @author Jeff Xiang
 */

public class MazeCycles extends MazeExplorer {

    private int s;
    private int t;
    private boolean targetFound = false;
    private boolean cycleFound = false;
    private int cyclestart;
    private boolean atCycleStart = false;
    private LinkedList<Integer> visited = new LinkedList<>();

    public MazeCycles(Maze m) {
        super(m);
        s = 0;
        t = maze.xyTo1D(maze.N(), maze.N());
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    @Override
    public void solve() {
        dfs(s, -1);
        cycleRemoveEdges(s, cyclestart, visited);
    }

    public void cycleRemoveEdges(int v, int cyclestart, LinkedList<Integer> visited) {

        if (v == cyclestart) {
            atCycleStart = true;
            return;
        }

        for (int w : maze.adj(v)) {
            if (visited.peekFirst() == w && w != cyclestart) {
                visited.removeFirst();
                edgeTo[w] = w;
                announce();
                distTo[w] = distTo[v] + 1;
                cycleRemoveEdges(w, cyclestart, visited);
                if (atCycleStart) {
                    return;
                }
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

        for (int w : maze.adj(v)) {
            if (w == prev) continue;
             if (marked[w]) {
                edgeTo[w] = v;
                distTo[w] = distTo[v] + 1;
                announce();
                cycleFound = true;
                cyclestart = w;
                return;
            } else {
                edgeTo[w] = v;
                announce();
                visited.add(w);
                distTo[w] = distTo[v] + 1;
                dfs(w, v);
                if (targetFound) {
                    return;
                }
                if (cycleFound) {
                    return;
                }
            }
        }
    }
}

