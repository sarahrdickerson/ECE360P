// tco343
// srd2729

import java.util.LinkedList;
import java.util.concurrent.locks.*;;

public class PriorityQueue {
        private LinkedList<Node> pQueue;
        int maxSize;
        Node head;

        private class Node {
                String name;
                int priority;

                Node next;
                ReentrantLock lock; // put lock in Node class to implement hand-over-hand locking instead of using global lock

                public Node(String name, int priority) {
                        this.name = name;
                        this.priority = priority;
                        Node next = null;
                        lock = new ReentrantLock();
                }
        }

	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
                pQueue = new LinkedList<Node>();
                this.maxSize = maxSize;
                head = null;    // TODO: Do we want to initialize head to null?
	}

	public int add(String name, int priority) {
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
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