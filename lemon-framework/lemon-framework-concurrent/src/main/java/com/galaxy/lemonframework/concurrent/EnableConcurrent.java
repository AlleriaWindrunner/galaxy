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

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;

import java.lang.annotation.*;

/**
 * enable concurrent
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ThreadPoolRegistrar.class, ProxyAsyncConcurrentConfiguration.class})
public @interface EnableConcurrent {

    /**
     *
     * @return
     */
    String[] basePackages() default {};

    /**
     * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
     * to standard Java interface-based proxies.
     * <p><strong>Applicable only if the {@link #mode} is set to {@link AdviceMode#PROXY}</strong>.
     * <p>The default is {@code false}.
     * <p>Note that setting this attribute to {@code true} will affect <em>all</em>
     * Spring-managed beans requiring proxying, not just those marked with {@code @AsyncConcurrent}.
     * For example, other beans marked with Spring's {@code @Transactional} annotation
     * will be upgraded to subclass proxying at the same time. This approach has no
     * negative impact in practice unless one is explicitly expecting one type of proxy
     * vs. another &mdash; for example, in tests.
     */
    boolean proxyTargetClass() default false;

    /**
     * Indicate how async advice should be applied.
     * <p><b>The default is {@link AdviceMode#PROXY}.</b>
     * Please note that proxy mode allows for interception of calls through the proxy
     * only. Local calls within the same class cannot get intercepted that way; an
     * {@link AsyncConcurrent} annotation on such a method within a local call will be ignored
     * since Spring's interceptor does not even kick in for such a runtime scenario.
     * For a more advanced mode of interception, consider switching this to
     * {@link AdviceMode#ASPECTJ}.
     */
    AdviceMode mode() default AdviceMode.PROXY;

    /**
     * Indicate the order in which the {@link AsyncAnnotationBeanPostProcessor}
     * should be applied.
     * <p>The default is {@link Ordered#LOWEST_PRECEDENCE} in order to run
     * after all other post-processors, so that it can add an advisor to
     * existing proxies rather than double-proxy.
     */
    int order() default Ordered.LOWEST_PRECEDENCE;
}
