package com.codingman.spring.cloud.metrics.export.sls;

import java.math.BigDecimal;
import java.util.Map;

/**
 * counter
 *
 * @author ty
 */
public class SlsCounter extends SlsMeter {
    private BigDecimal value;

    public SlsCounter(String name, Map<String, String> tags, String type, long time) {
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
        return "SlsCounter{" +
                super.toString() +
                "value=" + value +
                '}';
    }
}
