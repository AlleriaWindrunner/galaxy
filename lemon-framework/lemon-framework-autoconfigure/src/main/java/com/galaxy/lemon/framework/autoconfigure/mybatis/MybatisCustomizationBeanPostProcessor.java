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

package com.galaxy.lemon.framework.autoconfigure.mybatis;

import com.galaxy.lemon.common.LemonFramework;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class MybatisCustomizationBeanPostProcessor implements BeanFactoryAware, BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MybatisCustomizationBeanPostProcessor.class);

    static final String TYPE_ALIASES_PATTERN = "**.entity";
    static final String MAPPER_LOCATIONS_PATTERN = "**/mapper/*.xml";
    static final String CLASS_PATH = "classpath*:";
    static final String PACKAGE_SEPARATOR = ".";

    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MybatisProperties) {
            defaultMybatisProperties((MybatisProperties) bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void defaultMybatisProperties(MybatisProperties mybatisProperties) {
        if (JudgeUtils.isNotNull(mybatisProperties)) {
            String basePackage = Optional.ofNullable(LemonFramework.getScanPackage()).orElseGet(() ->{
                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                if (logger.isDebugEnabled()) {
                    logger.debug("Using auto-configuration base package '{}'", Optional.ofNullable(packages).map(s -> s.stream().collect(Collectors.joining(","))).orElse(""));
                }
                return Optional.ofNullable(packages).map(p -> p.get(0)).orElse(null);
            });
            if (logger.isInfoEnabled()) {
                logger.info("Mybatis scanner base package '{}'", basePackage);
            }
            JudgeUtils.callbackIfNecessary(JudgeUtils.isBlank(mybatisProperties.getTypeAliasesPackage()), () -> mybatisProperties.setTypeAliasesPackage(Optional.ofNullable(basePackage).map(b -> b + PACKAGE_SEPARATOR + TYPE_ALIASES_PATTERN).orElse(TYPE_ALIASES_PATTERN)));
            JudgeUtils.callbackIfNecessary(JudgeUtils.isEmpty(mybatisProperties.getMapperLocations()), () -> mybatisProperties.setMapperLocations(new String[] {CLASS_PATH + Optional.ofNullable(basePackage).map(b -> b + PACKAGE_SEPARATOR).map(b -> StringUtils.replace(b, PACKAGE_SEPARATOR, File.separator)).map(b -> b + MAPPER_LOCATIONS_PATTERN).orElse(MAPPER_LOCATIONS_PATTERN)} ) );
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    static class Registrar implements ImportBeanDefinitionRegistrar {

        private static final String BEAN_NAME = "mybatisCustomizationBeanPostProcessor";

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                            BeanDefinitionRegistry registry) {
            if (!registry.containsBeanDefinition(BEAN_NAME)) {
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(MybatisCustomizationBeanPostProcessor.class);
                beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                // We don't need this one to be post processed otherwise it can cause a
                // cascade of bean instantiation that we would rather avoid.
                beanDefinition.setSynthetic(true);
                registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
            }
        }

    }

}
