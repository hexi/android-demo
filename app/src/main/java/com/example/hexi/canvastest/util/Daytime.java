package com.example.hexi.canvastest.util;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hexi on 15/11/28.
 */
public class Daytime {

    public static void main(String[] args) throws IOException, ParseException {
        Date now = getDateFromNetwork();
        if (now == null) {
            return;
        }
        System.out.print(now);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Date getDateFromNetwork() throws IOException, ParseException {
        try (Socket socket = new Socket("time.nist.gov", 13)) {
            socket.setSoTimeout(15000);
            InputStream in = socket.getInputStream();
            StringBuilder time = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(in, "ASCII");
            for (int c = reader.read(); c != -1; c = reader.read()) {
                time.append((char)c);
            }
            String sTime = time.toString();
            if (sTime.equals("")) {
                return null;
            }
            return parseDate(sTime);
        }
    }

    private static Date parseDate(String s) throws ParseException {
        String[] pieces = s.split(" ");
        String dateTime = pieces[1] + " " + pieces[2] + " UTC";
        DateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss z");
        return format.parse(dateTime);
    }
}
