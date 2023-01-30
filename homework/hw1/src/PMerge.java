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
        int[] C = new int[10];
        int numThreads = 2;

        printArr(A);
        printArr(B);
        printArr(C);

        parallelMerge(A, B, C, numThreads);

        System.out.println("After parallel merge");
        printArr(C);
    }
}
