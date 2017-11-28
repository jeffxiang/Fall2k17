import java.util.Arrays;
import java.util.Comparator;

/** Minimal spanning tree utility.
 *  @author Jeff Xiang
 */
public class MST {

    /** Given an undirected, weighted, connected graph whose vertices are
     *  numbered 1 to V, and an array E of edges, returns a list of edges
     *  in E that form a minimal spanning tree of the input graph.
     *  Each edge in E is a three-element int array of the form (u, v, w),
     *  where 0 < u < v <= V are vertex numbers, and 0 <= w is the weight
     *  of the edge. The result is an array containing edges from E.
     *  Neither E nor the arrays in it may be modified.  There may be
     *  multiple edges between vertices.  The objects in the returned array
     *  are a subset of those in E (they do not include copies of the
     *  original edges, just the original edges themselves.) */
    public static int[][] mst(int V, int[][] E) {
        UnionFind set = new UnionFind(V);
        int[][] result = new int[V - 1][];
        int[][] nondecreasing = new int[E.length][];
        for (int i = 0; i < nondecreasing.length; i++) {
            nondecreasing[i] = E[i];
        }
        Arrays.sort(nondecreasing, EDGE_WEIGHT_COMPARATOR);
        int edgeindex = 0;
        int resultindex = 0;
        while (resultindex < result.length) {
            int[] edge = nondecreasing[edgeindex];
            int u = edge[0];
            int v = edge[1];
            if (!set.samePartition(u, v)) {
                result[resultindex] = edge;
                set.union(u, v);
                resultindex++;
            }
            edgeindex++;
        }
        return result;
    }

    public static void sortedges(int[][] E) {
        Comparator<int[]> c = EDGE_WEIGHT_COMPARATOR;
        for (int i  = 1; i < E.length; i++) {
            if (c.compare(E[i], E[i - 1]) < 0) {
                int[] smaller = E[i];
                E[i] = E[i - 1];
                E[i - 1] = smaller;
                int p = i - 1;
                while ((p - 1) >= 0 && c.compare(E[p], E[p - 1]) < 0) {
                    int[] smaller2 = E[p];
                    E[p] = E[p - 1];
                    E[p - 1] = smaller2;
                    p--;
                }
            }
        }
    }

    /** An ordering of edges by weight. */
    private static final Comparator<int[]> EDGE_WEIGHT_COMPARATOR =
        new Comparator<int[]>() {
            @Override
            public int compare(int[] e0, int[] e1) {
                return e0[2] - e1[2];
            }
        };

}
