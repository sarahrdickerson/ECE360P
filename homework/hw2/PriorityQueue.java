// tco343
// srd2729

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class PriorityQueue {
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
                System.out.println("Adding " + name + " with priority " + priority + " at position " + position);
                System.out.println("Current size: " + size);
                System.out.print("After add: [");
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
                                return position;
                        } else {
                                lockDebug(curNode.name);
                                curNode.lock.lock();
                        }
                        // loop to find position to place
                        while (curNode != null) {
                                // curNode.lock.lock();
                                // place after curNode
                                if (curNode.priority <= priority) {
                                        if (curNode.next != null) {
                                                curNode.next.lock.lock();
                                                lockDebug(curNode.next.name);
                                                curNode.lock.unlock();
                                                unlockDebug(curNode.name);
                                                prevNode = curNode;
                                                curNode = curNode.next;
                                                position++;
                                        }
                                        else {
                                                curNode.next = newNode;
                                                size++;
                                                position++;
                                                addDebug(name, priority, position);
                                                break;
                                        }
                                        
                                } else if (curNode.priority > priority) {
                                        // place before curNode

                                        prevNode.lock.lock();
                                        prevNode.next = newNode;
                                        prevNode.lock.unlock();

                                        newNode.next = curNode;
                                        size++;
                                        addDebug(name, priority, position);
                                        // position--;
                                        // curNode.lock.unlock(); 
                                        break;
                                } else if (curNode.name.equals(name)) {
                                        // curNode.lock.unlock();
                                        return -1;
                                }
                                else { // place at end?
                                        curNode.next = newNode;
                                        size++;
                                        position++;
                                        break;
                                }
                                // curNode.lock.unlock();
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
                // try {
                        
                //         // if list is empty
                //         if (head == null) {
                //                 head = newNode;
                //                 size++;

                //                 addDebug(name, priority);

                //                 return position;
                //         }
                //         else if (size == maxSize) {
                //                 while (size > maxSize) {
                //                         curNode.notFull.await();
                //                 }
                //         }
                //         else { // else list is not empty        // TODO need to change this to compare current node not curNode.next
                //                 position = 1;
                //                 curNode.lock.lock();
                //                 lockDebug(curNode.name);
                //                 // Node next = curNode.next;
                //                 // loop to find position to add Node
                //                 while (curNode.next != null) { 
                //                         try {
                //                                 curNode.next.lock.lock();
                //                                 lockDebug(curNode.next.name);
                //                                 // 
                //                                 if (curNode.next.priority < priority || (curNode.next.priority == priority && curNode.next.name.compareTo(name) <= 0)) {
                //                                         curNode.lock.unlock();
                //                                         unlockDebug(curNode.name);

                //                                         curNode = curNode.next;
                //                                         // Node prevNode = curNode;
                //                                         // curNode = next;
                //                                         // prevNode.lock.unlock();
                //                                         // next = next.next;
                //                                         position++;
                //                                 }
                //                                 else if (curNode.next.name.compareTo(name) > 0) {
                //                                         curNode.lock.unlock();
                //                                         unlockDebug(curNode.name);

                //                                         curNode.next = null;
                //                                         newNode.next = curNode;
                //                                         // Node prevNode = curNode;
                //                                         // curNode = next;
                //                                         // prevNode.lock.unlock();
                //                                         // next = next.next;
                //                                         position--;
                //                                 }
                //                                 else if (curNode.next.name.equals(name)) {
                //                                         curNode.next.lock.unlock();
                //                                         unlockDebug(curNode.next.name);
                //                                         return -1;
                //                                 }
                //                                 else {
                //                                         break;
                //                                 }

                //                         } catch (Exception e) {
                //                                 e.printStackTrace();
                //                         }
                //                         // } finally {
                //                         //         curNode.lock.unlock();
                //                         // }
                //                 }
                //                 // check against last node
                //                 if (curNode.priority > priority || (curNode.priority == priority && curNode.name.compareTo(name) < 0)) {
                //                         curNode.lock.unlock();
                //                         unlockDebug(curNode.name);

                //                         curNode.next = null;
                //                         newNode.next = curNode;
                //                         // Node prevNode = curNode;
                //                         // curNode = next;
                //                         // prevNode.lock.unlock();
                //                         // next = next.next;
                //                         position--;
                //                 }
                //                 else {
                //                         position++;
                //                 }

                //                 // found position to add, try to add
                //                 if (size < maxSize) {
                                        
                //                         curNode.next = newNode;
                //                         newNode.lock.lock();
                //                         lockDebug(newNode.name);

                                        
                                
                //                         // newNode.next = null;
                //                         // curNode.next = newNode;

                //                         // Node prevNode = curNode;
                //                         // curNode = newNode;
                //                         // // newNode = null;
                //                         // prevNode.lock.unlock();

                //                         size++;
                //                         // position++;
                //                         addDebug(name, priority);

                //                         curNode.lock.unlock();
                //                         unlockDebug(curNode.name);
                //                         curNode = newNode;

                //                         return position;
                //                 }
                //                 // else { // list is full
                //                 //         // wait until list is not full
                //                 //         while (size == maxSize) {
                //                 //                 curNode.notFull.await();
                //                 //         }
                //                 //         // list is not full, try to add again
                //                 //         Node newNode = new Node(name, priority);
                //                 //         newNode.next = curNode.next;
                //                 //         curNode.next = newNode;
                //                 //         size++;
                //                 //         return position;
                //                 // }
                //         }
                // } catch (Exception e) {
                //         e.printStackTrace();
                // } finally {
                //         if(curNode != null)
                //         {
                //                 curNode.lock.unlock();
                //                 unlockDebug(curNode.name);
                //         }
                //         // next unlock?
                // }
                // return 0;
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