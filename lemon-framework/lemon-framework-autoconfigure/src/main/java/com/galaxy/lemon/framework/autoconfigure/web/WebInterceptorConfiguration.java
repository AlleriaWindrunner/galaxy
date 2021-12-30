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

import com.galaxy.lemon.common.log.AccessLogger;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataInitializer;
import com.galaxy.lemon.framework.data.instantiator.AggregatedDataInstantiator;
import com.galaxy.lemon.framework.interceptor.ControllerAccessLoggerAdapter;
import com.galaxy.lemon.framework.interceptor.ControllerAspect;
import com.galaxy.lemon.framework.interceptor.GlobalExceptionHandler;
import com.galaxy.lemon.framework.interceptor.LemonDataCustomizer;
import com.galaxy.lemon.framework.interceptor.support.DefaultControllerAccessLoggerAdapter;
import com.galaxy.lemon.framework.interceptor.support.DefaultTradeLemonDataCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({ControllerAspect.class})
@ConditionalOnMissingClass("com.galaxy.lemon.gateway.GatewayBootApplication")
public class WebInterceptorConfiguration {

    @Value("${lemon.logger.request.enabled:true}")
    private boolean logRequest;

    @Value("${lemon.logger.response.enabled:true}")
    private boolean logResponse;

    @Bean
    @ConditionalOnMissingBean
    public LemonDataCustomizer tradeLemonDataCustomizer(AggregatedDataInstantiator aggregatedDataInstantiator,
                                                        LemonDataInitializer lemonDataInitializer,
                                                        InternalDataHelper internalDataHelper) {
        return new DefaultTradeLemonDataCustomizer(aggregatedDataInstantiator, lemonDataInitializer, internalDataHelper);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value="lemon.web.controllerAspect.enabled", matchIfMissing=true)
    public ControllerAccessLoggerAdapter defaultControllerAccessLoggerAdapter(AccessLogger webAccessLogger) {
        return new DefaultControllerAccessLoggerAdapter(webAccessLogger, this.logRequest, this.logResponse);
    }

    @Configuration
    @ConditionalOnClass({ControllerAspect.class})
    @ConditionalOnProperty(value="lemon.web.controllerAspect.enabled", matchIfMissing=true)
    @Import({ControllerAspect.class})
    public static class ControllerAopConfiguration {

    }

    @Configuration
    @ConditionalOnClass(GlobalExceptionHandler.class)
    @ConditionalOnProperty(value="lemon.web.globalExceptionHandler.enabled", matchIfMissing=true)
    @Import(GlobalExceptionHandler.class)
    public static class GlobalExceptionConfiguration {

    }

}
