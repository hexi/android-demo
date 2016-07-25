package com.example.hexi.canvastest.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by hexi on 14/12/8.
 */
public class BigDecimalUtil {
    public static String format(double v, int scale) {
        return new BigDecimal(v).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static double scale(double v, int scale) {
        return new BigDecimal(v).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double sub(double value1, double value2) {
        return new BigDecimal(String.valueOf(value1))
                .subtract(new BigDecimal(String.valueOf(value2)))
                .doubleValue();
    }

    public static double div(double value1, double value2, int scale) {
        return new BigDecimal(String.valueOf(value1))
                .divide(new BigDecimal(String.valueOf(value2)), scale, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    public static double mul(double value1, double value2) {
        return new BigDecimal(String.valueOf(value1))
                .multiply(new BigDecimal(String.valueOf(value2))).doubleValue();
    }


    public static double mulRoundDown(double value1, double value2, int scale) {
        return new BigDecimal(String.valueOf(value1))
                .multiply(new BigDecimal(String.valueOf(value2)))
                .setScale(scale, BigDecimal.ROUND_DOWN)
                .doubleValue();
    }

    public static double mul(double... values) {
        if (values == null || values.length == 0) {
            return 0D;
        }
        BigDecimal result = new BigDecimal(1);
        for (double value : values) {
            result = result.multiply(new BigDecimal(String.valueOf(value)));
        }
        return result.doubleValue();
    }

    public static double add(double... values) {
        if (values == null || values.length == 0) {
            return 0D;
        }
        BigDecimal result = new BigDecimal(0);
        for (double value : values) {
            result = result.add(new BigDecimal(String.valueOf(value)));
        }
        return result.doubleValue();
    }

    /**
     * 格式化数字  10000一下显示数字,否则显示x.x万
     *
     * @param num
     * @return
     */
    public static String formatNUm(long num) {
        int TEN_THOUSAND = 10000;
        int TEN_MILLION = 10000000;
        String result = num < TEN_THOUSAND ? num + "" : num < TEN_MILLION ? new DecimalFormat("#.0").format((double) num / TEN_THOUSAND) + "万" : new DecimalFormat("#.0").format((double) num / TEN_MILLION) + "千万";
        return result;
    }
}
