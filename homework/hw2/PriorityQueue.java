// tco343
// srd2729

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class PriorityQueue {
        private LinkedList<Node> pQueue;
        int maxSize;
        int size;
        Node head;

        private class Node {
                String name;
                int priority;

                Node next;
                ReentrantLock lock; // put lock in Node class to implement hand-over-hand locking instead of using global lock
                Condition notFull;
                Condition notEmpty;

                public Node(String name, int priority) {
                        this.name = name;
                        this.priority = priority;
                        Node next = null;
                        lock = new ReentrantLock();
                        notFull = lock.newCondition();
                        notEmpty = lock.newCondition();
                }
        }

	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
                pQueue = new LinkedList<Node>();
                this.maxSize = maxSize;
                head = null;    // TODO: Do we want to initialize head to null?
                size = 0;
	}

	public int add(String name, int priority) {
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
                Node curNode = head;
                int position = 0;
                curNode.lock.lock();

                try {
                        // if list is empty
                        if (head == null) {
                                head = new Node(name, priority);
                                size++;
                                return position;
                        }
                        else { // else list is not empty
                                Node next = curNode.next;
                                // loop to find position to add Node
                                while (next != null) { 
                                        next.lock.lock();
                                        try {
                                                // 
                                                if (next.priority < priority || (next.priority == priority && next.name.compareTo(name) < 0)) {
                                                        curNode = next;
                                                        next = next.next;
                                                        position++;
                                                }
                                                else if (next.name.equals(name)) {
                                                        return -1;
                                                }
                                                else {
                                                        break;
                                                }

                                        } catch (Exception e) {
                                                e.printStackTrace();
                                        } finally {
                                                curNode.lock.unlock();
                                        }
                                }

                                // found position to add, try to add
                                if (size < maxSize) {
                                        Node newNode = new Node(name, priority);
                                        newNode.next = next;
                                        curNode.next = newNode;
                                        size++;
                                        return position;
                                }
                                else { // list is full
                                        // wait until list is not full
                                        while (size == maxSize) {
                                                curNode.notFull.await();
                                        }
                                        // list is not full, try to add again
                                        Node newNode = new Node(name, priority);
                                        newNode.next = next;
                                        curNode.next = newNode;
                                        size++;
                                        return position;
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        curNode.lock.unlock();
                        // next unlock?
                }
                return 0;
	}

	public int search(String name) {
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
                return 0;
	}

	public String getFirst() {
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
                return "";
	}

}