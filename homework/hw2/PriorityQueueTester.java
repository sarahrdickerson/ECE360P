public class PriorityQueueTester {
    public static void main(String[] args) throws InterruptedException{
        PriorityQueue queue = new PriorityQueue(5);
        int numThreads = 2;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            if (i%2 == 0) {
                threads[i] = new Thread(new Thread1(queue));
            }
            else {
                threads[i] = new Thread(new Thread2(queue));
            }
        }
        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }

    private static class Thread1 implements Runnable {
        private PriorityQueue queue;

        public Thread1(PriorityQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((long) (Math.random() * 1000));
                queue.add("Thread1", 0);
                Thread.sleep((long) (Math.random() * 1000));
                queue.search("Thread1");
                Thread.sleep((long) (Math.random() * 1000));
                queue.getFirst();
                Thread.sleep((long) (Math.random() * 1000));
                queue.add("Thread1.2", 2);
                Thread.sleep((long) (Math.random() * 1000));
                queue.add("Thread1.3", 6);
                Thread.sleep((long) (Math.random() * 1000));
                queue.getFirst();
                Thread.sleep((long) (Math.random() * 1000));
                queue.search("Thread2.1");
                Thread.sleep((long) (Math.random() * 1000));
                queue.add("Thread1.4", 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Thread2 implements Runnable {
        private PriorityQueue queue;

        public Thread2(PriorityQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((long) (Math.random() * 1000));
                queue.add("Thread2", 0);
                Thread.sleep((long) (Math.random() * 1000));
                queue.search("Thread1");
                Thread.sleep((long) (Math.random() * 1000));
                queue.getFirst();
                Thread.sleep((long) (Math.random() * 1000));
                queue.add("Thread2.2", 3);
                Thread.sleep((long) (Math.random() * 1000));
                queue.add("Thread2.3", 9);
                Thread.sleep((long) (Math.random() * 1000));
                queue.getFirst();
                Thread.sleep((long) (Math.random() * 1000));
                queue.search("Thread1.1");
                Thread.sleep((long) (Math.random() * 1000));
                queue.add("Thread2.4", 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
