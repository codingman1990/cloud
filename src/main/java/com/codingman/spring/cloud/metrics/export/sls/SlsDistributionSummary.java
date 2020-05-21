package com.codingman.spring.cloud.metrics.export.sls;

import java.math.BigDecimal;
import java.util.Map;

/**
 * distribution summary
 *
 * @author ty
 */
public class SlsDistributionSummary extends SlsMeter {
    private BigDecimal sum;
    private long count;
    private BigDecimal mean;
    private BigDecimal upper;

    public SlsDistributionSummary(String name, Map<String, String> tags, String type, long time) {
        super(name, tags, type, time);
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public BigDecimal getMean() {
        return mean;
    }

    public void setMean(BigDecimal mean) {
        this.mean = mean;
    }

    public BigDecimal getUpper() {
        return upper;
    }

    public void setUpper(BigDecimal upper) {
        this.upper = upper;
    }

    @Override
    public String toString() {
        return "SlsDistributionSummary{" +
                super.toString() +
                "sum=" + sum +
                ", count=" + count +
                ", mean=" + mean +
                ", upper=" + upper +
                '}';
    }
}
