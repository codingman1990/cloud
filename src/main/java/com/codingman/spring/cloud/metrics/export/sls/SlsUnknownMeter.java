package com.codingman.spring.cloud.metrics.export.sls;

import java.math.BigDecimal;
import java.util.Map;

/**
 * unknown meter
 *
 * @author ty
 */
public class SlsUnknownMeter extends SlsMeter {
    private BigDecimal total;
    private BigDecimal totalTime;
    private BigDecimal count;
    private BigDecimal max;
    private BigDecimal value;
    private BigDecimal unknown;
    private BigDecimal activeTasks;
    private BigDecimal duration;

    public SlsUnknownMeter(String name, Map<String, String> tags, String type, long time) {
        super(name, tags, type, time);
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(BigDecimal totalTime) {
        this.totalTime = totalTime;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getUnknown() {
        return unknown;
    }

    public void setUnknown(BigDecimal unknown) {
        this.unknown = unknown;
    }

    public BigDecimal getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(BigDecimal activeTasks) {
        this.activeTasks = activeTasks;
    }

    public BigDecimal getDuration() {
        return duration;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "SlsUnknownMeter{" +
                super.toString() +
                "total=" + total +
                ", totalTime=" + totalTime +
                ", count=" + count +
                ", max=" + max +
                ", value=" + value +
                ", unknown=" + unknown +
                ", activeTasks=" + activeTasks +
                ", duration=" + duration +
                '}';
    }
}
