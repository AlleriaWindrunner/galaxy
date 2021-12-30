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

package com.galaxy.lemon.framework.stream;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Source {

    /**
     *
     * @return
     */
    @AliasFor("output")
    String value() default "";

    /**
     * binding name (eg.. channel)
     * @return
     */
    @AliasFor("value")
    String output() default "";

    /**
     *
     * the spring bean name of message listener handler
     * @return
     */
    String handlerBeanName();

    /**
     * Unique name that the binding belongs to (applies to producer only).
     * A empty String value indicates that binding with exchange and queue are not created, consumer must be creating binding before producer using it.
     * @return
     */
    String group() default "";

    /**
     *
     * @return
     */
    String prefix() default "";
}
