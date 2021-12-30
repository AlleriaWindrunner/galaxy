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

import java.util.function.Supplier;

/**
 * 分布式锁
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface DistributedLocker {
    /**
     * 获取锁
     * @param lockName 锁名
     * @param supplier 获取锁后的回调
     * @return callback 返回数据
     * @throws UnableToAcquireLockException
     * @throws Exception
     */
    <T> T lock(String lockName, Supplier<T> supplier) throws UnableToAcquireLockException;

    /**
     * 获取锁
     * @param lockName
     * @param supplier
     * @param leaseTime 锁到期自动解锁时间 * 自动解锁时间，单位秒；
     * 自动解锁时间一定得大于方法执行时间，否则会导致锁提前释放 
     * @param waitTime 等待锁时间
     * @return
     * @throws UnableToAcquireLockException
     * @throws Exception
     */
    <T> T lock(String lockName, int leaseTime, int waitTime, Supplier<T> supplier) throws UnableToAcquireLockException;
}
