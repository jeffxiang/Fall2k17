package db61b;

import java.util.HashMap;

/** A collection of Tables, indexed by name.
 *  @author Jeff Xiang */
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
        } catch (NumberFormatException excp) {
            this._tables.put(name, table);
            return;
        }
        throw Utils.error("invalid table name");
    }

    /** Returns my list of tables. */
    public HashMap<String, Table> gettables() {
        return this._tables;
    }

    /** Returns my HashMap of tables. */
    private HashMap<String, Table> _tables;
}
