package com.codingman.spring.cloud.metrics.export.sls;

import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryPropertiesConfigAdapter;

/**
 * Adapter to convert {@link SlsProperties} to an {@link SlsConfig}.
 *
 * @author ty
 */
class SlsPropertiesConfigAdapter extends StepRegistryPropertiesConfigAdapter<SlsProperties> implements SlsConfig {
    SlsPropertiesConfigAdapter(SlsProperties properties) {
        super(properties);
    }
}
