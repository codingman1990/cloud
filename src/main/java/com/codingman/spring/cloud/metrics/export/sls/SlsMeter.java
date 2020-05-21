package com.codingman.spring.cloud.metrics.export.sls;

import java.util.Collections;
import java.util.Map;

/**
 * sls基础计量类
 *
 * @author ty
 */
public class SlsMeter {
    private final String name;
    private final Map<String, String> tags;
    private final String type;
    private final long time;

    public SlsMeter(String name, Map<String, String> tags, String type, long time) {
        this.name = name;
        this.tags = Collections.unmodifiableMap(tags);
        this.type = type;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public String getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "SlsMeter{" +
                "name='" + name + '\'' +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                ", time=" + time +
                '}';
    }
}
