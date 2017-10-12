// This is a SUGGESTED skeleton for a class that describes a single
// Condition (such as CCN = '99776').  You can throw this away if you
// want,  but it is a good idea to try to understand it first.
// Our solution changes or adds about 30 lines in this skeleton.

// Comments that start with "//" are intended to be removed from your
// solutions.
package db61b;

import java.util.List;

/** Represents a single 'where' condition in a 'select' command.
 *  @author */
class Condition {

    /** A Condition representing COL1 RELATION COL2, where COL1 and COL2
     *  are column designators. and RELATION is one of the
     *  strings "<", ">", "<=", ">=", "=", or "!=". */
    Condition(Column col1, String relation, Column col2) {
        _col1 = col1;
        _col2 = col2;
        _relation = relation;
    }

    /** A Condition representing COL1 RELATION 'VAL2', where COL1 is
     *  a column designator, VAL2 is a literal value (without the
     *  quotes), and RELATION is one of the strings "<", ">", "<=",
     *  ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, (Column) null);
        _val2 = val2;
    }

    /** Assuming that ROWS are row indices in the respective tables
     *  from which my columns are selected, returns the result of
     *  performing the test I denote. */
    boolean test(Integer... rows) {
        Table table1 = _col1.getTable();
        int col1tblindex = _col1.gettableIndex();
        String val1 = table1.get(rows[col1tblindex], table1.findColumn(_col1.getName()));
        String val2 = null;
        if (_col2 == null) {
            val2 = _val2;
        }
        else if (_col2 != null) {
            Table table2 = _col2.getTable();
            int col2tblindex = _col2.gettableIndex();
            val2 = table2.get(rows[col2tblindex], table2.findColumn(_col2.getName()));
        }

        if (this._relation.equals("<")) {
            if (val1.compareTo(val2) < 0) {
                return true;
            }
        }
        if (this._relation.equals(">")) {
            if (val1.compareTo(val2) > 0) {
                return true;
            }
        }
        if (this._relation.equals("<=")) {
            if (val1.compareTo(val2) <= 0) {
                return true;
            }
        }
        if (this._relation.equals(">=")) {
            if (val1.compareTo(val2) >= 0) {
                return true;
            }
        }
        if (this._relation.equals("=")) {
            if (val1.compareTo(val2) == 0) {
                return true;
            }
        }
        if (this._relation.equals("!=")) {
            if (val1.compareTo(val2) != 0) {
                return true;
            }
        }
        return false;
    }

    /** Return true iff ROWS satisfies all CONDITIONS. */
    static boolean test(List<Condition> conditions, Integer... rows) {
        if (conditions != null) {
            for (Condition cond : conditions) {
                if (!cond.test(rows)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** The operands of this condition.  _col2 is null if the second operand
     *  is a literal. */
    private Column _col1, _col2;
    /** Second operand, if literal (otherwise null). */
    private String _val2;

    private String _relation;
}
