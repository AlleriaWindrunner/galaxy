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

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.IOUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.logger.http.HttpAccessLogger;
import com.galaxy.lemon.framework.security.HttpServletResponseStatusCode;
import com.galaxy.lemon.framework.security.refresh.RefreshFailureException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * Refresh token authentication
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RefreshTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    public static final String DEFAULT_REFRESH_TOKEN_PROCESSES_URL = "/security/refresh";
    public static final String DEFAULT_REFRESH_TOKEN_PARAMETER_KEY = "refreshToken";
    
    private boolean postOnly = true;
    private ObjectMapper objectMapper;
    private HttpAccessLogger accessLogger;

    public RefreshTokenAuthenticationProcessingFilter(String defaultFilterProcessesUrl,
                                                      ObjectMapper objectMapper,
                                                      HttpAccessLogger accessLogger) {
        super(defaultFilterProcessesUrl);
        this.objectMapper = objectMapper;
        this.accessLogger = accessLogger;
    }
    
    public RefreshTokenAuthenticationProcessingFilter(ObjectMapper objectMapper,
                                                      HttpAccessLogger accessLogger) {
        super(DEFAULT_REFRESH_TOKEN_PROCESSES_URL);
        this.objectMapper = objectMapper;
        this.accessLogger = accessLogger;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 
        throws AuthenticationException, IOException, ServletException {
        this.accessLogger.request(request, response, "PROTECTED");
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Refresh Token authentication method not supported: "+request.getMethod());
        }

        Map<String, String> requestParameters = null;
        String body = getRequestBody(request);
        if(JudgeUtils.isNotBlank(body)) {
            try {
                requestParameters = this.objectMapper.readValue(body, Map.class);
            } catch (IOException e) {
                logger.error(e);
            }
        }
        String refreshToken;
        if (JudgeUtils.isNotEmpty(requestParameters)) {
            refreshToken = requestParameters.get(this.getRefreshTokenParameter());
        } else {
            refreshToken = obtainRefreshToken(request);
        }

        if (JudgeUtils.isBlank(refreshToken)) {
            throw new RefreshFailureException(ErrorMsgCode.REFRESH_TOKEN_INVALID, HttpServletResponseStatusCode.SC_REFRESH_TOKEN_INVALID);
        }

        RefreshTokenAuthenticationToken authRequest = new RefreshTokenAuthenticationToken(refreshToken, requestParameters);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
    
    protected String getRefreshTokenParameter() {
        return DEFAULT_REFRESH_TOKEN_PARAMETER_KEY;
    }
    
    protected String obtainRefreshToken(HttpServletRequest request) {
        return request.getParameter(getRefreshTokenParameter());
    }
    
    protected void setDetails(HttpServletRequest request, RefreshTokenAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    private static InputStream getRequestInputStream(HttpServletRequest request) {
        try {
            return request.getInputStream();
        } catch (IOException ex) {
            throw LemonException.create(ex);
        }
    }

    private static String getRequestBody(HttpServletRequest request) {
        return Optional.ofNullable(getRequestInputStream(request)).map(IOUtils::toStringIgnoreException).orElse(null);
    }
}
