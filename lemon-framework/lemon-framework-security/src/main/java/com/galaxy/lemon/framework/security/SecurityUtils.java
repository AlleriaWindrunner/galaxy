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

import com.galaxy.lemon.common.context.LemonContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SecurityUtils {
    
    public static final String LEMON_CONTEXT_LOGIN_USER_INFO = "LEMON_CONTEXT_LOGIN_USER_INFO";
    /**
     * 获取登录用户信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public static UserInfoBase getLoginUser() {
        Object principal = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication()).map(a -> a.getPrincipal()).orElse(null);
        return Optional.ofNullable(principal).filter(p -> p instanceof LemonUser).map(pr -> (LemonUser)pr).map(lu -> lu.getUserInfo())
            .orElseGet(() -> SecurityUtils.getLoginUserAfterLoginRequest());
    }
    
    /**
     * 仅仅在登录请求交易中能取得到登录用户信息
     * @return
     */
    public static UserInfoBase getLoginUserAfterLoginRequest() {
        return Optional.ofNullable(LemonContext.getCurrentContext()).map(c -> c.get(LEMON_CONTEXT_LOGIN_USER_INFO)).map(UserInfoBase.class::cast).orElse(null);
    }
    
    /**
     * 用户登录成功完成验证后设置
     * @param userInfo
     */
    public static void setLoginUserAfterLoginRequest(UserInfoBase userInfo) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_LOGIN_USER_INFO, userInfo);
    }
    
    /**
     * 登录用户ID
     * @return
     */
    public static String getLoginUserId() {
        return Optional.ofNullable(getLoginUser()).map(u -> u.getUserId()).orElse(null);
    }
    
    /**
     * 获取登录名
     * @return
     */
    public static String getLoginName() {
        return Optional.ofNullable(getLoginUser()).map(u -> u.getLoginName()).orElse(null);
    }
    
}
