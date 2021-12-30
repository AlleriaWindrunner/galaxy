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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.Validate;

/**
 * 分布式锁Aspect
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@Aspect
public class DistributedLockerAspect implements Ordered {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLockerAspect.class);
    
    @Autowired
    private DistributedLocker distributedLocker;

    @Around("@annotation(locked)")
    public void lock(ProceedingJoinPoint pjp, Locked locked) {
        doLock(readMetadata(locked), pjp);
    }

    @Around("@annotation(distributedLocked)")
    public void distributedLock(ProceedingJoinPoint pjp, DistributedLocked distributedLocked) {
        doLock(readMetadata(distributedLocked), pjp);
    }

    private void doLock(LockMetadata lockMetadata, ProceedingJoinPoint pjp) {
        Validate.notEmpty(lockMetadata.getLockName());
        try {
            distributedLocker.lock(lockMetadata.getLockName(), lockMetadata.getLeaseTime(), lockMetadata.getWaitTime(),
                    () -> proceed(pjp));
        } catch (UnableToAcquireLockException e) {
            if(!(lockMetadata.isIgnoreUnableToAcquiredLockException() || lockMetadata.isIgnoreException())) {
                throw e;
            }
            if(logger.isWarnEnabled()) {
                logger.warn("Failed to acquire distributed lock with name {}, lease time {}, wait time {} at method {}",
                        lockMetadata.getLockName(), lockMetadata.getLeaseTime(), lockMetadata.getWaitTime(), pjp.getSignature().getName());
            }
        } catch (LemonException e) {
            if(! lockMetadata.isIgnoreException()) {
                throw e;
            }
            if(logger.isWarnEnabled()) {
                logger.warn("Failed to executing method \""+pjp.getSignature().getName()+"\".", e);
            }
        } catch (Throwable e) {
            if(! lockMetadata.isIgnoreException()) {
                LemonException.throwLemonException(e);
            }
            if(logger.isWarnEnabled()) {
                logger.warn("Unexpected throwable occurred at executing method \""+pjp.getSignature().getName()+"\".", e);
            }
        }
    }
    
    public Object proceed(ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (Throwable e) {
            throw LemonException.create(e);
        }
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE - 10;
    }

    private LockMetadata readMetadata(DistributedLocked distributedLocked) {
        return new LockMetadata(distributedLocked.lockName(), distributedLocked.waitTime(), distributedLocked.leaseTime(), distributedLocked.ignoreException(), distributedLocked.ignoreUnableToAcquiredLockException());
    }

    private LockMetadata readMetadata(Locked distributedLocked) {
        return new LockMetadata(distributedLocked.lockName(), distributedLocked.waitTime(), distributedLocked.leaseTime(), distributedLocked.ignoreException(), distributedLocked.ignoreUnableToAcquiredLockException());
    }

    public static class LockMetadata {
        /**
         * 锁名
         */
        private String lockName;
        /**
         * 等待锁超时时间，单位：秒
         * 默认30s
         */
        private int waitTime;

        /**
         * 自动解锁时间，单位秒
         * 自动解锁时间一定得大于方法执行时间，否则会导致锁提前释放
         * 默认100s
         */
        private int leaseTime;

        /**
         * 忽略所有异常，否则会往外抛
         */
        private boolean ignoreException;

        /**
         * 忽略没有获取到锁的异常，默认为true
         *
         */
        private boolean ignoreUnableToAcquiredLockException;

        public LockMetadata(String lockName, int waitTime, int leaseTime, boolean ignoreException, boolean ignoreUnableToAcquiredLockException) {
            this.lockName = lockName;
            this.waitTime = waitTime;
            this.leaseTime = leaseTime;
            this.ignoreException = ignoreException;
            this.ignoreUnableToAcquiredLockException = ignoreUnableToAcquiredLockException;
        }

        public String getLockName() {
            return lockName;
        }

        public void setLockName(String lockName) {
            this.lockName = lockName;
        }

        public int getWaitTime() {
            return waitTime;
        }

        public void setWaitTime(int waitTime) {
            this.waitTime = waitTime;
        }

        public int getLeaseTime() {
            return leaseTime;
        }

        public void setLeaseTime(int leaseTime) {
            this.leaseTime = leaseTime;
        }

        public boolean isIgnoreException() {
            return ignoreException;
        }

        public void setIgnoreException(boolean ignoreException) {
            this.ignoreException = ignoreException;
        }

        public boolean isIgnoreUnableToAcquiredLockException() {
            return ignoreUnableToAcquiredLockException;
        }

        public void setIgnoreUnableToAcquiredLockException(boolean ignoreUnableToAcquiredLockException) {
            this.ignoreUnableToAcquiredLockException = ignoreUnableToAcquiredLockException;
        }
    }
}
