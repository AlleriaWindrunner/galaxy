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

package com.galaxy.lemon.framework.autoconfigure.idgen.redis;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({ JedisConnection.class, RedisOperations.class, Jedis.class })
@EnableConfigurationProperties(IdGeneratorRedisProperties.class)
public class IdGeneratorRedisConfiguration {

    /**
     * Redis connection configuration.
     */
    @Configuration
    @ConditionalOnClass(GenericObjectPool.class)
    @Conditional(IdGeneratorExclusiveRedisCondition.class)
    public static class IdGeneratorRedisConnectionConfiguration {

        @Autowired
        private IdGeneratorRedisProperties properties;

        @Bean
        @ConditionalOnMissingBean(name = "idGeneratorRedisConnectionFactory")
        public JedisConnectionFactory idGeneratorRedisConnectionFactory()
                throws UnknownHostException {
            return applyProperties(createJedisConnectionFactory());
        }

        @Bean
        public RedisTemplate<String, Long> idgenRedisTemplate() throws UnknownHostException {
            RedisTemplate<String, Long> template =  new RedisTemplate<>();
            template.setConnectionFactory(idGeneratorRedisConnectionFactory());
            template.setKeySerializer( new StringRedisSerializer() );
            template.setHashValueSerializer( new GenericToStringSerializer<>( Long.class ) );
            template.setValueSerializer( new GenericToStringSerializer<>( Long.class ) );
            return template;
        }

        protected final JedisConnectionFactory applyProperties(
                JedisConnectionFactory factory) {
            configureConnection(factory);
            if (this.properties.isSsl()) {
                factory.setUseSsl(true);
            }
            factory.setDatabase(this.properties.getDatabase());
            if (this.properties.getTimeout() > 0) {
                factory.setTimeout(this.properties.getTimeout());
            }
            return factory;
        }

        private void configureConnection(JedisConnectionFactory factory) {
            if (StringUtils.hasText(this.properties.getUrl())) {
                configureConnectionFromUrl(factory);
            }
            else {
                factory.setHostName(this.properties.getHost());
                factory.setPort(this.properties.getPort());
                if (this.properties.getPassword() != null) {
                    factory.setPassword(this.properties.getPassword());
                }
            }
        }

        private void configureConnectionFromUrl(JedisConnectionFactory factory) {
            String url = this.properties.getUrl();
            if (url.startsWith("rediss://")) {
                factory.setUseSsl(true);
            }
            try {
                URI uri = new URI(url);
                factory.setHostName(uri.getHost());
                factory.setPort(uri.getPort());
                if (uri.getUserInfo() != null) {
                    String password = uri.getUserInfo();
                    int index = password.lastIndexOf(":");
                    if (index >= 0) {
                        password = password.substring(index + 1);
                    }
                    factory.setPassword(password);
                }
            }
            catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url,
                        ex);
            }
        }

        protected final RedisSentinelConfiguration getSentinelConfig() {
            IdGeneratorRedisProperties.Sentinel sentinelProperties = this.properties.getSentinel();
            if (sentinelProperties != null) {
                RedisSentinelConfiguration config = new RedisSentinelConfiguration();
                config.master(sentinelProperties.getMaster());
                config.setSentinels(createSentinels(sentinelProperties));
                return config;
            }
            return null;
        }

        /**
         * Create a {@link RedisClusterConfiguration} if necessary.
         * @return {@literal null} if no cluster settings are set.
         */
        protected final RedisClusterConfiguration getClusterConfiguration() {
            if (this.properties.getCluster() == null) {
                return null;
            }
            IdGeneratorRedisProperties.Cluster clusterProperties = this.properties.getCluster();
            RedisClusterConfiguration config = new RedisClusterConfiguration(
                    clusterProperties.getNodes());

            if (clusterProperties.getMaxRedirects() != null) {
                config.setMaxRedirects(clusterProperties.getMaxRedirects());
            }
            return config;
        }

        private List<RedisNode> createSentinels(IdGeneratorRedisProperties.Sentinel sentinel) {
            List<RedisNode> nodes = new ArrayList<>();
            for (String node : StringUtils
                    .commaDelimitedListToStringArray(sentinel.getNodes())) {
                try {
                    String[] parts = StringUtils.split(node, ":");
                    Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                    nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
                }
                catch (RuntimeException ex) {
                    throw new IllegalStateException(
                            "Invalid redis sentinel " + "property '" + node + "'", ex);
                }
            }
            return nodes;
        }

        private JedisConnectionFactory createJedisConnectionFactory() {
            JedisPoolConfig poolConfig = this.properties.getPool() != null
                    ? jedisPoolConfig() : new JedisPoolConfig();

            if (getSentinelConfig() != null) {
                return new JedisConnectionFactory(getSentinelConfig(), poolConfig);
            }
            if (getClusterConfiguration() != null) {
                return new com.galaxy.lemon.framework.jedis.JedisConnectionFactory(getClusterConfiguration(), poolConfig);
                //return new JedisConnectionFactory(getClusterConfiguration(), poolConfig);
            }
            return new JedisConnectionFactory(poolConfig);
        }

        private JedisPoolConfig jedisPoolConfig() {
            JedisPoolConfig config = new JedisPoolConfig();
            IdGeneratorRedisProperties.Pool props = this.properties.getPool();
            config.setMaxTotal(props.getMaxActive());
            config.setMaxIdle(props.getMaxIdle());
            config.setMinIdle(props.getMinIdle());
            config.setMaxWaitMillis(props.getMaxWait());
            return config;
        }

    }

}
