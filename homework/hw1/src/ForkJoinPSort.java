import java.util.Arrays;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinPSort extends RecursiveTask<Integer>{

    static int[] arr;
    static int beg;
    static int en;
    static boolean inc;

    public ForkJoinPSort(int[] A, int beginning, int end, boolean increasing) {
        arr = A;
        beg = beginning;
        en = end;
        inc = increasing;
    }

    @Override
    protected Integer compute() {
        if (beg >= en) {
            return null;
        }

        int pivotInd = partition(arr, beg, en);

        ForkJoinPSort left = new ForkJoinPSort(arr, beg, pivotInd-1, inc);
        ForkJoinPSort right = new ForkJoinPSort(arr, pivotInd+1, en, inc);

        left.fork();
        right.compute();

        left.join();

        return null;
    }

    private void sequentialSort(int[] A, int begin, int end) {
        for(int i = begin+1; i <= end; i++) {
            int val = A[i];
            int j = i-1;
            while(j>=0 && A[j] > val) {
                A[j+1] = A[j];
                j -= 1;
            }
            A[j+1] = val;
        }
    }

    private void swap(int[] A, int i, int j) {
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }

    private int partition(int[] A, int begin, int end)  {
        if(end-begin+1 <= 16) { // size <= 16 so perform sequential sort on this array
            sequentialSort(A, begin, end);
            return end;
        } else {
            int i = begin, j = end - 1;
            // pick last element as pivot
            int pivot = A[end];

            while (i <= j) {
                if (A[i] <= A[end]) {
                    i++;
                    continue;
                }
                if (A[j] >= A[end]) {
                    j--;
                    continue;
                }
                swap(A, i, j);
                j--;
                i++;
            }
            swap(A, j + 1, end);
            return j+1;
        }
    }

    public static void parallelSort(int[] A, int begin, int end, boolean increasing) {
        // your implementation goes here.
        arr = A;
        beg = begin;
        en = end;
        inc = increasing;
    }

    public static void main (String[] args) {
        int[] arr_5 = { 5, 3, 4, 1, 2};
        int[] arr_32 = {10, 30, 20, 50, 70, 40, 100};
        int[] arr = {32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

        System.out.println("arr size: " + arr.length);
        for (int j : arr) {
            System.out.print(j+ " ");
        }

        ForkJoinPool pool = ForkJoinPool.commonPool();

        pool.invoke(new ForkJoinPSort(arr, 0, arr.length-1, true));

        System.out.println();
        for (int j : arr) {
            System.out.print(j + " ");
        }
    }
}
