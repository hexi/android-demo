package com.example.hexi.canvastest;

import java.util.Date;

/**
 * Created by hexi on 15/12/25.
 */
public class PacketReader {
    private Thread readerThread;

    void start() {
        if (readerThread != null && readerThread.isAlive()) {
            return ;
        }
        readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("======in read:"+new Date());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        readerThread.setName("Packet write thread");
        readerThread.setDaemon(true);
        readerThread.start();
    }
}
