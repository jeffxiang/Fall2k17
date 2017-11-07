package qirkat;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.LinkedList;

import static qirkat.PieceColor.*;
import static qirkat.Move.*;

/** A Qirkat board.   The squares are labeled by column (a char value between
 *  'a' and 'e') and row (a char value between '1' and '5'.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (with row 0 being the bottom row)
 *  counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author Jeff Xiang
 */
class Board extends Observable {


    /** A new, cleared board at the start of the game. */
    Board() {
        _board = new PieceColor[Move.SIDE * Move.SIDE];
        clear();
    }

    /** A copy of B. */
    Board(Board b) {
        _board = new PieceColor[Move.SIDE * Move.SIDE];
        _whoseMove = b.whoseMove();
        internalCopy(b);
    }

    /** Return a constant view of me (allows any access method, but no
     *  method that modifies it). */
    Board constantView() {
        return this.new ConstantBoard();
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions. */
    void clear() {
        _whoseMove = WHITE;
        _gameOver = false;
        setPieces("wwwww wwwww bb-ww bbbbb bbbbb", _whoseMove);
        for (int i = 0; i < _previndices.length; i++) {
            _previndices[i] = -1;
        }

        setChanged();
        notifyObservers();
    }

    /** Copy B into me. */
    void copy(Board b) {
        internalCopy(b);
    }

    /** Copy B into me. */
    private void internalCopy(Board b) {
        for (int i = 0; i < _board.length; i++) {
            _board[i] = b._board[i];
        }
        _prevboards = b._prevboards;
        _whoseMove = b._whoseMove;
        _gameOver = b._gameOver;
    }

    /** Set my contents as defined by STR.  STR consists of 25 characters,
     *  each of which is b, w, or -, optionally interspersed with whitespace.
     *  These give the contents of the Board in row-major order, starting
     *  with the bottom row (row 1) and left column (column a). All squares
     *  are initialized to allow horizontal movement in either direction.
     *  NEXTMOVE indicates whose move it is.
     */
    void setPieces(String str, PieceColor nextMove) {
        if (nextMove == EMPTY || nextMove == null) {
            throw new IllegalArgumentException("bad player color");
        }
        str = str.replaceAll("\\s", "");
        if (!str.matches("[bw-]{25}")) {
            throw new IllegalArgumentException("bad board description");
        }

        _whoseMove = nextMove;

        for (int k = 0; k < str.length(); k += 1) {
            switch (str.charAt(k)) {
            case '-':
                set(k, EMPTY);
                break;
            case 'b': case 'B':
                set(k, BLACK);
                break;
            case 'w': case 'W':
                set(k, WHITE);
                break;
            default:
                break;
            }
        }
        for (int i = 0; i < _previndices.length; i++) {
            _previndices[i] = -1;
        }

        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if the current player has
     *  no moves. */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current contents of square C R, where 'a' <= C <= 'e',
     *  and '1' <= R <= '5'.  */
    PieceColor get(char c, char r) {
        assert validSquare(c, r);
        return get(index(c, r));
    }

    /** Return the current contents of the square at linearized index K. */
    PieceColor get(int k) {
        assert validSquare(k);
        return _board[k];
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'e', and
     *  '1' <= R <= '5'. */
    private void set(char c, char r, PieceColor v) {
        assert validSquare(c, r);
        set(index(c, r), v);
    }

    /** Set get(K) to V, where K is the linearized index of a square. */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        _board[k] = v;
    }

    /** Return true iff MOV is legal on the current board. */
    boolean legalMove(Move mov) {
        if (mov.isJump()) {
            return checkJump(mov, false);
        }
        char fromcol = mov.col0();
        char fromrow = mov.row0();
        char tocol = mov.col1();
        char torow = mov.row1();
        int fromindex = mov.fromIndex();
        int toindex = mov.toIndex();
        PieceColor frompiece = _board[index(fromcol, fromrow)];
        PieceColor topiece = _board[index(tocol, torow)];
        if (frompiece != whoseMove() || topiece.isPiece()) {
            return false;
        }
        if (!isRadiatingSpot(fromindex) && !isRadiatingSpot(toindex)) {
            return false;
        }
        if (frompiece == WHITE) {
            if (torow < fromrow) {
                return false;
            }
        }
        if (frompiece == BLACK) {
            if (torow > fromrow) {
                return false;
            }
        }
        if (_previndices[index(fromcol, fromrow)] == index(tocol, torow)) {
            return false;
        }
        return true;
    }

    /** Return a list of all legal moves from the current position. */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<>();
        getMoves(result);
        return result;
    }

    /** Add all legal moves from the current position to MOVES. */
    void getMoves(ArrayList<Move> moves) {
        if (gameOver()) {
            return;
        }
        if (jumpPossible()) {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getJumps(moves, k);
            }
        } else {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getMoves(moves, k);
            }
        }
    }

    /** Add all legal non-capturing moves from the position
     *  with linearized index K to MOVES. */
    void getMoves(ArrayList<Move> moves, int k) {
        char col = col(k);
        char row = row(k);
        for (int c = col - 1; c <= col + 1; c++) {
            for (int r = row - 1; r <= row + 1; r++) {
                if (validSquare((char) c, (char) r)
                        && index(col, row) != index((char) c, (char) r)) {
                    Move movehere = Move.move(col, row, (char) c, (char) r);
                    if (legalMove(movehere)) {
                        moves.add(movehere);
                    }
                }
            }
        }
    }

    /** Return list of legal one-off jumps from linearized index K. */

    ArrayList<Move> getOneJumps(int k) {
        ArrayList<Move> moves = new ArrayList<>();
        if (jumpPossible(k)) {
            char col = col(k);
            char row = row(k);
            for (int c = col - 2; c <= col + 2; c += 2) {
                for (int r = row - 2; r <= row + 2; r += 2) {
                    if (validSquare((char) c, (char) r)
                            && !_board[index((char) c, (char) r)].isPiece()) {
                        Move jumpmove = move(col, row, (char) c, (char) r);
                        int jumpedindex = jumpmove.jumpedIndex();
                        if (!isRadiatingSpot(k)
                                && !isRadiatingSpot(jumpedindex)) {
                            break;
                        }
                        PieceColor jumpedpiece = _board[jumpedindex];
                        if (jumpedpiece == _whoseMove.opposite()) {
                            moves.add(jumpmove);
                        }
                    }
                }
            }
        }
        return moves;
    }

    /** Add all legal captures from the position with linearized index K
     *  to MOVES. */
    void getJumps(ArrayList<Move> moves, int k) {
        ArrayList<Move> extra = new ArrayList<>();
        extra = getJumpsHelper(extra, k);
        for (Move move: extra) {
            if (legalMove(move)) {
                moves.add(move);
            }
        }
    }

    /** Helper method for getJumps.
     * Returns an ArrayList of moves, legal or not.
     * @param moves ArrayList of moves
     * @param k Lineared index
     * @return ArrayList<Moves>
     */
    ArrayList<Move> getJumpsHelper(ArrayList<Move> moves, int k) {
        if (!jumpPossible(k)) {
            return moves;
        }
        Move store = null;
        if (moves.size() != 0) {
            store = moves.get(moves.size() - 1);
        }
        for (Move possiblejump: getOneJumps(k)) {
            moves.add(Move.move(store, possiblejump));
            Board board = new Board(this);
            board.makeMovePartial(possiblejump);
            board.getJumpsHelper(moves, possiblejump.toIndex());
        }
        return moves;
    }

    /** Return true iff MOV is a valid jump sequence on the current board.
     *  MOV must be a jump or null.  If ALLOWPARTIAL, allow jumps that
     *  could be continued and are valid as far as they go.  */
    boolean checkJump(Move mov, boolean allowPartial) {
        Board copyboard = new Board(this);
        while (mov != null) {
            if (!mov.isJump()) {
                return false;
            }
            char fromcol = mov.col0();
            char fromrow = mov.row0();
            char tocol = mov.col1();
            char torow = mov.row1();
            int jumpedindex = mov.jumpedIndex();
            PieceColor piece = copyboard._board[index(fromcol, fromrow)];
            if (!copyboard.jumpPossible(fromcol, fromrow)) {
                return false;
            }
            copyboard.set(tocol, torow, piece);
            copyboard.set(fromcol, fromrow, EMPTY);
            copyboard.set(jumpedindex, EMPTY);
            if (mov.jumpTail() == null) {
                if (!allowPartial) {
                    if (copyboard.jumpPossible(tocol, torow)) {
                        return false;
                    }
                }
            }
            mov = mov.jumpTail();
        }
        return true;
    }

    /** Return true iff a jump is possible for a piece at position C R. */
    boolean jumpPossible(char c, char r) {
        return jumpPossible(index(c, r));
    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. */
    boolean jumpPossible(int k) {
        if (_board[k] != whoseMove()) {
            return false;
        }
        char col = col(k);
        char row = row(k);
        for (int c = col - 2; c <= col + 2; c += 2) {
            for (int r = row - 2; r <= row + 2; r += 2) {
                if (validSquare((char) c, (char) r)
                        && !_board[index((char) c, (char) r)].isPiece()) {
                    Move jumpmove = move(col, row, (char) c, (char) r);
                    int jumpedindex = jumpmove.jumpedIndex();
                    if (!isRadiatingSpot(k) && !isRadiatingSpot(jumpedindex)) {
                        continue;
                    }
                    PieceColor jumpedpiece = _board[jumpedindex];
                    if (jumpedpiece == _whoseMove.opposite()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Return true iff a jump is possible from the current board. */
    boolean jumpPossible() {
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                return true;
            }
        }
        return false;
    }

    /** Return true iff a position at C, R is radiating. */
    boolean isRadiatingSpot(char c, char r) {
        if (!validSquare(c, r)) {
            return false;
        } else if (r % 2 != 0) {
            if (c == 'b' || c == 'd') {
                return false;
            }
        } else if (r % 2 == 0) {
            if (c == 'a' || c == 'c' || c == 'e') {
                return false;
            }
        }
        return true;
    }

    /** Return true iff a position at linearized index K is a radiating spot. */
    boolean isRadiatingSpot(int k) {
        return isRadiatingSpot(col(k), row(k));
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        makeMove(Move.move(c0, r0, c1, r1, null));
    }

    /** Make the multi-jump C0 R0-C1 R1..., where NEXT is C1R1....
     *  Assumes the result is legal. */
    void makeMove(char c0, char r0, char c1, char r1, Move next) {
        makeMove(Move.move(c0, r0, c1, r1, next));
    }

    /** Make the Move MOV on this Board, allowing partial jumps. Returns
     * the resultant board.
     */
    Board makeMovePartial(Move mov) {
        while (mov != null) {
            char fromcol = mov.col0();
            char fromrow = mov.row0();
            char tocol = mov.col1();
            char torow = mov.row1();
            int fromindex = index(fromcol, fromrow);
            int toindex = index(tocol, torow);
            int jumpedindex = mov.jumpedIndex();
            this.set(fromindex, EMPTY);
            _previndices[fromindex] = -1;
            this.set(jumpedindex, EMPTY);
            _previndices[jumpedindex] = -1;
            this.set(toindex, _whoseMove);
            _previndices[toindex] = fromindex;
            mov = mov.jumpTail();
        }
        return this;
    }

    /** Make the Move MOV on this Board, assuming it is legal. */
    void makeMove(Move mov) {
        if (legalMove(mov)) {
            _prevboards.add(new Board(this));
            while (mov != null) {
                char fromcol = mov.col0();
                char fromrow = mov.row0();
                char tocol = mov.col1();
                char torow = mov.row1();
                int fromindex = index(fromcol, fromrow);
                int toindex = index(tocol, torow);
                int jumpedindex = mov.jumpedIndex();
                this.set(fromindex, EMPTY);
                _previndices[fromindex] = -1;
                this.set(jumpedindex, EMPTY);
                _previndices[jumpedindex] = -1;
                this.set(toindex, _whoseMove);
                _previndices[toindex] = fromindex;
                mov = mov.jumpTail();
            }
            _whoseMove = _whoseMove.opposite();

            setChanged();
            notifyObservers();
        } else {
            throw new AssertionError();
        }
    }

    /** Undo the last move, if any. */
    void undo() {
        this.internalCopy(_prevboards.pollLast());

        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Return a text depiction of the board.  If LEGEND, supply row and
     *  column numbers around the edges. */
    String toString(boolean legend) {
        String result = "";
        String currstring = "";
        for (int i = 0; i < _board.length; i++) {
            if (_board[i] == WHITE) {
                currstring = currstring.concat(" w");
            }
            if (_board[i] == BLACK) {
                currstring = currstring.concat(" b");
            }
            if (!_board[i].isPiece()) {
                currstring = currstring.concat(" -");
            }
            if ((i + 1) % 5 == 0) {
                if (i != 4) {
                    currstring = currstring.concat("\n ");
                }
                result = currstring.concat(result);
                currstring = "";
            }
        }
        return " " + result;
    }

    /** Return true iff there is a move for the current player. */
    boolean isMove() {
        return getMoves().size() != 0;
    }


    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Set true when game ends. */
    private boolean _gameOver;

    /** Represents contents of the game board. */
    private PieceColor[] _board;

    /** State of game board before latest move. */
    private LinkedList<Board> _prevboards = new LinkedList<>();

    /** Array of previous indices for each position. */
    private int[] _previndices = new int[Move.SIDE * Move.SIDE];

    /** Convenience value giving values of pieces at each ordinal position. */
    static final PieceColor[] PIECE_VALUES = PieceColor.values();

    /** One cannot create arrays of ArrayList<Move>, so we introduce
     *  a specialized private list type for this purpose. */
    private static class MoveList extends ArrayList<Move> {
    }

    /** A read-only view of a Board. */
    private class ConstantBoard extends Board implements Observer {
        /** A constant view of this Board. */
        ConstantBoard() {
            super(Board.this);
            Board.this.addObserver(this);
        }

        @Override
        void copy(Board b) {
            assert false;
        }

        @Override
        void clear() {
            assert false;
        }

        @Override
        void makeMove(Move move) {
            assert false;
        }

        /** Undo the last move. */
        @Override
        void undo() {
            assert false;
        }

        @Override
        public void update(Observable obs, Object arg) {
            super.copy((Board) obs);
            setChanged();
            notifyObservers(arg);
        }
    }
}
