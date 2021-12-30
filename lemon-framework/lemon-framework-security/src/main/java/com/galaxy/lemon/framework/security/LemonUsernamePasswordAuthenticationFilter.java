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

package com.galaxy.lemon.framework.security;

import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.IOUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.logger.http.HttpAccessLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * authentication filter
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public static final String RANDOM_PARAM_NAME = "random";
    private boolean postOnly = true;
    private ObjectMapper objectMapper;
    private HttpAccessLogger accessLogger;
    private String random = RANDOM_PARAM_NAME;
    
    public LemonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper,
                                                     HttpAccessLogger accessLogger) {
        super();
        this.objectMapper = objectMapper;
        this.accessLogger = accessLogger;
    }
    
    @SuppressWarnings("unchecked")
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        this.accessLogger.request(request, response, "PROTECTED");
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String username = "";
        String password = "";
        String random = "";
        
        Map<String, String> map = null;
        String body = getRequestBody(request);
        if(JudgeUtils.isNotBlank(body)) {
            try {
                map = this.objectMapper.readValue(body, Map.class);
            } catch (IOException e) {
            }
        }
        
        if (JudgeUtils.isNotEmpty(map)) {
            username = map.get(this.getUsernameParameter());
            password = map.get(this.getPasswordParameter());
            random = map.get(this.getRandom());
        } else {
            username = obtainUsername(request);
            password = obtainPassword(request);
            random = obtainRandom(request);
        }

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }
        
        if(random == null) {
            random = "";
        }

        username = username.trim();
        
        LemonUsernamePasswordAuthenticationToken authRequest = new LemonUsernamePasswordAuthenticationToken(
            username, password, random);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    public static String getRequestBody(HttpServletRequest request) {
        return Optional.ofNullable(getRequestInputStream(request)).map(IOUtils::toStringIgnoreException).orElse(null);
    }

    public static InputStream getRequestInputStream(HttpServletRequest request) {
        try {
            return request.getInputStream();
        } catch (IOException ex) {
            throw LemonException.create(ex);
        }
    }
    
    public String getRandom() {
        return this.random;
    }
    
    protected String obtainRandom(HttpServletRequest request) {
        return request.getParameter(getRandom());
    }
}
