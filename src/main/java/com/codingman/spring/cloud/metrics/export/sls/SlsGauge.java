package com.codingman.spring.cloud.metrics.export.sls;

import java.math.BigDecimal;
import java.util.Map;

/**
 * gauge
 *
 * @author ty
 */
public class SlsGauge extends SlsMeter {
    private BigDecimal value;

    public SlsGauge(String name, Map<String, String> tags, String type, long time) {
        super(name, tags, type, time);
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SlsGauge{" +
                super.toString() +
                "value=" + value +
                '}';
    }
}
