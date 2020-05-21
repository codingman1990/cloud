package com.codingman.spring.cloud.metrics.export.sls;

import com.epet.microservices.common.logging.LoggerBuilder;
import com.epet.microservices.common.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.step.StepMeterRegistry;

/**
 * 阿里云日志服务meter采集
 *
 * @author ty
 */
public class SlsMeterRegistry extends StepMeterRegistry {
    private final SlsConfig config;
    private Logger logger;

    public SlsMeterRegistry(SlsConfig config, Clock clock) {
        this(config, clock, Executors.defaultThreadFactory());
    }

    public SlsMeterRegistry(SlsConfig config, Clock clock, ThreadFactory threadFactory) {
        super(config, clock);
        config().namingConvention(new SlsNamingConvention());
        this.config = config;
        start(threadFactory);
    }

    @Override
    protected void publish() {
        createLoggerIfNecessary();
        this.getMeters().forEach(meter -> {
            SlsMeter slsMeter;
            if (meter instanceof Counter) {
                slsMeter = writeCounter((Counter) meter);
            } else if (meter instanceof FunctionCounter) {
                slsMeter = writeCounter((FunctionCounter) meter);
            } else if (meter instanceof Gauge) {
                slsMeter = writeGauge((Gauge) meter);
            } else if (meter instanceof Timer) {
                slsMeter = writeTimer((Timer) meter);
            } else if (meter instanceof FunctionTimer) {
                slsMeter = writeTimer((FunctionTimer) meter);
            } else if (meter instanceof DistributionSummary) {
                slsMeter = writeDistributionSummary((DistributionSummary) meter);
            } else if (meter instanceof LongTaskTimer) {
                slsMeter = writeLongTaskTimer((LongTaskTimer) meter);
            } else {
                slsMeter = writeUnknownMeter(meter);
            }
            log(slsMeter);
        });
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    private void createLoggerIfNecessary() {
        if (logger == null) {
            logger = LoggerBuilder.getIfAbsent("metricLogger", loggerName -> new LoggerBuilder("/wwwlogs/metric/", loggerName));
        }
    }

    private SlsCounter writeCounter(Counter counter) {
        Meter.Id id = counter.getId();
        SlsCounter slsCounter = new SlsCounter(name(id), tags(id), MetricType.COUNTER.getType(), time());
        slsCounter.setValue(BigDecimal.valueOf(counter.count()));
        return slsCounter;
    }

    private SlsCounter writeCounter(FunctionCounter functionCounter) {
        Meter.Id id = functionCounter.getId();
        SlsCounter slsCounter = new SlsCounter(name(id), tags(id), MetricType.COUNTER.getType(), time());
        slsCounter.setValue(BigDecimal.valueOf(functionCounter.count()));
        return slsCounter;
    }

    private SlsGauge writeGauge(Gauge gauge) {
        Meter.Id id = gauge.getId();
        SlsGauge slsGauge;
        if (gauge instanceof TimeGauge) {
            slsGauge = new SlsGauge(name(id), tags(id), MetricType.GAUGE.getType(), time());
            slsGauge.setValue(BigDecimal.valueOf(((TimeGauge) gauge).value(getBaseTimeUnit())));
        } else {
            slsGauge = new SlsGauge(name(id), tags(id), MetricType.GAUGE.getType(), time());
            slsGauge.setValue(BigDecimal.valueOf(gauge.value()));
        }
        return slsGauge;
    }

    private SlsTimer writeTimer(Timer timer) {
        Meter.Id id = timer.getId();
        SlsTimer slsTimer = new SlsTimer(name(id), tags(id), MetricType.HISTOGRAM.getType(), time());
        slsTimer.setSum(BigDecimal.valueOf(timer.totalTime(getBaseTimeUnit())));
        slsTimer.setCount(timer.count());
        slsTimer.setMean(BigDecimal.valueOf(timer.mean(getBaseTimeUnit())));
        slsTimer.setUpper(BigDecimal.valueOf(timer.max(getBaseTimeUnit())));
        return slsTimer;
    }

    private SlsFunctionTimer writeTimer(FunctionTimer functionTimer) {
        Meter.Id id = functionTimer.getId();
        SlsFunctionTimer slsFunctionTimer = new SlsFunctionTimer(name(id), tags(id), MetricType.HISTOGRAM.getType(), time());
        slsFunctionTimer.setSum(BigDecimal.valueOf(functionTimer.totalTime(getBaseTimeUnit())));
        slsFunctionTimer.setCount(BigDecimal.valueOf(functionTimer.count()));
        slsFunctionTimer.setMean(BigDecimal.valueOf(functionTimer.mean(getBaseTimeUnit())));
        return slsFunctionTimer;
    }

    private SlsDistributionSummary writeDistributionSummary(DistributionSummary distributionSummary) {
        Meter.Id id = distributionSummary.getId();
        SlsDistributionSummary slsDistributionSummary = new SlsDistributionSummary(name(id), tags(id), MetricType.HISTOGRAM.getType(), time());
        slsDistributionSummary.setSum(BigDecimal.valueOf(distributionSummary.totalAmount()));
        slsDistributionSummary.setCount(distributionSummary.count());
        slsDistributionSummary.setMean(BigDecimal.valueOf(distributionSummary.mean()));
        slsDistributionSummary.setUpper(BigDecimal.valueOf(distributionSummary.max()));
        return slsDistributionSummary;
    }

    private SlsLongTaskTimer writeLongTaskTimer(LongTaskTimer longTaskTimer) {
        Meter.Id id = longTaskTimer.getId();
        SlsLongTaskTimer slsLongTaskTimer = new SlsLongTaskTimer(name(id), tags(id), MetricType.LONG_TASK_TIMER.getType(), time());
        slsLongTaskTimer.setActiveTasks(longTaskTimer.activeTasks());
        slsLongTaskTimer.setDuration(BigDecimal.valueOf(longTaskTimer.duration(getBaseTimeUnit())));
        return slsLongTaskTimer;
    }

    private SlsUnknownMeter writeUnknownMeter(Meter meter) {
        Meter.Id id = meter.getId();
        SlsUnknownMeter slsUnknownMeter = new SlsUnknownMeter(name(id), tags(id), MetricType.UNKNOWN.getType(), time());
        meter.measure().forEach(measurement -> {
            switch (measurement.getStatistic()) {
                case TOTAL: {
                    slsUnknownMeter.setTotal(BigDecimal.valueOf(measurement.getValue()));
                    break;
                }
                case TOTAL_TIME: {
                    slsUnknownMeter.setTotalTime(BigDecimal.valueOf(measurement.getValue()));
                    break;
                }
                case COUNT: {
                    slsUnknownMeter.setCount(BigDecimal.valueOf(measurement.getValue()));
                    break;
                }
                case ACTIVE_TASKS: {
                    slsUnknownMeter.setActiveTasks(BigDecimal.valueOf(measurement.getValue()));
                    break;
                }
                case MAX: {
                    slsUnknownMeter.setMax(BigDecimal.valueOf(measurement.getValue()));
                    break;
                }
                case VALUE: {
                    slsUnknownMeter.setValue(BigDecimal.valueOf(measurement.getValue()));
                    break;
                }
                case DURATION: {
                    slsUnknownMeter.setDuration(BigDecimal.valueOf(measurement.getValue()));
                    break;
                }
                default: {
                    slsUnknownMeter.setUnknown(BigDecimal.valueOf(measurement.getValue()));
                    break;
                }
            }
        });
        return slsUnknownMeter;
    }

    private String name(Meter.Id id) {
        return getConventionName(id);
    }

    private Map<String, String> tags(Meter.Id id) {
        List<Tag> tagList = getConventionTags(id);
        Map<String, String> tags = new HashMap<>(tagList.size());
        tagList.forEach(tag -> tags.put(tag.getKey(), tag.getValue()));
        return tags;
    }

    private long time() {
        return clock.wallTime();
    }

    private void log(SlsMeter slsMeter) {
        try {
            logger.info(ObjectMapperUtil.getSnakeObjectMapper().writeValueAsString(slsMeter));
        } catch (JsonProcessingException e) {
            // ignore
        }
    }
}
