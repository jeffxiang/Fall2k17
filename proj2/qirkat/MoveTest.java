/* Author: Paul N. Hilfinger.  (C) 2008. */

package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import static qirkat.Move.*;

/** Test Move creation.
 *  @author
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
        Move move3 = move('c','5', 'c', '3');
        Move result1 = move(vestigialmove, move2);
        Move expected1 = move('c', '1', 'c', '3', move2);
        assertTrue(result1.equals(expected1));
        Move result2 = move(move3, vestigialmove);
        Move nextjump2 = move('c', '3','c', '1');
        Move expected2 = move('c', '5', 'c', '3', nextjump2);
        assertTrue(result2.equals(expected2));
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
}
