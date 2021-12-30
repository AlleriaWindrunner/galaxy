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

package com.galaxy.lemon.framework.autoconfigure.common;

import com.galaxy.lemon.common.exception.ConfigurableExceptionConversionService;
import com.galaxy.lemon.common.exception.DefaultExceptionConversionService;
import com.galaxy.lemon.common.exception.ExceptionConversionService;
import com.galaxy.lemon.common.exception.ExceptionConverter;
import com.galaxy.lemon.common.extension.SPIInjectBeanPostProcessor;
import com.galaxy.lemon.common.extension.SpringExtensionLoader;
import com.galaxy.lemon.framework.autoconfigure.cache.CacheAutoConfiguration;
import com.galaxy.lemon.framework.autoconfigure.core.CoreAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

import java.util.List;
import java.util.Optional;

/**
 * lemon-common
 * lemon-framework-common
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(SpringExtensionLoader.class)
@Import({CommonInitializerPostProcessor.Registrar.class})
@AutoConfigureBefore({CoreAutoConfiguration.class, CacheAutoConfiguration.class})
public class CommonAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(CommonAutoConfiguration.class);

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public SpringExtensionLoader extensionLoader() {
        return new SpringExtensionLoader();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static SPIInjectBeanPostProcessor spiInjectBeanPostProcessor() {
        return new SPIInjectBeanPostProcessor();
    }

    @Bean
    public ExceptionConversionService exceptionConversionService(ObjectProvider<List<ExceptionConverter>> exceptionConverters) {
        ConfigurableExceptionConversionService exceptionConversionService = new DefaultExceptionConversionService();
        List<ExceptionConverter> exceptionConverterList = exceptionConverters.getIfAvailable();
        Optional.ofNullable(exceptionConverterList).ifPresent(l -> {
            l.stream().forEach(exceptionConversionService::addConverter);
        });
        if (logger.isInfoEnabled()) {
            StringBuilder convertersLog = new StringBuilder();
            ((DefaultExceptionConversionService)exceptionConversionService).getExceptionConverters().stream().forEach(c -> convertersLog.append(c).append(","));
            logger.info("Exception converter ==> {}", convertersLog.toString());
        }
        return exceptionConversionService;
    }

}
