import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author Jeff Xiang
 */

public class MatrixUtilsTest {

    @Test
    public void testget() {
        double[][] matrix = {{2, 0, 3}, {3, 2, 3}, {-1, 0, 2}, {1, 2, 3}};
        assertEquals(-1, MatrixUtils.get(matrix, 2, 0), 0);
        assertEquals(Double.POSITIVE_INFINITY, MatrixUtils.get(matrix, 4, 0), 0);
    }

    @Test
    public void testtranspose() {
        double[][] matrix = {{2, 0, 3}, {-1, 0, 2}};
        double[][] expected = {{2, -1}, {0, 0}, {3, 2}};
        assertEquals(expected, MatrixUtils.transpose(matrix));
    }


    /**
     * Sample input:
     *  1000000   1000000   1000000   1000000
     *  1000000     75990     30003   1000000
     *  1000000     30002    103046   1000000
     *  1000000     29515     38273   1000000
     *  1000000     73403     35399   1000000
     *  1000000   1000000   1000000   1000000
     *
     *  Output for sample input:
     *  1000000   1000000   1000000   1000000
     *  2000000   1075990   1030003   2000000
     *  2075990   1060005   1133049   2030003
     *  2060005   1089520   1098278   2133049
     *  2089520   1162923   1124919   2098278
     *  2162923   2124919   2124919   2124919
     */

    @Test
    public void testaccumulatevertical() {
        double[][] input = {{1000000, 1000000, 1000000, 1000000}, {1000000, 75990, 30003, 1000000},
                {1000000, 30002, 103046, 1000000}, {1000000, 29515, 38273, 1000000},
                {1000000, 73403, 35399, 1000000}, {1000000, 1000000, 1000000, 1000000}};
        double[][] expected = {{1000000, 1000000, 1000000, 1000000}, {2000000, 1075990, 1030003, 2000000},
                {2075990, 1060005, 1133049, 2030003}, {2060005, 1089520, 1098278, 2133049},
                {2089520, 1162923, 1124919, 2098278}, {2162923, 2124919, 2124919, 2124919}};
        MatrixUtils.accumulateVertical(input);
        assertEquals(expected, MatrixUtils.accumulateVertical(input));
    }

    @Test
    public void testaccumulate() {
        double[][] input1 = {{2, 3, 0}, {1, 2, 9}, {3, 6, 5}, {0, 2, 3}};
        double[][] expected1 = {{2, 4, 3}, {1, 3, 12}, {3, 6, 7}, {0, 2, 5}};
        double[][] input2 = {{1000000, 1000000, 1000000, 1000000}, {1000000, 75990, 30003, 1000000},
                {1000000, 30002, 103046, 1000000}, {1000000, 29515, 38273, 1000000},
                {1000000, 73403, 35399, 1000000}, {1000000, 1000000, 1000000, 1000000}};
        double[][] expected2 = {{1000000, 1000000, 1000000, 1000000}, {2000000, 1075990, 1030003, 2000000},
                {2075990, 1060005, 1133049, 2030003}, {2060005, 1089520, 1098278, 2133049},
                {2089520, 1162923, 1124919, 2098278}, {2162923, 2124919, 2124919, 2124919}};
        assertEquals(expected1, MatrixUtils.accumulate(input1, MatrixUtils.Orientation.HORIZONTAL));
        assertEquals(expected2, MatrixUtils.accumulate(input2, MatrixUtils.Orientation.VERTICAL));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
