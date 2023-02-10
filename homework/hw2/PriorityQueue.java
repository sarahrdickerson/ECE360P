// tco343
// srd2729

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class PriorityQueue {
        int maxSize;
        int size;
        Node head;

        public class Node {
                String name;
                int priority;

                Node next;
                ReentrantLock lock; // put lock in Node class to implement hand-over-hand locking instead of using global lock
                Condition notFull;
                Condition notEmpty;

                public Node(String name, int priority) {
                        this.name = name;
                        this.priority = priority;
                        next = null;
                        lock = new ReentrantLock();
                        notFull = lock.newCondition();
                        notEmpty = lock.newCondition();
                }
        }

	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
                this.maxSize = maxSize;
                head = null;    // TODO: Do we want to initialize head to null?
                size = 0;
	}

        public void addDebug(String name, int priority, int position) {
                System.out.println("----Adding " + name + " with priority " + priority + " at position " + position);
                System.out.println("    Current size: " + size);
                System.out.print("    After add: [");
                for (Node curNode = head; curNode != null; curNode = curNode.next) {
                        System.out.print(curNode.name + ", ");
                }
                System.out.println("]");
        }
        public void lockDebug(String name) {
                System.out.println("Locking " + name);
        }
        public void unlockDebug(String name) {
                System.out.println("Unlocking " + name);
        }

	public int add(String name, int priority) {
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
                Node curNode = head;
                int position = 0;
                Node newNode = new Node(name, priority);
                Node prevNode = null;

                try {
                        if (size == 0) {
                                head = newNode;
                                size++;
                                addDebug(name, priority, position);
                                return position;
                        } else if (size == maxSize) {
                                curNode.lock.lock();
                                while (size == maxSize) {
                                        curNode.notFull.await();  
                                }
                        }
                        else {
                                // lockDebug(curNode.name);
                                curNode.lock.lock();
                        }
                        // loop to find position to place
                        while (curNode != null) {
                                // place after curNode
                                if (curNode.priority <= priority) {
                                        if (curNode.next != null) {
                                                curNode.next.lock.lock();
                                                // lockDebug(curNode.next.name);
                                                curNode.lock.unlock();
                                                // unlockDebug(curNode.name);
                                                prevNode = curNode;
                                                curNode = curNode.next;
                                                position++;
                                        }
                                        else if (!curNode.name.equals(name)) {
                                                curNode.next = newNode;
                                                size++;
                                                position++;
                                                addDebug(name, priority, position);
                                                break;
                                        }
                                        else {
                                                return -1;
                                        }
                                        
                                } else if (curNode.priority > priority) {
                                        // place before curNode

                                        prevNode.lock.lock();
                                        prevNode.next = newNode;
                                        prevNode.lock.unlock();

                                        newNode.next = curNode;
                                        size++;
                                        addDebug(name, priority, position);
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
                System.out.println("----Searching for " + name);
                Node curNode = head;
                int position = 0;
                if (curNode != null)
                        curNode.lock.lock();
                while (curNode != null) {
                        // curNode.lock.lock();
                        try {
                                if (curNode.name.equals(name)) {
                                        System.out.println("    Found " + name + " at position " + position);
                                        curNode.lock.unlock();
                                        return position;
                                }
                                if (curNode.next == null) {
                                        System.out.println("    Could not find " + name);
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
                System.out.println("    Could not find " + name);
                return -1;
	}

	public String getFirst() {
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
                if (size == 0) {
                        while (size == 0) {}
                }
                Node curNode = head;
                
                try {
                        curNode.lock.lock();
                        if (curNode.next != null) {
                                curNode.next.lock.lock();
                        }
                        head = curNode.next;
                        if (curNode.next != null) {
                                curNode.next.lock.unlock();
                        }
                        curNode.notFull.signalAll();
                        curNode.lock.unlock();
                        return curNode.name;
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
	}

}