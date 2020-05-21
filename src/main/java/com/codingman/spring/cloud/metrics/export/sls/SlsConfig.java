package com.codingman.spring.cloud.metrics.export.sls;

import io.micrometer.core.instrument.step.StepRegistryConfig;

/**
 * configuration for sls
 *
 * @author ty
 */
public interface SlsConfig extends StepRegistryConfig {
    /**
     * 前缀
     *
     * @return 前缀
     */
    @Override
    default String prefix() {
        return "sls";
    }
}
