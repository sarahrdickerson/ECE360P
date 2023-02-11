public class PriorityQueueTester {
    public static void main(String[] args) throws InterruptedException{
        // System.out.println("Thread1".compareTo("Thread2"));
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

        public void testAdd() throws InterruptedException {
            Thread.sleep((long) (Math.random() * 1000));
            queue.add("Thread1", 9);
            Thread.sleep((long) (Math.random() * 1000));
            queue.add("Thread1.2", 8);
            Thread.sleep((long) (Math.random() * 1000));
            queue.add("Thread1.2", 8);
            Thread.sleep((long) (Math.random() * 1000));
            queue.add("Thread1.6", 6);
        }

        public void testSearch() throws InterruptedException {
            Thread.sleep((long) (Math.random() * 1000));
            queue.search("Thread1");
            Thread.sleep((long) (Math.random() * 1000));
            queue.search("Thread2");
            Thread.sleep((long) (Math.random() * 1000));
            queue.search("Thread1.2");
            Thread.sleep((long) (Math.random() * 1000));
            queue.search("Thread1.6");
        }

        public void testGetFirst() throws InterruptedException {
            Thread.sleep((long) (Math.random() * 1000));
            System.out.println("----Getting first element: " + queue.getFirst());
            printQueue();
            Thread.sleep((long) (Math.random() * 1000));
            System.out.println("----Getting first element: " + queue.getFirst());
            printQueue();
        }

        public void printQueue() {
            System.out.print("Queue: [");
            for (PriorityQueue.Node curNode = queue.head; curNode != null; curNode = curNode.next) {
                System.out.print(curNode.name + ", ");
            }
            System.out.println("]");
        }

        public void testAll() throws InterruptedException {
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
        }

        public void testFull() throws InterruptedException {
            for(int i = 0; i < 15; i++) {
                queue.add("Thread1." + i, i%9);
                Thread.sleep((long) (Math.random() * 1000));
            }
        }

        public void testEmpty() throws InterruptedException {
            Thread.sleep((long) (8 * 1000));
            for(int i = 0; i < 15; i++) {
                queue.add("Thread1." + i, (int)(Math.random() * 9));
                Thread.sleep((long) (Math.random() * 1000));
            }
        }

        @Override
        public void run() {
            try {
                // testAdd();
                // testSearch();
                // testGetFirst();
                // testAll();
                // testFull();
                testEmpty();
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

        public void testAdd() throws InterruptedException {
            Thread.sleep((long) (Math.random() * 1000));
            queue.add("Thread2", 9);
            Thread.sleep((long) (Math.random() * 1000));
            queue.add("Thread2.2", 5);
            Thread.sleep((long) (Math.random() * 1000));
            queue.add("Thread2.3", 0);
            Thread.sleep((long) (Math.random() * 1000));
            queue.add("Thread2.4", 0);
        }

        public void testSearch() throws InterruptedException {
            Thread.sleep((long) (Math.random() * 1000));
            queue.search("Thread2");
            Thread.sleep((long) (Math.random() * 1000));
            queue.search("Thread1");
            Thread.sleep((long) (Math.random() * 1000));
            queue.search("Thread2.2");
            Thread.sleep((long) (Math.random() * 1000));
            queue.search("Thread2.4");
        }

        public void testAll() throws InterruptedException {
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
        }

        public void printQueue() {
            System.out.print("    Queue: [");
            for (PriorityQueue.Node curNode = queue.head; curNode != null; curNode = curNode.next) {
                System.out.print(curNode.name + ", ");
            }
            System.out.println("]");
        }

        public void testFull() throws InterruptedException {
            Thread.sleep((long) (10 * 1000));
            for (int i = 0; i < 10; i++) {
                System.out.println("----Getting first element, size now " + queue.size + ": " + queue.getFirst());
                // printQueue();
                Thread.sleep((long) (Math.random() * 1000));
            }
        }

        public void testEmpty() throws InterruptedException {
            for (int i = 0; i < 10; i++) {
                System.out.println("----Getting first element, size now " + queue.size + ": " + queue.getFirst());
                // printQueue();
                Thread.sleep((long) (Math.random() * 1000));
            }
        }

        @Override
        public void run() {
            try {
                // testFull();
                testEmpty();
                // testAdd();
                // testSearch();
                // testAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
