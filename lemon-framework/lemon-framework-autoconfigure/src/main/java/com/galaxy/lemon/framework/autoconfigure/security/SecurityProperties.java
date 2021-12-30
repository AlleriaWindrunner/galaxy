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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties(prefix = "lemon.security")
public class SecurityProperties {

    private Principal principal;

    private Authentication authentication;

    private AuthorizeRequests authorizeRequests;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public AuthorizeRequests getAuthorizeRequests() {
        return authorizeRequests;
    }

    public void setAuthorizeRequests(AuthorizeRequests authorizeRequests) {
        this.authorizeRequests = authorizeRequests;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public static class Authentication {
        private String loginPathPrefix;
        private String logoutPath;
        private String refreshPath;
        private boolean postOnly = true;

        public String getLoginPathPrefix() {
            return loginPathPrefix;
        }

        public void setLoginPathPrefix(String loginPathPrefix) {
            this.loginPathPrefix = loginPathPrefix;
        }

        public String getLogoutPath() {
            return logoutPath;
        }

        public void setLogoutPath(String logoutPath) {
            this.logoutPath = logoutPath;
        }

        public String getRefreshPath() {
            return refreshPath;
        }

        public void setRefreshPath(String refreshPath) {
            this.refreshPath = refreshPath;
        }

        public boolean isPostOnly() {
            return postOnly;
        }

        public void setPostOnly(boolean postOnly) {
            this.postOnly = postOnly;
        }
    }

    public static class AuthorizeRequests {
        private List<String> permitAll;

        public List<String> getPermitAll() {
            return permitAll;
        }

        public void setPermitAll(List<String> permitAll) {
            this.permitAll = permitAll;
        }
    }

    public static class Principal {
        private String principalNameExpression;

        public String getPrincipalNameExpression() {
            return principalNameExpression;
        }

        public void setPrincipalNameExpression(String principalNameExpression) {
            this.principalNameExpression = principalNameExpression;
        }
    }
}
