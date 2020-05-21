package com.codingman.spring.cloud.metrics.export.sls;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;

/**
 * 自定义的namingConvention,避免直接使用也有的,方便后续扩展
 *
 * @author ty
 */
public class SlsNamingConvention implements NamingConvention {
    private final NamingConvention delegate;

    public SlsNamingConvention() {
        this(NamingConvention.snakeCase);
    }

    public SlsNamingConvention(NamingConvention delegate) {
        this.delegate = delegate;
    }

    @Override
    public String name(String name, Meter.Type type, String baseUnit) {
        return delegate.name(name, type, baseUnit);
    }
}
