package com.hexi;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hexi on 15/7/3.
 */
public class ConcurrencyTest {

    @Test
    public void testConcurrency() throws InterruptedException {
        Timer timer = new Timer();

        final CountDownLatch countDownLatch = new CountDownLatch(2);
        Date when = new DateTime().plusSeconds(5).toDate();
        System.out.println("time: " + System.currentTimeMillis());
        final List<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                list.add(6);
                list.add(7);
                list.add(8);

                countDownLatch.countDown();
            }
        }, when);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Integer integer : list) {
                    System.out.println("A," + System.currentTimeMillis() + "," + integer);
                }
                countDownLatch.countDown();
            }
        }, when);
        countDownLatch.await();
    }

    private static AtomicInteger delay = new AtomicInteger(500);

    private void testName(final String name) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("time: " + System.currentTimeMillis() + ", name: " + name);
                countDownLatch.countDown();
            }
        }, delay.addAndGet(500));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
