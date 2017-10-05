package db61b;

import org.junit.Test;
import ucb.junit.textui;
import java.util.*;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the qirkat package.
 *  @author P. N. Hilfinger
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    @Test
    public void testcolumns() {
        String[] testlist = {"a", "b", "c"};
        Table testtable = new Table(testlist);
        assertEquals(3, testtable.columns());
    }

    @Test
    public void testgettitle() {
        String[] testlist = {"a", "b", "c"};
        Table testtable = new Table(testlist);
        assertEquals("a", testtable.getTitle(0));
        try {
            testtable.getTitle(9999);
            fail();
        }
        catch (DBException db) {
        }
    }

    @Test
    public void testfindcolumn() {
        String[] testlist = {"a", "b", "c"};
        Table testtable = new Table(testlist);
        assertEquals(1, testtable.findColumn("b"));
        assertEquals(-1, testtable.findColumn("haha"));
    }

    @Test
    public void testadd() {
        String[] testcols = {"a", "b", "c"};
        Table testtable = new Table(testcols);
        String[] row1 = {"1", "2", "3"};
        String[] row2 = {"4", "5", "6"};
        String[] duprow = {"4", "5", "6"};
        String[] row3 = {"7", "8", "9"};
        testtable.add(row1);
        testtable.add(row2);
        String getelement = testtable.get(1, 2);
        assertEquals("6", getelement);
        assertEquals(false, testtable.add(duprow));
        assertEquals(true, testtable.add(row3));
    }

    @Test
    public void testadd2() {
        String[] testcols1 = {"f", "g"};
        String[] row1 = {"1", "4"};
        String[] row2 = {"2", "5"};
        String[] row3 = {"3", "6"};
        Table testtable1 = new Table(testcols1);
        testtable1.add(row1);
        testtable1.add(row2);
        testtable1.add(row3);

        String[] testcols2 = {"x", "y"};
        String[] row4 = {"a", "d"};
        String[] row5 = {"b", "e"};
        String[] row6 = {"c", "f"};
        Table testtable2 = new Table(testcols2);
        testtable2.add(row4);
        testtable2.add(row5);
        testtable2.add(row6);

        String[] testcols3 = {"c", "d"};
        Table testtable3 = new Table(testcols3);

        Column col1 = new Column("f", testtable1, testtable2);
        Column col2 = new Column("x", testtable1, testtable2);

        List<Column> columns = new ArrayList<>();
        columns.add(col1);
        columns.add(col2);

        testtable3.add(columns, 0, 2);
        String getelement = testtable3.get(0,0);
        assertEquals("1", getelement);
        assertEquals("c", testtable3.get(0, 1));

        String[] duprow = {"1", "c"};
        assertEquals(false, testtable3.add(duprow));
    }
}
