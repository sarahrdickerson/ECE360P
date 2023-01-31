//UT-EID = tco343 & srd2729

import java.util.*;
import java.util.concurrent.*;

public class RunnablePSort {
    private static class QuickSort implements Runnable{
        int[] arr;
        int begin;
        int end;
        boolean increasing;

        QuickSort(int[] arr, int begin, int end, boolean increasing){
            this.arr = arr;
            this.begin = begin;
            this.end = end;
            this.increasing = increasing;
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

        private static void insertionSort(int[] arr, int begin, int end, boolean increasing) {
            if (increasing){
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
            else{
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

        @Override
        public void run() {
            if((end - begin + 1) <= 16){
                insertionSort(arr, begin, end, increasing);
            }
            else{
                int pivot = arr[(begin + end) / 2];
                int index = partition(pivot);
                QuickSort left = new QuickSort(arr, begin, index - 1, this.increasing);
                QuickSort right = new QuickSort(arr, index, end, this.increasing);
                Thread t2 = new Thread(right);
                Thread t1 = new Thread(left);
                t1.start();
                t2.start();
                try {
                    t1.join();
                    t2.join();
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException caught.\n");
                }
            }
        }
    }

    /* Notes:
     * The input array (A) is also the output array,
     * The range to be sorted extends from index begin, inclusive, to index end, exclusive,
     * Sort in increasing order when increasing=true, and decreasing order when increasing=false,
     */

    public static void parallelSort(int[] A, int begin, int end, boolean increasing) {
        // TODO: Implement your parallel sort function using Runnables
        QuickSort mainSort = new QuickSort(A, begin, end - 1, increasing);
        Thread mainThread = new Thread(mainSort);
        mainThread.start();
        try{
            mainThread.join();
        } catch(InterruptedException e){
            System.out.println("InterruptedException caught.\n");
        }
    }
}