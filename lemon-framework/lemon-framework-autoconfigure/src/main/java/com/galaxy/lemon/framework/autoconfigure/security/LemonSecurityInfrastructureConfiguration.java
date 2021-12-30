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

package com.galaxy.lemon.framework.autoconfigure.security;

import com.galaxy.lemon.common.utils.OrderUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.autoconfigure.security.SecurityProperties;
import com.galaxy.lemon.framework.autoconfigure.session.SessionObjectMapperCustomizer;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.galaxy.lemon.framework.security.*;
import com.galaxy.lemon.framework.security.callback.AuthenticationSuccessPostProcessor;
import com.galaxy.lemon.framework.security.callback.AuthenticationSuccessProcessorComposite;
import com.galaxy.lemon.framework.security.jackson2.CoreJackson2Module;
import com.galaxy.lemon.framework.security.session.*;
import com.galaxy.lemon.framework.session.support.PrincipalNameIndexNameSessionRepositorySupport;
import com.fasterxml.jackson.databind.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * gateway spring security configuration
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class LemonSecurityInfrastructureConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(LemonSecurityInfrastructureConfiguration.class);
    public static final String BEAN_NAME_PRINCIPAL_NAME_INDEX_NAME_AUTHENTICATION_SUCCESS_POST_PROCESSOR = "principalNameIndexNameAuthenticationSuccessPostProcessor";

    private ResponseMessageResolver responseMessageResolver;

    private AuthenticationSuccessProcessorComposite authenticationSuccessProcessorComposite;

    private InternalDataHelper internalDataHelper;

    private SecurityProperties securityProperties;

    @Autowired
    public LemonSecurityInfrastructureConfiguration(ResponseMessageResolver responseMessageResolver,
                                                    InternalDataHelper internalDataHelper,
                                                    SecurityProperties securityProperties,
                                                    ObjectProvider<List<AuthenticationSuccessPostProcessor>> authenticationSuccessPostProcessors) {
        this.responseMessageResolver = responseMessageResolver;
        this.internalDataHelper = internalDataHelper;
        this.securityProperties = securityProperties;
        this.authenticationSuccessProcessorComposite = new AuthenticationSuccessProcessorComposite();
        Optional.ofNullable(authenticationSuccessPostProcessors.getIfAvailable()).ifPresent(s ->
                OrderUtils.sortByOrder(s).stream().forEachOrdered(authenticationSuccessProcessorComposite::addAuthenticationSuccessPostProcessor));

    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass(value="com.galaxy.lemon.framework.security.refresh.auth.DefaultRefreshTokenService")
    public RefreshTokenService refreshTokenService() {
        return new RefreshTokenService.DummyRefreshTokenService();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessDeniedHandler lemonAccessDeniedHandler() {
        return new LemonAccessDeniedHandler(this.responseMessageResolver);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint lemonAuthenticationEntryPoint() {
        return new LemonAuthenticationEntryPoint(this.responseMessageResolver);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureHandler lemonAuthenticationFailureHandler() {
        return new LemonAuthenticationFailureHandler(this.responseMessageResolver);
    }
    
    @Bean
    @ConditionalOnMissingBean(name = "lemonAuthenticationSuccessHandler")
    public AuthenticationSuccessHandler lemonAuthenticationSuccessHandler(RefreshTokenService refreshTokenService) {
        return new LemonAuthenticationSuccessHandler(this.responseMessageResolver, refreshTokenService, this.internalDataHelper, authenticationSuccessProcessorComposite);
    }

    @Bean
    @ConditionalOnMissingBean
    public LogoutSuccessHandler lemonLogoutSuccessHandler() {
        return new LemonLogoutSuccessHandler(this.responseMessageResolver);
    }
    
    @Bean
    public SessionInformationExpiredStrategy lemonSessionInformationExpiredStrategy() {
        return new LemonSessionInformationExpiredStrategy(this.responseMessageResolver);
    }
    
    @Bean
    public SessionRegistry sessionRegistry(RedisOperationsSessionRepository sessionRepository) {
        return new SpringSessionBackedSessionRegistry((FindByIndexNameSessionRepository)sessionRepository);
    }

    @Configuration
    public static class PrincipalNameResolverConfiguration {
        private SecurityProperties securityProperties;

        public PrincipalNameResolverConfiguration(SecurityProperties securityProperties) {
            this.securityProperties = securityProperties;
        }
//
//        @Bean
//        public PrincipalNameExtractor principalNameExtractor() {
//            return new ExpressionPrincipalNameExtractor(StringUtils.getDefaultIfEmpty(Optional.ofNullable(this.securityProperties.getPrincipal()).map(SecurityProperties.Principal::getPrincipalNameExpression).orElse(null), "username"));
//        }

        @Bean
        public PrincipalNameResolver principalNameResolver() {
            String principalNameResolveExpression = Optional.ofNullable(this.securityProperties.getPrincipal()).map(SecurityProperties.Principal::getPrincipalNameExpression).orElse(null);
            return new ExpressionPrincipalNameResolver(StringUtils.getDefaultIfEmpty(principalNameResolveExpression, "username"));
        }

    }

    @Configuration
    public static class ExtractionSessionExpiringSessionAuthenticationStrategyConfiguration {
        @Bean
        @ConditionalOnProperty(prefix = "lemon.session", name = "expireExtractor", havingValue = "all")
        public RequiredExpireSessionExtractor requiredExpireSessionExtractor(SessionRegistry sessionRegistry,
                                                                             PrincipalNameResolver principalNameResolver) {
            return new ExpiringAllRequiredExpireSessionExtractor(sessionRegistry, principalNameResolver);
        }

        @Bean
        @ConditionalOnBean(RequiredExpireSessionExtractor.class)
        public SessionAuthenticationStrategy sessionAuthenticationStrategy(RequiredExpireSessionExtractor requiredExpireSessionExtractor, RefreshTokenService refreshTokenService) {
            return new ConcurrentSessionExpiringSessionAuthenticationStrategy(requiredExpireSessionExtractor, refreshTokenService);
        }

    }

    @Configuration
    public static class NullAuthenticatedSessionStrategyConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
            return new NullAuthenticatedSessionStrategy();
        }
    }

    @Bean
    public SessionObjectMapperCustomizer securitySessionObjectMapperCustomizer() {
        return mapper -> {
            List<Module> modules =  SecurityJackson2Modules.getModules(LemonSecurityInfrastructureConfiguration.class.getClassLoader());
            modules.add(new CoreJackson2Module());
            Optional.ofNullable(modules).ifPresent(s -> s.stream().forEach(mapper::registerModule));
            if (logger.isInfoEnabled()) {
                logger.info("Register security module {} to ObjectMapper {}", modules, mapper);
            }
        };
    }

    @Configuration
    @ConditionalOnClass(PrincipalNameIndexNameSessionRepositorySupport.class)
    public static class AuthenticationSuccessPostProcessorConfiguration {

        @Bean
        @ConditionalOnMissingBean(name = BEAN_NAME_PRINCIPAL_NAME_INDEX_NAME_AUTHENTICATION_SUCCESS_POST_PROCESSOR)
        public AuthenticationSuccessPostProcessor principalNameIndexNameAuthenticationSuccessPostProcessor(PrincipalNameResolver principalNameResolver) {
            return new PrincipalNameIndexNameAuthenticationSuccessPostProcessor(principalNameResolver);
        }
    }

    public static class PrincipalNameIndexNameAuthenticationSuccessPostProcessor extends PrincipalNameIndexNameSessionRepositorySupport
            implements AuthenticationSuccessPostProcessor {

        private PrincipalNameResolver principalNameResolver;

        public PrincipalNameIndexNameAuthenticationSuccessPostProcessor(PrincipalNameResolver principalNameResolver) {
            this.principalNameResolver = principalNameResolver;
        }

        @Override
        public void postProcessAfterAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            Optional.ofNullable(request.getSession(false)).ifPresent(s ->
                    this.setPrincipalNameIndexNameToSession(s, this.principalNameResolver.resolve(authentication.getPrincipal())));
        }
    }
}
