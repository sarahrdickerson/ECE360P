import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
//                System.out.println("From A[" + i + "] = " + A[i] + ": C[rank=" + rank + " + i=" + i + ", " + (rank+i) + "] = " + C[rank+i]);
            }

            for(int i = B.length-1; i >= 0; i--) {
                int rank = rank(B[i], A);
                if(C[C.length-1-(rank+i)] == B[i]) {
                    rank--;
                }
                C[C.length-1-(rank+i)] = B[i];
//                System.out.println("From B[" + i + "] = " + B[i] + ": C[rank=" + rank + " + i=" + i + ", " + (rank+i) + "] = " + C[rank+i]);
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

    public static void parallelMerge(int[] A, int[] B, int[] C, int numThreads) {
        // arrays A and B are sorted in the ascending order
        // These arrays may have different sizes.
        // array C is the merged array sorted in the descending order
        // your implementation goes here.
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

    public static void printArr(int[] arr) {
        System.out.print("array of len " + arr.length + ": ");
        for(int a : arr) {
            System.out.print(a + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] A = {1, 2, 3, 4, 5};
        int[] B = {4, 5, 6, 7, 8, 9, 10};
        int[] C = new int[A.length + B.length];
        int numThreads = 1;

        printArr(A);
        printArr(B);
//        printArr(C);

        parallelMerge(A, B, C, numThreads);

        System.out.println("After parallel merge");
        printArr(C);
    }
}
