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

import com.galaxy.lemon.common.NamedThreadFactory;
import com.galaxy.lemon.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class PerformanceMetricsPrinterScheduler {

    private Logger logger = LoggerFactory.getLogger(PerformanceMetricsManager.class);

    private static long SCHEDULED_PERIOD = 1*60;
    private long scheduledPeriod = SCHEDULED_PERIOD;
    private ScheduledExecutorService scheduledExecutor = null;
    private ScheduledFuture<?> scheduledFuture = null;
    private AtomicBoolean scheduled = new AtomicBoolean();


    public static class MainLoop implements Runnable {
        private Logger logger;

        public MainLoop(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void run() {
            doRun();
        }

        public void doRun() {
            try {
                if (logger.isInfoEnabled()) {
                    String performanceMetricsInfo = PerformanceMetricsManager.getInstance().encodeStringAndRestartAllMetricsCollectors();
                    if (StringUtils.isNotBlank(performanceMetricsInfo)) {
                        logger.info(performanceMetricsInfo);
                    }
                }
            } catch (Throwable t) {
            }
        }
    }

    /**
     * 调度打印性能指标信息
     */
    public void start() {
        if (scheduled.compareAndSet(false, true)) {
            scheduledExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("lemon-performance-metrics", false));
            scheduledFuture = scheduledExecutor.scheduleWithFixedDelay(
                    new MainLoop(this.logger), scheduledPeriod, scheduledPeriod, TimeUnit.SECONDS);
        }
    }

    /**
     * stop scheduler
     */
    public void shutdown() {

        if(scheduledExecutor != null){
            scheduledExecutor.shutdown();
        }
        if(scheduledFuture != null){
            scheduledFuture.cancel(true);
        }
    }

    protected Logger getLogger() {
        return this.logger;
    }

    /**
     * 设置打印Logger
     * @param logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * 设置调度周期
     * @param scheduledPeriod
     */
    public void setScheduledPeriod(long scheduledPeriod) {
        this.scheduledPeriod = scheduledPeriod;
    }

    public void configure(PerformanceMetricsConfigurer performanceMetricsConfigurer) {
        performanceMetricsConfigurer.configure(this);
    }
}
