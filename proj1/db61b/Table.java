package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author Jeff Xiang
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }

        _titles = columnTitles;
        _columns = new ValueList[_rowSize];

    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return this._rowSize;
    }

    /** Return my titles. */
    public String[] getTitles() {
        return this._titles;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        if (k >= this.columns()) {
            throw error("index error");
        }
        return this._titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < this._titles.length; i++) {
            if (this._titles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of rows in this table. */
    public int size() {
        if (this._columns[0] == null) {
            return 0;
        }
        return this._columns[0].size();
    }

    /** Return the value of column number COL (0 <= COL < columns())
     *  of record number ROW (0 <= ROW < size()). */
    public String get(int row, int col) {
        try {
            return this._columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }


    /** Add a new row whose column values are VALUES to me if no equal
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    public boolean add(String[] values) {
        boolean existequalrow = false;
        if (this._columns[0] != null) {
            for (int row = 0; row < this._columns[0].size(); row++) {
                if (values[0].equals(this._columns[0].get(row))) {
                    for (int col = 0; col < this._columns.length; col++) {
                        if (!values[col].equals(this._columns[col].get(row))) {
                            break;
                        } else if (col == this._columns.length - 1
                                && values[col].equals
                                (this._columns[col].get(row))) {
                            existequalrow = true;
                        }
                    }
                }
            }
            if (existequalrow) {
                return false;
            }
        }
        for (int i = 0; i < this._columns.length; i++) {
            if (this._columns[i] == null) {
                this._columns[i] = new ValueList();
            }
            this._columns[i].add(values[i]);
        }
        if (this._columns[0].size() == 1) {
            this._index.add(0);
        } else {
            int indexofindex = 0;
            int col = 0;
            int indexofjustadded = this._columns[col].size() - 1;
            while (indexofindex < this._index.size()) {
                indexofjustadded = this._columns[col].size() - 1;
                String valofjustadded = this._columns[col]
                        .get(indexofjustadded);
                String valintable = this._columns[col]
                        .get(this._index.get(indexofindex));
                int comparison = valofjustadded.compareTo(valintable);
                if (comparison < 0) {
                    this._index.add(indexofindex, indexofjustadded);
                    return true;
                } else if (comparison > 0) {
                    indexofindex++;
                    col = 0;
                } else {
                    col++;
                }
            }
            this._index.add(indexofindex, indexofjustadded);
        }
        return true;
    }

    /** Add a new row whose column values are extracted by COLUMNS from
     *  the rows indexed by ROWS, if no equal row already exists.
     *  Return true if anything was added, false otherwise. See
     *  Column.getFrom(Integer...) for a description of how Columns
     *  extract values. */
    public boolean add(List<Column> columns, Integer... rows) {
        String[] values = new String[_rowSize];
        for (int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);
            String val = col.getFrom(rows);
            values[i] = val;
        }
        return this.add(values);
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            File file = new File(
                    "/Users/Jeff/repo/proj1/testing/" + name + ".db");
            input = new BufferedReader(new FileReader(file));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            String line = input.readLine();
            while (line != null) {
                String[] values = line.split(",");
                table.add(values);
                line = input.readLine();
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            File file = new File(
                    "/Users/Jeff/repo/proj1/testing/" + name + ".db");
            output = new PrintStream(file);
            String[] titles = this._titles;
            for (int i = 0; i < this._rowSize; i++) {
                String title = titles[i];
                output.append(title);
                output.append(",");
            }
            output.println(sep);
            for (int row = 0; row < this.size(); row++) {
                for (int col = 0; col < this._rowSize; col++) {
                    String value = this.get(row, col);
                    output.append(value);
                    output.append(",");
                }
                output.println(sep);
            }

        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        for (int row = 0; row < this.size(); row++) {
            System.out.print("  ");
            for (int col = 0; col < this._rowSize; col++) {
                String value = this.get(this._index.get(row), col);
                System.out.print(value + " ");
            }
            System.out.println("");
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        for (int row = 0; row < this.size(); row++) {
            String[] rowvals = new String[columnNames.size()];
            boolean test = Condition.test(conditions, row, row);
            if (test) {
                for (String desiredcolumn: columnNames) {
                    for (int col = 0; col < this._rowSize; col++) {
                        String columntitle = this.getTitle(col);
                        if (columntitle.equals(desiredcolumn)) {
                            int descolind = columnNames.indexOf(desiredcolumn);
                            rowvals[descolind] = this.get(row, col);
                        }
                    }
                }
                result.add(rowvals);
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
        String[] tbl2names = table2.getTitles();
        List<String> commoncols = new ArrayList<>();
        for (int i = 0; i < this._titles.length; i++) {
            for (int j = 0; j < tbl2names.length; j++) {
                if (this._titles[i].equals(tbl2names[j])) {
                    commoncols.add(this._titles[i]);
                }
            }
        }
        for (int thisrow = 0; thisrow < this.size(); thisrow++) {
            for (int tbl2row = 0; tbl2row < table2.size(); tbl2row++) {
                String[] rowvals = new String[columnNames.size()];
                int commoncounter = 0;
                for (int commoncolindex = 0;
                     commoncolindex < commoncols.size(); commoncolindex++) {
                    String commoncol = commoncols.get(commoncolindex);
                    int thistblcommoncolindex = this.findColumn(commoncol);
                    int tbl2commoncolindex = table2.findColumn(commoncol);
                    String thistblval = this.get(thisrow,
                            thistblcommoncolindex);
                    String tbl2val = table2.get(tbl2row, tbl2commoncolindex);
                    if (thistblval.equals(tbl2val)) {
                        commoncounter++;
                    }
                }
                if (commoncounter == commoncols.size()) {
                    boolean test = Condition.test(conditions, thisrow, tbl2row);
                    if (test) {
                        for (String desiredcolumn: columnNames) {
                            int desiredcolindex =
                                    columnNames.indexOf(desiredcolumn);
                            for (int thiscol = 0;
                                 thiscol < this._rowSize; thiscol++) {
                                String thiscoltitle = this.getTitle(thiscol);
                                if (thiscoltitle.equals(desiredcolumn)) {
                                    rowvals[desiredcolindex] =
                                            this.get(thisrow, thiscol);
                                }
                            }
                            for (int tbl2col = 0;
                                 tbl2col < table2._rowSize; tbl2col++) {
                                String tbl2coltitle = table2.getTitle(tbl2col);
                                if (tbl2coltitle.equals(desiredcolumn)) {
                                    rowvals[desiredcolindex] =
                                            table2.get(tbl2row, tbl2col);
                                }
                            }
                        }
                        result.add(rowvals);
                    }
                }
            }
        }
        return result;
    }

    /** Returns my index. */
    public ArrayList<Integer> getIndex() {
        return this._index;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elememts
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(int k0, int k1) {
        for (int i = 0; i < _columns.length; i += 1) {
            int c = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    public static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order in at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;
}
