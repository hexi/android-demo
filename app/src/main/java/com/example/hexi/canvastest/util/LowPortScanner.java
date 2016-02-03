package com.example.hexi.canvastest.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by hexi on 15/11/28.
 */
public class LowPortScanner {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";

        for (int i = 1; i < 1024; i++) {
            try {
                Socket socket = new Socket(host, i);
                System.out.println(String.format("There is a server on port %d of %s",
                        i, host));
                socket.close();
            }catch (UnknownHostException e) {
                System.err.println(e);
            }catch (IOException e) {
                //这个端口上不是一个服务器
            }
        }
    }
}
