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

package com.galaxy.lemon.framework.autoconfigure.core;

import com.galaxy.lemon.common.codec.ObjectEncoder;
import com.galaxy.lemon.framework.metrics.PerformanceMetricsConfigurer;
import com.galaxy.lemon.framework.metrics.PerformanceMetricsManager;
import com.galaxy.lemon.framework.metrics.PerformanceMetricsPrinterScheduler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@EnableConfigurationProperties(MetricProperties.class)
public class MetricsConfiguration {

    private MetricProperties metricProperties;

    public MetricsConfiguration(MetricProperties metricProperties) {
        this.metricProperties = metricProperties;
    }

    @Bean
    public PerformanceMetricsConfigurer performanceMetricsConfigurer(ObjectEncoder objectEncoder) {
        return new PerformanceMetricsConfigurer() {
            @Override
            public void configure(PerformanceMetricsManager performanceMetricsManager) {
                performanceMetricsManager.setObjectEncoder(objectEncoder);
                Optional.ofNullable(metricProperties.getPerformance()).map(p -> p.getCollectors()).map(Map::values).ifPresent(s -> s.stream().forEach(
                        m -> performanceMetricsManager.registerMetricsCollector(m.getGroup(), m.getName(), m.getDimensions().toArray(new Long[m.getDimensions().size()]))
                ));
            }

            @Override
            public void configure(PerformanceMetricsPrinterScheduler performanceMetricsPrinterScheduler) {
                performanceMetricsPrinterScheduler.setLogger(LoggerFactory.getLogger("performanceMetricsPrinter"));
                Optional.ofNullable(metricProperties.getPrinter()).map(MetricProperties.Printer::getScheduledPeriod).ifPresent(performanceMetricsPrinterScheduler::setScheduledPeriod);
            }
        };
    }

    @Bean
    public PerformanceMetricsManager performanceMetricsManager(PerformanceMetricsConfigurer performanceMetricsConfigurer) {
        PerformanceMetricsManager performanceMetricsManager = PerformanceMetricsManager.getInstance();
        performanceMetricsManager.configure(performanceMetricsConfigurer);
        return performanceMetricsManager;
    }

    @Bean
    @ConditionalOnProperty(prefix = "lemon.metrics.printer", name = "enabled")
    public PerformanceMetricsPrinterScheduler performanceMetricsPrinterScheduler(PerformanceMetricsConfigurer performanceMetricsConfigurer) {
        PerformanceMetricsPrinterScheduler performanceMetricsPrinterScheduler = new PerformanceMetricsPrinterScheduler();
        performanceMetricsPrinterScheduler.configure(performanceMetricsConfigurer);
        return performanceMetricsPrinterScheduler;
    }

    @Configuration
    static class PerformanceMetricsPrinterSchedulerLifecycle {

        private PerformanceMetricsPrinterScheduler performanceMetricsPrinterScheduler;


        public PerformanceMetricsPrinterSchedulerLifecycle(ObjectProvider<PerformanceMetricsPrinterScheduler> performanceMetricsPrinterScheduler) {
            this.performanceMetricsPrinterScheduler = performanceMetricsPrinterScheduler.getIfAvailable();
        }

        @PostConstruct
        public void start() {
            if (null != this.performanceMetricsPrinterScheduler) {
                this.performanceMetricsPrinterScheduler.start();
            }
        }

        @PreDestroy
        public void destroy() {
            if (null != this.performanceMetricsPrinterScheduler) {
                this.performanceMetricsPrinterScheduler.shutdown();
            }
        }
    }

}
