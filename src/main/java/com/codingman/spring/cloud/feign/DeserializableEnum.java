package com.codingman.spring.cloud.feign;

/**
 * 可反序列化的枚举接口
 *
 * @author ty
 */
public interface DeserializableEnum {
    /**
     * 获取枚举结果值
     *
     * @return 枚举结果值
     */
    int getValue();
}
