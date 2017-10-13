package db61b;

import java.io.PrintStream;
import java.util.List;

import java.util.ArrayList;
import java.util.Scanner;

import static db61b.Utils.*;

/** An object that reads and interprets a sequence of commands from an
 *  input source.
 *  @author Jeff Xiang */
class CommandInterpreter {

    /* STRATEGY.
     *
     *   This interpreter parses commands using a technique called
     * "recursive descent." The idea is simple: we convert the BNF grammar,
     * as given in the specification document, into a program.
     *
     * First, we break up the input into "tokens": strings that correspond
     * to the "base case" symbols used in the BNF grammar.  These are
     * keywords, such as "select" or "create"; punctuation and relation
     * symbols such as ";", ",", ">="; and other names (of columns or tables).
     * All whitespace and comments get discarded in this process, so that the
     * rest of the program can deal just with things mentioned in the BNF.
     * The class Tokenizer performs this breaking-up task, known as
     * "tokenizing" or "lexical analysis."
     *
     * The rest of the parser consists of a set of functions that call each
     * other (possibly recursively, although that isn't needed for this
     * particular grammar) to operate on the sequence of tokens, one function
     * for each BNF rule. Consider a rule such as
     *
     *    <create statement> ::= create table <table name> <table definition> ;
     *
     * We can treat this as a definition for a function named (say)
     * createStatement.  The purpose of this function is to consume the
     * tokens for one create statement from the remaining token sequence,
     * to perform the required actions, and to return the resulting value,
     * if any (a create statement has no value, just side-effects, but a
     * select clause is supposed to produce a table, according to the spec.)
     *
     * The body of createStatement is dictated by the right-hand side of the
     * rule.  For each token (like create), we check that the next item in
     * the token stream is "create" (and report an error otherwise), and then
     * advance to the next token.  For a metavariable, like <table definition>,
     * we consume the tokens for <table definition>, and do whatever is
     * appropriate with the resulting value.  We do so by calling the
     * tableDefinition function, which is constructed (as is createStatement)
     * to do exactly this.
     *
     * Thus, the body of createStatement would look like this (_input is
     * the sequence of tokens):
     *
     *    _input.next("create");
     *    _input.next("table");
     *    String name = name();
     *    Table table = tableDefinition();
     *    _input.next(";");
     *
     * plus other code that operates on name and table to perform the function
     * of the create statement.  The .next method of Tokenizer is set up to
     * throw an exception (DBException) if the next token does not match its
     * argument.  Thus, any syntax error will cause an exception, which your
     * program can catch to do error reporting.
     *
     * This leaves the issue of what to do with rules that have alternatives
     * (the "|" symbol in the BNF grammar).  Fortunately, our grammar has
     * been written with this problem in mind.  When there are multiple
     * alternatives, you can always tell which to pick based on the next
     * unconsumed token.  For example, <table definition> has two alternative
     * right-hand sides, one of which starts with "(", and one with "as".
     * So all you have to do is test:
     *
     *     if (_input.nextIs("(")) {
     *         _input.next("(");
     *         // code to process "<column name>,  )"
     *     } else {
     *         // code to process "as <select clause>"
     *     }
     *
     * As a convenience, you can also write this as
     *
     *     if (_input.nextIf("(")) {
     *         // code to process "<column name>,  )"
     *     } else {
     *         // code to process "as <select clause>"
     *     }
     *
     * combining the calls to .nextIs and .next.
     *
     * You can handle the list of <column name>s in the preceding in a number
     * of ways, but personally, I suggest a simple loop:
     *
     *     ... = columnName();
     *     while (_input.nextIs(",")) {
     *         _input.next(",");
     *         ... = columnName();
     *     }
     *
     * or if you prefer even greater concision:
     *
     *     ... = columnName();
     *     while (_input.nextIf(",")) {
     *         ... = columnName();
     *     }
     *
     * (You'll have to figure out what do with the names you accumulate, of
     * course).
     */


    /** A new CommandInterpreter executing commands read from INP, writing
     *  prompts on PROMPTER, if it is non-null. */
    CommandInterpreter(Scanner inp, PrintStream prompter) {
        _input = new Tokenizer(inp, prompter);
        _database = new Database();
    }

    /** Parse and execute one statement from the token stream.  Return true
     *  iff the command is something other than quit or exit. */
    boolean statement() {
        switch (_input.peek()) {
        case "create":
            createStatement();
            break;
        case "load":
            loadStatement();
            break;
        case "exit": case "quit":
            exitStatement();
            return false;
        case "*EOF*":
            return false;
        case "insert":
            insertStatement();
            break;
        case "print":
            printStatement();
            break;
        case "select":
            selectStatement();
            break;
        case "store":
            storeStatement();
            break;
        default:
            throw error("unrecognizable command");
        }
        return true;
    }

    /** Parse and execute a create statement from the token stream. */
    void createStatement() {
        _input.next("create");
        _input.next("table");
        String name = name();
        Table table = tableDefinition();
        _database.put(name, table);
        _input.next(";");
    }

    /** Parse and execute an exit or quit statement. Actually does nothing
     *  except check syntax, since statement() handles the actual exiting. */
    void exitStatement() {
        if (!_input.nextIf("quit")) {
            _input.next("exit");
        }
        _input.next(";");
    }

    /** Parse and execute an insert statement from the token stream. */
    void insertStatement() {
        _input.next("insert");
        _input.next("into");
        Table table = tableName();
        _input.next("values");
        int cols = table.columns();

        String[] values = new String[cols];

        while (true) {
            _input.next("(");
            int k = 0;
            while (true) {
                String literal = literal();
                if (k >= cols) {
                    throw error("too many values for columns");
                }
                values[k] = literal;
                if (_input.nextIf(")")) {
                    break;
                }
                _input.next(",");
                k++;
            }
            table.add(values);
            if (_input.nextIf(",")) {
                continue;
            }
            break;
        }
        _input.next(";");
    }

    /** Parse and execute a load statement from the token stream. */
    void loadStatement() {
        _input.next("load");
        String name = name();
        _input.next(";");
        Table table = Table.readTable(name);
        _database.put(name, table);
        System.out.println("Loaded " + name + ".db");
    }

    /** Parse and execute a store statement from the token stream. */
    void storeStatement() {
        _input.next("store");
        String name = _input.peek();
        Table table = tableName();
        table.writeTable(name);
        System.out.printf("Stored %s.db%n", name);
        _input.next(";");
    }

    /** Parse and execute a print statement from the token stream. */
    void printStatement() {
        _input.next("print");
        String name = name();
        Table result = _database.get(name);
        if (result == null) {
            throw error("Unknown table: " + name);
        }
        _input.next(";");
        System.out.println("Contents of " + name + ":");
        result.print();
    }

    /** Parse and execute a select statement from the token stream. */
    void selectStatement() {
        try {
            Table table = selectClause();
            System.out.println("Search results:");
            table.print();
            _input.next(";");
        } catch (NullPointerException excp) {
            throw error("table or column not found");
        }
    }

    /** Parse and execute a table definition, returning the specified
     *  table. */
    Table tableDefinition() {
        Table table;
        if (_input.nextIf("(")) {
            List<String> columnnames = new ArrayList<>();
            String columnname = columnName();
            columnnames.add(columnname);
            while (_input.nextIf(",")) {
                columnname = columnName();
                if (columnnames.contains(columnname)) {
                    throw error("there cannot be duplicate column names");
                }
                columnnames.add(columnname);

            }
            if (_input.nextIf(")")) {
                String[] columnTitles = new String[columnnames.size()];
                columnTitles = columnnames.toArray(columnTitles);
                return new Table(columnTitles);
            }
        }
        _input.next("as");
        table = selectClause();
        return table;
    }

    /** Parse and execute a select clause from the token stream, returning the
     *  resulting table. */
    Table selectClause() {
        _input.next("select");
        List<String> colnames = new ArrayList<>();
        List<Table> tables = new ArrayList<>();
        while (true) {
            colnames.add(name());
            if (_input.nextIf("from")) {
                break;
            }
            _input.next(",");
        }
        while (true) {
            tables.add(_database.get(name()));
            if (_input.nextIf("where")) {
                break;
            } else if (_input.nextIs(";")) {
                if (tables.size() == 2) {
                    return tables.get(0).select(tables.get(1), colnames, null);
                }
                return tables.get(0).select(colnames, null);
            }
            _input.next(",");
        }
        int numtables = tables.size();
        List<Condition> conditions = new ArrayList<>();
        while (true) {
            if (_input.nextIs(";")) {
                break;
            }
            String c1n = name();
            String relation = relation();
            if (numtables == 2) {
                Column col1 = new Column(c1n, tables.get(0), tables.get(1));
                if (_input.nextIs(Tokenizer.IDENTIFIER)) {
                    String c2n = name();
                    Column col2 = new Column(c2n, tables.get(0), tables.get(1));
                    conditions.add(new Condition(col1, relation, col2));
                } else if (_input.nextIs(Tokenizer.LITERAL)) {
                    String val2 = literal();
                    conditions.add(new Condition(col1, relation, val2));
                }
            } else if (numtables == 1) {
                Column col1 = new Column(c1n, tables.get(0));
                if (_input.nextIs(Tokenizer.IDENTIFIER)) {
                    String c2n = name();
                    Column col2 = new Column(c2n, tables.get(0));
                    conditions.add(new Condition(col1, relation, col2));
                } else if (_input.nextIs(Tokenizer.LITERAL)) {
                    String val2 = literal();
                    conditions.add(new Condition(col1, relation, val2));
                }
            }
            _input.nextIf("and");
        }
        if (numtables == 2) {
            return tables.get(0).select(tables.get(1), colnames, conditions);
        }
        return tables.get(0).select(colnames, conditions);
    }

    /** Parse and return a valid name (identifier) from the token stream. */
    String name() {
        return _input.next(Tokenizer.IDENTIFIER);
    }

    /** Parse and return a valid column name from the token stream. Column
     *  names are simply names; we use a different method name to clarify
     *  the intent of the code. */
    String columnName() {
        return name();
    }

    /** Parse a valid table name from the token stream, and return the Table
     *  that it designates, which must be loaded. */
    Table tableName() {
        String name = name();
        Table table = _database.get(name);
        if (table == null) {
            throw error("unknown table: %s", name);
        }
        return table;
    }

    /** Parse a literal and return the string it represents (i.e., without
     *  single quotes). */
    String literal() {
        String lit = _input.next(Tokenizer.LITERAL);
        return lit.substring(1, lit.length() - 1).trim();
    }

    /** Parse a relation and return the string it represents. */
    String relation() {
        String rel = _input.next(Tokenizer.RELATION);
        return rel;
    }

    /** Advance the input past the next semicolon. */
    void skipCommand() {
        while (true) {
            try {
                while (!_input.nextIf(";") && !_input.nextIf("*EOF*")) {
                    _input.next();
                }
                return;
            } catch (DBException excp) {
                /* No action */
            }
        }
    }

    /** The command input source. */
    private Tokenizer _input;
    /** Database containing all tables. */
    private Database _database;
}
