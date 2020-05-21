package com.codingman.spring.cloud.metrics.export.sls;

import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Clock;

/**
 * sls集成micrometer自动配置
 *
 * @author ty
 */
@Configuration
@AutoConfigureBefore({CompositeMeterRegistryAutoConfiguration.class, SimpleMetricsExportAutoConfiguration.class})
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnBean(Clock.class)
@ConditionalOnProperty(prefix = "management.metrics.export.sls", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SlsProperties.class)
public class SlsMetricsExportAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public SlsConfig slsConfig(SlsProperties slsProperties) {
        return new SlsPropertiesConfigAdapter(slsProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SlsMeterRegistry slsMeterRegistry(SlsConfig slsConfig, Clock clock) {
        return new SlsMeterRegistry(slsConfig, clock);
    }
}
