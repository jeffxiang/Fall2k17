import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 *  @author Jeff Xiang
 */

public class ListsTest {

    @Test
    public void testnaturalruns(){
        IntList testlist = new IntList(1, null);
        IntList testlist2 = IntList.list(1, 3, 7, 5, 4, 6, 9, 10, 10, 11);
        int[][] array2 = {{1, 3, 7}, {5}, {4, 6, 9, 10}, {10, 11}};
        int[][] array = {{1}};
        int[][] array3 = {{5},{4},{3},{2},{1}};
        IntList testlist3 = IntList.list(5,4,3,2,1);
        assertEquals(IntList2.list(array2), Lists.naturalRuns(testlist2));
        assertEquals(IntList2.list(array), Lists.naturalRuns(testlist));
        assertEquals(IntList2.list(array3), Lists.naturalRuns(testlist3));
    }


    // It might initially seem daunting to try to set up
    // Intlist2 expected.
    //
    // There is an easy way to get the IntList2 that you want in just
    // few lines of code! Make note of the IntList2.list method that
    // takes as input a 2D array.

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
