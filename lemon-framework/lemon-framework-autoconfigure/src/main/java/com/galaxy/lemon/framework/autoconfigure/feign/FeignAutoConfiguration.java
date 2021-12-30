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

package com.galaxy.lemon.framework.autoconfigure.feign;

import com.galaxy.lemon.common.log.AccessLogger;
import com.galaxy.lemon.common.log.LoggingCodec;
import com.galaxy.lemon.framework.autoconfigure.feign.FeignHttpClientConfiguration;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataMessageConverter;
import com.galaxy.lemon.framework.remoting.RemoteInvocationDataProcessor;
import com.galaxy.lemon.framework.remoting.RemoteInvocationDataProcessorComposite;
import com.galaxy.lemon.framework.springcloud.fegin.LemonBodyParameterProcessor;
import com.galaxy.lemon.framework.springcloud.fegin.LemonDataTransportResolveRequestInterceptor;
import com.galaxy.lemon.framework.springcloud.fegin.QueryBodyParameterParser;
import com.galaxy.lemon.framework.springcloud.fegin.QueryBodyParameterProcessor;
import com.galaxy.lemon.framework.springcloud.fegin.interceptor.ProxyFeignClientConfiguration;
import com.galaxy.lemon.framework.springcloud.fegin.logging.DefaultFeignAccessLoggerAdapter;
import com.galaxy.lemon.framework.springcloud.fegin.logging.FeignAccessLogger;
import com.galaxy.lemon.framework.springcloud.fegin.logging.FeignAccessLoggerAdapter;
import com.galaxy.lemon.framework.springcloud.fegin.logging.FeignAccessLoggingRequestInterceptor;
import com.galaxy.lemon.framework.springcloud.fegin.support.BasicRemoteInvocationDataProcessor;
import com.galaxy.lemon.framework.springcloud.fegin.support.QueryBodyParameterQueryStringResolver;
import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.feign.AnnotatedParameterProcessor;
import org.springframework.cloud.netflix.feign.annotation.PathVariableParameterProcessor;
import org.springframework.cloud.netflix.feign.annotation.RequestHeaderParameterProcessor;
import org.springframework.cloud.netflix.feign.annotation.RequestParamParameterProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.*;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({Feign.class})
@Import({ProxyFeignClientConfiguration.class, FeignHttpClientConfiguration.class})
public class FeignAutoConfiguration {

    @Bean
    public QueryBodyParameterQueryStringResolver queryBodyParameterQueryStringResolver(InternalDataHelper internalDataHelper) {
        return new QueryBodyParameterQueryStringResolver(Arrays.asList(internalDataHelper.getLemonDataPropertyWrapper().getProperties()));
    }

    @Bean
    @ConditionalOnMissingBean(name = "feignAccessLogger")
    public AccessLogger feignAccessLogger(LoggingCodec loggingCodec) {
        return new FeignAccessLogger(loggingCodec);
    }

    @Bean
    @ConditionalOnMissingBean
    public FeignAccessLoggerAdapter feignAccessLoggerAdapter(AccessLogger feignAccessLogger) {
        return new DefaultFeignAccessLoggerAdapter(feignAccessLogger);
    }

    @Bean
    public RequestInterceptor feignAccessLoggingRequestInterceptor(FeignAccessLoggerAdapter feignAccessLoggerAdapter) {
        return new FeignAccessLoggingRequestInterceptor(feignAccessLoggerAdapter);
    }

    @Bean
    public RequestInterceptor httpGetDTOResolverRequestInterceptor(LemonDataMessageConverter lemonDataMessageConverter) {
        return new LemonDataTransportResolveRequestInterceptor<>(lemonDataMessageConverter);
    }

    @Bean
    public List<AnnotatedParameterProcessor> parameterProcessors(QueryBodyParameterParser queryBodyParameterParser) {
        List<AnnotatedParameterProcessor> annotatedArgumentResolvers = new ArrayList<>();
        annotatedArgumentResolvers.add(new PathVariableParameterProcessor());
        annotatedArgumentResolvers.add(new RequestParamParameterProcessor());
        annotatedArgumentResolvers.add(new RequestHeaderParameterProcessor());
        annotatedArgumentResolvers.add(new LemonBodyParameterProcessor(queryBodyParameterParser));
        annotatedArgumentResolvers.add(new QueryBodyParameterProcessor(queryBodyParameterParser));
        return annotatedArgumentResolvers;
    }

    @Bean
    public RemoteInvocationDataProcessor basicRemoteInvocationDataProcessor(InternalDataHelper internalDataHelper) {
        return new BasicRemoteInvocationDataProcessor(internalDataHelper);
    }

    @Bean
    public RemoteInvocationDataProcessorComposite remoteInvocationDataProcessorComposite(ObjectProvider<List<RemoteInvocationDataProcessor>> remoteInvocationDataProcessors) {
        List<RemoteInvocationDataProcessor> remoteInvocationDataProcessorList = Optional.ofNullable(remoteInvocationDataProcessors.getIfAvailable()).orElse(Collections.EMPTY_LIST);
        return new RemoteInvocationDataProcessorComposite(remoteInvocationDataProcessorList);
    }
}
