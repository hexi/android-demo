package com.hexi.model;

import org.joda.time.DateTime;

/**
 * Created by hexi on 14/12/11.
 */
public enum LineType {
    avg("AVG"),
    avg2d("AVG2"),
    k5m("5m"),
    k15m("15m"),
    k30m("30m"),
    k60m("60m"),
    k120m("120m"),
    k180m("180m"),
    k240m("240m"),
    k1d("1d"),
    k1w("1w"),
    k1M("1M");

    public String value;

    private LineType(String value) {
        this.value = value;
    }

    public String formatQueryTime(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        switch (this) {
            case avg:
                return dateTime.toString("YYYYMMdd HH:mm");
            case k1d:
            case k1w:
            case k1M:
                return dateTime.toString("YYYYMMdd");
            case k5m:
            case k15m:
            case k30m:
            case k60m:
            case k120m:
            case k180m:
            case k240m:
                return dateTime.toString("YYYYMMddHHmmss");
            default:
                return dateTime.toString("YYYYMMdd");
        }
    }

    public static LineType getByValue(String value) {
        for (LineType lineType : values()) {
            if (lineType.value.equals(value)) {
                return lineType;
            }
        }
        return null;
    }
}
