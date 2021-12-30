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

package com.galaxy.lemon.framework.stream.client;

import com.galaxy.lemon.common.log.LoggingCodec;
import com.galaxy.lemon.framework.stream.client.interceptor.LoggerRequestInterceptor;
import com.galaxy.lemon.framework.stream.logging.OutputLogger;
import com.galaxy.lemon.framework.stream.logging.SimpleOutputLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class StreamOutputConfiguration {

    public static final String STREAM_REQUEST_LOGGER_BEAN_NAME = "streamLoggerRequestInterceptor";

    @Bean
    @ConditionalOnMissingBean
    public OutputLogger outputLogger(LoggingCodec loggingCodec) {
        return new SimpleOutputLogger(loggingCodec, SimpleOutputLogger.class);
    }

    @Bean
    public BindingMetadataHolder bindingMetadataHolder() {
        return new BindingMetadataHolder();
    }

    @Bean
    @ConditionalOnMissingBean(name = STREAM_REQUEST_LOGGER_BEAN_NAME)
    public RequestInterceptor streamLoggerRequestInterceptor(OutputLogger outputLogger,
                                                             BindingMetadataHolder bindingMetadataHolder) {
        return new LoggerRequestInterceptor(outputLogger, bindingMetadataHolder);
    }

}
