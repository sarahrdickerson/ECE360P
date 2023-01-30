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
            int i = begin-1;
            // pick last element as pivot
            int pivot = A[end];

            for(int j = begin; j <= end-1; j++) {
                // if element is smaller than pivot swap with beginning of array
                if (A[j] < pivot) {
                    i++;
                    swap(A, i, j);
                }
            }
            // A[i] is last index of elements smaller than pivot
            swap(A, i+1, end);
            return i+1; // return pivot index
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
        int[] arr_slay = {10, 30, 20, 50, 70, 40, 100};
        int[] arr_notworking = {32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        int[] arr = {10, 6, 3, 5, 89, 2, 7, 21, 18, 29, 20, 17, 15, 14, 22, 38, 90, 98, 70, 66};

        System.out.println("arr size: " + arr.length);
        for (int j : arr) {
            System.out.print(j+ " ");
        }

        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("\nNumber of processors: " + processors);
        ForkJoinPSort fork = new ForkJoinPSort(arr, 0, arr.length-1, true);
        ForkJoinPool pool = new ForkJoinPool(processors);

        pool.invoke(fork);

        System.out.println();
        for (int j : arr) {
            System.out.print(j + " ");
        }
    }
}
