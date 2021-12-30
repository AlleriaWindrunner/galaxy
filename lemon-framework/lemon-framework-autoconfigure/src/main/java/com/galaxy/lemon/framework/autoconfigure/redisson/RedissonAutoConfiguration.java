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

package com.galaxy.lemon.framework.autoconfigure.redisson;

import com.galaxy.lemon.common.utils.JudgeUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.classreading.MethodMetadataReadingVisitor;

import static com.galaxy.lemon.common.utils.NumberUtils.getDefaultIfNull;

/**
 * redisson configuration
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(value = "lemon.redisson.enabled", matchIfMissing = true)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration {
    
    private static final String REDISSON_SINGLE_PREFIX = "redis://";
    private static final int DEFAULT_DATABASE = 0;
    private static final int DEFAULT_POOL_SIZE = 20;
    private static final int DEFAULT_IDLE_SIZE = 5;
    private static final int DEFAULT_IDLE_TIMEOUT = 60000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
    private static final int DEFAULT_TIMEOUT = 10000;
    
    @Autowired
    private RedissonProperties redissonProperties;
    
    @Bean
    @ConditionalOnMissingBean(Config.class)
    @Conditional(RedissonServerModeCondition.class)
    public Config singleConfig() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_SINGLE_PREFIX + redissonProperties.getAddress());
        if (JudgeUtils.isNotEmpty(redissonProperties.getPassword())) {
            config.useSingleServer().setPassword(redissonProperties.getPassword());
        }
        config.useSingleServer().setDatabase(getDefaultIfNull(redissonProperties.getDatabase(), DEFAULT_DATABASE));
        config.useSingleServer().setConnectionPoolSize(getDefaultIfNull(redissonProperties.getPoolSize(), DEFAULT_POOL_SIZE));
        config.useSingleServer().setConnectionMinimumIdleSize(getDefaultIfNull(redissonProperties.getIdleSize(), DEFAULT_IDLE_SIZE));
        config.useSingleServer().setIdleConnectionTimeout(getDefaultIfNull(redissonProperties.getConnectionTimeout(), DEFAULT_IDLE_TIMEOUT));
        config.useSingleServer().setConnectTimeout(getDefaultIfNull(redissonProperties.getConnectionTimeout(), DEFAULT_CONNECTION_TIMEOUT));
        config.useSingleServer().setTimeout(getDefaultIfNull(redissonProperties.getTimeout(), DEFAULT_TIMEOUT));
        return config;
    }

    @Bean
    @ConditionalOnMissingBean(Config.class)
    @Conditional(RedissonServerModeCondition.class)
    public Config masterSlaveConfig() {
        Config config = new Config();
        config.useMasterSlaveServers().setMasterAddress(redissonProperties.getMasterAddress());

        config.useMasterSlaveServers().addSlaveAddress(redissonProperties.getSlaveAddress());
        if (JudgeUtils.isNotEmpty(redissonProperties.getPassword())) {
            config.useMasterSlaveServers().setPassword(redissonProperties.getPassword());
        }
        config.useMasterSlaveServers().setDatabase(getDefaultIfNull(redissonProperties.getDatabase(), DEFAULT_DATABASE));
        config.useMasterSlaveServers().setMasterConnectionPoolSize(getDefaultIfNull(redissonProperties.getPoolSize(), DEFAULT_POOL_SIZE));
        config.useMasterSlaveServers().setMasterConnectionMinimumIdleSize(getDefaultIfNull(redissonProperties.getIdleSize(), DEFAULT_IDLE_SIZE));
        config.useMasterSlaveServers().setSlaveConnectionPoolSize(getDefaultIfNull(redissonProperties.getPoolSize(), DEFAULT_POOL_SIZE));
        config.useMasterSlaveServers().setSlaveConnectionMinimumIdleSize(getDefaultIfNull(redissonProperties.getIdleSize(), DEFAULT_IDLE_SIZE));
        config.useMasterSlaveServers().setIdleConnectionTimeout(getDefaultIfNull(redissonProperties.getConnectionTimeout(), DEFAULT_IDLE_TIMEOUT));
        config.useMasterSlaveServers().setConnectTimeout(getDefaultIfNull(redissonProperties.getConnectionTimeout(), DEFAULT_CONNECTION_TIMEOUT));
        config.useMasterSlaveServers().setTimeout(getDefaultIfNull(redissonProperties.getTimeout(), DEFAULT_TIMEOUT));
        return config;
    }

    @Bean
    @ConditionalOnMissingBean(Config.class)
    @Conditional(RedissonServerModeCondition.class)
    public Config sentinelConfig() {
        Config config = new Config();
        config.useSentinelServers().setMasterName(redissonProperties.getMasterName());
        config.useSentinelServers().addSentinelAddress(redissonProperties.getSentinelAddress());
        if (JudgeUtils.isNotEmpty(redissonProperties.getPassword())) {
            config.useSentinelServers().setPassword(redissonProperties.getPassword());
        }
        config.useSentinelServers().setDatabase(getDefaultIfNull(redissonProperties.getDatabase(), DEFAULT_DATABASE));
        config.useSentinelServers().setMasterConnectionPoolSize(getDefaultIfNull(redissonProperties.getPoolSize(), DEFAULT_POOL_SIZE));
        config.useSentinelServers().setMasterConnectionMinimumIdleSize(getDefaultIfNull(redissonProperties.getIdleSize(), DEFAULT_IDLE_SIZE));
        config.useSentinelServers().setSlaveConnectionPoolSize(getDefaultIfNull(redissonProperties.getPoolSize(), DEFAULT_POOL_SIZE));
        config.useSentinelServers().setSlaveConnectionMinimumIdleSize(getDefaultIfNull(redissonProperties.getIdleSize(), DEFAULT_IDLE_SIZE));
        config.useSentinelServers().setIdleConnectionTimeout(getDefaultIfNull(redissonProperties.getConnectionTimeout(), DEFAULT_IDLE_TIMEOUT));
        config.useSentinelServers().setConnectTimeout(getDefaultIfNull(redissonProperties.getConnectionTimeout(), DEFAULT_CONNECTION_TIMEOUT));
        config.useSentinelServers().setTimeout(getDefaultIfNull(redissonProperties.getTimeout(), DEFAULT_TIMEOUT));
        return config;
    }

    @Bean
    @ConditionalOnMissingBean(Config.class)
    @Conditional(RedissonServerModeCondition.class)
    public Config clusterConfig() {
        Config config = new Config();
        config.useClusterServers().addNodeAddress(redissonProperties.getNodeAddress());
        if (JudgeUtils.isNotEmpty(redissonProperties.getPassword())) {
            config.useClusterServers().setPassword(redissonProperties.getPassword());
        }
        config.useClusterServers().setMasterConnectionPoolSize(getDefaultIfNull(redissonProperties.getPoolSize(), DEFAULT_POOL_SIZE));
        config.useClusterServers().setMasterConnectionMinimumIdleSize(getDefaultIfNull(redissonProperties.getIdleSize(), DEFAULT_IDLE_SIZE));
        config.useClusterServers().setSlaveConnectionPoolSize(getDefaultIfNull(redissonProperties.getPoolSize(), DEFAULT_POOL_SIZE));
        config.useClusterServers().setSlaveConnectionMinimumIdleSize(getDefaultIfNull(redissonProperties.getIdleSize(), DEFAULT_IDLE_SIZE));
        config.useClusterServers().setIdleConnectionTimeout(getDefaultIfNull(redissonProperties.getConnectionTimeout(), DEFAULT_IDLE_TIMEOUT));
        config.useClusterServers().setConnectTimeout(getDefaultIfNull(redissonProperties.getConnectionTimeout(), DEFAULT_CONNECTION_TIMEOUT));
        config.useClusterServers().setTimeout(getDefaultIfNull(redissonProperties.getTimeout(), DEFAULT_TIMEOUT));
        return config;
    }

    @Bean
    @ConditionalOnBean(Config.class)
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(Config config) {
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    public static class RedissonServerModeCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String mode = context.getEnvironment().getProperty("lemon.redisson.mode");
            if(StringUtils.isEmpty(mode)) {
                return false;
            }
            String methodName = ((MethodMetadataReadingVisitor)metadata).getMethodName().toLowerCase();
            return methodName.startsWith(mode.toLowerCase());
        }
        
    }
}
