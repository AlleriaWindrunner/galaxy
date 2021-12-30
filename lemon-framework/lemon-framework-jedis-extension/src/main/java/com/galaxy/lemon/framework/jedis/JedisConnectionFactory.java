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

package com.galaxy.lemon.framework.jedis;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.data.redis.connection.ClusterCommandExecutor;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class JedisConnectionFactory extends org.springframework.data.redis.connection.jedis.JedisConnectionFactory{
    /**
     * Constructs a new <code>JedisConnectionFactory</code> instance with default settings (default connection pooling, no
     * shard information).
     */
    public JedisConnectionFactory() {}

    /**
     * Constructs a new <code>JedisConnectionFactory</code> instance. Will override the other connection parameters passed
     * to the factory.
     *
     * @param shardInfo shard information
     */
    public JedisConnectionFactory(JedisShardInfo shardInfo) {
        super(shardInfo);
    }

    /**
     * Constructs a new <code>JedisConnectionFactory</code> instance using the given pool configuration.
     *
     * @param poolConfig pool configuration
     */
    public JedisConnectionFactory(JedisPoolConfig poolConfig) {
        super(poolConfig);
    }

    /**
     * Constructs a new {@link org.springframework.data.redis.connection.jedis.JedisConnectionFactory} instance using the given {@link JedisPoolConfig} applied to
     * {@link JedisSentinelPool}.
     *
     * @param sentinelConfig
     * @since 1.4
     */
    public JedisConnectionFactory(RedisSentinelConfiguration sentinelConfig) {
        this(sentinelConfig, null);
    }

    /**
     * Constructs a new {@link org.springframework.data.redis.connection.jedis.JedisConnectionFactory} instance using the given {@link JedisPoolConfig} applied to
     * {@link JedisSentinelPool}.
     *
     * @param sentinelConfig
     * @param poolConfig pool configuration. Defaulted to new instance if {@literal null}.
     * @since 1.4
     */
    public JedisConnectionFactory(RedisSentinelConfiguration sentinelConfig, JedisPoolConfig poolConfig) {
        super(sentinelConfig, poolConfig);
    }

    /**
     * Constructs a new {@link org.springframework.data.redis.connection.jedis.JedisConnectionFactory} instance using the given {@link RedisClusterConfiguration} applied
     * to create a {@link JedisCluster}.
     *
     * @param clusterConfig
     * @since 1.7
     */
    public JedisConnectionFactory(RedisClusterConfiguration clusterConfig) {
        super(clusterConfig);
    }

    /**
     * Constructs a new {@link org.springframework.data.redis.connection.jedis.JedisConnectionFactory} instance using the given {@link RedisClusterConfiguration} applied
     * to create a {@link JedisCluster}.
     *
     * @param clusterConfig
     * @since 1.7
     */
    public JedisConnectionFactory(RedisClusterConfiguration clusterConfig, JedisPoolConfig poolConfig) {
        super(clusterConfig, poolConfig);
    }

    /*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.connection.RedisConnectionFactory#getClusterConnection()
	 */
    @Override
    public RedisClusterConnection getClusterConnection() {
        RedisClusterConnection redisClusterConnection = super.getClusterConnection();
        DirectFieldAccessor dfa = new DirectFieldAccessor(redisClusterConnection);
        JedisCluster cluster = (JedisCluster)dfa.getPropertyValue("cluster");
        ClusterCommandExecutor executor = (ClusterCommandExecutor)dfa.getPropertyValue("clusterCommandExecutor");
        return new JedisClusterConnection(cluster, executor);
    }

}
