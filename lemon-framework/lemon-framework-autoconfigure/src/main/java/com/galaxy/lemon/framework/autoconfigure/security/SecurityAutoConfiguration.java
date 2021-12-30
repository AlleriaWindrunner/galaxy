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

import com.galaxy.lemon.framework.autoconfigure.session.SessionAutoConfiguration;
import com.galaxy.lemon.framework.security.auth.AuthenticationProcessor;
import com.galaxy.lemon.framework.autoconfigure.security.LemonSecurityConfiguration;
import com.galaxy.lemon.framework.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.IgnoredRequestCustomizer;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(AuthenticationProcessor.class)
@Import({LemonSecurityInfrastructureConfiguration.class, SecurityAuthenticationConfiguration.class, LemonSecurityConfiguration.class})
@EnableConfigurationProperties(SecurityProperties.class)
@AutoConfigureAfter(SessionAutoConfiguration.class)
public class SecurityAutoConfiguration {

    private static final List<String> DEFAULT_LEMON_IGNORED = Arrays.asList("/druid", "/druid/**");

    @Bean
    public IgnoredRequestCustomizer ignoredRequestCustomizer(ServerProperties serverProperties) {
        return new DefaultLemonIgnoredRequestCustomizer(serverProperties);
    }

    private class DefaultLemonIgnoredRequestCustomizer implements IgnoredRequestCustomizer {

        private final ServerProperties server;

        DefaultLemonIgnoredRequestCustomizer(ServerProperties server) {
            this.server = server;
        }

        @Override
        public void customize(WebSecurity.IgnoredRequestConfigurer configurer) {
            List<String> ignored = getIgnored();
            String[] paths = this.server.getPathsArray(ignored);
            List<RequestMatcher> matchers = new ArrayList<>();
            if (!ObjectUtils.isEmpty(paths)) {
                for (String pattern : paths) {
                    matchers.add(new AntPathRequestMatcher(pattern, null));
                }
            }
            if (!matchers.isEmpty()) {
                configurer.requestMatchers(new OrRequestMatcher(matchers));
            }
        }

        private List<String> getIgnored() {
            List<String> ignored = new ArrayList<>();
            ignored.addAll(DEFAULT_LEMON_IGNORED);
            return ignored;
        }

        private String normalizePath(String errorPath) {
            String result = StringUtils.cleanPath(errorPath);
            if (!result.startsWith("/")) {
                result = "/" + result;
            }
            return result;
        }

    }


}
