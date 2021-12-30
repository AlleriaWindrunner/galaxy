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

import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.scheduling.annotation.AsyncAnnotationAdvisor;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class AsyncConcurrentAnnotationBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

    private AsyncUncaughtExceptionHandler exceptionHandler;

    public AsyncConcurrentAnnotationBeanPostProcessor() {
        setBeforeExistingAdvisors(true);
    }

    /**
     * Set the {@link AsyncUncaughtExceptionHandler} to use to handle uncaught
     * exceptions thrown by asynchronous method executions.
     * @since 4.1
     */
    public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        AsyncAnnotationAdvisor advisor = new AsyncAnnotationAdvisor(null, this.exceptionHandler);
        advisor.setAsyncAnnotationType(AsyncConcurrent.class);
        advisor.setBeanFactory(beanFactory);
        this.advisor = advisor;
    }
}
