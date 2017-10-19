import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.ArrayList;

public class ArrayHeapTest {

    /** Basic test of adding, checking, and removing two elements from a heap */
    @Test
    public void simpleTest() {
        ArrayHeap<String> pq = new ArrayHeap<>();
        pq.insert("Qir", 2);
        pq.insert("Kat", 1);
        assertEquals(2, pq.size());

        String first = pq.removeMin();
        assertEquals("Kat", first);
        assertEquals(1, pq.size());

        String second = pq.removeMin();
        assertEquals("Qir", second);
        assertEquals(0, pq.size());
    }

    @Test
    public void testchangepriority() {
        ArrayHeap<Integer> testheap = new ArrayHeap<>();
        testheap.insert(3,3);
        testheap.insert(4,4);
        testheap.insert(5,5);
        testheap.insert(6,6);
        testheap.insert(14,14);
        testheap.insert(10,10);
        testheap.insert(7,7);
        testheap.insert(12,12);

        testheap.changePriority(12, 1);
        testheap.changePriority(4, 8);

        assertEquals(8, testheap.size());
        List<Object> result = new ArrayList<>();
        while (testheap.size() >= 1) {
            int root = testheap.removeMin();
            result.add(root);
        }
        List<Object> expected = new ArrayList<>();
        expected.add(12);
        expected.add(3);
        expected.add(5);
        expected.add(6);
        expected.add(7);
        expected.add(4);
        expected.add(10);
        expected.add(14);

        assertEquals(expected, result);
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArrayHeapTest.class));
    }
}
