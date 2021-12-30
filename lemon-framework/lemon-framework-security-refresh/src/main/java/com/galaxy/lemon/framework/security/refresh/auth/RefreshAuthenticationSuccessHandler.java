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

import com.galaxy.lemon.common.utils.BeanUtils;
import com.galaxy.lemon.framework.data.DefaultRspDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.galaxy.lemon.framework.security.AbstractAuthenticationSuccessHandler;
import com.galaxy.lemon.framework.security.LemonAuthenticationSuccessHandler;
import com.galaxy.lemon.framework.security.RefreshTokenService;
import com.galaxy.lemon.framework.security.SecurityUtils;
import com.galaxy.lemon.framework.security.callback.AuthenticationSuccessPostProcessor;
import com.galaxy.lemon.framework.security.refresh.RefreshToken;
import com.galaxy.lemon.framework.security.refresh.auth.RefreshTokenAuthenticationProvider;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RefreshAuthenticationSuccessHandler extends AbstractAuthenticationSuccessHandler {

    private RefreshTokenService refreshTokenService;

    public RefreshAuthenticationSuccessHandler(ResponseMessageResolver responseMessageResolver,
                                               RefreshTokenService refreshTokenService,
                                               InternalDataHelper internalDataHelper) {
        this(responseMessageResolver, refreshTokenService, internalDataHelper, null);

    }

    public RefreshAuthenticationSuccessHandler(ResponseMessageResolver responseMessageResolver,
                                               RefreshTokenService refreshTokenService,
                                               InternalDataHelper internalDataHelper,
                                               AuthenticationSuccessPostProcessor authenticationSuccessPostProcessor) {
        super(responseMessageResolver, internalDataHelper, authenticationSuccessPostProcessor);
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected DefaultRspDTO<LemonAuthenticationSuccessHandler.LoginUser> doCreateResponseDTO(HttpServletRequest request, Authentication authentication) {
        LemonAuthenticationSuccessHandler.LoginUser loginUser = new LemonAuthenticationSuccessHandler.LoginUser();
        loginUser.setRefreshToken(getRefreshToken(request));
        BeanUtils.copyProperties(loginUser, SecurityUtils.getLoginUser());
        return DefaultRspDTO.newSuccessInstance(loginUser);
    }

    private String getRefreshToken(HttpServletRequest httpServletRequest) {
        Object refreshTokenObject = httpServletRequest.getAttribute(RefreshTokenAuthenticationProvider.REQUEST_ATTRIBUTE_REFRESH_TOKEN);
        if (null == refreshTokenObject) {
            return this.refreshTokenService.extractRefreshToken(httpServletRequest.getSession());
        }
        RefreshToken refreshToken = (RefreshToken) refreshTokenObject;
        return refreshToken.getValue();
    }
}
