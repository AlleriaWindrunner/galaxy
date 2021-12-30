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

import com.galaxy.lemon.framework.logger.http.HttpAccessLogger;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 支持渠道登录
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class GenericAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_PREFIX_FILTER_PROCESSES_URL = "/security/login";

    private static final String DEFAULT_FILTER_PROCESSES_URL = DEFAULT_PREFIX_FILTER_PROCESSES_URL + "/**";
    private boolean postOnly = true;
    private HttpAccessLogger accessLogger;

    public GenericAuthenticationFilter(HttpAccessLogger accessLogger) {
        this(DEFAULT_FILTER_PROCESSES_URL, accessLogger);
    }

    public GenericAuthenticationFilter(String filterProcessesUrl,
                                       HttpAccessLogger accessLogger) {
        this(filterProcessesUrl, accessLogger, "POST");
    }

    public GenericAuthenticationFilter(String filterProcessesUrl,
                                       HttpAccessLogger accessLogger,
                                       String httpMethod) {

        super(new AntPathRequestMatcher(filterProcessesUrl, httpMethod));
        this.accessLogger = accessLogger;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        this.accessLogger.request(request, response, "PROTECTED");
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        GenericAuthenticationToken authRequest = new GenericAuthenticationToken(new AuthenticationRequest(request));
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request,
                              GenericAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * Defines whether only HTTP POST requests will be allowed by this filter. If set to
     * true, and an authentication request is received which is not a POST request, an
     * exception will be raised immediately and authentication will not be attempted. The
     * <tt>unsuccessfulAuthentication()</tt> method will be called as if handling a failed
     * authentication.
     * <p>
     * Defaults to <tt>true</tt> but may be overridden by subclasses.
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}
