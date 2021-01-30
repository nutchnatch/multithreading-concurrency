package create.threads;

import java.util.Arrays;
import java.util.List;

public class MultiExecutor {
    public static void main(String[] args) {
        Test test = new Test();
        MultiExecutor multiExecutor = new MultiExecutor(Arrays.asList(test));
        multiExecutor.executeAll();
    }

    // Add any necessary member variables here
    private List<Runnable> tasks;
    /*
     * @param tasks to executed concurrently
     */
    public MultiExecutor(List<Runnable> tasks) {
        // Complete your code here
        this.tasks = tasks;
    }

    /**
     * Starts and executes all the tasks concurrently
     */
    public void executeAll() {
        // complete your code here
        for(Runnable task : tasks) {
            Thread thread = new Thread(task);
            thread.start();
        }
    }

    private static class Test implements Runnable {
         public void run() {
             System.out.println("Hi there.");
         }
    }
}