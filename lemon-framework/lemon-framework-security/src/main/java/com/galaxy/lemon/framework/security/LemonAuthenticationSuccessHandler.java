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

import com.galaxy.lemon.common.utils.BeanUtils;
import com.galaxy.lemon.framework.data.DefaultRspDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.galaxy.lemon.framework.security.callback.AuthenticationSuccessPostProcessor;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * auth success
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonAuthenticationSuccessHandler extends AbstractAuthenticationSuccessHandler {

    private RefreshTokenService refreshTokenService;

    public LemonAuthenticationSuccessHandler(ResponseMessageResolver responseMessageResolver,
                                             RefreshTokenService refreshTokenService,
                                             InternalDataHelper internalDataHelper) {
        this(responseMessageResolver, refreshTokenService, internalDataHelper, null);

    }

    public LemonAuthenticationSuccessHandler(ResponseMessageResolver responseMessageResolver,
                                             RefreshTokenService refreshTokenService,
                                             InternalDataHelper internalDataHelper,
                                             AuthenticationSuccessPostProcessor authenticationSuccessPostProcessor) {
        super(responseMessageResolver, internalDataHelper, authenticationSuccessPostProcessor);
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected DefaultRspDTO<LoginUser> doCreateResponseDTO(HttpServletRequest request, Authentication authentication) {
        LoginUser loginUser = new LoginUser();
        if (!this.refreshTokenService.isDummy()) {
            loginUser.setRefreshToken(this.refreshTokenService.resolveRefreshToken(request.getSession()));
        }
        BeanUtils.copyProperties(loginUser, SecurityUtils.getLoginUser());
        return DefaultRspDTO.newSuccessInstance(loginUser);
    }

    /**
     * @author yuzhou
     * @date 2017年8月11日
     * @time 下午12:06:44
     *
     */
    public static class LoginUser extends LoginUserBase {
        private String userId;
        private String loginName;
        private String refreshToken;
        
        public String getRefreshToken() {
            return refreshToken;
        }
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }
        public String getMblNo() {
            return "";
        }
    }
}
