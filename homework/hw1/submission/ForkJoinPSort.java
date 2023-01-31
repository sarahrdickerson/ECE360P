//UT-EID = tco343 & srd2729

import java.awt.geom.QuadCurve2D;
import java.util.*;
import java.util.concurrent.*;

public class ForkJoinPSort {
    /* Notes:
     * The input array (A) is also the output array,
     * The range to be sorted extends from index begin, inclusive, to index end, exclusive,
     * Sort in increasing order when increasing=true, and decreasing order when increasing=false,
     */

    private static class QuickSort extends RecursiveAction{
        int[] arr;
        int begin;
        int end;
        boolean increasing;

        QuickSort(int[] arr, int begin, int end, boolean increasing) {
            this.arr = arr;
            this.begin = begin;
            this.end = end;
            this.increasing = increasing;
        }

        private static void insertionSort(int[] arr, int begin, int end, boolean increasing) {
            if (increasing) {
                for (int i = begin + 1; i <= end; i++) {
                    int current = arr[i];
                    int j = i - 1;
                    while (j >= begin && arr[j] > current) {
                        arr[j + 1] = arr[j];
                        j = j - 1;
                    }
                    arr[j + 1] = current;
                }
            }
            else {
                for (int i = begin + 1; i <= end; i++) {
                    int current = arr[i];
                    int j = i - 1;
                    while (j >= begin && arr[j] < current) {
                        arr[j + 1] = arr[j];
                        j = j - 1;
                    }
                    arr[j + 1] = current;
                }
            }
        }
        private int partition(int pivot){
            int left = begin;
            int right = end;
            while(left <= right){
                if(increasing) {
                    while (arr[left] < pivot)
                        left++;
                    while (arr[right] > pivot)
                        right--;
                }
                else{
                    while (arr[left] > pivot)
                        left++;
                    while (arr[right] < pivot)
                        right--;
                }
                if(left <= right){
                    int temp = arr[left];
                    arr[left] = arr[right];
                    arr[right] = temp;
                    left++;
                    right--;
                }
            }
            return left;
        }

        @Override
        protected void compute() {
            if((end - begin + 1) <= 16){
                insertionSort(arr, begin, end, increasing);
            }
            else {
                int pivot = arr[(begin + end) / 2];
                int index = partition(pivot);
                QuickSort left = new QuickSort(arr, begin, index - 1, this.increasing);
                QuickSort right = new QuickSort(arr, index, end, this.increasing);
                // Might change!!!
                left.fork();
                right.compute();
                left.join();
                /*
                left.compute();
                right.compute();
                 */
            }
        }
    }

    public static void parallelSort(int[] A, int begin, int end, boolean increasing) {
        // TODO: Implement your parallel sort function using ForkJoinPool
        int processors = Runtime.getRuntime().availableProcessors();
        QuickSort qs = new QuickSort(A, begin, end - 1, increasing);
        ForkJoinPool pool = new ForkJoinPool(processors);
        pool.invoke(qs);
    }
}