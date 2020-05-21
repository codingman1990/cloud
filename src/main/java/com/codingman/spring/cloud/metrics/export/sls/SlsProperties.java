package com.codingman.spring.cloud.metrics.export.sls;

import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties} for configuring sls metrics export. // TODO 待扩展
 *
 * @author ty
 */
@ConfigurationProperties(prefix = "management.metrics.export.sls")
public class SlsProperties extends StepRegistryProperties {
}
