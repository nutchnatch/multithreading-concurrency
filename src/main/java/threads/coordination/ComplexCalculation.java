package threads.coordination;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class ComplexCalculation{

    public static void main(String[] args) {
        ComplexCalculation complexCalculation = new ComplexCalculation();
        BigInteger result = complexCalculation.calculateResult(new BigInteger("2"), new BigInteger("2"), new BigInteger("2"), new BigInteger("2"));
        System.out.println("The result is: " + result);
    }

    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) {
        BigInteger result = BigInteger.ZERO;
        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */
        PowerCalculatingThread power1Thread = new PowerCalculatingThread(base1, power1);
        PowerCalculatingThread power2Thread = new PowerCalculatingThread(base2, power2);
        power1Thread.setDaemon(true);
        power1Thread.setName("Thread-Power1");
        power2Thread.setDaemon(true);
        power2Thread.setName("Thread-Power2");
        List<PowerCalculatingThread> threads = Arrays.asList(power1Thread, power2Thread);

        for(Thread thread : threads) {
            try {
                thread.start();
                thread.join(200);
            } catch (InterruptedException e) {
                System.out.println("Thread " + thread.getName() + " has been interrupted.");
            }
        }

        for(int i = 0; i < 2; i ++) {
            PowerCalculatingThread thread = threads.get(i);
            if(thread.isFinished) {
                result = result.add(thread.getResult());
            } else {
                System.out.println("Thread " + thread.getName() + " could not finished yet.");
            }
        }
        return result;
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private BigInteger base;
        private BigInteger power;
        private boolean isFinished = false;

        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
           /*
           Implement the calculation of result = base ^ power
           */
           result = pow();
           isFinished = true;
        }

        private BigInteger pow() {
            BigInteger result = BigInteger.ONE;
            for(BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Thread Interrupted...");
                }
                result = result.multiply(base);
            }
            return result;
        }

        public BigInteger getResult() { return result; }

        public boolean isFinished() {
            return isFinished;
        }
    }
}