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

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface HttpSecurityCustomizer {

    void customize(HttpSecurity httpSecurity, Metadata metadata);

    class Metadata {
        private AuthenticationManager authenticationManager;
        private AuthenticationFailureHandler authenticationFailureHandler;
        private SessionAuthenticationStrategy sessionAuthenticationStrategy;

        public Metadata(AuthenticationManager authenticationManager, AuthenticationFailureHandler authenticationFailureHandler, SessionAuthenticationStrategy sessionAuthenticationStrategy) {
            this.authenticationManager = authenticationManager;
            this.authenticationFailureHandler = authenticationFailureHandler;
            this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
        }

        public AuthenticationManager getAuthenticationManager() {
            return authenticationManager;
        }

        public AuthenticationFailureHandler getAuthenticationFailureHandler() {
            return authenticationFailureHandler;
        }

        public SessionAuthenticationStrategy getSessionAuthenticationStrategy() {
            return sessionAuthenticationStrategy;
        }
    }
}
