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

package com.galaxy.lemon.framework.mybatis;

import com.galaxy.lemon.common.IndicatePackages;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.utils.JudgeUtils;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * lemon framework @Mapper scanner
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonMapperScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final Logger logger = LoggerFactory.getLogger(LemonMapperScannerRegistrar.class);

    protected static final String BEAN_NAME_LEMON_MYBATIS_SCANNER_PACKAGES = "lemonMybatisScannerPackages";
    protected static final String[] DEFAULT_LEMON_BASE_PACKAGE = new String[]{LemonConstants.FRAMEWORK_BASE_PACKAGE};


    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        List<String> packages = Arrays.asList(DEFAULT_LEMON_BASE_PACKAGE);
        if (this.beanFactory != null) {
            try {
                IndicatePackages indicatePackages = this.beanFactory.getBean(BEAN_NAME_LEMON_MYBATIS_SCANNER_PACKAGES, IndicatePackages.class);
                if (JudgeUtils.isNotNull(indicatePackages)) {
                    packages = indicatePackages.get();
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }

        if (JudgeUtils.isEmpty(packages)) {
            packages = Arrays.asList(DEFAULT_LEMON_BASE_PACKAGE);
        }
        logger.debug("Searching for mappers annotated with @Mapper for lemon environment");

        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        if (this.resourceLoader != null) {
            scanner.setResourceLoader(this.resourceLoader);
        }

        if (logger.isDebugEnabled()) {
            for (String pkg : packages) {
                logger.debug("Using lemon framework indicate base package '{}'", pkg);
            }
        }

        scanner.setAnnotationClass(Mapper.class);
        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(packages));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
