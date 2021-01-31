package atomic.operations;

import java.util.Random;

public class StockMetrics {

    public static void main(String[] args) {
        MinMaxMetrics minMaxMetrics = new MinMaxMetrics();

        BusinessLogic businessLogic = new BusinessLogic(minMaxMetrics);
        GetMin getMin = new GetMin(minMaxMetrics);
        GetMax getMax = new GetMax(minMaxMetrics);

        businessLogic.start();
        getMin.start();
        getMax.start();
    }

    public static class GetMin extends Thread {
        private MinMaxMetrics minMaxMetrics;

        public GetMin(MinMaxMetrics minMaxMetrics) {
            this.minMaxMetrics = minMaxMetrics;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(100);
                } catch(InterruptedException e) {
                }
                System.out.println("Min metric value is: " + minMaxMetrics.getMin());
            }
        }
    }

    public static class GetMax extends Thread {
        private MinMaxMetrics minMaxMetrics;

        public GetMax(MinMaxMetrics minMaxMetrics) {
            this.minMaxMetrics = minMaxMetrics;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(100);
                } catch(InterruptedException e) {
                }
                System.out.println("Max metric value is: " + minMaxMetrics.getMax());
            }
        }
    }

    public static class BusinessLogic extends Thread {
        private MinMaxMetrics minMaxMetrics;
        private Random random = new Random();
        private long newSample;

        public BusinessLogic(MinMaxMetrics minMaxMetrics) {
            this.minMaxMetrics = minMaxMetrics;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(random.nextInt(10));
                } catch(InterruptedException e) {
                }

                newSample = random.nextLong();
                minMaxMetrics.addSample(newSample);
            }
        }
    }

    public static class MinMaxMetrics {
        // Add all necessary member variables
        private volatile long min;
        private volatile long max;

        /**
         * Initializes all member variables
         */
        public MinMaxMetrics() {
            // Add code here
            this.min = Long.MIN_VALUE;
            this.max = Long.MAX_VALUE;
        }

        /**
         * Adds a new sample to our metrics.
         */
        public  void addSample(long newSample) {
            // Add code here
            synchronized(this) {
                this.min = Math.min(newSample, this.min);
                this.max = Math.max(newSample, this.max);
            }
        }

        /**
         * Returns the smallest sample we've seen so far.
         */
        public long getMin() {
            // Add code here
            return this.min;
        }

        /**
         * Returns the biggest sample we've seen so far.
         */
        public long getMax() {
            // Add code here
            return this.max;
        }
    }
}
