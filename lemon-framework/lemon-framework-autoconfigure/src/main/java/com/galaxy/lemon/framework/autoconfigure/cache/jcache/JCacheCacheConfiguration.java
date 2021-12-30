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

package com.galaxy.lemon.framework.autoconfigure.cache.jcache;

import com.galaxy.lemon.framework.autoconfigure.cache.CacheProperties;
import com.galaxy.lemon.framework.cache.NameResolvedCacheResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({javax.cache.Caching.class, JCacheCacheManager.class, CacheManager.class})
@Conditional({JCacheCacheConfiguration.JCacheAvailableCondition.class })
@EnableConfigurationProperties(CacheProperties.class)
public class JCacheCacheConfiguration {

    private final CacheProperties cacheProperties;

    private final CacheManagerCustomizers customizers;

    private final javax.cache.configuration.Configuration<?, ?> defaultCacheConfiguration;

    private final List<JCacheManagerCustomizer> cacheManagerCustomizers;

    private final List<JCachePropertiesCustomizer> cachePropertiesCustomizers;

    JCacheCacheConfiguration(CacheProperties cacheProperties,
                             CacheManagerCustomizers customizers,
                             ObjectProvider<javax.cache.configuration.Configuration<?, ?>> defaultCacheConfiguration,
                             ObjectProvider<List<JCacheManagerCustomizer>> cacheManagerCustomizers,
                             ObjectProvider<List<JCachePropertiesCustomizer>> cachePropertiesCustomizers) {
        this.cacheProperties = cacheProperties;
        this.customizers = customizers;
        this.defaultCacheConfiguration = defaultCacheConfiguration.getIfAvailable();
        this.cacheManagerCustomizers = cacheManagerCustomizers.getIfAvailable();
        this.cachePropertiesCustomizers = cachePropertiesCustomizers.getIfAvailable();
    }

    @Bean
    public org.springframework.cache.CacheManager jcacheCacheManager(CacheManager jsr107CacheManager) {
        JCacheCacheManager cacheManager = new JCacheCacheManager(jsr107CacheManager);
        return this.customizers.customize(cacheManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheManager jsr107CacheManager() throws IOException {
        CacheManager jCacheCacheManager = createCacheManager();
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!CollectionUtils.isEmpty(cacheNames)) {
            for (String cacheName : cacheNames) {
                jCacheCacheManager.createCache(cacheName, getDefaultCacheConfiguration());
            }
        }
        customize(jCacheCacheManager);
        return jCacheCacheManager;
    }

    private CacheManager createCacheManager() throws IOException {
        CachingProvider cachingProvider = getCachingProvider(
                this.cacheProperties.getJcache().getProvider());
        Properties properties = createCacheManagerProperties();
        Resource configLocation = this.cacheProperties
                .resolveConfigLocation(this.cacheProperties.getJcache().getConfig());
        if (configLocation != null) {
            return cachingProvider.getCacheManager(configLocation.getURI(),
                    cachingProvider.getDefaultClassLoader(), properties);
        }
        return cachingProvider.getCacheManager(null, null, properties);
    }

    private CachingProvider getCachingProvider(String cachingProviderFqn) {
        if (StringUtils.hasText(cachingProviderFqn)) {
            return Caching.getCachingProvider(cachingProviderFqn);
        }
        return Caching.getCachingProvider();
    }

    private Properties createCacheManagerProperties() {
        Properties properties = new Properties();
        if (this.cachePropertiesCustomizers != null) {
            for (JCachePropertiesCustomizer customizer : this.cachePropertiesCustomizers) {
                customizer.customize(this.cacheProperties, properties);
            }
        }
        return properties;
    }

    private javax.cache.configuration.Configuration<?, ?> getDefaultCacheConfiguration() {
        if (this.defaultCacheConfiguration != null) {
            return this.defaultCacheConfiguration;
        }
        return new MutableConfiguration<Object, Object>();
    }

    private void customize(CacheManager cacheManager) {
        if (this.cacheManagerCustomizers != null) {
            AnnotationAwareOrderComparator.sort(this.cacheManagerCustomizers);
            for (JCacheManagerCustomizer customizer : this.cacheManagerCustomizers) {
                customizer.customize(cacheManager);
            }
        }
    }

    @Bean
    public CacheResolver jCacheCacheResolver(org.springframework.cache.CacheManager jcacheCacheManager) {
        return new NameResolvedCacheResolver(jcacheCacheManager);
    }

    /**
     * Determine if JCache is available. This either kicks in if a provider is available
     * as defined per {@link JCacheProviderAvailableCondition} or if a
     * {@link CacheManager} has already been defined.
     */
    @Order(Ordered.LOWEST_PRECEDENCE)
    static class JCacheAvailableCondition extends AnyNestedCondition {

        JCacheAvailableCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @Conditional(JCacheProviderAvailableCondition.class)
        static class JCacheProvider {

        }

        @ConditionalOnSingleCandidate(CacheManager.class)
        static class CustomJCacheCacheManager {

        }

    }

    /**
     * Determine if a JCache provider is available. This either kicks in if a default
     * {@link CachingProvider} has been found or if the property referring to the provider
     * to use has been set.
     */
    @Order(Ordered.LOWEST_PRECEDENCE)
    static class JCacheProviderAvailableCondition extends SpringBootCondition {

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context,
                                                AnnotatedTypeMetadata metadata) {
            ConditionMessage.Builder message = ConditionMessage.forCondition("JCache");
            RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
                    context.getEnvironment(), "lemon.cache.jcache.");
            if (resolver.containsProperty("provider")) {
                return ConditionOutcome
                        .match(message.because("JCache provider specified"));
            }
            Iterator<CachingProvider> providers = Caching.getCachingProviders()
                    .iterator();
            if (!providers.hasNext()) {
                return ConditionOutcome
                        .noMatch(message.didNotFind("JSR-107 provider").atAll());
            }
            providers.next();
            if (providers.hasNext()) {
                return ConditionOutcome
                        .noMatch(message.foundExactly("multiple JSR-107 providers"));

            }
            return ConditionOutcome
                    .match(message.foundExactly("single JSR-107 provider"));
        }

    }
}
