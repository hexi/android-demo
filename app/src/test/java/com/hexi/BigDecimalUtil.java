package com.hexi;

import java.math.BigDecimal;

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

    public static double div(double value1, double value2) {
        return new BigDecimal(String.valueOf(value1))
                .divide(new BigDecimal(String.valueOf(value2)))
                .doubleValue();
    }

    public static double mul(double value1, double value2) {
        return new BigDecimal(String.valueOf(value1))
                .multiply(new BigDecimal(String.valueOf(value2))).doubleValue();
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
}
