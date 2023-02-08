// tco343
// srd2729

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FairUnifanBathroom {
    final int size = 7;
    ReentrantLock mutex = new ReentrantLock();

    private int count = 0;
    private int totalTicket = 0;
    private int nextTicketToEnter = 0;

    Condition isUT = mutex.newCondition();
    Condition isOU = mutex.newCondition();
    //0 for UT and 1 for OU
    private int turn = -1;

    public void enterBathroomUT() throws InterruptedException {
        // Called when a UT fan wants to enter bathroom
        mutex.lock();
        int myTicket = totalTicket;
        System.out.println("UT wants to enter with ticket " + myTicket);
        totalTicket++;
        while(count == size || turn == 1 || myTicket > nextTicketToEnter)
            isUT.await();
        // Entering
        System.out.println("UT entered");
        nextTicketToEnter = myTicket + 1;
        if(count == 0)
            turn = 0;
        count++;
        mutex.unlock();
    }

    public void enterBathroomOU() throws InterruptedException{
        // Called when a OU fan wants to enter bathroom
        mutex.lock();
        int myTicket = totalTicket;
        System.out.println("OU wants to enter with ticket " + myTicket);
        totalTicket++;
        while(count == size || turn == 0 || myTicket > nextTicketToEnter)
            isOU.await();
        // Entering
        System.out.println("OU entered");
        nextTicketToEnter = myTicket + 1;
        if(count == 0)
            turn = 1;
        count++;
        mutex.unlock();
    }

    public void leaveBathroomUT() {
        // Called when a UT fan wants to leave bathroom
        mutex.lock();
        count--;
        turn = -1;
        isUT.signalAll();
        if(count == 0)
            isOU.signalAll();
        System.out.println("UT leaving");
        mutex.unlock();
    }

    public void leaveBathroomOU() {
        // Called when a OU fan wants to leave bathroom
        mutex.lock();
        count--;
        turn = -1;
        isOU.signalAll();
        if(count == 0)
            isUT.signalAll();
        System.out.println("OU leaving");
        mutex.unlock();
    }
}