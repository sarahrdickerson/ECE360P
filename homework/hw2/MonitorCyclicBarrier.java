// tco343
// srd2729

//* Use only Java monitors to accomplish the required synchronization */
public class MonitorCyclicBarrier implements CyclicBarrier {

    final private int parties;
    private int count;
    private boolean isActivated;

    public MonitorCyclicBarrier(int parties) {
        this.parties = parties;
        this.count = 0;
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
    public synchronized int await() throws InterruptedException {
        int res = count;
        count = (count + 1) % parties;
        if(isActivated) {
            if (count > 0)
                this.wait();
            else {
                notifyAll();
            }
        }
        else{
            notifyAll();
        }
        return res;
    }


    /*
     * This method activates the cyclic barrier. If it is already in
     * the active state, no change is made.
     * If the barrier is in the inactive state, it is activated and
     * the state of the barrier is reset to its initial value.
     */
    public void activate() {
        if(isActivated)
            return;
        isActivated = true;
        count = 0;
    }

    /*
     * This method deactivates the cyclic barrier.
     * It also releases any waiting threads
     */
    public void deactivate() {
        isActivated = false;

    }
}