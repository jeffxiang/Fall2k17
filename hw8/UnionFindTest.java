import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the UnionFind structure and its methods.
 * @author Jeff Xiang
 */

public class UnionFindTest {
    @Test
    public void testUnionFindConstructor() {
        UnionFind test = new UnionFind(20);
        test.union(2,3);
        test.union(3, 4);
        test.union(20, 2);
        assertTrue(test.samePartition(2, 3));
        assertTrue(test.samePartition(3, 4));
        assertTrue(test.samePartition(2, 4));
        assertTrue(test.samePartition(20, 4));
    }
}
