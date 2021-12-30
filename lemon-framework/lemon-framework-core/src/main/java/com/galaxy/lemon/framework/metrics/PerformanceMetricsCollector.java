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
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * 性能指标统计
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Data
public class PerformanceMetricsCollector implements MetricsCollector<Long> {
    private MetricName metricName;
    @JsonIgnore
    private Long[] dimensions;
    /**
     * 统计总次数
     */
    private LongAdder count;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Metrics> metrics;


    protected PerformanceMetricsCollector(MetricName metricName, Long... dimensions){
        if (CommonUtils.isEmpty(dimensions)) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Performance metrics dimension can not be null.");
        }
        this.metricName = metricName;
        this.dimensions = dimensions;
        this.count = new LongAdder();
        resolveDimensions(dimensions);
    }

    private PerformanceMetricsCollector(){}

    private void resolveDimensions(Long[] dimensions) {
        this.metrics = new ArrayList<>(16);
        Stream.of(dimensions).sorted((o1, o2) -> Long.compare(o2, o1)).forEachOrdered(d -> this.metrics.add(new Metrics(d, new LongAdder())));
    }

    /**
     * 收集指标
     * @param duration
     */
    @Override
    public void collect(Long duration) {
        if (null == this.startTime) {
            this.startTime = LocalDateTime.now();
        }
        this.count.increment();
        this.metrics.stream().filter(d -> d.getMetrics() <= duration).findFirst().ifPresent(Metrics::increase);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PerformanceMetricsCollector{");
        sb.append("metricName=").append(metricName);
        sb.append(", count=").append(count);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime == null ? LocalDateTime.now() : endTime);
        sb.append(", metrics=").append(this.metrics.stream().map(Metrics::toString).collect(joining(LemonConstants.COMMA)));
        sb.append('}');
        return sb.toString();
    }

    /**
     * 重新开始统计,高并发时不数据不精确
     */
    public PerformanceMetricsCollector copyAndRestart() {
        PerformanceMetricsCollector collector = new PerformanceMetricsCollector();
        collector.setStartTime(collector.getStartTime());
        collector.setCount(this.getCount());
        collector.setMetricName(this.getMetricName());
        collector.setMetrics(this.getMetrics());
        clear();
        collector.setEndTime(LocalDateTime.now());
        return collector;
    }

    /**
     * 清理指标
     */
    public void clear() {
        this.count = new LongAdder();
        this.startTime = null;
        this.endTime = null;
        resolveDimensions(this.dimensions);
    }

    @Data
    @AllArgsConstructor
    static class Metrics {
        private Long metrics;
        private LongAdder count;


        public void increase() {
            this.count.increment();
        }
    }

}
