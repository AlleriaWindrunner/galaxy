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

package com.galaxy.lemon.framework.autoconfigure.cache;

import com.galaxy.lemon.framework.autoconfigure.cache.CacheAutoConfiguration.CacheConfigurationImportSelector;
import com.galaxy.lemon.framework.autoconfigure.cache.jcache.JCacheCacheConfiguration;
import com.galaxy.lemon.framework.autoconfigure.cache.redis.RedisCacheConfiguration;
import com.galaxy.lemon.framework.cache.DefaultCachingConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 多缓存配置
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@EnableCaching
@AutoConfigureAfter({RedisAutoConfiguration.class})
@EnableConfigurationProperties(CacheProperties.class)
@Import(CacheConfigurationImportSelector.class)
@AutoConfigureBefore(org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration.class)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(
            ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
        return new CacheManagerCustomizers(customizers.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultCachingConfigurer defaultCachingConfigurer(ObjectProvider<Map<String, CacheResolver>> cacheResolvers) {
        return new DefaultCachingConfigurer(cacheResolvers.getIfAvailable());
    }

    static class CacheConfigurationImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            List<Class<?>> list = new ArrayList<>();
            list.add(RedisCacheConfiguration.class);
            list.add(JCacheCacheConfiguration.class);
            return list.stream().map(Class::getName).collect(Collectors.toList()).toArray(new String[list.size()]);
        }

    }

}
