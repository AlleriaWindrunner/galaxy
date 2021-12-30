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

import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.security.HttpServletResponseStatusCode;
import com.galaxy.lemon.framework.security.auth.AbstractAuthenticationProvider;
import com.galaxy.lemon.framework.security.auth.AuthenticationProcessor;
import com.galaxy.lemon.framework.security.refresh.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RefreshTokenAuthenticationProvider extends AbstractAuthenticationProvider implements AuthenticationProvider {
    public static final String LEMON_CONTEXT_REQUEST = "LEMON_CONTEXT_REQUEST";
    public static final String REQUEST_ATTRIBUTE_REFRESH_TOKEN = "REQUEST_ATTRIBUTE_REFRESH_TOKEN";

    private AuthenticationProcessor authenticationProcessor;
    private TokenService tokenService;
    private final Method changeSessionIdMethod;
    
    public RefreshTokenAuthenticationProvider(AuthenticationProcessor authenticationProcessor,
                                              TokenService tokenService) {
        this.authenticationProcessor = authenticationProcessor;
        this.tokenService = tokenService;

        this.changeSessionIdMethod = ReflectionUtils.findMethod(HttpServletRequest.class, "changeSessionId");
        if (this.changeSessionIdMethod == null) {
            throw new IllegalStateException("HttpServletRequest.changeSessionId is undefined. Are you using a Servlet 3.1+ environment?");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication));
    }
    
    @Override
    protected AuthenticationProcessor getAuthenticationProcessor(Authentication authentication) {
        return authenticationProcessor;
    }

    @Override
    protected Authentication preProcessAuthentication(Authentication authentication) {
        try {
            refreshToken(resolveHttpSession(), authentication);
            return authentication;
        }catch (TokenService.InvalidRefreshTokenException invalidRefreshToken) {
            throw new RefreshFailureException(invalidRefreshToken, HttpServletResponseStatusCode.SC_REFRESH_TOKEN_INVALID);
        }
    }

    private void refreshToken(HttpSession session, Authentication authentication) {
        AccessToken accessToken = this.tokenService.refreshAccessToken(session, (String)authentication.getCredentials());
        if (authentication instanceof RefreshTokenAuthenticationToken
                && accessToken instanceof LemonAccessToken) {
            LemonRefreshToken refreshToken = (LemonRefreshToken)accessToken.getRefreshToken();
            ((RefreshTokenAuthenticationToken )authentication).setPrincipal(refreshToken.getUserId());
        }
        this.getHttpServletRequest().setAttribute(REQUEST_ATTRIBUTE_REFRESH_TOKEN, accessToken.getRefreshToken());
    }

    private HttpSession resolveHttpSession() {
        return applySessionFixation(getHttpServletRequest());
    }
    
    private HttpSession applySessionFixation(HttpServletRequest request) {
        if(JudgeUtils.isNotNull(request.getSession(false))) {
            ReflectionUtils.invokeMethod(this.changeSessionIdMethod, request);
        }
        return request.getSession();
    }

    /**
     * @return HttpServletRequest
     */
    private HttpServletRequest getHttpServletRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes()).filter(s -> s instanceof ServletRequestAttributes).map(a -> (ServletRequestAttributes) a).map(s -> s.getRequest())
                .orElseGet(() -> Optional.of(LemonContext.getCurrentContext()).map(c -> c.get(LEMON_CONTEXT_REQUEST)).map(r -> (HttpServletRequest) r).orElse(null));
    }
}
