package com.codingman.spring.cloud.feign;

import com.codingman.spring.cloud.util.ClassUtil;
import com.codingman.spring.cloud.util.StringUtil;
import com.codingman.spring.cloud.web.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import feign.QueryMapEncoder;
import feign.codec.EncodeException;

/**
 * 把@RequestObject对象编码为查询参数Map对象(MethodMetadata.queryMapIndex是唯一可以自定义对象编码的契机了)
 *
 * @author ty
 */
public class RequestObjectQueryMapEncoder implements QueryMapEncoder {
    private final ConcurrentHashMap<Class<?>, List<Field>> fieldMap = new ConcurrentHashMap<>();
    private final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 专门应对{@link com.codingman.spring.cloud.web.Page}仅需要输出的属性
     */
    private static final String[] PRESENT_FIELD_NAME = new String[]{"pageSize", "curPage"};
    private static boolean JACKSON_PRESENT;

    static {
        try {
            Class.forName("com.fasterxml.jackson.annotation.JsonProperty");
            JACKSON_PRESENT = true;
        } catch (ClassNotFoundException e) {
            JACKSON_PRESENT = false;
        }
    }

    @Override
    public Map<String, Object> encode(Object object) {
        if (ClassUtils.isPrimitiveOrWrapper(object.getClass())) {
            throw new EncodeException("@ParamObject can't be primitive or wrapper type");
        }
        Class<?> clazz = object.getClass();
        List<Field> fieldList = fieldMap.computeIfAbsent(clazz, this::fieldList);
        /*List<Field> fieldList = fieldMap.get(clazz);
        if (fieldList == null) {
            fieldList = fieldList(clazz);
            fieldMap.put(clazz, fieldList);
        }*/
        Map<String, Object> map = new HashMap<>(fieldList.size());
        try {
            for (Field field : fieldList) {
                Object fieldObj = field.get(object);
                if (fieldObj == null) {
                    continue;
                }
                Class<?> fieldClazz = field.getType();
                String name;
                // 支持@JsonProperty
                if (JACKSON_PRESENT && field.getDeclaredAnnotation(JsonProperty.class) != null) {
                    name = field.getDeclaredAnnotation(JsonProperty.class).value();
                } else {
                    // 默认camel转snake
                    name = StringUtil.camel2Snake(field.getName());
                }

                // DeserializableEnum特殊处理
                if (DeserializableEnum.class.isAssignableFrom(fieldClazz)) {
                    DeserializableEnum deserializableEnum = (DeserializableEnum) fieldObj;
                    map.put(name, deserializableEnum.getValue());
                }
                // LocalDate
                else if (LocalDate.class.isAssignableFrom(fieldClazz)) {
                    String localDate = LOCAL_DATE_FORMATTER.format((LocalDate) fieldObj);
                    map.put(name, localDate);
                }
                // LocalDateTime
                else if (LocalDateTime.class.isAssignableFrom(fieldClazz)) {
                    String localDateTime = LOCAL_DATE_TIME_FORMATTER.format((LocalDateTime) fieldObj);
                    map.put(name, localDateTime);
                }
                // 基本类型数组
                else if (ClassUtil.isPrimitiveArray(fieldClazz)) {
                    // byte[]
                    if (ClassUtil.isByteArray(fieldClazz)) {
                        map.put(name, StringUtil.join((byte[]) fieldObj, ","));
                    }
                    // char[]
                    else if (ClassUtil.isCharArray(fieldClazz)) {
                        map.put(name, StringUtil.join((char[]) fieldObj, ","));
                    }
                    // short[]
                    else if (ClassUtil.isShortArray(fieldClazz)) {
                        map.put(name, StringUtil.join((short[]) fieldObj, ","));
                    }
                    // int[]
                    else if (ClassUtil.isIntArray(fieldClazz)) {
                        map.put(name, StringUtil.join((int[]) fieldObj, ","));
                    }
                    // float[]
                    else if (ClassUtil.isFloatArray(fieldClazz)) {
                        map.put(name, StringUtil.join((float[]) fieldObj, ","));
                    }
                    // long[]
                    else if (ClassUtil.isLongArray(fieldClazz)) {
                        map.put(name, StringUtil.join((long[]) fieldObj, ","));
                    }
                    // double[]
                    else if (ClassUtil.isDoubleArray(fieldClazz)) {
                        map.put(name, StringUtil.join((double[]) fieldObj, ","));
                    }
                }
                // 基本包装类型数组
                else if (ClassUtil.isPrimitiveWrapperArray(fieldClazz)) {
                    map.put(name, StringUtil.join((Object[]) fieldObj, ","));
                }
                // String[]
                else if (String[].class.isAssignableFrom(fieldClazz)) {
                    map.put(name, StringUtil.join((String[]) fieldObj, ","));
                } else {
                    map.put(name, fieldObj);
                }
            }
            return map;
        } catch (IllegalAccessException e) {
            throw new EncodeException("Fail encode ParamObject into query Map", e);
        }
    }

    private List<Field> fieldList(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (illegalField(field)) {
                fields.add(field);
            }
        }
        // 支持继承的父类属性
        for (Class<?> superClazz : ClassUtils.getAllSuperclasses(clazz)) {
            if (!Object.class.equals(superClazz)) {
                // Page class
                boolean isPage = superClazz.equals(Page.class);
                Arrays.stream(superClazz.getDeclaredFields())
                        .filter(field -> !isPage || (isPage && Arrays.stream(PRESENT_FIELD_NAME).anyMatch(s -> s.equalsIgnoreCase(field.getName()))))
                        .forEach(field -> {
                            if (illegalField(field)) {
                                fields.add(field);
                            }
                        });
                /*for (Field field : superClazz.getDeclaredFields()) {
                    if (illegalField(field)) {
                        fields.add(field);
                    }
                }*/
            }
        }
        return fields;
    }

    private boolean illegalField(Field field) {
        Class<?> fieldType = field.getType();
        // 暂时只能支持一层属性编码,所以必须是基础类型或者包装类型,基础类型或者包装类型数组,String,String[],DeserializableEnum类型
        // 2019-3-8 fix:新增JAVA8 LocalDate和LocalDateTime支持
        if (ClassUtils.isPrimitiveOrWrapper(fieldType)
                || ClassUtil.isPrimitiveOrWrapperArray(fieldType)
                || String.class.isAssignableFrom(fieldType) || String[].class.isAssignableFrom(fieldType)
                || DeserializableEnum.class.isAssignableFrom(fieldType)
                || LocalDateTime.class.isAssignableFrom(fieldType) || LocalDate.class.isAssignableFrom(fieldType)
                // 2019-4-15 fix:新增BigDecimal和BigInteger支持
                || BigDecimal.class.isAssignableFrom(fieldType) || BigInteger.class.isAssignableFrom(fieldType)) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return true;
        }
        return false;
    }
}
