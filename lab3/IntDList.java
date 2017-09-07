
public class IntDList {

    protected DNode _front, _back;

    public IntDList() {
        _front = _back = null;
    }

    public IntDList(Integer... values) {
        _front = _back = null;
        for (int val : values) {
            insertBack(val);
        }
    }

    /** Returns the first item in the IntDList. */
    public int getFront() {
        return _front._val;
    }

    /** Returns the last item in the IntDList. */
    public int getBack() {
        return _back._val;
    }

    /** Return value #I in this list, where item 0 is the first, 1 is the
     *  second, ...., -1 is the last, -2 the second to last.... */
    public int get(int i) {
        DNode curr = _front;
        DNode curr2 = _back;
      if (i >= 0) {
        while (i > 0) {
          curr = curr._next;
          i -= 1;
        }
        return curr._val;
      }
      else {
        while (i < -1) {
          curr2 = curr2._prev;
          i++;
        }
        return curr2._val;
      }
    }

    /** The length of this list. */
    public int size() {
        int i = 0;
        DNode curr = _front;
        while (curr != null) {
          curr = curr._next;
          i++;
        }
        return i;
    }

    /** Adds D to the front of the IntDList. */
    public void insertFront(int d) {
        if (_front == null) {
          DNode newfront = new DNode(d);
          _front = newfront;
          _back = newfront;
        }
        else {
          DNode newfront = new DNode(null, d, _front);
          _front._prev = newfront;
          _front = newfront;
        }
    }

    /** Adds D to the back of the IntDList. */
    public void insertBack(int d) {
      if (_back == null) {
        insertFront(d);
      }
      else {
        DNode newback = new DNode(_back, d, null);
        _back._next = newback;
        _back = newback;
      }
    }

    /** Removes the last item in the IntDList and returns it.
     * This is an extra challenge problem. */
    public int deleteBack() {
        return 0;   // Your code here

    }

    /** Returns a string representation of the IntDList in the form
     *  [] (empty list) or [1, 2], etc.
     * This is an extra challenge problem. */
    public String toString() {
        return null;   // Your code here
    }

    /* DNode is a "static nested class", because we're only using it inside
     * IntDList, so there's no need to put it outside (and "pollute the
     * namespace" with it. This is also referred to as encapsulation.
     * Look it up for more information! */
    protected static class DNode {
        protected DNode _prev;
        protected DNode _next;
        protected int _val;

        private DNode(int val) {
            this(null, val, null);
        }

        private DNode(DNode prev, int val, DNode next) {
            _prev = prev;
            _val = val;
            _next = next;
        }
    }

}
