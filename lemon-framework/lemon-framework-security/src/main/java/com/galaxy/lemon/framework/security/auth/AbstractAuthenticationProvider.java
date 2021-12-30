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

import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.security.LemonUser;
import com.galaxy.lemon.framework.security.SecurityUtils;
import com.galaxy.lemon.framework.security.UserInfoBase;
import com.galaxy.lemon.framework.security.auth.GenericAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {
    public static final String GENERIC_ROLE = "ROLE_GENERIC";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication afterPreProcessAuthentication = preProcessAuthentication(authentication);
        UserDetails authUser;
        try {
            authUser = resolveAuthentication(afterPreProcessAuthentication);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }

        return createAuthenticationToken(authUser, afterPreProcessAuthentication, authUser);
    }

    /**
     * 认证前处理
     *
     * @param authentication
     * @return
     */
    protected Authentication preProcessAuthentication(Authentication authentication) {
        return authentication;
    }

    protected UserDetails resolveAuthentication(Authentication authentication) {
        UserInfoBase userInfo = this.getAuthenticationProcessor(authentication).processAuthentication(authentication);

        doAfterAuthentication(userInfo);

        return new LemonUser(userInfo.getLoginName(), userInfo, true, true, true, true, resolveGrantedAuthority());
    }

    /**
     * 获取AuthenticationProcessor
     *
     * @param authentication
     * @return
     */
    protected abstract AuthenticationProcessor getAuthenticationProcessor(Authentication authentication);

    protected void doAfterAuthentication(UserInfoBase userInfo) {
        SecurityUtils.setLoginUserAfterLoginRequest(userInfo);
        LemonDataHolder.getLemonData().setLoginName(userInfo.getLoginName());
        LemonDataHolder.getLemonData().setUserId(userInfo.getUserId());
    }

    protected List<GrantedAuthority> resolveGrantedAuthority() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(GENERIC_ROLE));
        return grantedAuthorities;
    }

    protected Authentication createAuthenticationToken(Object principal, Authentication authentication, UserDetails user) {
        GenericAuthenticationToken genericAuthenticationToken = new GenericAuthenticationToken(principal, user.getAuthorities());
        genericAuthenticationToken.setDetails(authentication.getDetails());
        return genericAuthenticationToken;
    }
}
