import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

/** FIXME
 *  @author Josh Hug
 */

public class MySortingAlgorithmsTest {

    @Test
    public void correctnessTest() {
        /* Don't set maxValue too high or Distribution Sort will use
           up all available memory and your program will crash. */
        int numInts = 22;
        int maxValue = 1000;
        int[] original = BenchmarkUtility.randomInts(numInts, maxValue);
        int[] correct = BenchmarkUtility.copy(original);
        SortingAlgorithm javaSort = new MySortingAlgorithms.JavaSort();
        javaSort.sort(correct, correct.length);

        SortingAlgorithm[] algorithms = {
            new MySortingAlgorithms.InsertionSort(),
            new MySortingAlgorithms.SelectionSort(),
            new MySortingAlgorithms.QuickSort()};

        for (SortingAlgorithm sa : algorithms) {
            int[] input = BenchmarkUtility.copy(original);
            sa.sort(input, input.length);
            assertArrayEquals("Result for " + sa + " inorrect: ",
                              correct, input);
        }

        int k = 20;

        correct = BenchmarkUtility.copy(original);
        javaSort.sort(correct, k);

        for (SortingAlgorithm sa : algorithms) {
            int[] input = BenchmarkUtility.copy(original);
            sa.sort(input, k);
            assertArrayEquals("Result for " + sa + " inorrect: ",
                              correct, input);
        }
    }

    @Test
    public void testSort() {
        SortingAlgorithm InsertionSort = new MySortingAlgorithms.InsertionSort();
        SortingAlgorithm SelectionSort = new MySortingAlgorithms.SelectionSort();
        SortingAlgorithm QuickSort = new MySortingAlgorithms.QuickSort();
        int[] testarray = {3,123,51,231,1,2,16,4,45};
        int[] testarray2 = {3,123,51,231,1,2,16,4,45};
        int[] testarray3 = {3,123,51,231,1,2,16,4,45};
        InsertionSort.sort(testarray, testarray.length);
        SelectionSort.sort(testarray2, testarray.length);
        QuickSort.sort(testarray3, testarray.length);
    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MySortingAlgorithmsTest.class));
    }
}
