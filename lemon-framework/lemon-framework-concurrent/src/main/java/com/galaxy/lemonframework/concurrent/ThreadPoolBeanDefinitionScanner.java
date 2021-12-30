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

import com.galaxy.lemon.common.scanner.ClassPathClassFileScanner;
import com.galaxy.lemon.common.utils.ClassUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.StringValueResolver;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see EnableConcurrent
 * @see EnableThreadPool
 * @see ThreadPoolRegistrar
 * @since 1.0.0
 */

public class ThreadPoolBeanDefinitionScanner extends ClassPathClassFileScanner {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolBeanDefinitionScanner.class);
    private static Class<?> annotationClass = EnableThreadPool.class;
    private static String annotationClassNames = annotationClass.getName();
    private BeanDefinitionRegistry registry;
    private Environment environment;

    public ThreadPoolBeanDefinitionScanner(ResourceLoader resourceLoader, BeanDefinitionRegistry registry, Environment environment) {
        super(resourceLoader);
        this.registry = registry;
        this.environment = environment;
    }

    public void scan(String... basePackages) {
        logger.info("Scanning packages {} for register thread pool beans.", Arrays.toString(basePackages));
        scan((mr, mrf) -> doCallback(mr, mrf), basePackages);
    }

    private void doCallback(MetadataReader mr, MetadataReaderFactory mrf) {
        if (isCandidateClass(mr)) {
            Set<MethodMetadata> methodMetadataSet = mr.getAnnotationMetadata().getAnnotatedMethods(annotationClassNames);
            methodMetadataSet.stream().forEach(m -> {
                ThreadPoolMetadata threadPoolMetadata = new ThreadPoolMetadata(m.getAnnotationAttributes(annotationClassNames), str -> this.resolveStringValue(str));
                if (!this.registry.containsBeanDefinition(threadPoolMetadata.getName())) {
                    //(String name, int min, int max, long keepAliveTime, int queue)
                    RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(LemonThreadPool.class);
                    rootBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, threadPoolMetadata.getName());
                    rootBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, threadPoolMetadata.getCorePoolSize());
                    rootBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(2, threadPoolMetadata.getMaximumPoolSize());
                    rootBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(3, threadPoolMetadata.getKeepAliveTime());
                    rootBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(4, threadPoolMetadata.getQueueSize());
                    this.registry.registerBeanDefinition(threadPoolMetadata.getName(), rootBeanDefinition);
                    logger.info("Registered ThreadPool {} with name {}.", rootBeanDefinition, threadPoolMetadata.getName());
                } else {
                    try {
                        Class<?> threadPoolClass = ClassUtils.forName(this.registry.getBeanDefinition(threadPoolMetadata.getName()).getBeanClassName(), ThreadPoolBeanDefinitionScanner.class.getClassLoader());
                        if (! ThreadPoolExecutor.class.isAssignableFrom(threadPoolClass)) {
                            throw new IllegalStateException("The existing bean with name \""+threadPoolMetadata.getName()+"\" is not assignable from \"ThreadPoolExecutor\". ");
                        }
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                    logger.warn("The bean with name {} has been already exist.", threadPoolMetadata.getName());
                }
            });
        }
    }

    private String resolveStringValue(String value) {
        if (StringUtils.isBlank(value)) {
            throw new NullPointerException("LemonThreadPool param can't be blank.");
        }
        return this.environment.resolvePlaceholders(value);
    }

    private boolean isCandidateClass(MetadataReader mr) {
        return mr.getAnnotationMetadata().hasAnnotatedMethods(annotationClassNames);
    }

    static class ThreadPoolMetadata {
        private String name;

        private int corePoolSize;

        private int maximumPoolSize;

        private long keepAliveTime;

        private long queueSize;

        public ThreadPoolMetadata(Map<String, Object> annotationAttributes, StringValueResolver stringValueResolver) {
            this(
                    stringValueResolver.resolveStringValue((String)annotationAttributes.get("name")),
                    Integer.valueOf(stringValueResolver.resolveStringValue((String)annotationAttributes.get("corePoolSize"))),
                    Integer.valueOf(stringValueResolver.resolveStringValue((String)annotationAttributes.get("maximumPoolSize"))),
                    Long.valueOf(stringValueResolver.resolveStringValue((String)annotationAttributes.get("keepAliveTime"))),
                    Long.valueOf(stringValueResolver.resolveStringValue((String)annotationAttributes.get("queueSize")))

            );
        }

        public ThreadPoolMetadata(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, long queueSize) {
            this.name = name;
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
            this.queueSize = queueSize;
        }

        public String getName() {
            return name;
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public long getKeepAliveTime() {
            return keepAliveTime;
        }

        public long getQueueSize() {
            return queueSize;
        }
    }
}

