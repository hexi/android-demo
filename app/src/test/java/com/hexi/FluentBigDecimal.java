package com.hexi;

import java.math.BigDecimal;

/**
 * Created by hexi on 15/12/25.
 */
public class FluentBigDecimal {

    private BigDecimal result;
    public FluentBigDecimal with(double value) {
        this.result = new BigDecimal(String.valueOf(value));
        return this;
    }
    public FluentBigDecimal div(double value) {
        checkStatus();
        result = result.divide(new BigDecimal(String.valueOf(value)));
        return this;
    }

    public FluentBigDecimal add(double value) {
        checkStatus();
        result = result.add(new BigDecimal(String.valueOf(value)));
        return this;
    }

    public double value() {
        checkStatus();
        return this.result.doubleValue();
    }

    private void checkStatus() {
        if (this.result == null) {
            throw new IllegalStateException("must called with method");
        }
    }
}
