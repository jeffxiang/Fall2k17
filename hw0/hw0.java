public class hw0 {
  public static int max_forloop(int[] array) {
    int value = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] > value) {
        value = array[i];
      }
    }
    return value;
  }

  public static int max_whileloop(int[] array) {
    int value = array[0];
    int i = 1;
    while (i < array.length) {
      if (array[i] > value) {
        value = array[i];
      }
      i++;
    }
    return value;
  }

  public static boolean threesum(int[] a) {
    for (int i = 0; i < a.length; i++) {
      int num_i = a[i];
      for (int j = 0; j < a.length; j++) {
        int num_j = a[j];
        for (int k = 0; k < a.length; k++) {
          int num_k = a[k];
          int sum = num_j + num_k + num_i;
          if (sum == 0) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public static boolean threesum_distinct(int[] a) {
    for (int i = 0; i < a.length; i++) {
      int num_i = a[i];
      for (int j = 0; j < a.length; j++) {
        int num_j = a[j];
        for (int k = 0; k < a.length; k++) {
          int num_k = a[k];
          int sum = num_j + num_k + num_i;
          if ((sum == 0) && (i != j) && (j != k) && (i != k)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public static void main(String[] args) {
    int[] testarray = {5,1,0,3,6};
    boolean result = threesum(testarray);
    System.out.println(result);
  }
}
