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

package com.galaxy.lemon.framework.springcloud.fegin;

import com.galaxy.lemon.framework.remoting.RemoteInvocationDataProcessorComposite;
import com.galaxy.lemon.framework.springcloud.fegin.LemonResponseEntityDecoder;
import com.galaxy.lemon.framework.springcloud.fegin.LemonSpringEncoder;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import javax.validation.Validator;

/**
 * 客户化feign配置
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(Feign.class)
public class LemonFeignClientsConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(LemonFeignClientsConfiguration.class);
    
    private ObjectFactory<HttpMessageConverters> messageConverters;

    private QueryBodyParameterResolver queryBodyParameterResolver;
    
    private Validator validator;

    private RemoteInvocationDataProcessorComposite remoteInvocationDataProcessorComposite;

    @Value("${lemon.feign.validation.enabled:false}")
    private boolean enabledValidation;
    
    public LemonFeignClientsConfiguration(ObjectFactory<HttpMessageConverters> messageConverters,
                                          QueryBodyParameterResolver queryBodyParameterResolver,
                                          Validator validator,
                                          RemoteInvocationDataProcessorComposite remoteInvocationDataProcessorComposite) {
        this.messageConverters = messageConverters;
        this.queryBodyParameterResolver = queryBodyParameterResolver;
        this.validator = validator;
        this.remoteInvocationDataProcessorComposite = remoteInvocationDataProcessorComposite;
    }
    
    @Bean
    public Decoder feignDecoder() {
        return new LemonResponseEntityDecoder(new SpringDecoder(this.messageConverters), this.remoteInvocationDataProcessorComposite);
    }
    
    @Bean
    public Encoder feignEncoder(ConversionService feignConversionService) {
        return new LemonSpringEncoder(this.messageConverters,
                this.queryBodyParameterResolver,
                feignConversionService,
                this.remoteInvocationDataProcessorComposite,
                this.validator,
                this.enabledValidation);
    }

}
