package nonblocking.lockfree;

public class SyncStack {

    public static class StandardStack<T> {
        private StackNode<T> head;
        private int counter = 0;

        public synchronized void push(T value) {
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
