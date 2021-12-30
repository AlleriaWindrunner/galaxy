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

package com.galaxy.lemon.framework.data.interceptor;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 初始化LemonData
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface InitialLemonData {

    /**
     * spring bean name for LemonDataInitializer
     * @return
     */
    @AliasFor("value")
    String lemonDataInitializer() default "";

    /**
     * spring bean name for LemonDataInitializer
     * @return
     */
    @AliasFor("lemonDataInitializer")
    String value() default "";

    /**
     *
     * lemon data source
     *
     * @return
     */
    LemonDataSource source() default LemonDataSource.LEMON_DATA_INITIALIZER;


    /**
     *
     * required clear context after finishing trade
     *
     * @return
     */
    boolean requiredClearContext() default true;

    /**
     * lemon data Source
     */
    enum LemonDataSource {
        /**
         * {@link #lemonDataInitializer()}
         */
        LEMON_DATA_INITIALIZER,
        /**
         * 实例化lemonData
         *
         */
        LEMON_DATA_INSTANCE,
        /**
         * 简单初始化,设置entryTx,requestId
         */
        SIMPLE_LEMON_DATA_INITIALIZER,
        /**
         * 从被注解的方法的参数拷贝
         *
         */
        COPY_FROM_ARGUMENT,
        /**
         * 除entryTx,RequestId,MsgId，其他的同 {@code COPY_FROM_ARGUMENT}
         */
        COPY_SOME_FROM_ARGUMENT

    }
}
