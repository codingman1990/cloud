package com.codingman.spring.cloud.metrics.export.sls;

import java.math.BigDecimal;
import java.util.Map;

/**
 * functionTimer
 *
 * @author ty
 */
public class SlsFunctionTimer extends SlsMeter {
    private BigDecimal sum;
    private BigDecimal count;
    private BigDecimal mean;

    public SlsFunctionTimer(String name, Map<String, String> tags, String type, long time) {
        super(name, tags, type, time);
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getMean() {
        return mean;
    }

    public void setMean(BigDecimal mean) {
        this.mean = mean;
    }

    @Override
    public String toString() {
        return "SlsFunctionTimer{" +
                super.toString() +
                "sum=" + sum +
                ", count=" + count +
                ", mean=" + mean +
                '}';
    }
}
