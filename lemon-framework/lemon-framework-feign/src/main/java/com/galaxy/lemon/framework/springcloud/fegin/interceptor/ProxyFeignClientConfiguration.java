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

package com.galaxy.lemon.framework.springcloud.fegin.interceptor;

import com.galaxy.lemon.framework.remoting.RemoteInvocationDataProcessorComposite;
import com.galaxy.lemon.framework.springcloud.fegin.logging.FeignAccessLoggerAdapter;
import com.galaxy.lemon.framework.springcloud.fegin.interceptor.BeanFactoryFeignClientPointcutAdvisor;
import com.galaxy.lemon.framework.springcloud.fegin.interceptor.FeignClientInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class ProxyFeignClientConfiguration{
    private FeignAccessLoggerAdapter feignAccessLoggerAdapter;
    private RemoteInvocationDataProcessorComposite remoteInvocationDataProcessorComposite;

    public ProxyFeignClientConfiguration(FeignAccessLoggerAdapter feignAccessLoggerAdapter,
                                         RemoteInvocationDataProcessorComposite remoteInvocationDataProcessorComposite) {
        this.feignAccessLoggerAdapter = feignAccessLoggerAdapter;
        this.remoteInvocationDataProcessorComposite = remoteInvocationDataProcessorComposite;
    }

    @Bean
    public MethodInterceptor feignClientInterceptor() {
        return new FeignClientInterceptor(this.remoteInvocationDataProcessorComposite, this.feignAccessLoggerAdapter);
    }

    @Bean
    public AbstractPointcutAdvisor feignClientPointcutAdvisor() {
        BeanFactoryFeignClientPointcutAdvisor advisor = new BeanFactoryFeignClientPointcutAdvisor();
        advisor.setAdvice(feignClientInterceptor());
        advisor.setOrder(1);
        return advisor;
    }
}
