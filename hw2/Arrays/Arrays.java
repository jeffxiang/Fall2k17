

/* NOTE: The file ArrayUtil.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @ Jeff Xiang
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        int[] result = new int[A.length+B.length];
        int i = 0;
        while (i < A.length) {
            result[i] = A[i];
            i++;
        }
        int j = 0;
        while (j < B.length) {
            result[i+j] = B[j];
            j++;
        }
        return result;
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        if (A.length == 0) {
            return new int[0];
        }
        int i = 0;
        int[] headlist = new int[start];
        int[] taillist = new int[A.length-start-len];
        while (i < start) {
            headlist[i] = A[i];
            i++;
        }
        i = i + len;
        int j = 0;
        while (i < A.length) {
            taillist[j] = A[i];
            i++;
            j++;
        }
        return catenate(headlist, taillist);
    }

    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    /*static int[][] naturalRuns(int[] A) {
        int len = A.length;
        if (len == 0) {
            return null;
        }
        int endrun = 0;
        for (int i = 0; i < A.length && A[i] < A[i+1]; i++) {
            endrun = i;
        }
        int[] run = remove(A, endrun+1, A.length-endrun);
        int[] tail = Utils.subarray(A, endrun+1, A.length-endrun);
        return null;
*/

    static int[][] naturalRuns(int[] A) {
        if (A.length == 0) {
            return new int[0][];
        }
        int runcount = 0;
        int[] copy = A;
        int i = 0;
        while (i+1 < copy.length) {
            if (copy[i] >= copy[i+1]) {
                runcount++;
            }
            i++;
        }
        runcount++;
        int[][] result = new int[runcount][];
        for (int j = 0; j < result.length; j++) {
            for (int k = 0; k < A.length; k++) {
                if (k+1 == A.length) {
                    int[] run = A;
                    result[j] = run;
                    break;
                }
                if (k+1 < A.length && A[k] >= A[k+1]) {
                    int endrun = k;
                    int[] run = remove(A, endrun+1, A.length-endrun-1);
                    result[j] = run;
                    A = Utils.subarray(A, endrun+1, A.length-endrun-1);
                    break;
                }
            }
        }
        return result;
    }
}
