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

package com.galaxy.lemon.framework.autoconfigure.cache.redis;

import com.galaxy.lemon.common.KVPair;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.cache.NameResolvedCacheResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringValueResolver;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.galaxy.lemon.common.KVPair.instance;
import static com.galaxy.lemon.common.utils.JudgeUtils.isNotEmpty;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({ JedisConnection.class, Jedis.class })
@ConditionalOnProperty(prefix = "lemon.cache.redis", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(RedisCacheProperties.class)
@Import({CacheRedisConfiguration.class})
public class RedisCacheConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheConfiguration.class);

    @Configuration
    public static class InternalRedisCacheConfiguration implements EmbeddedValueResolverAware, EnvironmentAware {

        private static final int DEFAULT_EXPIRE_TIME = 600; //s
        private static final String DEFAULT_REDIS_CACHE_PREFIX = "CACHE";
        public static final String CACHE_REDIS_EXPIRES = "lemon.cache.redis.expires";
        public static final String CACHE_NAME_PREFIX_SEPARATOR = ":";


        private StringValueResolver stringValueResolver;
        private Environment environment;

        private final CacheManagerCustomizers customizerInvoker;
        private final RedisCacheProperties redisCacheProperties;

        public InternalRedisCacheConfiguration(CacheManagerCustomizers customizerInvoker,
                                       RedisCacheProperties properties) {
            this.customizerInvoker = customizerInvoker;
            this.redisCacheProperties = properties;
        }

        @Bean
        public CacheManager redisCacheManager(RedisTemplate cacheRedisTemplate) {
            RedisCacheManager redisCacheManager = new RedisCacheManager(cacheRedisTemplate);
            redisCacheManager.setUsePrefix(true);
            //设置缓存过期时间
            redisCacheManager.setDefaultExpiration(getDefaultExpireTime());//秒
            //cache name 对应的缓存时间
            Map<String, Long> expires = parseRedisCacheExpires();
            redisCacheManager.setExpires(expires);
            if(logger.isInfoEnabled()) {
                logger.info("redis default expiration is {}/s, cache expires are {}", getDefaultExpireTime(), expires);
            }
            List<String> cacheNames = this.redisCacheProperties.getCacheNames();
            if (!cacheNames.isEmpty()) {
                redisCacheManager.setCacheNames(cacheNames);
            }
            return this.customizerInvoker.customize(redisCacheManager);

        }

        @Bean
        public CacheResolver redisCacheResolver(CacheManager redisCacheManager) {
            //return new NameResolvedCacheResolver(redisCacheManager, new CacheNamePrefix.SimpleCacheNamePrefix(getCacheNamePrefix()));
            return new NameResolvedCacheResolver(redisCacheManager, originalCacheName -> resolveCacheName(originalCacheName));
        }

        private int getDefaultExpireTime() {
            return redisCacheProperties.getDefaultExpiry() == -1 ? DEFAULT_EXPIRE_TIME : redisCacheProperties.getDefaultExpiry();
        }

        /**
         * 解析redis过期时间
         * @return
         */
        private Map<String, Long> parseRedisCacheExpires() {
            return Optional.of(new RelaxedPropertyResolver(this.environment, CACHE_REDIS_EXPIRES)).map(p -> p.getSubProperties("."))
                    .filter(JudgeUtils::isNotEmpty).map(this::parseRedisCacheExpires).orElse(null);
        }

        private Map<String, Long> parseRedisCacheExpires(Map<String, Object> subProperties) {
            Map<String, Long> redisExpires = subProperties.entrySet().stream().filter(sp -> isNotEmpty(sp.getKey()))
                    .map(sp -> instance(resolveCacheName(sp.getKey()), toLong(String.valueOf(sp.getValue())))
                    ).collect(Collectors.toMap(KVPair::getK, KVPair::getV));
            return Collections.unmodifiableMap(redisExpires);
        }

        private String resolveCacheName(String cacheName) {
            cacheName = this.stringValueResolver.resolveStringValue(cacheName);
            StringBuilder cacheNameStringBuilder = new StringBuilder();
            String cacheNamePrefix = getCacheNamePrefix();
            if (JudgeUtils.isNotEmpty(cacheNamePrefix) && ! cacheName.startsWith(cacheNamePrefix)) {
                cacheNameStringBuilder.append(cacheNamePrefix).append(CACHE_NAME_PREFIX_SEPARATOR);
            }
            cacheNameStringBuilder.append(cacheName);
            return cacheNameStringBuilder.toString();
        }

        private String getCacheNamePrefix() {
            return Optional.ofNullable(this.redisCacheProperties.getCacheNamePrefix()).filter(JudgeUtils::isNotEmpty).orElse(DEFAULT_REDIS_CACHE_PREFIX);
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public void setEmbeddedValueResolver(StringValueResolver resolver) {
            this.stringValueResolver = resolver;
        }

    }


}
