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

import com.galaxy.lemon.common.codec.ObjectCodec;
import com.galaxy.lemon.common.codec.ObjectDecoder;
import com.galaxy.lemon.common.codec.ObjectEncoder;
import com.galaxy.lemon.framework.autoconfigure.common.CommonAutoConfiguration;
import com.galaxy.lemon.framework.autoconfigure.core.LoggingConfiguration;
import com.galaxy.lemon.framework.autoconfigure.core.MetricsConfiguration;
import com.galaxy.lemon.framework.autoconfigure.core.ResponseMessageConfiguration;
import com.galaxy.lemon.framework.autoconfigure.core.SystemStatusConfiguration;
import com.galaxy.lemon.framework.config.CoreProperties;
import com.galaxy.lemon.framework.config.LemonEnvironment;
import com.galaxy.lemon.framework.config.LemonProperties;
import com.galaxy.lemon.framework.data.DTOMessageConverter;
import com.galaxy.lemon.framework.data.DataOperationPrivilegeCheckerPostProcessor;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataMessageConverter;
import com.galaxy.lemon.framework.data.instantiator.AggregatedDataInstantiator;
import com.galaxy.lemon.framework.data.instantiator.GenericDTOInstantiator;
import com.galaxy.lemon.framework.data.instantiator.LemonDataInstantiator;
import com.galaxy.lemon.framework.data.instantiator.RelaxedAggregatedDataInstantiator;
import com.galaxy.lemon.framework.data.interceptor.EnableInitialLemonData;
import com.galaxy.lemon.framework.data.support.DTOMessageConverterSupport;
import com.galaxy.lemon.framework.data.support.DefaultLemonDataMessageConverter;
import com.galaxy.lemon.framework.data.support.OnlyInstantiationContextLemonDataInitializer;
import com.galaxy.lemon.framework.i18n.DefaultLocaleMessageSource;
import com.galaxy.lemon.framework.i18n.LocaleMessageSource;
import com.galaxy.lemon.framework.interceptor.support.WebRequestLemonDataInitializer;
import com.galaxy.lemon.framework.jackson.ObjectMapperObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
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
@EnableConfigurationProperties({LemonProperties.class, CoreProperties.class})
@ConditionalOnClass(LemonEnvironment.class)
@Import({ObjectMapperConfiguration.class, LoggingConfiguration.class, ResponseMessageConfiguration.class, SystemStatusConfiguration.class, MetricsConfiguration.class})
@AutoConfigureAfter(CommonAutoConfiguration.class)
public class CoreAutoConfiguration {
    public final static String BATCH_SCHEDULED = "com.galaxy.lemon.framework.schedule.batch.BatchScheduled";
    public final static String SCHEDULED = "org.springframework.scheduling.annotation.Scheduled";

    @Configuration
    @EnableInitialLemonData(supportAnnotationClassNames={CoreAutoConfiguration.BATCH_SCHEDULED}, proxyTargetClass= false)
    @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false", matchIfMissing = false)
    public static class JdkDynamicAutoProxyConfiguration {

    }

    @Configuration
    @EnableInitialLemonData(supportAnnotationClassNames={CoreAutoConfiguration.BATCH_SCHEDULED}, proxyTargetClass= true)
    @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true", matchIfMissing = true)
    public static class CglibAutoProxyConfiguration {

    }

    @Bean
    @ConditionalOnClass(DefaultLocaleMessageSource.class)
    @ConditionalOnMissingBean
    public LocaleMessageSource localeMessageSource(MessageSource messageSource) {
        return new DefaultLocaleMessageSource(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public LemonEnvironment lemonEnvironment(LemonProperties lemonConfig) {
        return new LemonEnvironment(lemonConfig);
    }

    @Configuration
    @ConditionalOnClass(ObjectMapperObjectCodec.class)
    @ConditionalOnBean(ObjectMapper.class)
    public static class ObjectCodecConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ObjectCodec objectCodec(ObjectMapper objectMapper) {
            return new ObjectMapperObjectCodec(objectMapper);
        }

        @Bean
        @ConditionalOnMissingBean(name = "objectEncoder")
        public ObjectEncoder objectEncoder(ObjectCodec objectCodec) {
            return objectCodec;
        }

        @Bean
        @ConditionalOnMissingBean(name = "objectDecoder")
        public ObjectDecoder objectDecoder(ObjectCodec objectCodec) {
            return objectCodec;
        }
    }

    @Configuration
    @Import(DataOperationPrivilegeCheckerPostProcessor.Registry.class)
    public static class LemonDataConfiguration {

        @Bean
        public InternalDataHelper internalDataHelper(AggregatedDataInstantiator aggregatedDataInstantiator) {
            return new InternalDataHelper(aggregatedDataInstantiator);
        }

        @Bean
        public DTOMessageConverter dtoMessageConverter(ObjectCodec objectCodec,
                                                       GenericDTOInstantiator genericDTOInstantiator,
                                                       InternalDataHelper internalDataHelper) {
            return new DTOMessageConverterSupport(objectCodec, genericDTOInstantiator, internalDataHelper);
        }

        @Bean
        public LemonDataMessageConverter lemonDataMessageConverter(LemonDataInstantiator lemonDataInstantiator,
                                                                   ObjectCodec objectCodec) {
            return new DefaultLemonDataMessageConverter(lemonDataInstantiator, objectCodec);
        }
    }

    @Configuration
    public static class DataInstantiationConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public AggregatedDataInstantiator aggregatedDataInstantiator(CoreProperties coreProperties) {
            return new RelaxedAggregatedDataInstantiator(coreProperties);
        }

    }

    @Configuration
    public static class LemonDataInitializationConfiguration {
        @Bean
        @ConditionalOnMissingBean(name = "onlyInstantiationContextLemonDataInitializer")
        public OnlyInstantiationContextLemonDataInitializer onlyInstantiationContextLemonDataInitializer(AggregatedDataInstantiator aggregatedDataInstantiator) {
            return new OnlyInstantiationContextLemonDataInitializer(aggregatedDataInstantiator);
        }

        @Bean
        @ConditionalOnMissingBean(name = "lemonDataInitializer")
        @ConditionalOnMissingClass("com.galaxy.lemon.gateway.GatewayBootApplication")
        public WebRequestLemonDataInitializer lemonDataInitializer(AggregatedDataInstantiator aggregatedDataInstantiator) {
            return new WebRequestLemonDataInitializer(aggregatedDataInstantiator);
        }

    }

}
