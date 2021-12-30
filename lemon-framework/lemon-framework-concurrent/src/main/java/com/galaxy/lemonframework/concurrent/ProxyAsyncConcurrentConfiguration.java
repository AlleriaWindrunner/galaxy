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

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see AsyncConcurrent
 * @see EnableConcurrent
 * @since 1.0.0
 */

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyAsyncConcurrentConfiguration implements ImportAware {

    public static final String ASYNC_CONCURRENT_ANNOTATION_PROCESSOR_BEAN_NAME = "asyncConcurrentAdvisor";

    protected AnnotationAttributes enableConcurrent;

    protected AsyncUncaughtExceptionHandler exceptionHandler;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableConcurrent = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableConcurrent.class.getName(), false));
        if (this.enableConcurrent == null) {
            throw new IllegalArgumentException(
                    "@EnableConcurrent is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean(name = ASYNC_CONCURRENT_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AsyncConcurrentAnnotationBeanPostProcessor asyncConcurrentAdvisor() {
        Assert.notNull(this.enableConcurrent, "@EnableConcurrent annotation metadata was not injected");
        AsyncConcurrentAnnotationBeanPostProcessor bpp = new AsyncConcurrentAnnotationBeanPostProcessor();
        if (this.exceptionHandler != null) {
            bpp.setExceptionHandler(this.exceptionHandler);
        }
        bpp.setProxyTargetClass(this.enableConcurrent.getBoolean("proxyTargetClass"));
        bpp.setOrder(this.enableConcurrent.<Integer>getNumber("order"));
        return bpp;
    }

    @Autowired(required = false)
    void setConfigurers(Collection<AsyncConcurrentConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException("Only one AsyncConfigurer may exist");
        }
        AsyncConcurrentConfigurer configurer = configurers.iterator().next();
        this.exceptionHandler = configurer.getAsyncUncaughtExceptionHandler();
    }
}
