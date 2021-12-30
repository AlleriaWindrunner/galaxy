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

package com.galaxy.lemon.framework.autoconfigure.security.refresh;

import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.autoconfigure.security.AuthenticationManagerBuilderCustomizer;
import com.galaxy.lemon.framework.autoconfigure.security.HttpSecurityCustomizer;
import com.galaxy.lemon.framework.autoconfigure.security.IndicatePermitAllUrlPattern;
import com.galaxy.lemon.framework.autoconfigure.security.SecurityProperties;
import com.galaxy.lemon.framework.autoconfigure.web.IndicateLemonDataInitialUrlPattern;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.logger.http.HttpAccessLogger;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.galaxy.lemon.framework.security.RefreshTokenService;
import com.galaxy.lemon.framework.security.auth.AuthenticationProcessor;
import com.galaxy.lemon.framework.security.refresh.DefaultTokenService;
import com.galaxy.lemon.framework.security.refresh.DelegatedSessionRepository;
import com.galaxy.lemon.framework.security.refresh.TokenService;
import com.galaxy.lemon.framework.security.refresh.TokenStore;
import com.galaxy.lemon.framework.security.refresh.auth.*;
import com.galaxy.lemon.framework.security.refresh.redis.JacksonSerializationStrategy;
import com.galaxy.lemon.framework.security.refresh.redis.RedisTokenStore;
import com.galaxy.lemon.framework.security.refresh.redis.RedisTokenStoreSerializationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(DefaultTokenService.class)
public class RefreshAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RefreshAutoConfiguration.class);

    public static final String REFRESH_PATH = RefreshTokenAuthenticationProcessingFilter.DEFAULT_REFRESH_TOKEN_PROCESSES_URL;
    public static final String BEAN_NAME_REFRESH_AUTHENTICATION_SUCCESS_HANDLER = "refreshAuthenticationSuccessHandler";

    @Value("${lemon.session.refreshTokenExpiration:30}")
    private Integer refreshTokenExpiration;

    private SecurityProperties securityProperties;

    public RefreshAutoConfiguration(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public JacksonSerializationStrategy redisTokenStoreSerializationStrategy() {
        return new JacksonSerializationStrategy();
    }

    @Bean
    public TokenStore tokenStore(RedisConnectionFactory redisConnectionFactory,
                                 @Autowired(required = false) RedisTokenStoreSerializationStrategy redisTokenStoreSerializationStrategy) {
        if (logger.isDebugEnabled()) {
            logger.debug("Refresh session redis factory {}", redisConnectionFactory);
        }
        RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
        Optional.ofNullable(redisTokenStoreSerializationStrategy).ifPresent(tokenStore::setSerializationStrategy);
        return tokenStore;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public TokenService defaultTokenService(TokenStore tokenStore, DelegatedSessionRepository delegatedSessionRepository) {
        return new DefaultTokenService(tokenStore, delegatedSessionRepository, refreshTokenExpiration);
    }
    
    @Bean
    public RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider(TokenService tokenService,
                                                                                 AuthenticationProcessor refreshTokenAuthenticationProcessor) {
        return new RefreshTokenAuthenticationProvider(refreshTokenAuthenticationProcessor, tokenService);
    }

    @Bean
    public AuthenticationManagerBuilderCustomizer refreshAuthenticationManagerBuilderCustomizer(RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider) {
        return (authenticationManagerBuilder, metadata) -> authenticationManagerBuilder.authenticationProvider(refreshTokenAuthenticationProvider);
    }

    @Bean
    public RevokingRefreshTokenLogoutHandler revokingRefreshTokenLogoutHandler(TokenService tokenService) {
        return new RevokingRefreshTokenLogoutHandler(tokenService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RefreshTokenService refreshTokenService(TokenService tokenService) {
        return new DefaultRefreshTokenService(tokenService);
    }

    @Configuration
    @ConditionalOnClass(RedisOperationsSessionRepository.class)
    public static class RedisDelegatedSessionRepositoryConfiguration {

        @Bean
        @ConditionalOnBean(RedisOperationsSessionRepository.class)
        public DelegatedSessionRepository delegatedSessionRepository(RedisOperationsSessionRepository sessionRepository) {
            return sessionId -> sessionRepository.delete(sessionId);
        }
    }

    @Bean
    @ConditionalOnMissingBean(name = BEAN_NAME_REFRESH_AUTHENTICATION_SUCCESS_HANDLER)
    public RefreshAuthenticationSuccessHandler refreshAuthenticationSuccessHandler(ResponseMessageResolver responseMessageResolver,
                                                                                   RefreshTokenService refreshTokenService,
                                                                                   InternalDataHelper internalDataHelper) {
        return new RefreshAuthenticationSuccessHandler(responseMessageResolver, refreshTokenService, internalDataHelper);
    }

    @Bean
    public IndicateLemonDataInitialUrlPattern refreshIndicateLemonDataInitialUrlPattern() {
        List<String> urls = new ArrayList();
        urls.add(getRefreshPath());
        return new IndicateLemonDataInitialUrlPattern(urls);
    }

    @Bean
    public IndicatePermitAllUrlPattern indicatePermitAllUrlPattern() {
        List<String> permitAllUrls = new ArrayList<>();
        permitAllUrls.add(getRefreshPath());
        return new IndicatePermitAllUrlPattern(permitAllUrls);
    }

    @Bean
    public HttpSecurityCustomizer refreshHttpSecurityCustomizer(AuthenticationSuccessHandler refreshAuthenticationSuccessHandler,
                                                                HttpAccessLogger httpAccessLogger,
                                                                ObjectMapper objectMapper) {
        return (httpSecurity, metadata) -> httpSecurity.addFilterBefore(refreshTokenAuthenticationProcessingFilter(refreshAuthenticationSuccessHandler, metadata, objectMapper, httpAccessLogger),
                UsernamePasswordAuthenticationFilter.class);
    }

    public RefreshTokenAuthenticationProcessingFilter refreshTokenAuthenticationProcessingFilter(
                                                AuthenticationSuccessHandler refreshAuthenticationSuccessHandler,
                                                HttpSecurityCustomizer.Metadata metadata,
                                                ObjectMapper objectMapper,
                                                HttpAccessLogger httpAccessLogger) {
        RefreshTokenAuthenticationProcessingFilter refreshTokenAuthenticationProcessingFilter =  new RefreshTokenAuthenticationProcessingFilter(getRefreshPath(), objectMapper, httpAccessLogger);
        refreshTokenAuthenticationProcessingFilter.setAuthenticationManager(metadata.getAuthenticationManager());
        refreshTokenAuthenticationProcessingFilter.setAuthenticationSuccessHandler(refreshAuthenticationSuccessHandler);
        refreshTokenAuthenticationProcessingFilter.setAuthenticationFailureHandler(metadata.getAuthenticationFailureHandler());
        refreshTokenAuthenticationProcessingFilter.setSessionAuthenticationStrategy(metadata.getSessionAuthenticationStrategy());
        return refreshTokenAuthenticationProcessingFilter;
    }

    private String getRefreshPath() {
        return StringUtils.getDefaultIfEmpty(Optional.ofNullable(this.securityProperties.getAuthentication()).map(a -> a.getRefreshPath()).orElse(null), REFRESH_PATH);
    }
    
}
