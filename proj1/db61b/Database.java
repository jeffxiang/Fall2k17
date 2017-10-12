// This is a SUGGESTED skeleton for a class that contains the Tables your
// program manipulates.  You can throw this away if you want, but it is a good
// idea to try to understand it first.  Our solution changes about 6
// lines in this skeleton.

// Comments that start with "//" are intended to be removed from your
// solutions.
package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.*;

/** A collection of Tables, indexed by name.
 *  @author */
class Database {
    /** An empty database. */
    public Database() {
        _tables = new HashMap<>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        return this._tables.get(name);
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        String firstchar = Character.toString(name.charAt(0));
        try {
            Integer.parseInt(firstchar);
        }
        catch (NumberFormatException excp) {
            //* Not an integer, valid name */
            this._tables.put(name, table);
            return;
        }
        //* First character is an integer, invalid name */
        throw Utils.error("invalid table name");
    }

    //* Returns my list of tables */
    public HashMap<String, Table> gettables() {
        return this._tables;
    }

    private HashMap<String, Table> _tables;
}
