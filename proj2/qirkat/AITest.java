package qirkat;

import org.junit.Test;

import static org.junit.Assert.*;

public class AITest {

    @Test
    public void testStaticScore() {
        Board b = new Board();
        CommandSources c = new CommandSources();
        TextReporter t = new TextReporter();
        Game g = new Game(b, c, t);
        AI ai = new AI(g, PieceColor.WHITE);
        b.setPieces("wwwww b---- ----- ----- -----", PieceColor.WHITE);
        assertEquals(4, ai.staticScore(b));
        b.makeMove(Move.parseMove("a1-a3"));
        assertEquals(Integer.MAX_VALUE, ai.staticScore(b));
    }

    @Test
    public void testFindMove() {
        Board b = new Board();
        b.setPieces("wwwww b---- ----- ----- -----", PieceColor.WHITE);
        CommandSources c = new CommandSources();
        TextReporter t = new TextReporter();
        Game g = new Game(b, c, t);
        AI ai = new AI(g, PieceColor.WHITE);
        Move m = ai.findMove();
        assertEquals(m, Move.parseMove("a1-a3"));
        Board b2 = new Board();
        b2.setPieces("----- bbbbb --w-- ----- -----", PieceColor.BLACK);
        CommandSources c2 = new CommandSources();
        TextReporter t2 = new TextReporter();
        Game g2 = new Game(b2, c2, t2);
        AI ai2 = new AI(g2, PieceColor.BLACK);
        Move m2 = ai2.findMove();
        b2.makeMove(m2);
        assertTrue(b2.gameOver());
        Board b3 = new Board();
        b3.setPieces("wwwww wwww- b---b bb--- b---b", PieceColor.WHITE);
        CommandSources c3 = new CommandSources();
        TextReporter t3 = new TextReporter();
        Game g3 = new Game(b3, c3, t3);
        AI ai3 = new AI(g3, PieceColor.WHITE);
        b3.makeMove(Move.parseMove("d2-e2"));
        b3.makeMove(Move.parseMove("e5-e4"));
        Board b4 = new Board();
        CommandSources c4 = new CommandSources();
        TextReporter t4 = new TextReporter();
        Game g4 = new Game(b4, c4, t4);
        AI ai4 = new AI(g4, PieceColor.WHITE);
        b4.makeMove(ai4.findMove());
    }

}
