/* NOTE: The file Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2, Problem #1. */

/** List problem.
 *  @author Jeff Xiang
 */
class Lists {
    /** Return the list of lists formed by breaking up L into "natural runs":
     *  that is, maximal strictly ascending sublists, in the same order as
     *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     *  then result is the four-item list
     *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     *  Destructive: creates no new IntList items, and may modify the
     *  original list pointed to by L. */
    static IntList2 naturalRuns(IntList L) {
        if (L == null) {
            return null;
        }
        IntList a = L;
        IntList curr = L;
        while (curr.tail != null && curr.head < curr.tail.head) {
            curr = curr.tail;
            if (curr.tail == null) {
                break;
            }
        }
        IntList rest = curr.tail;
        curr.tail = null;

        return new IntList2(a, naturalRuns(rest));
    }

}
