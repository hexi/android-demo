package com.example.hexi.canvastest.concurrent;

/**
 * Created by hexi on 15/11/18.
 */
public class PriorityExecutorServiceTest {

    public static void main(String[] args) throws InterruptedException {
        PriorityExecutorService s = Executors.newPriorityFixedThreadPool(2);

        for(int i=0;i<10;i++)
            s.submit(new TestThread(3), 3);

        for(int i=0;i<10;i++)
            s.submit(new TestThread(8), 8);

        for(int i=0;i<10;i++)
            s.submit(new TestThread(5), 5);

        s.changePriorities(5, 10);

    }

    private static class TestThread implements Runnable
    {
        int priority;
        TestThread(int priority)
        {
            this.priority = priority;
        }
        @Override
        public void run()
        {
            System.out.println("Thread Id: "+Thread.currentThread().getId()+"| Original Task Priority: "+priority+"| Current Task priority: "+Thread.currentThread().getPriority());

            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
