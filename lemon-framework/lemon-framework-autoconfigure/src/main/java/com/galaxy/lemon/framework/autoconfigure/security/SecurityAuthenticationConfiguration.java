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

import com.galaxy.lemon.common.codec.ObjectDecoder;
import com.galaxy.lemon.framework.security.SecurityUtils;
import com.galaxy.lemon.framework.security.SimpleUserInfo;
import com.galaxy.lemon.framework.security.auth.*;
import com.galaxy.lemon.framework.security.callback.AuthenticationSuccessPostProcessor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({GenericAuthenticationProvider.class})
@Import({SecurityAuthenticationConfiguration.MockAuthenticationProcessorConfiguration.class, SecurityAuthenticationConfiguration.MockRefreshAuthenticationProcessorConfiguration.class})
public class SecurityAuthenticationConfiguration {
    public static final String BEAN_NAME_REFRESH_TOKEN_AUTHENTICATION_PROCESSOR = "refreshTokenAuthenticationProcessor";
    public static final String SESSION_ATTRIBUTE_KEY_MBL_NO = "mblNo";

    private List<MatchableAuthenticationProcessor> matchableAuthenticationProcessors;

    public SecurityAuthenticationConfiguration(ObjectProvider<List<MatchableAuthenticationProcessor>> matchableAuthenticationProcessors) {
        this.matchableAuthenticationProcessors = matchableAuthenticationProcessors.getIfAvailable();
    }

    @Bean
    public GenericAuthenticationProvider genericAuthenticationProvider() {
        return new GenericAuthenticationProvider(this.matchableAuthenticationProcessors);
    }

    @Bean
    @ConditionalOnProperty(prefix = "lemon.security.authentication.MobileNoSetSession", name = "enabled", havingValue = "true", matchIfMissing = true)
    public AuthenticationSuccessPostProcessor setSessionMblNoAuthenticationSuccessPostProcessor(@Value("${lemon.security.authentication.MobileNoSetSession.attributeKey:" + SESSION_ATTRIBUTE_KEY_MBL_NO+"}") String mblNoSessionAttributeKey) {
        return (request, response, authentication) -> {
            String mblNo = Optional.ofNullable(SecurityUtils.getLoginUser()).map(l -> l.getMblNo()).orElse(null);
            if (null != request.getSession(false) && null != mblNo) {
                request.getSession(false).setAttribute(mblNoSessionAttributeKey, mblNo);
            }
        };
    }

    @ConditionalOnMissingBean(MatchableAuthenticationProcessor.class)
    public static class MockAuthenticationProcessorConfiguration {
        private SecurityProperties securityProperties;

        public MockAuthenticationProcessorConfiguration(SecurityProperties securityProperties) {
            this.securityProperties = securityProperties;
        }

        @Bean
        @ConditionalOnMissingBean
        public MatchableAuthenticationProcessor matchableAuthenticationProcessor(ObjectDecoder objectDecoder) {
            return new MockUserNamePasswordMatchableAuthenticationProcessor(getLoginPathPrefix() + "/mock",
                    objectDecoder);
        }

        private String getLoginPathPrefix() {
            return Optional.ofNullable(this.securityProperties.getAuthentication()).map(a
                    -> a.getLoginPathPrefix()).orElse(GenericAuthenticationFilter.DEFAULT_PREFIX_FILTER_PROCESSES_URL);
        }
    }

    public static class MockRefreshAuthenticationProcessorConfiguration {

        @Bean
        @ConditionalOnMissingBean(name = BEAN_NAME_REFRESH_TOKEN_AUTHENTICATION_PROCESSOR)
        public AuthenticationProcessor refreshTokenAuthenticationProcessor() {
            return authentication -> new SimpleUserInfo("mock123456", "mock", "12345678900");
        }
    }

}
