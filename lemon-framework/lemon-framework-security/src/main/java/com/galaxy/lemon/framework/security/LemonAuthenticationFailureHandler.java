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

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(LemonAuthenticationFailureHandler.class);
    private ResponseMessageResolver responseMessageResolver;
    
    public LemonAuthenticationFailureHandler(ResponseMessageResolver responseMessageResolver) {
        this.responseMessageResolver = responseMessageResolver;
    }
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponseStatusCode.SC_AUTHENTICATION_FAILURE);
        if(exception instanceof LemonAuthenticationException) {
            LemonAuthenticationException lae = (LemonAuthenticationException) exception;
            if (null != lae.getStatusCode()) {
                response.setStatus(lae.getStatusCode());
            }
            AlertCapable alertCapable = lae.getAlertCapable();
            if (null != alertCapable) {
                if (alertCapable instanceof BaseDTO) {
                    BaseDTO<?> rspDTO = (BaseDTO) alertCapable;
                    rspDTO.setBody(null);
                    this.responseMessageResolver.resolveResponse(request, response, rspDTO);
                    if(logger.isDebugEnabled()) {
                        logger.debug("authentication failure, response dto {}", rspDTO);
                    }
                    return;
                }
                this.responseMessageResolver.resolve(request, response, alertCapable);
                return;
            }
            this.responseMessageResolver.resolve(request, response, ErrorMsgCode.AUTHENTICATION_FAILURE);
            return;
        }

        AlertCapable alert;
        if (exception instanceof AlertCapable) {
            alert = (AlertCapable)exception;
        } else if (exception.getCause() instanceof AlertCapable) {
            alert = (AlertCapable) exception.getCause();
        } else {
            alert = ErrorMsgCode.SYS_ERROR;
        }
        if (logger.isErrorEnabled()) {
            logger.error("authentication failure, msgCd is "+alert.getMsgCd(), exception);
        }
        this.responseMessageResolver.resolve(request, response, alert);
    }
}
