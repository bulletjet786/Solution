public class QuickSort {

  /* 双端扫描交换法：
   * i,j分别向前后扫描，遇到逆序队则交换
   * 注意点：
   * 1. 循环退出条件：i<=j
   *    在内部循环时始终要检查该条件是否成立,因为i，j一直在更新
   * 2. 循环退出后，i所在位置就是轴应在位置
   **/
  public static void sort_swap(int[] array, int low, int high) {
    if (low >= high) {
      return;
    }

    int pivot = array[low];
    int i = low + 1, j = high;
    // 被划分为 >pivot 和 <= pivot 两个部分
    while (i <= j) {
      while (i <= j && array[i] <= pivot) {
        i++;       // i 最终停在一个 > pivot  的地方
      }
      while (i <= j && array[j] > pivot) {
        j--;      // j 最终停在一个 <= high 的地方
      }
      if (i <= j) {       // 因为知道逆序对退出
        swap(array, i, j);
      }
    }
    // 此时划分完毕，且 i处于>pivot区首位置, j处于<=pivot区尾位置
    swap(array, low, j);               // i 指向划分点
    sort_swap(array, low, j - 1);
    sort_swap(array, j + 1, high);
  }

  /* 双端扫描填空法：
   * i,j分别向前后扫描，将空位数据保存在pivot中，一端始终持有空位，另一端扫描数据填入空位，交替操作
   * 注意点：
   * 1. 循环退出条件： i<j
   *    当i==j时，交换完成，array[i]即为空位，填入pivot
   **/
  public static void sort_fill(int[] array, int low, int high) {
    if (low >= high) {
      return;
    }

    int pivot = array[low];
    int i = low, j = high;
    // 划分为<=pivot 和 >pivot 两部分
    while (i < j) {     // i,j交替指向空位
      while (i < j && array[j] > pivot) {
        j--;
      }
      array[i] = array[j];
      while (i < j && array[i] <= pivot) {
        i++;
      }
      array[j] = array[i];
    }
    // i==j,且为空位
    array[i] = pivot;
    sort_fill(array, low, i - 1);
    sort_fill(array, i + 1, high);
  }

  /* 单向扫描三分法：
   * [low,i) <pivot; [i,j) =pivot; [j,k] 未扫描; (k,high] >pivot
   * 注意点:
   * 1. 当遇到array[j] > pivot时,只更换位置，不更新j（换回来的array[k]也可能大于）
   *
   **/
  public static void sort_single_3(int[] array, int low, int high) {
    if (low >= high) {
      return;
    }

    int pivot = array[low];
    int i = low, j = low + 1, k = high;
    while (j <= k) {
      if (array[j] == pivot) {
        j++;
      } else if (array[j] < pivot) {
        swap(array, i, j);
        i++;
        j++;
      } else {
        swap(array, j, k);
        k--;
      }
    }
    sort_single_3(array, low, i - 1);
    sort_single_3(array, k + 1, high);
  }


  /*
   * 双向扫描三分法: 类似单向扫描三分法
   * [low + 1,i) <pivot; [i,j) =pivot; [j,k] 未扫描; (k,high] >pivot
   * 1. 当遇到array[j] > pivot时,向前查找到首个<=pivot的元素，根据是<pivot或是=pivot分别处理,注意始终检查 j<= k是否成立
   **/
  public static void sort_double_3(int[] array, int low, int high) {
    if (low >= high) {
      return;
    }

    int pivot = array[low];
    int i = low, j = low + 1, k = high;
    while (j <= k) {
      if (array[j] == pivot) {
        j++;
      } else if (array[j] < pivot) {
        swap(array, i, j);
        i++;
        j++;
      } else {
        // 找到第一个<=pivot的数，找不到(处理完毕)则会退出外循环
        while (j <= k && array[k] > pivot) {
          k--;
        }
        if (array[k] == pivot) {    // 相等
          swap(array, k, j);
          j++;
          k--;
        } else {            // 小于
          swap(array, i, k);
          swap(array, j, k);
          i++;
          j++;
          k--;
        }
      }
    }
    sort_double_3(array, low, i - 1);
    sort_double_3(array, k + 1, high);
  }

  /* 双轴双向三分法: pivot1 < pivot2
   * [low,i) <pivot1; [i,j) pivot1 <= x <= pivot2; [j,k] 未扫描; (k,high] >pivot2
   * 注意点:
   * 1. 以上几种算法实现均可以保证递进条件(即每次递归至少使得要处理的数据规模减少1，一般为轴)，该实现可能会出现递进失败的情形
   * 2. 递进失败情形的处理：
   *    (1) 出现条件：
   *      当只能划出一个分区时，则递进失败；
   *      由算法的轴可以知道，算法一定会划分出中间分区；
   *      即 递进失败出现在只能划分出中间分区时，此时必然有(i = low && j = high + 1)；
   *         当(i = low && j = high + 1)时，必然只有一个中间分区，必然导致递进失败
   *    (2) 若划分完成，(i = low && j = high + 1)条件成立，则必然low为最小值，而high为最大值,则对(i+1, j-2)进行递进
   **/
  public static void sort_dual_pivot_3(int[] array, int low, int high) {
    if (low >= high) {
      return;
    }

    if (array[low] > array[high]) {
      swap(array, low, high);
    }

    int pivot1 = array[low], pivot2 = array[high];
    int i = low, j = low + 1, k = high;
    while (j <= k) {
      if (array[j] < pivot1) {
        swap(array, i, j);
        j++;
        i++;
      } else if (pivot1 <= array[j] && array[j] <= pivot2) {
        j++;
      } else {
        while (j <= k && array[k] > pivot2) {
          k--;
        }
        if (array[j] < pivot1) {
          swap(array, i, k);
          swap(array, j, k);
          i++;
          k--;
          j++;
        } else {
          swap(array, j, k);
          j++;
          k--;
        }
      }
    }
    if (low == i && j == high + 1) {
      sort_dual_pivot_3(array, i + 1, j - 2);
    } else {
      sort_dual_pivot_3(array, low, i - 1);
      sort_dual_pivot_3(array, i, j - 1);
      sort_dual_pivot_3(array, k + 1, high);
    }

  }


  public static void swap(int[] array, int i, int j) {
    int temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }

  public static void main(String[] args) {
    int[] array = new int[]{4, 2, 7, 1, 4, 3, 5, 6};
    sort_dual_pivot_3(array, 0, array.length - 1);
    for (int i = 0; i < array.length; i++) {
      System.out.print(array[i] + " ");
    }

  }

}
