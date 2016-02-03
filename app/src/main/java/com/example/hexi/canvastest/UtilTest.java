package com.example.hexi.canvastest;

/**
 * Created by hexi on 15/12/25.
 */
public class UtilTest {

    public static void main(String[] args) {
        new PacketReader().start();

        System.out.println("===start===");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
