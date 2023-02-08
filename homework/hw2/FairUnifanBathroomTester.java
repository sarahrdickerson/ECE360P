public class FairUnifanBathroomTester {
    public static void main(String[] args) throws InterruptedException {
        FairUnifanBathroom bathroom = new FairUnifanBathroom();
        int numThreads = 20;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            if (i % 2 == 0) {
                threads[i] = new Thread(new UTThread(bathroom));
            } else {
                threads[i] = new Thread(new OUThread(bathroom));
            }
        }
        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }
    private static class UTThread implements Runnable {
        private FairUnifanBathroom bathroom;
        public UTThread(FairUnifanBathroom bathroom) {
            this.bathroom = bathroom;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((long) (Math.random() * 1000));
                bathroom.enterBathroomUT();
                Thread.sleep((long) (Math.random() * 1000));
                bathroom.leaveBathroomUT();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private static class OUThread implements Runnable {
        private FairUnifanBathroom bathroom;

        public OUThread(FairUnifanBathroom bathroom) {
            this.bathroom = bathroom;
        }
        @Override
        public void run() {
            try {
                Thread.sleep((long) (Math.random() * 1000));
                bathroom.enterBathroomOU();
                //sleep for random amount of under 1 second
                Thread.sleep((long) (Math.random() * 1000));
                bathroom.leaveBathroomOU();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
