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

package com.galaxy.lemon.framework.autoconfigure.web;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.framework.autoconfigure.web.IndicateLemonDataInitialUrlPattern;
import com.galaxy.lemon.framework.autoconfigure.web.WebProperties;
import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.data.LemonDataInitializer;
import com.galaxy.lemon.framework.metrics.MetricsCollector;
import com.galaxy.lemon.framework.metrics.PerformanceMetricsManager;
import com.galaxy.lemon.framework.response.FailureHandlerResponseFlusher;
import com.galaxy.lemon.framework.web.WebRequestMetricsCollector;
import com.galaxy.lemon.framework.web.filter.FixedIpAccessFilter;
import com.galaxy.lemon.framework.web.filter.TradeContextCustomizer;
import com.galaxy.lemon.framework.web.filter.TradeContextCustomizerFilter;
import com.galaxy.lemon.framework.web.filter.TradeEntryPointFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class HttpServletFilterConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(HttpServletFilterConfiguration.class);

    private static final String[] FIXED_IP_ACCESS_URL_PATTERNS = new String[]{"/shutdown", "/offline"};
    private static final String URL_PATTERN_SEPARATOR = LemonConstants.COMMA;

    @Configuration
    @ConditionalOnClass(TradeEntryPointFilter.class)
    public static class TradeEntryPointFilterConfiguration {

        @Value("${lemon.metrics.performance.webRequest.enabled:false}")
        private Boolean requiredCollection;

        @Bean
        @ConditionalOnMissingBean(TradeEntryPointFilter.class)
        public TradeEntryPointFilter tradeEntryPointFilter(FailureHandlerResponseFlusher failureHandlerResponseFlusher,
                                                           PerformanceMetricsManager performanceMetricsManager) {
            if (requiredCollection) {
                MetricsCollector metricsCollector = new WebRequestMetricsCollector(performanceMetricsManager,
                        WebRequestMetricsCollector.WEB_METRIC_GROUP, true);
                return new TradeEntryPointFilter(failureHandlerResponseFlusher, metricsCollector);
            }
            return new TradeEntryPointFilter(failureHandlerResponseFlusher);
        }

        @Bean
        @ConditionalOnBean(name = "tradeEntryPointFilter")
        public FilterRegistrationBean tradeEntryPointFilterRegistration(TradeEntryPointFilter tradeEntryFilter) {
            FilterRegistrationBean registration = new FilterRegistrationBean();
            registration.setFilter(tradeEntryFilter);
            registration.addUrlPatterns("/*");
            registration.setName("tradeEntryPointFilter");
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return registration;
        }

    }

    @Configuration
    @ConditionalOnClass(TradeContextCustomizerFilter.class)
    public static class TradeContextCustomizerFilterConfiguration {

        @Bean
        @ConditionalOnMissingBean(TradeContextCustomizerFilter.class)
        public TradeContextCustomizerFilter tradeContextCustomizerFilter(ObjectProvider<TradeContextCustomizer> tradeContextCustomizer) {
            return new TradeContextCustomizerFilter(tradeContextCustomizer.getIfAvailable() == null ? new TradeContextCustomizer.DummyTradeContextCustomizer() : tradeContextCustomizer.getIfAvailable());
        }

        @Bean
        @ConditionalOnBean(name = "tradeContextCustomizerFilter")
        public FilterRegistrationBean tradeContextCustomizerFilterRegistration(TradeContextCustomizerFilter tradeContextCustomizerFilter) {
            FilterRegistrationBean registration = new FilterRegistrationBean();
            registration.setFilter(tradeContextCustomizerFilter);
            registration.addUrlPatterns("/*");
            registration.setName("tradeContextCustomizerFilter");
            // after springSecurityFilterChain
            registration.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER + 10);
            return registration;
        }

    }

    @Configuration
    @ConditionalOnClass(FixedIpAccessFilter.class)
    public static class FixedIpAccessFilterConfiguration {
        private WebProperties actuatorProperties;

        public FixedIpAccessFilterConfiguration(WebProperties actuatorProperties) {
            this.actuatorProperties = actuatorProperties;
        }

        @Bean
        public FixedIpAccessFilter fixedIpAccessFilter() {
            return new FixedIpAccessFilter();
        }

        @Bean
        public FilterRegistrationBean fixedIpAccessFilterRegistration() {
            String[] urlPatterns = Optional.ofNullable(actuatorProperties.getFixedIpAccess()).map(f -> f.getUrlPatterns()).map(p -> p.split(URL_PATTERN_SEPARATOR)).orElse(FIXED_IP_ACCESS_URL_PATTERNS);
            if (logger.isInfoEnabled()) {
                logger.info("Fixed ip access url list {}", Stream.of(urlPatterns).collect(Collectors.joining(URL_PATTERN_SEPARATOR)));
            }
            FilterRegistrationBean registration = new FilterRegistrationBean();
            registration.setFilter(fixedIpAccessFilter());
            registration.addUrlPatterns(urlPatterns);
            registration.setName("fixedIpAccessFilter");
            registration.addInitParameter("allowAccessIpList", Optional.ofNullable(actuatorProperties.getFixedIpAccess()).map(f -> f.getAllowIpList()).orElse(LemonConstants.EMPTY_STRING));
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return registration;
        }
    }

    @Configuration
    @ConditionalOnBean(IndicateLemonDataInitialUrlPattern.class)
    @ConditionalOnProperty(value = "lemon.web.filter.lemonDataInitializer.enabled", matchIfMissing = true)
    public static class LemonDataInitializerFilterConfiguration {
        private static final String PREFIX_TX_NAME = "FILTER.";
        private LemonDataInitializer lemonDataInitializer;
        private String[] lemonDataInitializerFilterUrlPatterns;

        public LemonDataInitializerFilterConfiguration(LemonDataInitializer lemonDataInitializer,
                                                       ObjectProvider<List<IndicateLemonDataInitialUrlPattern>> indicateLemonDataInitialUrlPatterns) {
            this.lemonDataInitializer = lemonDataInitializer;
            List<String> urlList = new ArrayList();
            Optional.ofNullable(indicateLemonDataInitialUrlPatterns.getIfAvailable()).ifPresent(s ->
                    s.stream().map(IndicateLemonDataInitialUrlPattern::getUrlPatterns).forEach(urlList::addAll));
            this.lemonDataInitializerFilterUrlPatterns = urlList.toArray(new String[urlList.size()]);
        }

        @Bean
        public OncePerRequestFilter lemonDataInitializerFilter() {
            return new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                    LemonContextUtils.setCurrentTxName(PREFIX_TX_NAME + Optional.ofNullable(request.getHeader(LemonConstants.HTTP_HEADER_BUSINESS)).orElse("UNKNOW"));
                    lemonDataInitializer.initialLemonData();
                    filterChain.doFilter(request, response);
                }
            };
        }

        @Bean
        public FilterRegistrationBean lemonDataInitializerFilterRegistration() {
            FilterRegistrationBean registration = new FilterRegistrationBean();
            registration.setFilter(lemonDataInitializerFilter());
            registration.addUrlPatterns(this.lemonDataInitializerFilterUrlPatterns);
            registration.setName("lemonDataInitializerFilter");
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 3);
            return registration;
        }
    }
}
