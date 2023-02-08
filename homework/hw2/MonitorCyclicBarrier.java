// tco343
// srd2729

// tco343
// srd2729

/* Use only Java monitors to accomplish the required synchronization */
public class MonitorCyclicBarrier implements CyclicBarrier {

    private int parties;
    private int count;
    private int numLeft;
    private boolean isActive;

    public MonitorCyclicBarrier(int parties) {
        this.parties = parties;
        this.count = 0;
        this.numLeft = 0;
        this.isActive = true;
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
        // Check if the previous threads have finished
        checkLeft();
        // Update the counter and get the arrival index
        int res = getCount();
        // Wait until rest of the barrier calls await
        if(isActive) {
            checkRest();
            updateLeft();
        }
        //System.out.println("Leaving");
        return res;
    }

    private synchronized void checkRest() throws InterruptedException {
        while(count != 0){
            wait();
        }
        notifyAll();
        //System.out.println("Wait is over");
    }

    private synchronized void checkLeft() throws InterruptedException {
        //System.out.println("Want to enter the barrier");
        while(numLeft != 0)
            wait();
        //System.out.println("Entered the barrier");
    }
    private synchronized int getCount(){
        int res = count;
        count = (count + 1) % parties;
        //System.out.println("Changed count, count is now " + count);
        return res;
    }
    private synchronized void updateLeft(){
        numLeft = (numLeft + 1) % parties;
        //System.out.println("Changed numLeft, it is now " + numLeft);
        if(numLeft == 0)
            notifyAll();
    }


    /*
     * This method activates the cyclic barrier. If it is already in
     * the active state, no change is made.
     * If the barrier is in the inactive state, it is activated and
     * the state of the barrier is reset to its initial value.
     */
    public void activate() throws InterruptedException {
        if(isActive)
            return;
        isActive = true;
        count = 0;
        numLeft = 0;
    }

    /*
     * This method deactivates the cyclic barrier.
     * It also releases any waiting threads
     */
    public void deactivate() throws InterruptedException {
        isActive = false;
        count = 0;
    }
}