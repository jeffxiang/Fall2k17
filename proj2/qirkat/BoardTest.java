package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

/** Tests of the Board class.
 *  @author Jeff Xiang
 */
public class BoardTest {

    private static final String INIT_BOARD =
        "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";

    private static final String[] GAME1 =
    { "c2-c3", "c4-c2",
      "c1-c3", "a3-c1",
      "c3-a3", "c5-c4",
      "a3-c5-c3",
    };

    private static final String GAME1_BOARD =
        "  b b - b b\n  b - - b b\n  - - w w w\n  w - - w w\n  w w b w w";

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(Move.parseMove(s));
        }
    }

    @Test
    public void testInit1() {
        Board b0 = new Board();
        assertEquals(INIT_BOARD, b0.toString());
    }

    @Test
    public void testMoves1() {
        Board b0 = new Board();
        makeMoves(b0, GAME1);
        b0.toString();
        assertEquals(GAME1_BOARD, b0.toString());
    }

    @Test
    public void testUndo() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        Board b2 = new Board(b0);
        for (int i = 0; i < GAME1.length; i += 1) {
            b0.undo();
        }
        assertEquals("failed to return to start", b1.toString(), b0.toString());
        makeMoves(b0, GAME1);
        assertEquals("second pass failed to reach same position",
                b2.toString(), b0.toString());
    }

    @Test
    public void testIsRadiatingSpot() {
        Board b = new Board();
        assertFalse(b.isRadiatingSpot(5));
        assertFalse(b.isRadiatingSpot(11));
        assertTrue(b.isRadiatingSpot(12));
        assertFalse(b.isRadiatingSpot(-1));
    }

    @Test
    public void testJumpPossible() {
        Board b = new Board();
        b.setPieces("---w- -w-b- -wb-- --b-w -----", PieceColor.WHITE);
        assertTrue(b.jumpPossible(3));
        assertTrue(b.jumpPossible(6));
        assertFalse(b.jumpPossible(8));
        assertFalse(b.jumpPossible(17));
        assertFalse(b.jumpPossible(2));
        assertFalse(b.jumpPossible('c', '3'));
        assertFalse(b.jumpPossible(10));
        assertFalse(b.jumpPossible(20));
        assertFalse(b.jumpPossible(1));
        assertFalse(b.jumpPossible(19));
        Board b2 = new Board();
        b2.setPieces("---w- -w-b- -b--- --b-- -----", PieceColor.BLACK);
        assertTrue(b2.jumpPossible(11));
        Board b3 = new Board();
        assertFalse(b3.jumpPossible(1));
        assertFalse(b3.jumpPossible('b', '2'));
        assertFalse(b3.jumpPossible());
        Board b4 = new Board();
        b4.setPieces("----- -ww-- -bbb- --b-- -----", PieceColor.WHITE);
        assertFalse(b4.jumpPossible(7));
    }

    @Test
    public void testSetPieces() {
        Board b = new Board();
        b.setPieces("---w- -w-b- -wb-- --b-- -----",
                PieceColor.WHITE);
        String boardstring =
                "  - - - - -\n  - - b - -\n  "
                        + "- w b - -\n  - w - b -\n  - - - w -";
        assertEquals(b.get(3), PieceColor.WHITE);
        assertEquals(b.get(12), PieceColor.BLACK);
        assertEquals(boardstring, b.toString());
    }

    @Test
    public void testCheckJump() {
        Board b = new Board();
        b.setPieces("---w- -w-b- -wb-- --b-- -----", PieceColor.WHITE);

        Move move1 = Move.parseMove("b2-d4-b4");
        assertTrue(b.checkJump(move1, false));
        Move move2 = Move.parseMove("b2-d2-b4");
        assertFalse(b.checkJump(move2, false));
        Move move3 = Move.parseMove("b2-d4");
        assertFalse(b.checkJump(move3, false));

        Board b2 = new Board();
        b2.setPieces("----- -w--- ---w- ---w- -bb--", PieceColor.BLACK);
        Move move4 = Move.parseMove("c5-e3-c3-a1");
        Move move5 = Move.parseMove("c5-e3-c3");
        Move move6 = Move.parseMove("c5-e3");
        assertTrue(b2.checkJump(move4, false));
        assertTrue(b2.checkJump(move4, true));
        assertFalse(b2.checkJump(move5, false));
        assertFalse(b2.checkJump(move6, false));
        assertTrue(b2.checkJump(move5, true));
        assertTrue(b2.checkJump(move6, true));
    }

    @Test
    public void testLegalMove() {
        Board b = new Board();
        b.setPieces("----- -wb-- ---w- ---w- -bb--", PieceColor.BLACK);
        Move move1 = Move.parseMove("c5-e3-c3-a1");
        Move move2 = Move.parseMove("c5-e3-c3");
        Move move3 = Move.parseMove("d3-d4");
        Move move4 = Move.parseMove("d3-d2");
        Move move5 = Move.parseMove("d4-e5");
        Move move6 = Move.parseMove("c2-c3");
        Move move7 = Move.parseMove("c2-d2");

        assertTrue(b.legalMove(move1));
        assertFalse(b.legalMove(move2));
        assertFalse(b.legalMove(move3));
        assertFalse(b.legalMove(move4));
        assertFalse(b.legalMove(move5));
        assertFalse(b.legalMove(move6));
        assertTrue(b.legalMove(move7));

        Board b2 = new Board();
        b2.setPieces("----- -wb-- ---w- ---w- -bb--", PieceColor.WHITE);
        Move move8 = Move.parseMove("b2-d2");
        Move move9 = Move.parseMove("d4-e5");
        Move move10 = Move.parseMove("d3-d2");

        assertTrue(b2.legalMove(move8));
        assertTrue(b2.legalMove(move9));
        assertFalse(b2.legalMove(move10));

        Board b3 = new Board();
        b3.setPieces("---w- -w-b- -wb-- -wbw- --w--", PieceColor.WHITE);
        String[] moves = {"b3-a3", "c3-d3"};
        makeMoves(b3, moves);
        Move illegalmove = Move.parseMove("a3-b3");
        assertFalse(b3.legalMove(illegalmove));
        b3.makeMove(Move.parseMove("a3-a4"));
        assertTrue(b3.whoseMove() == PieceColor.BLACK);
        assertFalse(b3.legalMove(Move.parseMove("d3-c3")));
        b3.makeMove(Move.parseMove("d2-e1"));
        b3.makeMove(Move.parseMove("b2-a3"));
        b3.makeMove(Move.parseMove("d3-d5-b5-b3"));
        b3.makeMove(Move.parseMove("d1-c1"));
        b3.makeMove(Move.parseMove("b3-c3"));
        b3.makeMove(Move.parseMove("c1-b1"));
        assertFalse(b3.legalMove(Move.parseMove("c3-b3")));

        Board b4 = new Board();
        b4.setPieces("wwwww wwbww bb-ww bb-bb bbbbb", PieceColor.WHITE);
        assertFalse(b4.legalMove(Move.parseMove("d3-c4")));
    }

    @Test
    public void testGetMoves() {
        Board b = new Board();
        ArrayList<Move> moves = new ArrayList<>();
        b.getMoves(moves, 13);
        String gotmove = moves.get(0).toString();
        assertEquals("d3-c3", gotmove);

        b.getMoves(moves, 11);
        assertEquals(1, moves.size());

        b.getMoves(moves, 7);
        assertEquals("c2-c3", moves.get(1).toString());

        b.getMoves(moves, 3);
        assertEquals(2, moves.size());

        ArrayList<Move> moves2 = new ArrayList<>();
        b.getMoves(moves2);
        assertTrue(moves2.contains(Move.parseMove("b2-c3")));
        assertTrue(moves2.contains(Move.parseMove("c2-c3")));
        assertTrue(moves2.contains(Move.parseMove("d2-c3")));
        assertTrue(moves2.contains(Move.parseMove("d3-c3")));
        assertEquals(4, moves2.size());
    }

    @Test
    public void testOneJumps() {
        Board b = new Board();
        b.setPieces("----- -ww-- -bbb- --b-- -----", PieceColor.WHITE);
        ArrayList<Move> l1 = b.getOneJumps(7);
        ArrayList<Move> l2 = b.getOneJumps(6);
        assertEquals(0, l1.size());
        assertEquals(2, l2.size());
        assertTrue(l2.contains(Move.parseMove("b2-b4")));
        assertTrue(l2.contains(Move.parseMove("b2-d4")));
        b.setPieces("----- -ww-- -bbb- --b-- -----", PieceColor.BLACK);
        l1 = b.getOneJumps(11);
        assertEquals(1, l1.size());
        assertTrue(l1.contains(Move.parseMove("b3-b1")));
    }

    @Test
    public void testGetJumps() {
        Board b = new Board();
        b.setPieces("----- -ww-- -bbb- --b-- -----", PieceColor.WHITE);
        ArrayList<Move> moves = new ArrayList<>();
        b.getJumps(moves, 6);
        assertEquals(5, moves.size());
        assertTrue(moves.contains(Move.parseMove("b2-d4-d2")));
        b.setPieces("----- -ww-- -bbb- --b-- -----", PieceColor.BLACK);
        moves.clear();
        b.getJumps(moves, 12);
        assertTrue(moves.contains(Move.parseMove("c3-c1-a3")));
        assertTrue(moves.size() == 2);
    }

    @Test
    public void testGetJumpHelper() {
        Board b = new Board();
        b.setPieces("----- -ww-- -bbb- --b-- -----", PieceColor.WHITE);
        ArrayList<Move> moves = new ArrayList<>();
        b.getJumpsHelper(moves, 6);
        System.out.println(moves);
    }

    @Test
    public void testGetMoves1() {
        Board b = new Board();
        b.setPieces("bbb-- bwb-- bbb-- ----- -----", PieceColor.WHITE);
        assertTrue(b.isMove());
    }

    @Test
    public void testconstantboard() {
        Board b = new Board();
        b.setPieces("wwwww wwwww bb-ww wwwww wwwww", PieceColor.BLACK);
        Board b2 = b.constantView();
    }
}
