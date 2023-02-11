// tco343
// srd2729

import javax.crypto.SealedObject;
import java.util.concurrent.Semaphore;
import java.lang.Thread;

/* Use only semaphores to accomplish the required synchronization */
public class SemaphoreCyclicBarrier implements CyclicBarrier {

    final private int parties;
    private int count;
    private Semaphore mutex;
    private Semaphore[] newBarrier;
    private boolean isActivated;
    private int turn;

    public SemaphoreCyclicBarrier(int parties) {
        this.parties = parties;
        this.count = 0;
        this.mutex = new Semaphore(1);
        this.newBarrier = new Semaphore[2];
        this.newBarrier[0] = new Semaphore(0);
        this.newBarrier[1] = new Semaphore(0);
        this.turn = 0;
        this.isActivated = true;
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
        mutex.acquire();
        int res = count;
        count = (count + 1) % parties;
        mutex.release();
        if(isActivated) {
            if (count == 0) {
                if(turn == 0) {
                    turn = 1;
                    newBarrier[0].release(parties - 1);
                }
                else {
                    turn = 0;
                    newBarrier[1].release(parties - 1);
                }
            }
            else{
                if(turn == 0)
                    newBarrier[0].acquire();
                else
                    newBarrier[1].acquire();
            }
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
        if(isActivated)
            return;
        isActivated = true;
        newBarrier[0] = new Semaphore(0);
        newBarrier[1] = new Semaphore(0);
    }

    /*
     * This method deactivates the cyclic barrier.
     * It also releases any waiting threads
     */
    public void deactivate() throws InterruptedException {
        isActivated = false;
        newBarrier[0].release(parties - 1);
        newBarrier[1].release(parties - 1);
    }
}