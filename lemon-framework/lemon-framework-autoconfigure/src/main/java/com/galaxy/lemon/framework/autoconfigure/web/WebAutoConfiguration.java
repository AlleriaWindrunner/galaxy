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

import com.galaxy.lemon.common.codec.ObjectEncoder;
import com.galaxy.lemon.common.exception.ExceptionConversionService;
import com.galaxy.lemon.framework.autoconfigure.idgen.IdGeneratorAutoConfiguration;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.instantiator.AggregatedDataInstantiator;
import com.galaxy.lemon.framework.interceptor.support.WebFailureHandlerResponseFlusherResolver;
import com.galaxy.lemon.framework.interceptor.support.WebKeywordsExpressionEvaluator;
import com.galaxy.lemon.framework.jackson.ObjectMapperHolder;
import com.galaxy.lemon.framework.jackson.ObjectMapperObjectCodec;
import com.galaxy.lemon.framework.logger.http.HttpAccessLogger;
import com.galaxy.lemon.framework.logger.http.SimpleHttpAccessLogger;
import com.galaxy.lemon.framework.web.filter.TradeEntryPointFilter;
import com.galaxy.lemon.common.log.*;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Map;
import java.util.stream.Stream;

/**
 * lemon web 相关配置
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(TradeEntryPointFilter.class)
@ConditionalOnWebApplication
@EnableConfigurationProperties(WebProperties.class)
@Import({WebInterceptorConfiguration.class, HttpServletFilterConfiguration.class})
@AutoConfigureAfter(IdGeneratorAutoConfiguration.class)
public class WebAutoConfiguration {

    @Bean
    public WebFailureHandlerResponseFlusherResolver failureHandlerResponseResolver (
            AggregatedDataInstantiator aggregatedDataInstantiator,
            ExceptionConversionService exceptionConversionService,
            InternalDataHelper internalDataHelper,
            ObjectMapperHolder responseObjectMapperHolder) {
        ObjectEncoder objectEncoder = new ObjectMapperObjectCodec(responseObjectMapperHolder.getObjectMapper());
        return new WebFailureHandlerResponseFlusherResolver(exceptionConversionService, aggregatedDataInstantiator, internalDataHelper, objectEncoder);
    }

    @Bean
    public KeywordsExpressionEvaluator logKeywordsExpressionEvaluator() {
        return new WebKeywordsExpressionEvaluator();
    }

    @Configuration
    public static class HttpAccessLoggerConfiguration {

        @Bean
        @ConditionalOnMissingBean(name="webAccessLogger")
        public AccessLogger webAccessLogger(KeywordsResolver keywordsResolver,
                                            LoggingCodec loggingCodec) {
            return new DefaultAccessLogger(keywordsResolver, loggingCodec, LoggerFactory.getLogger("webRequestAccessLogger"));
        }

        @Bean
        @ConditionalOnMissingBean
        public HttpAccessLogger httpAccessLogger(AccessLogger webAccessLogger) {
            return new SimpleHttpAccessLogger(webAccessLogger);
        }
    }

    @Configuration
    @ConditionalOnClass(DefaultErrorAttributes.class)
    public static class ErrorConfiguration {
        private String[] filterErrorAttributes = new String[]{"exception", "message"};

        @Bean
        @ConditionalOnProperty(value = "lemon.web.error.filterErrorAttributes.enabled", matchIfMissing = true)
        public DefaultErrorAttributes errorAttributes(){
            return new DefaultErrorAttributes() {
                @Override
                public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
                    Map<String, Object> result = super.getErrorAttributes(requestAttributes, includeStackTrace);
                    Stream.of(filterErrorAttributes).forEach(s -> result.remove(s));
                    return result;
                }
            };
        }
    }

}
