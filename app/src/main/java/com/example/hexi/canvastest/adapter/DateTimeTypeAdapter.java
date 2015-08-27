package com.example.hexi.canvastest.adapter;

import com.example.hexi.canvastest.util.DateUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;

import java.io.IOException;

/**
 * Created by hexi on 15/6/30.
 */
public class DateTimeTypeAdapter extends TypeAdapter<DateTime> {
    @Override
    public void write(JsonWriter out, DateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(value.getMillis());
    }

    @Override
    public DateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return deserializeToDate(in.nextString());
    }

    private synchronized DateTime deserializeToDate(String json) {
        try {
            return DateUtil.parse_yyyyddMM_hhmmss(json);
        } catch (Exception e){}

        try {
            return DateUtil.parse_yyyyddMM(json);
        } catch (Exception e){}

        return null;
    }
}
