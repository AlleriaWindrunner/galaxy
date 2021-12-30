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

import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.common.utils.Validate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redison实现 分布式锁
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RedisDistributedLocker implements DistributedLocker, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLocker.class);
    
    private final static String LOCKER_PREFIX = "LOCK:";
    
    private Integer defaultLeaseTime;
    
    private Integer defaultWaitTime;
    
    private RedissonClient redissonClient;
    
    /**
     * @param redissonClient    redisson client
     * @param defaultLeaseTime  默认自动解锁时间
     * @param defaultWaitTime   默认的获取锁等待时间
     */
    public RedisDistributedLocker(RedissonClient redissonClient,
                                  Integer defaultLeaseTime,
                                  Integer defaultWaitTime) {
        this.redissonClient = redissonClient;
        this.defaultLeaseTime = defaultLeaseTime;
        this.defaultWaitTime = defaultWaitTime;
    }
    
    @Override
    public <T> T lock(String lockName, Supplier<T> supplier)
            throws UnableToAcquireLockException {
        return lock(lockName, this.defaultLeaseTime, defaultWaitTime, supplier);
    }

    @Override
    public <T> T lock(String lockName, int leaseTime, int waitTime, Supplier<T> supplier)
            throws UnableToAcquireLockException {
        RLock lock = redissonClient.getLock(LOCKER_PREFIX + lockName);
        Instant startInstant = Instant.now();
        boolean acquired;
        try {
            acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new UnableToAcquireLockException(e);
        }

        if (acquired) {
            if(logger.isDebugEnabled()) {
                logger.debug("Acquired distributed lock {}, wait time {}/ms. ", lockName, DateTimeUtils.durationMillis(startInstant, Instant.now()));
            }
            try {
                return supplier.get();
            } finally {
                lock.unlock();
                if(logger.isDebugEnabled()) {
                    logger.debug("Release distributed lock {}. used lock time {}/ms", lockName, DateTimeUtils.durationMillis(startInstant, Instant.now()));
                }
            }
        }
        throw new UnableToAcquireLockException();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(this.defaultLeaseTime, "Default expire time must not be null, please configure with key \"lemon.lock.defaultLeaseTime\"");
        Validate.notNull(this.defaultWaitTime, "Default expire time must not be null, please configure with key \"lemon.lock.defaultWaitTime\"");
        Validate.notNull(this.redissonClient, "Redisson client can not be null.");
    }

}
