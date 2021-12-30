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

package com.galaxy.lemon.framework.autoconfigure.lock;

import com.galaxy.lemon.framework.autoconfigure.redisson.RedissonAutoConfiguration;
import com.galaxy.lemon.framework.lock.DistributedLocker;
import com.galaxy.lemon.framework.autoconfigure.lock.LockAutoConfiguration.LockProperties;
import com.galaxy.lemon.framework.lock.DistributedLockerAspect;
import com.galaxy.lemon.framework.lock.RedisDistributedLocker;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.galaxy.lemon.common.utils.NumberUtils.getDefaultIfNull;

/**
 * 分布式锁配置
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnBean(RedissonClient.class)
@EnableConfigurationProperties(LockProperties.class)
@AutoConfigureAfter({RedissonAutoConfiguration.class})
public class LockAutoConfiguration {
    public static final int DEFAULT_LEASE_TIME = 100;
    public static final int DEFAULT_WAIT_TIME = 30; 

    @Autowired
    private LockProperties lockProperties;

    @Bean
    @ConditionalOnMissingBean
    public DistributedLocker distributedLocker(RedissonClient redissonClient) {
        return new RedisDistributedLocker(redissonClient, getDefaultIfNull(this.lockProperties.getDefaultLeaseTime(), DEFAULT_LEASE_TIME),
                getDefaultIfNull(this.lockProperties.getDefaultWaitTime(), DEFAULT_WAIT_TIME));
    }

    @Configuration
    @ConditionalOnClass(DistributedLocker.class)
    @Import(DistributedLockerAspect.class)
    public static class LockAspectConfiguration {

    }
    
    @ConfigurationProperties(prefix = "lemon.lock")
    public static class LockProperties {
        private Integer defaultLeaseTime;
        private Integer defaultWaitTime;
        
        public Integer getDefaultLeaseTime() {
            return defaultLeaseTime;
        }
        public void setDefaultLeaseTime(Integer defaultLeaseTime) {
            this.defaultLeaseTime = defaultLeaseTime;
        }
        public Integer getDefaultWaitTime() {
            return defaultWaitTime;
        }
        public void setDefaultWaitTime(Integer defaultWaitTime) {
            this.defaultWaitTime = defaultWaitTime;
        }
        
    }
}
