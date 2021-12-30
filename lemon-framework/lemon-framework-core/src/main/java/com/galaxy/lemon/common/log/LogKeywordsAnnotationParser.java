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

package com.galaxy.lemon.common.log;

import com.galaxy.lemon.common.LemonFramework;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LogKeywordsAnnotationParser implements MergedBeanDefinitionPostProcessor, BeanFactoryPostProcessor {

    private LogKeywordsAnnotationReader logKeywordsAnnotationReader;
    private ClassFilter logKeywordsReaderClassFilter;

    public LogKeywordsAnnotationParser() {
        this.logKeywordsAnnotationReader = new LogKeywordsAnnotationReader();
        this.logKeywordsReaderClassFilter = new DefaultClassFilter();

    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (this.logKeywordsReaderClassFilter.match(beanType)) {
            this.logKeywordsAnnotationReader.read(beanType);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.addBeanPostProcessor(this);
    }

    public Map<Class<?>, Map<Method, LogKeywordsAnnotationReader.LogKeywordsMetadata>> getAllMethodLogKeywordsMetadata() {
        return this.logKeywordsAnnotationReader.getMethodLogKeywordsMetadataMap();
    }

    public Map<Class<?>, Map<String, LogKeywordsAnnotationReader.LogKeywordsMetadata>> getAllNameLogKeywordsMetadata() {
        return this.logKeywordsAnnotationReader.getNameLogKeywordsMetadataMap();
    }

    /**
     * class filter
     */
    public interface ClassFilter {
        boolean match(Class<?> clazz);

        class True implements ClassFilter {

            @Override
            public boolean match(Class<?> clazz) {
                return true;
            }
        }
    }

    public class DefaultClassFilter implements ClassFilter {

        @Override
        public boolean match(Class<?> clazz) {
            String packageName = Optional.ofNullable(clazz).map(Class::getPackage).map(Package::getName).orElse(null);
            return null == packageName ? false : packageName.startsWith(LemonFramework.getScanPackage());
        }
    }
}
