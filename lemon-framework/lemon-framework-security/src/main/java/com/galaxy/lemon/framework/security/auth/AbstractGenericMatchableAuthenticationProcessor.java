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

package com.galaxy.lemon.framework.security.auth;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.security.LemonAuthenticationException;
import com.galaxy.lemon.framework.security.UserInfoBase;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractGenericMatchableAuthenticationProcessor<A extends GenericAuthenticationToken> implements MatchableAuthenticationProcessor<A> {

    private RequestMatcher requiresAuthenticationRequestMatcher;

    /**
     *
     * @param filterProcessesUrl 认证Url, 前缀必须与GenericAuthenticationFilter拦截的Url前缀一致
     */
    public AbstractGenericMatchableAuthenticationProcessor(String filterProcessesUrl) {
        if (StringUtils.isEmpty(filterProcessesUrl) || StringUtils.endsWith(filterProcessesUrl, "*")) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Illegal process url \""+filterProcessesUrl+"\".");
        }
        this.requiresAuthenticationRequestMatcher = createAuthenticationProcessesUrlMatcher(filterProcessesUrl);
    }

    @Override
    public boolean match(A authentication) {
        AuthenticationRequest authenticationRequest = authentication.getAuthenticationRequest();
        return requiresAuthenticationRequestMatcher.matches(authenticationRequest.getHttpServletRequest());
    }

    @Override
    public UserInfoBase processAuthentication(Authentication authentication) throws LemonAuthenticationException {
        if (! (authentication instanceof GenericAuthenticationToken)) {
            throw LemonException.create(ErrorMsgCode.SYS_ERROR, "Only support GenericAuthenticationToken in \"AbstractMatchableAuthenticationProcessor\".");
        }
        GenericAuthenticationToken genericAuthenticationToken = (GenericAuthenticationToken) authentication;
        return doProcessAuthentication(genericAuthenticationToken);
    }

    protected abstract UserInfoBase doProcessAuthentication(GenericAuthenticationToken genericAuthenticationToken) throws AuthenticationException;

    public void setRequiresAuthenticationRequestMatcher(RequestMatcher requiresAuthenticationRequestMatcher) {
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
    }

    protected RequestMatcher createAuthenticationProcessesUrlMatcher(String filterProcessesUrl) {
        return new AntPathRequestMatcher(filterProcessesUrl, "POST");
    }

    protected static InputStream getRequestInputStream(AuthenticationRequest authenticationRequest) {
        try {
            return authenticationRequest.getHttpServletRequest().getInputStream();
        } catch (IOException ex) {
            throw LemonException.create(ex);
        }
    }
}
