/* Author: Paul N. Hilfinger.  (C) 2008. */

package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import static qirkat.Move.*;

/** Test Move creation.
 *  @author Jeff Xiang
 */
public class MoveTest {

    @Test
    public void testMove1() {
        Move m = move('a', '3', 'b', '2');
        assertNotNull(m);
        assertFalse("move should not be jump", m.isJump());
    }

    @Test
    public void testMove2() {
        Move vestigialmove = move('c', '1', 'c', '1');
        Move move2 = move('c', '3', 'c', '5');
        Move move3 = move('a', '1', 'a', '3');
        Move afterm4 = move('a', '5', 'c', '5');
        Move move4 = move('a', '3', 'a', '5', afterm4);
        Move result1 = move(vestigialmove, move2);
        Move expected1 = move('c', '1', 'c', '3', move2);
        assertTrue(result1.equals(expected1));
        Move result2 = move(move3, move4);
        Move expected2 = move('a', '1', 'a', '3',
                move('a', '3', 'a', '5',
                        move('a', '5', 'c', '5')));
        assertTrue(result2.equals(expected2));

        Move movec = move('a', '5', 'c', '5');
        Move moveb = move('a', '3', 'a', '5', movec);
        Move movea = move('a', '1', 'a', '3', moveb);

        Move movef = move('c', '1', 'e', '1');
        Move movee = move('c', '3', 'c', '1', movef);
        Move moved = move('c', '5', 'c', '3', movee);

        Move hugeexpected = move('a', '1', 'a', '3',
                move('a', '3', 'a', '5',
                        move('a', '5', 'c', '5',
                                move('c', '5', 'c', '3',
                                        move('c', '3', 'c', '1',
                                                move('c', '1', 'e', '1'))))));

        Move hugeresult = move(movea, moved);
        assertTrue(hugeresult.equals(hugeexpected));
    }

    @Test
    public void testIsDirectionMove() {
        Move move0 = move('a', '2', 'a', '1');
        Move move1 = move('c', '3', 'b', '3');
        Move move2 = move('b', '1', 'c', '1');
        assertFalse(move0.isRightMove());
        assertTrue(move1.isLeftMove());
        assertTrue(move2.isRightMove());
        assertFalse(move2.isLeftMove());
    }

    @Test
    public void testJumpedIndex() {
        Move move0 = move('a', '2', 'a', '4');
        int result0 = move0.jumpedIndex();
        assertEquals(10, result0);

        Move move1 = move('a', '2', 'c', '4');
        int result1 = move1.jumpedIndex();
        assertEquals(11, result1);
    }

    @Test
    public void testJump1() {
        Move m = move('a', '3', 'a', '5');
        assertNotNull(m);
        assertTrue("move should be jump", m.isJump());
    }

    @Test
    public void testString() {
        assertEquals("a3-b2", move('a', '3', 'b', '2').toString());
        assertEquals("a3-a5", move('a', '3', 'a', '5').toString());
        assertEquals("a3-a5-c3", move('a', '3', 'a', '5',
                                      move('a', '5', 'c', '3')).toString());
    }

    @Test
    public void testParseString() {
        assertEquals("a3-b2", parseMove("a3-b2").toString());
        assertEquals("a3-a5", parseMove("a3-a5").toString());
        assertEquals("a3-a5-c3", parseMove("a3-a5-c3").toString());
        assertEquals("a3-a5-c3-e1", parseMove("a3-a5-c3-e1").toString());
    }

    @Test
    public void testTailIndex() {
        assertEquals(0, parseMove("c1-c3-a3-a1").tailIndex());
        assertEquals(10, parseMove("a1-c1-c3-a3").tailIndex());
    }
}
