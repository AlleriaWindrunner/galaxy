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
import com.galaxy.lemon.common.exception.ExceptionConversionService;
import com.galaxy.lemon.framework.alerting.AlertingResolver;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.instantiator.AggregatedDataInstantiator;
import com.galaxy.lemon.framework.jackson.ObjectMapperHolder;
import com.galaxy.lemon.framework.jackson.ObjectMapperObjectCodec;
import com.galaxy.lemon.framework.logger.http.HttpAccessLogger;
import com.galaxy.lemon.framework.response.*;
import com.galaxy.lemon.framework.response.*;
import com.galaxy.lemon.framework.utils.WebUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConditionalOnClass(ResponseMessageResolver.class)
public class ResponseMessageConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ResponseFlushPreprocessor accessLoggingResponseFlushPreprocessor(HttpAccessLogger httpAccessLogger) {
        return new AccessLoggingResponseFlushPreprocessor(httpAccessLogger);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestIdExtractor requestIdExtractor() {
        return () -> WebUtils.resolveRequestId(WebUtils.getHttpServletRequest(), false);
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseMessageResolver defaultResponseMessageResolver(AlertingResolver alertingResolver,
                                                                  ResponseFlushPreprocessor responseFlushPreprocessor,
                                                                  InternalDataHelper internalDataHelper,
                                                                  RequestIdExtractor requestIdExtractor,
                                                                  AggregatedDataInstantiator aggregatedDataInstantiator,
                                                                  ObjectMapperHolder responseObjectMapperHolder) {
        ObjectEncoder objectEncoder = new ObjectMapperObjectCodec(responseObjectMapperHolder.getObjectMapper());
        return new DefaultResponseMessageResolver(alertingResolver, responseFlushPreprocessor, internalDataHelper, requestIdExtractor, objectEncoder, aggregatedDataInstantiator);
    }

    @Bean
    @ConditionalOnMissingBean
    public FailureHandlerResponseResolver failureHandlerResponseResolver(AggregatedDataInstantiator aggregatedDataInstantiator,
                                                                         ExceptionConversionService exceptionConversionService,
                                                                         InternalDataHelper internalDataHelper) {
        return new RelaxedFailureHandlerResponseResolver(exceptionConversionService, aggregatedDataInstantiator, internalDataHelper);
    }


}
