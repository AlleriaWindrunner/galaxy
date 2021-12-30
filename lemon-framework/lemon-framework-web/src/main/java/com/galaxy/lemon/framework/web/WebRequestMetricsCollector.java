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

package com.galaxy.lemon.framework.web;

import com.galaxy.lemon.framework.metrics.MetricsCollector;
import com.galaxy.lemon.framework.metrics.PerformanceMetricsManager;
import com.galaxy.lemon.framework.utils.WebUtils;

import java.util.function.Supplier;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class WebRequestMetricsCollector implements MetricsCollector<Supplier<Long>> {
    public static final String WEB_METRIC_GROUP = "webRequest";

    private String metricGroup;
    private boolean requiredCollection;
    private PerformanceMetricsManager performanceMetricsManager;

    public WebRequestMetricsCollector(PerformanceMetricsManager performanceMetricsManager,
                                      String metricGroup,
                                      boolean requiredCollection) {
        this.performanceMetricsManager = performanceMetricsManager;
        this.metricGroup = metricGroup;
        this.requiredCollection = requiredCollection;
    }

    @Override
    public void collect(Supplier<Long> supplier) {
        if (requiredCollection &&
                null != WebUtils.getHttpServletRequest()) {
            MetricsCollector<Long> metricsCollector = this.getPerformanceMetricsManager()
                    .getMetricsCollector(this.metricGroup, WebUtils.getHttpServletRequest().getRequestURI());
            if (null != metricsCollector) {
                metricsCollector.collect(supplier.get());
            }
        }

    }

    public PerformanceMetricsManager getPerformanceMetricsManager() {
        return performanceMetricsManager;
    }
}
