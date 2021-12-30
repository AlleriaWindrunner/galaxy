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

package com.galaxy.lemon.framework.lock;

import java.lang.annotation.*;

/**
 * 标注一个方法，表示该方法会用分布式锁锁定
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DistributedLocked {
    /**
     * 锁名
     * @return
     */
    String lockName() default "";
    /**
     * 等待锁超时时间，单位：秒
     * 默认30s
     * @return
     */
    int waitTime() default 30;

    /**
     * 自动解锁时间，单位秒
     * 自动解锁时间一定得大于方法执行时间，否则会导致锁提前释放
     * 默认100s
     * @return
     */
    int leaseTime() default 100;

    /**
     * 忽略所有异常，否则会往外抛
     * @return
     */
    boolean ignoreException() default false;

    /**
     * 忽略没有获取到锁的异常，默认为true
     *
     * @return
     */
    boolean ignoreUnableToAcquiredLockException() default true;
}
