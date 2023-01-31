//UT-EID = tco343 & srd2729

import java.util.*;
import java.util.concurrent.*;

public class PMerge{

    private static class Merge extends Thread {
        int[] A, B, C;

        public Merge(int[] A, int[] B, int[] C) {
            this.A = A;
            this.B = B;
            this.C = C;
        }

        public void run() {
            for(int i = A.length-1; i >= 0; i--) {
                int rank = rank(A[i], B);
                C[C.length-1-(rank+i)] = A[i];
            }

            for(int i = B.length-1; i >= 0; i--) {
                int rank = rank(B[i], A);
                if(rank > 0 && A[rank-1] == B[i]) {
                    rank--;
                }
                C[C.length-1-(rank+i)] = B[i];
            }
        }

        /*
         * Given some number a in arr, return the index of the largest element in arr smaller than a
         * @param   int     a       some number in arr
         *          int[]   arr     array to search through
         * @return  int     left    index of largest element in arr smaller than a OR 0 if no elements smaller
         */
        public static int rank(int a, int[] arr) {
            int left = 0;
            int right = arr.length;

            while(left < right) {
                int mid = left + (right-left)/2;
                if (a < arr[mid]) {
                    right = mid;
                } else {
                    left = mid+1;
                }
            }
            return left;
        }
    }

    /* Notes:
     * Arrays A and B are sorted in the ascending order
     * These arrays may have different sizes.
     * Array C is the merged array sorted in the descending order
     */
    public static void parallelMerge(int[] A, int[] B, int[] C, int numThreads) {
        // TODO: Implement your parallel merge function

        try {
            ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
            for(int i = 0; i < numThreads; i++) {
                threadPool.submit(new Merge(A, B, C));
            }
            threadPool.shutdown();
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
