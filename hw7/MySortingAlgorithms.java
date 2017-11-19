import java.util.Arrays;

/**
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Distribution Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i  = 1; i < k; i++) {
                if (array[i] < array[i - 1]) {
                    int smaller = array[i];
                    array[i] = array[i - 1];
                    array[i - 1] = smaller;
                    int p = i - 1;
                    while ((p - 1) >= 0 && array[p] < array[p - 1]) {
                        int smaller2 = array[p];
                        array[p] = array[p - 1];
                        array[p - 1] = smaller2;
                        p--;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            int t = 0;
            int i = 0;
            int c = 0;
            while (t < k) {
                int currcompare = array[t];
                int stationcompare = array[i];
                if (currcompare < stationcompare) {
                    i = t;
                }
                if (t == k - 1) {
                    int temp = array[c];
                    array[c] = array[i];
                    array[i] = temp;
                    c++;
                    i = c;
                    t = i;
                }
                t++;
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Distribution Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class DistributionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Distribution Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            SortingAlgorithm InsertionSort = new InsertionSort();
            int i = 0;
            while (i < 4) {
                partition(array, k);
                i++;
            }
            InsertionSort.sort(array, k);
        }

        public void partition(int[] array, int endlength) {
            int pivotval = array[0];
            int j = endlength - 1;
            int i = 1;
            while (i <= j) {
                int currval = array[i];
                int endval = array[j];
                if (currval > pivotval && currval > endval && endval < pivotval) {
                    array[i] = endval;
                    array[j] = currval;
                    j--;
                    i++;
                } else if (currval > pivotval && endval > pivotval){
                    j--;
                } else {
                    i++;
                }
            }
            int small = array[j];
            array[0] = small;
            array[j] = pivotval;
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of k-bit numbers.  For
     * example, if you take k to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * k to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of k. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {

        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
