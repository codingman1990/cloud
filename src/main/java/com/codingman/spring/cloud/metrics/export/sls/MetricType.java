package com.codingman.spring.cloud.metrics.export.sls;

/**
 * 计量类型
 *
 * @author ty
 */
public enum MetricType {
    /**
     * {@link io.micrometer.core.instrument.Counter} AND {@link io.micrometer.core.instrument.FunctionCounter}
     */
    COUNTER("counter"),
    /**
     * {@link io.micrometer.core.instrument.Gauge} AND {@link io.micrometer.core.instrument.TimeGauge}
     */
    GAUGE("gauge"),
    /**
     * {@link io.micrometer.core.instrument.Timer} AND {@link io.micrometer.core.instrument.FunctionTimer}
     * AND {@link io.micrometer.core.instrument.DistributionSummary}
     */
    HISTOGRAM("histogram"),
    /**
     * {@link io.micrometer.core.instrument.LongTaskTimer}
     */
    LONG_TASK_TIMER("long_task_timer"),
    /**
     * unknown
     */
    UNKNOWN("unknown");
    private final String type;

    MetricType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
