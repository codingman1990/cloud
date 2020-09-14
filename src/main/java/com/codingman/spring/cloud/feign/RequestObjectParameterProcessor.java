package com.codingman.spring.cloud.feign;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import feign.MethodMetadata;

import static feign.Util.checkState;

/**
 * 处理@RequestObject类型参数,实现和@RequestParam相同功能
 *
 * @author ty
 */
public class RequestObjectParameterProcessor implements AnnotatedParameterProcessor {
    private static final Class<RequestObject> ANNOTATION = RequestObject.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {
        int parameterIndex = context.getParameterIndex();
        Class<?> parameterType = method.getParameterTypes()[parameterIndex];
        MethodMetadata data = context.getMethodMetadata();
        // 非基本类型或包装类型
        if (!ClassUtils.isPrimitiveOrWrapper(parameterType)) {
            // 只能有一个@QueryMap或者Map或者@RequestObject
            checkState(data.queryMapIndex() == null, "Query map can only be present once.");
            data.queryMapIndex(parameterIndex);
        }
        return true;
    }
}
