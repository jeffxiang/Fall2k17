package qirkat;

import org.junit.Test;

import static org.junit.Assert.*;


/** Tests of the Game class.
 * @author Jeff Xiang
 */

public class GameTest {

    @Test
    public void testDoAuto() {
        Board b = new Board();
        CommandSources c = new CommandSources();
        TextReporter t = new TextReporter();
        Game g = new Game(b, c, t);
        String[] operands = {"   White "};
        g.doAuto(operands);
        assertFalse(g.isWhiteManual());
        String[] operands2 = {"       Black "};
        g.doAuto(operands2);
        assertFalse(g.isBlackManual());
    }

    @Test
    public void testDoManual() {
        Board b = new Board();
        CommandSources c = new CommandSources();
        TextReporter t = new TextReporter();
        Game g = new Game(b, c, t);
        String[] operands = {"     White "};
        g.doManual(operands);
        assertTrue(g.isWhiteManual());
        String[] operands2 = {"Black"};
        g.doManual(operands2);
        assertTrue(g.isBlackManual());
    }

    @Test
    public void testDoMove() {
        Board b = new Board();
        CommandSources c = new CommandSources();
        TextReporter t = new TextReporter();
        Game g = new Game(b, c, t);
        String[] operands = {"     c4-c3"};
        g.doMove(operands);
    }

    @Test
    public void testSet() {
        Board b = new Board();
        CommandSources c = new CommandSources();
        TextReporter t = new TextReporter();
        Game g = new Game(b, c, t);
        String[] operands = {" Black", "ww-ww wwwww bb-ww bbbbb bbbbb"};
        g.doSet(operands);
        assertTrue(g.board().whoseMove() == PieceColor.BLACK);
        assertFalse(g.board().whoseMove() == PieceColor.WHITE);
    }
}
