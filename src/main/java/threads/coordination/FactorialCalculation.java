package threads.coordination;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FactorialCalculation {

    public static void main(String[] args) throws InterruptedException {
        List<Long> inputNumbers = Arrays.asList(100000000L, 3435L, 35435L, 2324L, 4656L, 23L, 5556L);
        List<FactorialThread> threads = new ArrayList<>();

        for(Long inputNumber: inputNumbers) {
            threads.add(new FactorialThread(inputNumber));
        }

        /**
         * Race condition between the two for loops
         * Tey are concurrently trying to reach the thread
         */
        for(FactorialThread thread: threads) {
            thread.setDaemon(true);
            thread.start();
        }

        /**
         * To solve the race condition, lets add a join to resolve, by forcing the main thread to wait until all the
         * factorial threads are finished
         */
        for(Thread thread: threads) {
            /**
             * For every thread, join method will return only when that thread is terminated
             * When th main thread finished, it is guaranteed that all the factorial threads are terminated
             * If we use an input that takes too long (e.g 100000000L) the main thread will be waiting for so long
             * We should define how long we are willing to wait in the join method
             * In the examp,le bellow, I say that I will wait until 2 seconds for the thread to terminate, after it will return
             * But the application still waits for the long running thread to terminate
             * We can interrupt it on a given condition
             * Other solution would be to run all threads as daemon (setDaemon(true)), when the main thread terminates,
             * all threads are terminated
             */
//            thread.join();
            thread.join(2000);
        }

        for(int i = 0; i < threads.size(); i ++) {
            FactorialThread factorialThread = threads.get(i);
            if(factorialThread.isFinished()) {
                System.out.println("Factorial of " + inputNumbers.get(i) + " is " + factorialThread.getResult());
            } else {
                System.out.println("The calculation for " + inputNumbers.get(i) + " is still in progress");
            }
        }
    }

    public static class FactorialThread extends Thread {
        private long inputNumber;
        private BigInteger result;
        private boolean isFinished;

        public FactorialThread(long inputNumber) {
            this.result = BigInteger.ZERO;
            this.isFinished = false;
            this.inputNumber = inputNumber;
        }

        @Override
        public void run() {
            this.result = this.factorial(this.inputNumber);
            this.isFinished = true;
        }

        public BigInteger factorial(long n) {
            BigInteger tempResult = BigInteger.ONE;

            for(long i = n; i > 0L; --i) {
                tempResult = tempResult.multiply(new BigInteger(Long.toString(i)));
            }

            return tempResult;
        }

        public BigInteger getResult() {
            return this.result;
        }

        public boolean isFinished() {
            return this.isFinished;
        }
    }
}
