// tco343
// srd2729

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class PriorityQueue {
        int maxSize;
        int size;
        Node head;
        ReentrantLock qLock;
        Condition notFull;
        Condition notEmpty;

        public class Node {
                String name;
                int priority;

                Node next;
                ReentrantLock lock; // put lock in Node class to implement hand-over-hand locking instead of using global lock
                // Condition notFull;
                // Condition notEmpty;

                public Node(String name, int priority) {
                        this.name = name;
                        this.priority = priority;
                        next = null;
                        lock = new ReentrantLock();
                }
        }
            
	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
                this.maxSize = maxSize;
                head = null;   
                size = 0;
                qLock = new ReentrantLock();
                notFull = qLock.newCondition();
                notEmpty = qLock.newCondition();
	}

	public int add(String name, int priority) {
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
                qLock.lock();
                Node curNode = head;
                int position = 0;
                Node newNode = new Node(name, priority);
                Node prevNode = null;

                try {
                        while (size == maxSize) {
                                notFull.await();
                        }
                        if (size == 0) {
                                head = newNode;
                                size++;
                                prevNode = newNode;
                                notEmpty.signal();
                                qLock.unlock();
                                return position;
                        } 
                        else {
                                // lockDebug(curNode.name);
                                curNode.lock.lock(); 
                                qLock.unlock();
                        }
                        // loop to find position to place
                        while (curNode != null) {
                                // place after curNode
                                if (curNode.priority >= priority) {
                                        if (curNode.name.equals(name)) {
                                                return -1;
                                        }
                                        if (curNode.next != null) {
                                                curNode.next.lock.lock();
                                                curNode.lock.unlock();
                                                prevNode = curNode;
                                                curNode = curNode.next;
                                                position++;
                                        }
                                        else if (!curNode.name.equals(name)) {
                                                curNode.next = newNode;
                                                size++;
                                                position++;
                                                break;
                                        }
                                        
                                } else if (curNode.priority < priority) {
                                        // place before curNode

                                        if(prevNode != null) {
                                                prevNode.lock.lock();
                                                prevNode.next = newNode;
                                                newNode.next = curNode;
                                                size++;
                                                prevNode.lock.unlock();
                                        } else {
                                                qLock.lock();
                                                head = newNode;
                                                newNode.next = curNode;
                                                size++;
                                                qLock.unlock();
                                        }
                                        break;
                                } else if (curNode.name.equals(name)) {
                                        return -1;
                                }
                        }
                        return position;

                } catch (Exception e) {
                        e.printStackTrace();
                        return -1;
                } finally {
                        if(curNode != null) {
                                curNode.lock.unlock();
                        }
                }
	} 

	public int search(String name) {
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
                qLock.lock();
                Node curNode = head;
                int position = 0;
                if (curNode != null) {
                        curNode.lock.lock();
                        qLock.unlock();
                        while (curNode != null) {
                                try {
                                        if (curNode.name.equals(name)) {
                                                curNode.lock.unlock();
                                                return position;
                                        }
                                        if (curNode.next == null) {
                                                 curNode.lock.unlock();
                                                return -1;
                                        }
                                        curNode.next.lock.lock();
                                        curNode.lock.unlock();
                                        curNode = curNode.next;
                                        position++;
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                } else {
                        qLock.unlock();
                }
                return -1;
	}

	public String getFirst() {
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
                qLock.lock();
                
                try {
                        while (size == 0) {
                                notEmpty.await();
                        }
                        Node curNode = head;
                        if (curNode != null) {
                                curNode.lock.lock();

                                if (curNode.next != null) {
                                        curNode.next.lock.lock();
                                }
                                head = curNode.next;
                                if (curNode.next != null) {
                                        curNode.next.lock.unlock();
                                }
                                size--;
                                notFull.signal();
                                qLock.unlock();

                                curNode.lock.unlock();
                                return curNode.name;
                        } else {
                                qLock.unlock();
                        }
                        return null;
                } catch (Exception e) {
                        qLock.unlock();
                        e.printStackTrace();
                        return null;
                }
	}

}