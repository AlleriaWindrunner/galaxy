/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.galaxy.lemon.framework.metrics;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.codec.CodecException;
import com.galaxy.lemon.common.codec.ObjectEncoder;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.framework.metrics.MetricName;
import com.galaxy.lemon.framework.metrics.PerformanceMetricsCollector;
import com.galaxy.lemon.framework.metrics.PerformanceMetricsConfigurer;

import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class PerformanceMetricsManager {
    private ObjectEncoder objectEncoder;

    /**
     * Metrics name:Metrics
     */
    private Map<String, PerformanceMetricsCollector> performanceMetricsCollectorMap;

    static class SingleInstance  {
        public static final PerformanceMetricsManager performanceMetricsManager = new PerformanceMetricsManager();
    }

    public static PerformanceMetricsManager getInstance() {
        return SingleInstance.performanceMetricsManager;
    }

    private PerformanceMetricsManager(){
        this.performanceMetricsCollectorMap = new ConcurrentHashMap<>(16);
    }

    /**
     * 注册指标收集器
     * @param metricGroup
     * @param metricName
     * @param dimensions
     * @return
     */
    public PerformanceMetricsCollector registerMetricsCollector(String metricGroup ,String metricName, Long... dimensions) {
        return this.registerMetricsCollector(metricGroup, metricName, true, dimensions);
    }

    protected PerformanceMetricsCollector registerMetricsCollector(String metricGroup ,String metricName, boolean checked, Long... dimensions) {
        String registerKey = toRegisterKey(metricGroup, metricName);
        if (checked && this.performanceMetricsCollectorMap.containsKey(registerKey)) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Found exists metrics collector with group {1} and name {2}.", new String[]{metricGroup, metricName});
        }
        return this.performanceMetricsCollectorMap.put(registerKey, new PerformanceMetricsCollector(new MetricName(metricGroup, metricName), dimensions));
    }

    /**
     * 清除所有注册
     */
    public void unregisterAll() {
        this.performanceMetricsCollectorMap.clear();
    }

    /**
     * 清除注册
     * @param metricsGroup
     * @param metricsName
     */
    public void unregister(String metricsGroup, String metricsName) {
        this.performanceMetricsCollectorMap.remove(toRegisterKey(metricsGroup, metricsName));
    }

    /**
     * 获取已注册的指标收集器
     * @param metricGroup
     * @param metricName
     * @return
     */
    public PerformanceMetricsCollector getMetricsCollector(String metricGroup ,String metricName) {
        return this.performanceMetricsCollectorMap.get(toRegisterKey(metricGroup, metricName));
    }

    /**
     * 获取所有已注册的指标收集器
     * @return
     */
    public Collection<PerformanceMetricsCollector> getAllMetricsCollectors() {
        return this.performanceMetricsCollectorMap.values();
    }

    /**
     * set object encoder
     * @param objectEncoder
     */
    public void setObjectEncoder(ObjectEncoder objectEncoder) {
        this.objectEncoder = objectEncoder;
    }

    private ObjectEncoder getObjectEncoder() {

        if (null == this.objectEncoder) {
            this.objectEncoder = new ObjectEncoder() {
                @Override
                public String writeValueAsString(Object object) throws CodecException {
                    return String.valueOf(object);
                }

                @Override
                public byte[] writeValueAsBytes(Object object) throws CodecException {
                    throw new UnsupportedOperationException("Can't supported \"writeValueAsBytes\" operation.");
                }

                @Override
                public void writeValue(Writer writer, Object value) throws CodecException {
                    throw new UnsupportedOperationException("Can't supported \"writeValue\" operation.");
                }
            };
        }

        return this.objectEncoder;
    }

    private String encodeMetricCollector(PerformanceMetricsCollector performanceMetricsCollector) throws CodecException {
        return this.getObjectEncoder().writeValueAsString(performanceMetricsCollector);
    }

    private String safetyEncodeMetricCollector(PerformanceMetricsCollector performanceMetricsCollector) {
        try {
            return this.encodeMetricCollector(performanceMetricsCollector);
        } catch (CodecException e) {
            return LemonConstants.EMPTY_STRING;
        }
    }

    public String encodeAndRestartMetricsCollector(String metricGroup ,String metricName) throws CodecException {
        PerformanceMetricsCollector metricsCollector = this.getMetricsCollector(metricGroup, metricName);
        metricsCollector.setEndTime(LocalDateTime.now());
        this.registerMetricsCollector(metricGroup, metricName, false, metricsCollector.getDimensions());
        return this.encodeMetricCollector(metricsCollector);
    }

    public String encodeStringAndRestartAllMetricsCollectors() {
        StringBuilder encodeString = new StringBuilder();
        this.performanceMetricsCollectorMap.values().parallelStream().map(this::restartPerformanceMetricsCollector)
                .filter(p -> p.getCount().longValue() > 0)
                .map(this::safetyEncodeMetricCollector)
                .forEach(encodeString::append);
        return encodeString.toString();
    }

    private PerformanceMetricsCollector restartPerformanceMetricsCollector(PerformanceMetricsCollector performanceMetricsCollector) {
        performanceMetricsCollector.setEndTime(LocalDateTime.now());
        this.registerMetricsCollector(performanceMetricsCollector.getMetricName().getGroup(), performanceMetricsCollector.getMetricName().getName(), false, performanceMetricsCollector.getDimensions());
        return performanceMetricsCollector;
    }

    private String toRegisterKey(String group, String name) {
        return group + name;
    }

    public void configure(PerformanceMetricsConfigurer performanceMetricsConfigurer) {
        performanceMetricsConfigurer.configure(this);
    }

}
