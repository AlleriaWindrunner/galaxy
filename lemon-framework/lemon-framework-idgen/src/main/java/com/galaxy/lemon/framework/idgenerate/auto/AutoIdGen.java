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

package com.galaxy.lemon.framework.idgenerate.auto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.galaxy.lemon.framework.idgenerate.auto.IdGenStrategy;
import org.springframework.core.annotation.AliasFor;

/**
 * 注解DO ID property
 * 生成ID值并自动赋值
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Deprecated
public @interface AutoIdGen {
    /**
     * Id store key in redis/memcache/database etc..
     * @return
     */
    @AliasFor("key")
    String value() default "";
    
    /**
     * Id store key in redis/memcache/database etc..
     * @return
     */
    @AliasFor("value")
    String key() default "";
    
    /**
     * Id前缀
     * 
     * @return
     */
    String prefix() default "";
    
    /**
     * Id 生成策略
     * @return
     */
    Class<? extends IdGenStrategy> idGenStrategy() default DefaultIdGenStrategy.class;
}
