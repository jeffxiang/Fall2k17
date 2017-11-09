package qirkat;

/* Author: P. N. Hilfinger */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

import static qirkat.PieceColor.*;
import static qirkat.Game.State.*;
import static qirkat.Command.Type.*;
import static qirkat.GameException.error;

/** Controls the play of the game.
 *  @author Jeff Xiang
 */
class Game {

    /** States of play. */
    static enum State {
        SETUP, PLAYING;
    }

    /** A new Game, using BOARD to play on, reading initially from
     *  BASESOURCE and using REPORTER for error and informational messages. */
    Game(Board board, CommandSource baseSource, Reporter reporter) {
        _inputs.addSource(baseSource);
        _board = board;
        _constBoard = _board.constantView();
        _reporter = reporter;
    }

    /** Run a session of Qirkat gaming. */
    void process() {
        Player white, black;
        white = black = null;
        doClear(null);
        while (true) {
            while (_state == SETUP) {
                doCommand();
            }
            if (_whiteIsManual) {
                white = new Manual(this, PieceColor.WHITE);
            } else {
                white = new AI(this, PieceColor.WHITE);
            }
            if (_blackIsManual) {
                black = new Manual(this, PieceColor.BLACK);
            } else {
                black = new AI(this, PieceColor.BLACK);
            }
            Player currplayer;
            if (_board.whoseMove() == PieceColor.WHITE) {
                currplayer = white;
            } else {
                currplayer = black;
            }
            while (_state != SETUP && !_board.gameOver()) {
                Command cmnd = null;
                if (currplayer == white) {
                    if (_whiteIsManual) {
                        cmnd = getMoveCmnd("White: ");
                    }
                } else {
                    if (_blackIsManual) {
                        cmnd = getMoveCmnd("Black: ");
                    }
                }
                if (_state == PLAYING) {
                    if (currplayer.isManual()) {
                        this.doMove(cmnd.operands());
                    } else {
                        this.doMove(currplayer.myMove());
                    }
                    if (_board.whoseMove() == WHITE) {
                        currplayer = white;
                    } else {
                        currplayer = black;
                    }
                }
            }
            if (_state == PLAYING) {
                reportWinner();
            }
            _state = SETUP;
        }
    }

    /** Return a read-only view of my game board. */
    Board board() {
        return _constBoard;
    }

    /** Perform the next command from our input source. */
    void doCommand() {
        try {
            Command cmnd =
                Command.parseCommand(_inputs.getLine("qirkat: "));
            _commands.get(cmnd.commandType()).accept(cmnd.operands());
        } catch (GameException excp) {
            _reporter.errMsg(excp.getMessage());
        }
    }

    /** Read and execute commands until encountering a move or until
     *  the game leaves playing state due to one of the commands. Return
     *  the terminating move command, or null if the game first drops out
     *  of playing mode. If appropriate to the current input source, use
     *  PROMPT to prompt for input. */
    Command getMoveCmnd(String prompt) {
        while (_state == PLAYING) {
            try {
                Command cmnd = Command.parseCommand(_inputs.getLine(prompt));
                switch (cmnd.commandType()) {
                case PIECEMOVE:
                    return cmnd;
                default:
                    _commands.get(cmnd.commandType()).accept(cmnd.operands());
                }
            } catch (GameException excp) {
                _reporter.errMsg(excp.getMessage());
            }
        }
        return null;
    }

    /** Return random integer between 0 (inclusive) and MAX>0 (exclusive). */
    int nextRandom(int max) {
        return _randoms.nextInt(max);
    }

    /** Report a move, using a message formed from FORMAT and ARGS as
     *  for String.format. */
    void reportMove(String format, Object... args) {
        _reporter.moveMsg(format, args);
    }

    /** Report an error, using a message formed from FORMAT and ARGS as
     *  for String.format. */
    void reportError(String format, Object... args) {
        _reporter.errMsg(format, args);
    }

    /* Command Processors */

    /** Perform the command 'auto OPERANDS[0]'. */
    void doAuto(String[] operands) {
        _state = SETUP;
        String expr = removeWhitespace(operands[0]);
        expr = expr.replaceAll("w", "W");
        expr = expr.replaceAll("b", "B");
        switch (expr) {
        case "White":
            _whiteIsManual = false;
            break;
        case "Black":
            _blackIsManual = false;
            break;
        default:
            reportError("Invalid player entry.");
            break;
        }
    }

    /** Perform a 'help' command. */
    void doHelp(String[] unused) {
        InputStream helpIn =
            Game.class.getClassLoader().getResourceAsStream("qirkat/help.txt");
        if (helpIn == null) {
            System.err.println("No help available.");
        } else {
            try {
                BufferedReader r
                    = new BufferedReader(new InputStreamReader(helpIn));
                while (true) {
                    String line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    System.out.println(line);
                }
                r.close();
            } catch (IOException e) {
                /* Ignore IOException */
            }
        }
    }

    /** Perform the command 'load OPERANDS[0]'. */
    void doLoad(String[] operands) {
        try {
            FileReader reader = new FileReader("../proj2/qirkat/"
                    + operands[0]);
            ReaderSource source = new ReaderSource(reader, false);
            _inputs.addSource(source);
            process();

        } catch (IOException e) {
            throw error("Cannot open file %s", operands[0]);
        }
    }

    /** Perform the command 'manual OPERANDS[0]'. */
    void doManual(String[] operands) {
        _state = SETUP;
        String expr = removeWhitespace(operands[0]);
        expr = expr.replaceAll("w", "W");
        expr = expr.replaceAll("b", "B");
        switch (expr) {
        case "White":
            _whiteIsManual = true;
            break;
        case "Black":
            _blackIsManual = true;
            break;
        default:
            reportError("Invalid player entry.");
            break;
        }
    }

    /** Exit the program. */
    void doQuit(String[] unused) {
        Main.reportTotalTimes();
        System.exit(0);
    }

    /** Perform the command 'start'. */
    void doStart(String[] unused) {
        _state = PLAYING;
    }

    /** Perform the move OPERANDS[0]. */
    void doMove(String[] operands) {
        try {
            String expr = removeWhitespace(operands[0]);
            Move move = Move.parseMove(expr);
            String whomoved = _board.whoseMove().toString();
            _board.makeMove(move);
            _constBoard = _board;
            if (_board.whoseMove() != PieceColor.WHITE) {
                if (!_whiteIsManual) {
                    reportMove(whomoved + " moves " + move.toString() + ".");
                }
            } else if (_board.whoseMove() != PieceColor.BLACK) {
                if (!_blackIsManual) {
                    reportMove(whomoved + " moves " + move.toString() + ".");
                }
            }
        } catch (AssertionError excp) {
            reportError("Illegal move. Request another move.");
        }
    }

    /** Performs move m.
     * @param m Move */
    void doMove(Move m) {
        String[] operands = new String[1];
        operands[0] = m.toString();
        doMove(operands);
    }

    /** Perform the command 'clear'. */
    void doClear(String[] unused) {
        _state = SETUP;
        _board = new Board();
        _blackIsManual = false;
        _whiteIsManual = true;
    }

    /** Get rid of whitespace from String cmnd and return the
     * resultant string.
     * @param cmnd String of command */
    String removeWhitespace(String cmnd) {
        return cmnd.replaceAll("\\s*", "");
    }

    /** Perform the command 'set OPERANDS[0] OPERANDS[1]'. */
    void doSet(String[] operands) {
        _state = SETUP;
        _blackIsManual = true;
        _whiteIsManual = true;
        String playerturn = removeWhitespace(operands[0]);
        playerturn = playerturn.replaceAll("w", "W");
        playerturn = playerturn.replaceAll("b", "B");
        switch (playerturn) {
        case "White":
            _board.setPieces(operands[1], PieceColor.WHITE);
            break;
        case "Black":
            _board.setPieces(operands[1], PieceColor.BLACK);
            break;
        default:
            reportError("Invalid entry");
            break;
        }

    }

    /** Perform the command 'dump'. */
    void doDump(String[] unused) {
        System.out.println("===");
        System.out.println(_board.toString());
        System.out.println("===");
    }

    /** Execute 'seed OPERANDS[0]' command, where the operand is a string
     *  of decimal digits. Silently substitutes another value if
     *  too large. */
    void doSeed(String[] operands) {
        try {
            _randoms.setSeed(Long.parseLong(operands[0]));
        } catch (NumberFormatException e) {
            _randoms.setSeed(Long.MAX_VALUE);
        }
    }

    /** Returns true iff white is manual. */
    boolean isWhiteManual() {
        return _whiteIsManual;
    }

    /** Returns true iff black is manual. */
    boolean isBlackManual() {
        return _blackIsManual;
    }

    /** Execute the artificial 'error' command. */
    void doError(String[] unused) {
        throw error("Command not understood");
    }

    /** Report the outcome of the current game. */
    void reportWinner() {
        String msg;
        String winner = null;
        if (_board.gameOver()) {
            winner = _board.whoWon();
        }
        msg = winner + " wins.";
        _reporter.outcomeMsg(msg);
    }

    /** Mapping of command types to methods that process them. */
    private final HashMap<Command.Type, Consumer<String[]>> _commands =
        new HashMap<>();

    {
        _commands.put(AUTO, this::doAuto);
        _commands.put(CLEAR, this::doClear);
        _commands.put(DUMP, this::doDump);
        _commands.put(HELP, this::doHelp);
        _commands.put(MANUAL, this::doManual);
        _commands.put(PIECEMOVE, this::doMove);
        _commands.put(SEED, this::doSeed);
        _commands.put(SETBOARD, this::doSet);
        _commands.put(START, this::doStart);
        _commands.put(LOAD, this::doLoad);
        _commands.put(QUIT, this::doQuit);
        _commands.put(ERROR, this::doError);
        _commands.put(EOF, this::doQuit);
    }

    /** Input source. */
    private final CommandSources _inputs = new CommandSources();

    /** My board and its read-only view. */
    private Board _board, _constBoard;
    /** Indicate which players are manual players (as opposed to AIs). */
    private boolean _whiteIsManual, _blackIsManual;
    /** Current game state. */
    private State _state;
    /** Used to send messages to the user. */
    private Reporter _reporter;
    /** Source of pseudo-random numbers (used by AIs). */
    private Random _randoms = new Random();
}
