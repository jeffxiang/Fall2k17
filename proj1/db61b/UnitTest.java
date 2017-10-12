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
    public void testsize() {
        String[] testlist = {"a", "b", "c"};
        Table testtable = new Table(testlist);
        Table testtable2 = new Table(testlist);
        String[] row1 = {"1", "2", "3"};
        String[] row2 = {"4", "5", "6"};
        testtable.add(row1);
        testtable.add(row2);
        assertEquals(2, testtable.size());
        assertEquals(0, testtable2.size());

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
        boolean testnewrow = testtable.add(row3);

        String getelement = testtable.get(1, 2);
        assertEquals("6", getelement);
        assertEquals(false, testtable.add(duprow));
        assertEquals(true, testnewrow);
    }

    @Test
    public void testaddindex() {
        String[] testcols = {"Name", "Age"};
        Table testtable = new Table(testcols);
        String[] row1 = {"Bob", "20"};
        String[] row2 = {"Susan", "19"};
        String[] row3 = {"Jane", "32"};
        String[] row4 = {"Amy", "18"};
        testtable.add(row1);
        testtable.add(row2);
        testtable.add(row3);
        testtable.add(row4);

        ArrayList<Integer> testtableindex = testtable.get_index();
        ArrayList<Integer> expectedindex = new ArrayList<>();
        expectedindex.add(3);
        expectedindex.add(0);
        expectedindex.add(2);
        expectedindex.add(1);
        assertEquals(expectedindex, testtableindex);

        String[] row5 = {"Henry", "21"};
        testtable.add(row5);
        ArrayList<Integer> testtableindex2 = testtable.get_index();
        expectedindex.add(2, 4);
        assertEquals(expectedindex, testtableindex2);

        String[] testcols1 = {"a", "b", "c"};
        Table testtable1 = new Table(testcols1);
        String[] row6 = {"1", "1", "3"};
        String[] row7 = {"1", "2", "3"};
        String[] row8 = {"2", "3", "1"};
        String[] row9 = {"3", "1", "0"};
        String[] row10 = {"2", "3", "2"};
        testtable1.add(row6);
        testtable1.add(row7);
        testtable1.add(row8);
        testtable1.add(row9);
        testtable1.add(row10);

        ArrayList<Integer> testtable1index = testtable1.get_index();
        ArrayList<Integer> expectedindex1 = new ArrayList<>();
        expectedindex1.add(0);
        expectedindex1.add(1);
        expectedindex1.add(2);
        expectedindex1.add(4);
        expectedindex1.add(3);

        assertEquals(expectedindex1, testtable1index);
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

    @Test
    public void testreadtable() {
        String filename = "enrolled";
        Table table = Table.readTable(filename);
        String getelement = table.get(0, 1);
        assertEquals("21228", getelement);
        assertEquals("SID", table.getTitle(0));
    }

    @Test
    public void testwritetable() {
        String[] testlist = {"a", "b", "c"};
        Table testtable = new Table(testlist);
        String[] row1 = {"1", "2", "3"};
        String[] row2 = {"4", "5", "6"};
        testtable.add(row1);
        testtable.add(row2);
        testtable.writeTable("testwrite");
        Table resulttable = Table.readTable("testwrite");
        assertEquals(testtable.getTitle(0), resulttable.getTitle(0));
        assertEquals(testtable.get(0, 1), resulttable.get(0, 1));
    }

    @Test
    public void testselect() {
        String[] testlist = {"a", "b", "c"};
        Table testtable = new Table(testlist);
        String[] row1 = {"2", "2", "3"};
        String[] row2 = {"4", "5", "5"};
        String[] row3 = {"1", "2", "2"};
        testtable.add(row1);
        testtable.add(row2);
        testtable.add(row3);

        List<Condition> testconditions = new ArrayList<>();
        Condition cond1 = new Condition(new Column("a", testtable), "<", new Column("b", testtable));
        Condition cond2 = new Condition(new Column("b", testtable), "=", new Column("c", testtable));
        testconditions.add(cond1);
        testconditions.add(cond2);

        List<String> columnnames = new ArrayList<>();
        columnnames.add("b");
        columnnames.add("a");
        Table resulttable = testtable.select(columnnames, testconditions);

        assertEquals(2, resulttable.size());
        assertEquals(2, resulttable.columns());
        assertEquals("5", resulttable.get(0,0));
        assertEquals("2", resulttable.get(1, 0));
        assertEquals("1", resulttable.get(1, 1));
    }

    @Test
    public void testselect2() {
        Table students = Table.readTable("students");
        Table enrolled = Table.readTable("enrolled");

        List<String> columnnames = new ArrayList<>();
        columnnames.add("Firstname");
        columnnames.add("Lastname");
        columnnames.add("Grade");

        Column col = new Column("CCN", students, enrolled);
        Condition cond = new Condition(col, "=", "21001");
        List<Condition> testconditions = new ArrayList<>();
        testconditions.add(cond);

        Table joined = students.select(enrolled, columnnames, testconditions);

        assertEquals(3, joined.columns());
        assertEquals(4, joined.size());
        assertEquals("Jason", joined.get(0, 0));
        assertEquals("Knowles", joined.get(0, 1));
        assertEquals("B", joined.get(3, 2));
    }

    @Test
    public void testput() {
        Table students = Table.readTable("students");
        Table enrolled = Table.readTable("enrolled");

        Database testdatabase = new Database();
        testdatabase.put("students", students);
        testdatabase.put("enrolled", enrolled);

        Table result1 = testdatabase.get("students");
        Table result2 = testdatabase.get("enrolled");

        assertEquals("101", result1.get(0,0));
        assertEquals("B", result2.get(0, 2));
    }
}
