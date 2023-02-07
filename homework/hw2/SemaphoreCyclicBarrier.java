// tco343
// srd2729

import java.util.concurrent.Semaphore;
import java.lang.Thread;

/* Use only semaphores to accomplish the required synchronization */
public class SemaphoreCyclicBarrier implements CyclicBarrier {

    private int parties;
    private Semaphore mutex;
    private int numArrived;
    private boolean isActive;
    private Semaphore lock;
    private int numLeft;
    private Semaphore leftLock;


    public SemaphoreCyclicBarrier(int parties) {
        this.parties = parties;
        mutex = new Semaphore(1);
        lock = new Semaphore(1);
        leftLock = new Semaphore(1);
        numArrived = 0;
        numLeft = 0;
    }

    /*
     * An active CyclicBarrier waits until all parties have invoked
     * await on this CyclicBarrier. If the current thread is not
     * the last to arrive then it is disabled for thread scheduling
     * purposes and lies dormant until the last thread arrives.
     * An inactive CyclicBarrier does not block the calling thread. It
     * instead allows the thread to proceed by immediately returning.
     * Returns: the arrival index of the current thread, where index 0
     * indicates the first to arrive and (parties-1) indicates
     * the last to arrive.
     */
    public int await() throws InterruptedException {
        // TODO Implement this function
        mutex.acquire();
        if(numArrived == 0) {
            lock.acquire();
        }
        int res = numArrived;
        numArrived = (numArrived + 1) % parties;
        mutex.release();
        while(numArrived != 0){
        }
        if(res == 0) {
            System.out.println("0 entered");
            while (numLeft != parties - 1) {
                System.out.println("Wait " + numLeft);
            }
        }
        System.out.println("About to acquire the lock " + res);
        leftLock.acquire();
        numLeft = (numLeft + 1) % parties;
        System.out.println("Res: " + res + " NumLeft: " + numLeft + " Parties - 1: " + (parties - 1));
        leftLock.release();
        System.out.println("Res: " + res + " released the lock.");
        if(res == 0) {
            numLeft = 0;
            lock.release();
        }
        return res;
    }

    /*
     * This method activates the cyclic barrier. If it is already in
     * the active state, no change is made.
     * If the barrier is in the inactive state, it is activated and
     * the state of the barrier is reset to its initial value.
     */
    public void activate() throws InterruptedException {
        // TODO Implement this function
        isActive = true;
        numArrived = 0;
    }

    /*
     * This method deactivates the cyclic barrier.
     * It also releases any waiting threads
     */
    public void deactivate() throws InterruptedException {
        // TODO Implement this function
        isActive = false;
    }
}