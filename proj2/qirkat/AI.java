package qirkat;

import java.util.ArrayList;

import static qirkat.PieceColor.*;

/** A Player that computes its own moves.
 *  @author Jeff Xiang
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 8;
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
        Board b = new Board(this.board());
        if (myColor() == WHITE) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
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
        Move best;
        best = null;

        if (depth == 0 || board.gameOver()) {
            return staticScore(board);
        }
        int maxsofar = -INFTY;
        int minsofar = INFTY;
        for (Move m: board.getMoves()) {

            Board copy = new Board(board);

            copy.makeMove(m);
            if (sense == 1) {
                if (staticScore(copy) >= maxsofar) {
                    best = m;
                    maxsofar = staticScore(copy);
                    alpha = Integer.max(alpha, staticScore(copy));
                    if (beta <= alpha) {
                        break;
                    }
                }
            } else if (sense == -1) {
                if (staticScore(copy) <= minsofar) {
                    best = m;
                    minsofar = staticScore(copy);
                    beta = Integer.min(beta, staticScore(copy));
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            findMove(copy, depth - 1, saveMove, sense, alpha, beta);
        }

        if (saveMove) {
            _lastFoundMove = best;
            if (!board().legalMove(_lastFoundMove)) {
                ArrayList<Move> moves = board().getMoves();
                int index = 0;
                if (moves.size() != 1) {
                    index = game().nextRandom(moves.size() - 1);
                }
                _lastFoundMove = moves.get(index);
            }
        }

        return maxsofar;
    }

    /** Return a heuristic value for BOARD. */
    int staticScore(Board board) {
        int whitecount = 0;
        int blackcount = 0;
        String boardstring = board.toString();
        for (int i = 0; i < boardstring.length(); i++) {
            if (boardstring.charAt(i) == 'w') {
                whitecount++;
            } else if (boardstring.charAt(i) == 'b') {
                blackcount++;
            }
        }
        if (whitecount == 0) {
            return -INFTY;
        }
        if (blackcount == 0) {
            return INFTY;
        }
        return whitecount - blackcount;
    }
}
