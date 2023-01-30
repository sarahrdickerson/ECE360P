import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PMerge{
    // holds indices of where each subarray starts based on numThreads
    static int[] aSubArrIndices, bSubArrIndices;
    static int numThreads;

    private static class Merge extends Thread {
        int[] A, B, C;
        int aStart, aEnd, bStart, bEnd;

        public Merge(int[] A, int[] B, int[] C, int aStart, int aEnd, int bStart, int bEnd) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.aStart = aStart;
            this.aEnd = aEnd;
            this.bStart = bStart;
            this.bEnd = bEnd;
        }

        public void run() {
            for(int i = aEnd-1; i >= aStart; i--) {
                int rank = rank(A[i], B);
//                if(C[C.length-1-(rank+i)] == A[i]) {
//                    rank--;
//                }
                C[C.length-1-(rank+i)] = A[i];
//                System.out.println("From A[" + i + "] = " + A[i] + ": C[rank=" + rank + " + i=" + i + ", " + (C.length-1-(rank+i)) + "] = " + C[C.length-1-(rank+i)]);
            }

            for(int i = bEnd-1; i >= bStart; i--) {
                int rank = rank(B[i], A);
//                if(C[C.length-1-(rank+i)] == B[i]) {
//                    rank--;
//                }
                if(rank > 0 && A[rank-1] == B[i]) {
//                    System.out.println("A[" + (rank-1) + "] = " + A[rank-1] + " B[" + i + "] = " + B[i]);
                    rank--;
                }
                C[C.length-1-(rank+i)] = B[i];
//                System.out.println("From B[" + i + "] = " + B[i] + ": C[rank=" + rank + " + i=" + i + ", " + (C.length-1-(rank+i)) + "] = " + C[C.length-1-(rank+i)]);
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

    private static void calculateSubArrIndicies(int[] arrIndicies, int arrSize, int numThreads) {
        int bucketSize = arrSize/numThreads;
        for(int i = 0, j=0; i < arrSize && j < arrIndicies.length-1; i++) {
            if(i%(bucketSize) == 0) {
                arrIndicies[j] = i;
                j++;
            }
        }
        arrIndicies[arrIndicies.length-1] = arrSize;
    }

    public static void parallelMerge(int[] A, int[] B, int[] C, int numThreads) {
        // arrays A and B are sorted in the ascending order
        // These arrays may have different sizes.
        // array C is the merged array sorted in the descending order
        // your implementation goes here.

        // Create subarray dividers based on numThreads needed
        PMerge.numThreads = numThreads;
        aSubArrIndices = new int[numThreads+1];
        bSubArrIndices = new int[numThreads+1];

        calculateSubArrIndicies(aSubArrIndices, A.length, numThreads);
        calculateSubArrIndicies(bSubArrIndices, B.length, numThreads);

        try {
            ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
            for(int i = 0; i < numThreads; i++) {
                System.out.println("thread " + i + " with A[" + aSubArrIndices[i] + ", " + aSubArrIndices[i+1] + ") and B[" + bSubArrIndices[i] + ", " + bSubArrIndices[i+1] + ")");
                threadPool.submit(new Merge(A, B, C, aSubArrIndices[i], aSubArrIndices[i+1], bSubArrIndices[i], bSubArrIndices[i+1]));
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
//        int[] A = {1, 2, 3, 4, 5};
//        int[] B = {6, 7, 8, 9, 10};
//        int[] A = {1, 5, 7, 8, 9, 11, 23, 24, 25, 26};
//        int[] B = {1, 2, 3, 4, 5, 7, 8, 9, 10, 15, 16, 30, 31, 33, 80};
        int[] C = new int[A.length + B.length];
        int numThreads = 5;

        printArr(A);
        printArr(B);
//        printArr(C);

        parallelMerge(A, B, C, numThreads);

        System.out.println("After parallel merge");
        printArr(C);
    }
}
