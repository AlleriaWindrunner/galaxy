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

import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.galaxy.lemon.framework.security.callback.AuthenticationSuccessPostProcessor;
import com.galaxy.lemon.framework.utils.LemonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * 认证成功handler
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAuthenticationSuccessHandler.class);

    private ResponseMessageResolver responseMessageResolver;
    private AuthenticationSuccessPostProcessor authenticationSuccessPostProcessor;
    private InternalDataHelper internalDataHelper;

    public AbstractAuthenticationSuccessHandler(ResponseMessageResolver responseMessageResolver,
                                             InternalDataHelper internalDataHelper) {
        this(responseMessageResolver, internalDataHelper, null);

    }

    public AbstractAuthenticationSuccessHandler(ResponseMessageResolver responseMessageResolver,
                                             InternalDataHelper internalDataHelper,
                                             AuthenticationSuccessPostProcessor authenticationSuccessPostProcessor) {
        this.responseMessageResolver = responseMessageResolver;
        this.internalDataHelper = internalDataHelper;
        this.authenticationSuccessPostProcessor = authenticationSuccessPostProcessor;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        request.getSession();//创建session
        response.setStatus(HttpServletResponse.SC_OK);
        LemonDataHolder.getLemonData().setToken(request.getSession().getId());
        Optional.ofNullable(this.authenticationSuccessPostProcessor).ifPresent(p -> p.postProcessAfterAuthenticationSuccess(request, response, authentication));
        BaseDTO loginRspDTO = createResponseDTO(request, authentication);
        if(logger.isDebugEnabled()) {
            logger.debug("authentication success {}, response DTO {}", authentication.getPrincipal(), loginRspDTO);
        }
        this.responseMessageResolver.resolveResponse(request, response, loginRspDTO);
    }

    private <T extends LoginUserBase, R extends BaseDTO<T> & ConfigurableAlerting> R createResponseDTO(HttpServletRequest request, Authentication authentication) {
        R responseDTO = doCreateResponseDTO(request, authentication);
        internalDataHelper.setRequestId(responseDTO, LemonUtils.getRequestId());
        responseDTO.getBody().setSessionId(request.getSession().getId());
        return responseDTO;
    }

    protected abstract <T extends LoginUserBase, R extends BaseDTO<T> & ConfigurableAlerting> R doCreateResponseDTO(HttpServletRequest request, Authentication authentication);

    public void setAuthenticationSuccessPostProcessor(AuthenticationSuccessPostProcessor authenticationSuccessPostProcessor) {
        this.authenticationSuccessPostProcessor = authenticationSuccessPostProcessor;
    }

    public static class LoginUserBase {
        private String sessionId;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }
}
