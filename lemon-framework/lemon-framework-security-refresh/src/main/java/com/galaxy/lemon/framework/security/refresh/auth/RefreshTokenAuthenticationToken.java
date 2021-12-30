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

package com.galaxy.lemon.framework.security.refresh.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Map;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RefreshTokenAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = -611230686173603990L;
    private Map<String, String> requestParameters;  //request parameters
    private String refreshToken;
    private String principal;                       //refresh token

    public RefreshTokenAuthenticationToken(String refreshToken, Map<String, String> requestParameters) {
        super(null);
        this.refreshToken = refreshToken;
        this.requestParameters = requestParameters;
    }

    @Override
    public Object getCredentials() {
        return refreshToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    public String getParameter(String key) {
        return Optional.ofNullable(this.requestParameters).map(m -> m.get(key)).orElse(null);
    }
}
