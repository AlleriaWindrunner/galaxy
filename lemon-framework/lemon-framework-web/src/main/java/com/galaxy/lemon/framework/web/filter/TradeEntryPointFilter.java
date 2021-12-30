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

package com.galaxy.lemon.framework.web.filter;

import com.galaxy.lemon.common.Counter;
import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.common.log.logback.MDCUtil;
import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.metrics.DelegateMetricsCollector;
import com.galaxy.lemon.framework.metrics.MetricsCollector;
import com.galaxy.lemon.framework.response.FailureHandlerResponseFlusher;
import com.galaxy.lemon.framework.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class TradeEntryPointFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(TradeEntryPointFilter.class);

    private FailureHandlerResponseFlusher failureHandlerResponseFlusher;
    private Counter concurrent = new Counter();
    private MetricsCollector<Supplier> metricsCollector;

    public TradeEntryPointFilter(FailureHandlerResponseFlusher failureHandlerResponseFlusher) {
        this(failureHandlerResponseFlusher, new DelegateMetricsCollector(null, false));
    }

    public TradeEntryPointFilter(FailureHandlerResponseFlusher failureHandlerResponseFlusher,
                                 MetricsCollector<Supplier> webRequestMetricsCollector) {
        this.failureHandlerResponseFlusher = failureHandlerResponseFlusher;
        this.metricsCollector = webRequestMetricsCollector;
    }

    public TradeEntryPointFilter(){}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            LemonContextUtils.setAccessConcurrent((int)this.concurrent.incrementAndGet());
            Optional.ofNullable(WebUtils.resolveRequestId(request, false)).filter(StringUtils::isNotBlank).ifPresent(MDCUtil::putMDCKey);
            LemonContextUtils.setTradeStartTime();
            WebUtils.setLemonContextRequest(request);
            filterChain.doFilter(request, response);
        } catch (Exception e){
            if (null != this.failureHandlerResponseFlusher) {
                if (logger.isErrorEnabled()) {
                    logger.error("Unexpected error occurred.", e);
                }
                this.failureHandlerResponseFlusher.flushFailure(e, null, response);
            } else {
                throw e;
            }
        } finally {
            this.metricsCollector.collect(() -> DateTimeUtils.durationMillis(LemonContextUtils.getTradeStartTime(), DateTimeUtils.getCurrentLocalDateTime()));
            LemonDataHolder.clear();
            LemonContext.clearCurrentContext();
            MDCUtil.removeMDCKey();
            this.concurrent.decrement();
        }
    }
    
}
