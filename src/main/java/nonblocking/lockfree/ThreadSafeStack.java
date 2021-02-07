package nonblocking.lockfree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class ThreadSafeStack {

    public static void main(String[] args) throws InterruptedException {
        /**
         * Compare performance of those two classes
         * Counting the number of operation each one of them can execute
         *
         * StandardStack - 197,620,748 operations were performed in 10 seconds
         * LockFreeStack - 507,435,663 operations were performed in 10 seconds
         * As we see we had 3 almost three times more operations with LockFreeStack
         */
        StandardStack<Integer> stack = new StandardStack<>();
//        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random random = new Random();

        for (int i = 0; i < 100000; i++) {
            stack.push(random.nextInt());
        }

        List<Thread> threads = new ArrayList<>();

        int pushingThreads = 2;
        int poppingThreads = 2;

        for (int i = 0; i < pushingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });

            thread.setDaemon(true);
            threads.add(thread);
        }

        for (int i = 0; i < poppingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });

            thread.setDaemon(true);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(10000);

        System.out.println(String.format("%,d operations were performed in 10 seconds ", stack.getCounter()));
    }

    public static class LockFreeStack<T> {
        private AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private AtomicInteger counter = new AtomicInteger(0);

        public void push(T value) {
            StackNode<T> newHeadNode = new StackNode<>(value);

            /**
             * This loop will repeatedly try to make this new head candidate
             */
            while(true) {
                StackNode<T> currentHeadNode = head.get();
                newHeadNode.next = currentHeadNode;
                /**
                 * if this validation succeeds, it means that the current value was not modified by any other thread
                 * between reading the read and writing to it
                 */
                if(this.head.compareAndSet(currentHeadNode, newHeadNode)) {
                    break;
                } else {
                    // If we are here, it means that the head was changed by an other thread
                    // Wait before try again with the loop
                    LockSupport.parkNanos(1);
                }
            }
            this.counter.incrementAndGet();
        }

        public T pop() {
            StackNode<T> currentHeadNode = this.head.get();
            StackNode<T> newHeadNode;

            while(currentHeadNode != null) {
                newHeadNode = currentHeadNode.next;
                if(this.head.compareAndSet(currentHeadNode, newHeadNode)) {
                    break;
                } else {
                    // If we are here, it means that the head was changed by an other thread
                    LockSupport.parkNanos(1);
                    currentHeadNode = this.head.get();
                }
            }
            this.counter.incrementAndGet();
            return currentHeadNode != null ? currentHeadNode.value : null;
        }

        public int getCounter() {
            return this.counter.get();
        }
    }

    public static class StandardStack<T> {
        private StackNode<T> head;
        private int counter = 0;

        public synchronized  void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            newHead.next = head;
            this.head = newHead;
            this.counter ++;
        }

        public synchronized T pop() {
            if(this.head == null) {
                counter ++;
                return null;
            }

            T value = this.head.value;
            this.head = head.next;
            this.counter ++;
            return value;
        }

        public int getCounter() {
            return this.counter;
        }
    }

    private static class StackNode<T> {
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
            this.next = next;
        }
    }
}
