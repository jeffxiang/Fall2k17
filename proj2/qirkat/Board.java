package qirkat;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Observable;
import java.util.Observer;

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
        _board = new PieceColor[Move.SIDE*Move.SIDE];
        clear();
    }

    /** A copy of B. */
    Board(Board b) {
        _board = new PieceColor[Move.SIDE*Move.SIDE];
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

        // FIXME

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
        PieceColor frompiece = _board[index(fromcol, fromrow)];
        PieceColor topiece = _board[index(tocol, torow)];
        if (frompiece != whoseMove() || topiece.isPiece()) {
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
        if (frompiece.prevIndex() == index(tocol, torow)) {
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
                if (validSquare((char) c, (char) r) &&
                        index(col, row) != index((char) c, (char) r)) {
                    Move movehere = Move.move(col, row, (char) c, (char) r);
                    if (legalMove(movehere)) {
                        moves.add(movehere);
                    }
                }
            }
        }
    }

    /** Add all legal captures from the position with linearized index K
     *  to MOVES. */
    private void getJumps(ArrayList<Move> moves, int k) {
        // FIXME
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

    /** Return true iff a jump is possible for position C R. */
    boolean jumpPossibleempty(char c, char r) {
        return jumpPossibleempty(index(c, r));
    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. */
    boolean jumpPossible(int k) {
        if (_board[k] != whoseMove()) {
            return false;
        }
        char col = col(k);
        char row = row(k);
        if (isRadiatingSpot(k)) {
            for (int c = col - 2; c <= col + 2; c += 2) {
                    for (int r = row - 2; r <= row + 2; r += 2) {
                        if (validSquare((char) c, (char) r) &&
                                !_board[index((char) c, (char) r)].isPiece()) {
                            Move jumpmove = move(col, row, (char) c, (char) r);
                            int jumpedindex = jumpmove.jumpedIndex();
                            PieceColor jumpedpiece = _board[jumpedindex];
                            if (jumpedpiece == _whoseMove.opposite()) {
                                return true;
                            }
                        }
                    }
            }
        } else if (!isRadiatingSpot(k)) {
            int leftcol = col - 2;
            int rightcol = col + 2;
            int uprow = row + 2;
            int downrow = row - 2;
            if (validSquare((char) leftcol, row) &&
                    !_board[index((char) leftcol, row)].isPiece()) {
                Move jumpmove = move(col, row, (char) leftcol, row);
                int jumpedindex = jumpmove.jumpedIndex();
                PieceColor jumpedpiece = _board[jumpedindex];
                if (jumpedpiece == _whoseMove.opposite()) {
                    return true;
                }
            }
            if (validSquare((char) rightcol, row) &&
                    !_board[index((char) rightcol, row)].isPiece()) {
                Move jumpmove = move(col, row, (char) rightcol, row);
                int jumpedindex = jumpmove.jumpedIndex();
                PieceColor jumpedpiece = _board[jumpedindex];
                if (jumpedpiece == _whoseMove.opposite()) {
                    return true;
                }
            }
            if (validSquare(col, (char) uprow) &&
                    !_board[index(col, (char) uprow)].isPiece()) {
                Move jumpmove = move(col, row, col, (char) uprow);
                int jumpedindex = jumpmove.jumpedIndex();
                PieceColor jumpedpiece = _board[jumpedindex];
                if (jumpedpiece == _whoseMove.opposite()) {
                    return true;
                }
            }
            if (validSquare(col, (char) downrow) &&
                    !_board[index(col, (char) downrow)].isPiece()) {
                Move jumpmove = move(col, row, col, (char) downrow);
                int jumpedindex = jumpmove.jumpedIndex();
                PieceColor jumpedpiece = _board[jumpedindex];
                if (jumpedpiece == _whoseMove.opposite()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Return true iff a jump is possible from a piece at linearized
     * index K, regardless of whether a piece exists there already */
    boolean jumpPossibleempty(int k) {
        char col = col(k);
        char row = row(k);
        if (isRadiatingSpot(k)) {
            for (int c = col - 2; c <= col + 2; c += 2) {
                if (_whoseMove == WHITE) {
                    for (int r = row; r <= row + 2; r += 2) {
                        if (validSquare((char) c, (char) r) &&
                                !_board[index((char) c, (char) r)].isPiece()) {
                            Move jumpmove = move(col, row, (char) c, (char) r);
                            int jumpedindex = jumpmove.jumpedIndex();
                            PieceColor jumpedpiece = _board[jumpedindex];
                            if (jumpedpiece == BLACK) {
                                return true;
                            }
                        }
                    }
                } else if (_whoseMove == BLACK) {
                    for (int r = row; r >= row - 2; r -= 2) {
                        if (validSquare((char) c, (char) r) &&
                                !_board[index((char) c, (char) r)].isPiece()) {
                            Move jumpmove = move(col, row, (char) c, (char) r);
                            int jumpedindex = jumpmove.jumpedIndex();
                            PieceColor jumpedpiece = _board[jumpedindex];
                            if (jumpedpiece == WHITE) {
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (!isRadiatingSpot(k)) {
            for (int c = col - 2; c <= col + 2; c += 2) {
                int r = row;
                if (_whoseMove == WHITE) {
                    if (c == col) {
                        r = row + 2;
                    }
                    if (validSquare((char) c, (char) r) &&
                            !_board[index((char) c, (char) r)].isPiece()) {
                        Move jumpmove = move(col, row, (char) c, (char) r);
                        int jumpedindex = jumpmove.jumpedIndex();
                        PieceColor jumpedpiece = _board[jumpedindex];
                        if (jumpedpiece == BLACK) {
                            return true;
                        }
                    }
                }
                if (_whoseMove == BLACK) {
                    if (c == col) {
                        r = row - 2;
                    }
                    if (validSquare((char) c, (char) r) &&
                            !_board[index((char) c, (char) r)].isPiece()) {
                        Move jumpmove = move(col, row, (char) c, (char) r);
                        int jumpedindex = jumpmove.jumpedIndex();
                        PieceColor jumpedpiece = _board[jumpedindex];
                        if (jumpedpiece == WHITE) {
                            return true;
                        }
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

    /** Make the Move MOV on this Board, assuming it is legal. */
    void makeMove(Move mov) {
        assert legalMove(mov);
        while (mov != null) {
            char fromcol = mov.col0();
            char fromrow = mov.row0();
            char tocol = mov.col1();
            char torow = mov.row1();
            int fromindex = index(fromcol, fromrow);
            int toindex = index(tocol, torow);
            int jumpedindex = mov.jumpedIndex();
            this.set(fromindex, EMPTY);
            _board[fromindex].previndex = -1;
            this.set(jumpedindex, EMPTY);
            _board[jumpedindex].previndex = -1;
            this.set(toindex, _whoseMove);
            _board[toindex].previndex = fromindex;
            mov = mov.jumpTail();
        }
        _whoseMove = _whoseMove.opposite();

        setChanged();
        notifyObservers();
    }

    /** Undo the last move, if any. */
    void undo() {
        // FIXME

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
    private boolean isMove() {
        return false;  // FIXME
    }


    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Set true when game ends. */
    private boolean _gameOver;

    /** Represents contents of the game board. */
    private PieceColor[] _board;

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
