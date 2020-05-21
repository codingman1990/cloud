package com.codingman.spring.cloud.metrics.export.sls;

import java.math.BigDecimal;
import java.util.Map;

/**
 * longTaskTimer
 *
 * @author ty
 */
public class SlsLongTaskTimer extends SlsMeter {
    private int activeTasks;
    private BigDecimal duration;

    public SlsLongTaskTimer(String name, Map<String, String> tags, String type, long time) {
        super(name, tags, type, time);
    }

    public int getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(int activeTasks) {
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
        return "SlsLongTaskTimer{" +
                super.toString() +
                "activeTasks=" + activeTasks +
                ", duration=" + duration +
                '}';
    }
}
