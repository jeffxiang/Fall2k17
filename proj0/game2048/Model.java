package game2048;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author
 */
class Model extends Observable {

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to _board[c][r].  Be careful! This is not the usual 2D matrix
     * numbering, where rows are numbered from the top, and the row
     * number is the *first* index. Rather it works like (x, y) coordinates.
     */

    /** Largest piece value. */
    static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    Model(int size) {
        _board = new Tile[size][size];
        _score = _maxScore = 0;
        _gameOver = false;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there. */
    Tile tile(int col, int row) {
        return _board[col][row];
    }

    /** Return the number of squares on one side of the board. */
    int size() {
        return _board.length;
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current score. */
    int score() {
        return _score;
    }

    /** Return the current maximum game score (updated at end of game). */
    int maxScore() {
        return _maxScore;
    }

    /** Clear the board to empty and reset the score. */
    void clear() {
        _score = 0;
        _gameOver = false;
        for (Tile[] column : _board) {
            Arrays.fill(column, null);
        }
        setChanged();
    }

    /** Add TILE to the board.  There must be no Tile currently at the
     *  same position. */
    void addTile(Tile tile) {
        assert _board[tile.col()][tile.row()] == null;
        _board[tile.col()][tile.row()] = tile;
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board. */
    boolean tilt(Side side) {
        boolean changed;
        changed = false;

        for (int c = 0; c < size(); c++) {
            int spcount = 0;
            for (int r = size()-1; r >= 0; r--) {
                int stcol = side.col(c, r, size());
                int strow = side.row(c, r, size());
                Tile tile1 = _board[stcol][strow];
                if (tile1 == null) {
                    spcount++;
                }
                else {
                    int targetrow = r+spcount;
                    setVtile(c, targetrow, side, tile1);
                    if (r != targetrow) {
                        changed = true;
                    }
                }
            }
        }

       for (int c = 0; c < size(); c++) {
            for (int r = size()-1; r >= 0; r--) {
                int stcol = side.col(c, r, size());
                int strow = side.row(c, r, size());
                Tile currtile = _board[stcol][strow];
                if (r > 0) {
                    Tile adjtile = _board[side.col(c, r - 1, size())][side.row(c, r - 1, size())];
                    if (currtile != null && adjtile != null) {
                        if (currtile.value() == adjtile.value()) {
                            setVtile(c, r, side, adjtile);
                            int newtilestcol = side.col(c, r, size());
                            int newtilestrow = side.row(c, r, size());
                            int newtileval = _board[newtilestcol][newtilestrow].value();
                            _score = _score + newtileval;
                            changed = true;
                            int spcount1 = 0;
                            for (int r1 = r-1; r1 >= 0; r1--) {
                                int stcol1 = side.col(c, r1, size());
                                int strow1 = side.row(c, r1, size());
                                Tile tile2 = _board[stcol1][strow1];
                                if (tile2 == null) {
                                    spcount1++;
                                }
                                else {
                                    int targetrow = r1+spcount1;
                                    setVtile(c, targetrow, side, tile2);
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
       }
       checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }





    /** Return the current Tile at (COL, ROW), when sitting with the board
     *  oriented so that SIDE is at the top (farthest) from you. */
    private Tile vtile(int col, int row, Side side) {
        return _board[side.col(col, row, size())][side.row(col, row, size())];
    }

    /** Move TILE to (COL, ROW), merging with any tile already there,
     *  where (COL, ROW) is as seen when sitting with the board oriented
     *  so that SIDE is at the top (farthest) from you. */
    private void setVtile(int col, int row, Side side, Tile tile) { /* TILE is the tile we want to move */
        int pcol = side.col(col, row, size()),
            prow = side.row(col, row, size()); /* pcol,prow = target position */
        if (tile.col() == pcol && tile.row() == prow) {
            return; /* if tile is at target position, do nothing */
        }
        Tile tile1 = vtile(col, row, side); /* tile1 is the tile at target position */
        _board[tile.col()][tile.row()] = null; /* nullify TILE position */

        if (tile1 == null) {
            _board[pcol][prow] = tile.move(pcol, prow); /* if target position is null, move tile to that position */
        } else {
            _board[pcol][prow] = tile.merge(pcol, prow, tile1); /* else, merge the two tiles */
        }
    }

    /** Determine whether game is over and update _gameOver and _maxScore
     *  accordingly. */
    private void checkGameOver() {
        for (int c = 0; c < size(); c++) {
            for (int r = 0; r < size(); r++) {
                Tile currtile = _board[c][r];
                if (currtile == null) {
                    return;
                }
                if (currtile != null) {
                    if (currtile.value() == 2048) {
                        _gameOver = true;
                        if (_score > _maxScore) {
                            _maxScore = _score;
                        }
                    }
                    if (exist(c + 1)) {
                        Tile righttile = _board[c + 1][r];
                        if (righttile != null) {
                            if (righttile.value() == currtile.value()) {
                                return;
                            }
                        }
                    }
                    if (exist(c - 1)) {
                        Tile lefttile = _board[c - 1][r];
                        if (lefttile != null) {
                            if (lefttile.value() == currtile.value()) {
                                return;
                            }
                        }
                    }
                    if (exist(r + 1)) {
                        Tile uptile = _board[c][r + 1];
                        if (uptile != null) {
                            if (uptile.value() == currtile.value()) {
                                return;
                            }
                        }
                    }
                    if (exist(r - 1)) {
                        Tile downtile = _board[c][r - 1];
                        if (downtile != null) {
                            if (downtile.value() == currtile.value()) {
                                return;
                            }
                        }
                    }
                }
            }
        }
        _gameOver = true;
        if (_score > _maxScore) {
            _maxScore = _score;
        }
    }

    /** Returns True if col or row exists */

    boolean exist(int a) {
        if (a >= 0 && a < size()) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        out.format("] %d (max: %d)", score(), maxScore());
        return out.toString();
    }

    /** Current contents of the board. */
    private Tile[][] _board;
    /** Current score. */
    private int _score;
    /** Maximum score so far.  Updated when game ends. */
    private int _maxScore;
    /** True iff game is ended. */
    private boolean _gameOver;

}
