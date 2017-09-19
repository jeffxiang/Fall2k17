import org.junit.Test;
import static org.junit.Assert.*;

/**
 *  @author Jeff Xiang
 */

public class ArraysTest {
    @Test
    public void testcatenate(){
        int[] array1 = {1, 3, 4, 6};
        int[] array2 = {5, 3, 2, 3, 9};
        int[] array = {};
        int[] expected = {1, 3, 4, 6, 5, 3, 2, 3, 9};
        int[] expected2 = {5, 3, 2, 3, 9};
        assertArrayEquals(expected, Arrays.catenate(array1, array2));
        assertArrayEquals(expected2, Arrays.catenate(array2, array));
    }

    @Test
    public void testremove(){
        int[] array1 = {1, 3, 4, 6, 5, 3, 2, 3, 9};
        int[] array2 = {};
        int[] expected1 = {1, 3, 4, 3, 9};
        int[] expected2 = {};
        int[] expected3 = {1, 3, 4};
        Arrays.remove(array1, 3, 4);
        assertArrayEquals(expected1, Arrays.remove(array1, 3, 4));
        assertArrayEquals(expected2, Arrays.remove(array2, 3, 2));
        assertArrayEquals(expected3, Arrays.remove(array1, 3, 6));
    }

    @Test
    public void testnaturalruns1() {
        int[] input1 = {1, 3, 7, 5, 4, 6, 9, 10};
        int[][] expected1 = {{1, 3, 7}, {5}, {4, 6, 9, 10}};
        int[] input2 = {5, 4, 3, 2, 1};
        int[][] expected2 = {{5}, {4}, {3}, {2}, {1}};
        int[] input3 = {};
        int[][] expected3 = {};
        assertArrayEquals(expected1, Arrays.naturalRuns(input1));
        assertArrayEquals(expected2, Arrays.naturalRuns(input2));
        assertArrayEquals(expected3, Arrays.naturalRuns(input3));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
