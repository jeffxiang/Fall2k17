package qirkat;

import static qirkat.PieceColor.*;

/** A Player that computes its own moves.
 *  @author Jeff Xiang
 *  with pseudocode for findMove from Wikipedia: Alpha-Beta Pruning.
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 3;
    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        Main.startTiming();
        Move move = findMove();
        Main.endTiming();
        return move;
    }

    @Override
    /** Returns false because I am not manual. */
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    Move findMove() {
        Board myboard = new Board(this.board());
        if (myColor() == WHITE) {
            findMove(myboard, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(myboard, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        int v;
        Move best;
        best = null;
        int b = INFTY * -sense;

        if (depth == 0 || board.gameOver()) {
            return staticScore(board);
        }
        if (board.getMoves().isEmpty() && board.whoseMove() == WHITE) {
            return -INFTY;
        }
        if (board.getMoves().isEmpty() && board.whoseMove() == BLACK) {
            return INFTY;
        }
        for (Move m: board.getMoves()) {
            board.makeMove(m);
            if (sense == 1) {
                v = findMove(board, depth - 1, false, -1, alpha, beta);
                if (v >= alpha) {
                    best = m;
                    alpha = Math.max(alpha, v);
                }
                board.undo();
                if (beta <= alpha) {
                    break;
                }
            } else if (sense == -1) {
                v = findMove(board, depth - 1, false, 1, alpha, beta);
                if (v <= beta) {
                    best = m;
                    beta = Math.min(beta, v);
                }
                board.undo();
                if (beta >= alpha) {
                    break;
                }
            }
        }
        if (saveMove) {
            _lastFoundMove = best;
        }
        return b;
    }

    /** Return a heuristic value for BOARD. */
    int staticScore(Board board) {
        int whitecount = 0;
        int blackcount = 0;
        int bottomwhitecount = 0;
        int topblackcount = 0;
        String boardstring = board.toString();
        for (int i = 0; i < boardstring.length(); i++) {
            if (boardstring.charAt(i) == 'w') {
                whitecount++;
            } else if (boardstring.charAt(i) == 'b') {
                blackcount++;
            }
        }
        int length = Move.SIDE * Move.SIDE;
        for (int i = 0; i < length; i++) {
            if (i < 5) {
                PieceColor bottomrowpiece = board.get(i);
                if (bottomrowpiece == WHITE) {
                    bottomwhitecount++;
                }
            } else if (i >= length - 5) {
                PieceColor toprowpiece = board.get(i);
                if (toprowpiece == BLACK) {
                    topblackcount++;
                }
            }
        }
        if (whitecount == 0 || board.whoWon().equals("Black")) {
            return -INFTY;
        }
        if (blackcount == 0 || board.whoWon().equals("White")) {
            return INFTY;
        }
        return whitecount - blackcount + bottomwhitecount - topblackcount;
    }
}
