package com.codingman.spring.cloud.feign;

import com.netflix.hystrix.HystrixCommand;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.annotation.PathVariableParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestHeaderParameterProcessor;
import org.springframework.cloud.openfeign.annotation.RequestParamParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;

import feign.Contract;
import feign.Feign;
import feign.hystrix.HystrixFeign;

/**
 * 为支持复杂对象类型查询参数自动配置类
 *
 * @author ty
 */
@Configuration
@ConditionalOnClass(Feign.class)
@ConditionalOnProperty(prefix = "feign.request", name = "object", havingValue = "true", matchIfMissing = true)
public class FeignRequestObjectAutoConfiguration {
    /**
     * 覆盖FeignClientsConfiguration默认
     */
    @Bean
    public Contract feignContract(ConversionService feignConversionService) {
        List<AnnotatedParameterProcessor> annotatedArgumentResolvers = new ArrayList<>();
        annotatedArgumentResolvers.add(new PathVariableParameterProcessor());
        annotatedArgumentResolvers.add(new RequestParamParameterProcessor());
        annotatedArgumentResolvers.add(new RequestHeaderParameterProcessor());
        // 新增的处理复杂对象类型查询参数
        annotatedArgumentResolvers.add(new RequestObjectParameterProcessor());
        return new SpringMvcContract(annotatedArgumentResolvers, feignConversionService);
    }

    /**
     * 覆盖FeignClientsConfiguration默认
     */
    @Configuration
    @ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
    protected static class HystrixFeignConfiguration {
        @Bean
        @Scope("prototype")
        @ConditionalOnProperty(name = "feign.hystrix.enabled")
        public Feign.Builder feignHystrixBuilder() {
            HystrixFeign.Builder builder = HystrixFeign.builder();
            builder.queryMapEncoder(new RequestObjectQueryMapEncoder());
            return builder;
        }
    }
}
