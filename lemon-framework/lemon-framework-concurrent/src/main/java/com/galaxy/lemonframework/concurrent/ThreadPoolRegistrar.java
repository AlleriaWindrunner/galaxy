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

package com.galaxy.lemonframework.concurrent;

import com.galaxy.lemon.common.utils.CommonUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see EnableThreadPool
 * @since 1.0.0
 */

public class ThreadPoolRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private static final String PREFIX_PROPERTY_THREAD_POOL = "lemon.concurrent.threadPools";
    private Environment environment;
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableConcurrent.class.getName()));
        registerAnnotationThreadPool(importingClassMetadata, annotationAttributes, registry);
    }

    /**
     * yml configuration
     * @param registry
     */
    private void registerThreadPool(BeanDefinitionRegistry registry) {
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(this.environment, PREFIX_PROPERTY_THREAD_POOL);
        Map<String, Object> threadPools =  propertyResolver.getSubProperties(".");
        if (CommonUtils.isEmpty(threadPools)) {

        }

    }

    /**
     * @see EnableThreadPool
     * @param importingClassMetadata
     * @param annotationAttributes
     * @param registry
     */
    private void registerAnnotationThreadPool(AnnotationMetadata importingClassMetadata, AnnotationAttributes annotationAttributes, BeanDefinitionRegistry registry) {
        String defaultBasePackage = ClassUtils.getPackageName(importingClassMetadata.getClassName());
        ThreadPoolBeanDefinitionScanner threadPoolBeanDefinitionScanner = new ThreadPoolBeanDefinitionScanner(this.resourceLoader, registry, this.environment);
        threadPoolBeanDefinitionScanner.scan(CommonUtils.isEmpty(annotationAttributes.getStringArray("basePackages")) ? new String[]{defaultBasePackage} : annotationAttributes.getStringArray("basePackages"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
