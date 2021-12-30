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

package com.galaxy.lemon.framework.security.refresh;

import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.utils.LemonUtils;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultTokenService implements TokenService {
    private static final String SESSION_ATTRIBUTE_KEY_ACCESS_TOKEN = "SESSION_ATTRIBUTE_KEY_ACCESS_TOKEN";
    
    private TokenStore tokenStore;
    private int refreshTokenExpiration;
    private boolean reuseRefreshToken = false;
    private DelegatedSessionRepository sessionRepository;
    
    public DefaultTokenService(TokenStore tokenStore, DelegatedSessionRepository sessionRepository, int refreshTokenExpiration) {
        this.tokenStore = tokenStore;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.sessionRepository = sessionRepository;
    }
    
    @Override
    public AccessToken createAccessToken(HttpSession session) {
        RefreshToken refreshToken = createRefreshToken();
        this.tokenStore.storeRefreshToken(refreshToken);
        AccessToken accessToken  = createAccessToken(session, refreshToken);
        session.setAttribute(SESSION_ATTRIBUTE_KEY_ACCESS_TOKEN, accessToken);
        this.tokenStore.storeAccessToken(accessToken);
        return accessToken;
    }
    
    /**
     * @return
     */
    private RefreshToken createRefreshToken() {
        return createRefreshToken(LemonUtils.getUserId());
    }
    
    /**
     * @return
     */
    private RefreshToken createRefreshToken(String userId) {
        LocalDateTime expire = DateTimeUtils.getCurrentLocalDateTime().plusDays(refreshTokenExpiration);
        LemonRefreshToken refreshToken = new LemonRefreshToken(expire);
        refreshToken.setUserId(userId);
        return refreshToken;
    }

    private AccessToken createAccessToken(HttpSession session, RefreshToken refreshToken) {
        LemonAccessToken token = new LemonAccessToken(session.getId());
        token.setRefreshToken(refreshToken);
        return token;
    }

    @Override
    public AccessToken getAccessToken(HttpSession session) {
        return (AccessToken)session.getAttribute(SESSION_ATTRIBUTE_KEY_ACCESS_TOKEN);
    }

    @Override
    public AccessToken refreshAccessToken(HttpSession session, String refreshTokenValue) {
        LemonAccessToken accessToken = new LemonAccessToken(session.getId());
        RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
        if(JudgeUtils.isNull(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid refresh token: " + refreshTokenValue);
        }
        
        //删除accessToken,不一定是原session
        session.removeAttribute(SESSION_ATTRIBUTE_KEY_ACCESS_TOKEN);
        String accessTokenValue = this.tokenStore.removeAccessTokenUsingRefreshToken(refreshTokenValue);
        if(JudgeUtils.isNotBlank(accessTokenValue)) {
            sessionRepository.delete(accessTokenValue);
        }
        
        if(isExpired(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid refresh token (expire): " + refreshTokenValue);
        }
        
        if(! reuseRefreshToken) {
            String userId = null;
            if(refreshToken instanceof LemonRefreshToken) {
                userId = ((LemonRefreshToken) refreshToken).getUserId();
            }
            this.tokenStore.removeRefreshToken(refreshToken);
            refreshToken = createRefreshToken(userId);
            this.tokenStore.storeRefreshToken(refreshToken);
        }
        
        accessToken.setRefreshToken(refreshToken);
        this.tokenStore.storeAccessToken(accessToken);
        session.setAttribute(SESSION_ATTRIBUTE_KEY_ACCESS_TOKEN, accessToken);
        return accessToken;
    }
    
    protected boolean isExpired(RefreshToken refreshToken) {
        if (refreshToken instanceof LemonRefreshToken) {
            LemonRefreshToken expiringToken = (LemonRefreshToken) refreshToken;
            return expiringToken.getExpiration() == null
                    || System.currentTimeMillis() > DateTimeUtils.toEpochMilli(expiringToken.getExpiration());
        }
        return false;
    }

    /* 
     * session 有可能为null
     * @see com.galaxy.lemon.gateway.core.token.TokenService#revokeToken(javax.servlet.http.HttpSession, java.lang.String)
     */
    @Override
    public void revokeToken(HttpSession session, String accessTokenValue) {
        AccessToken accessToken = this.tokenStore.readAccessToken(accessTokenValue);
        this.tokenStore.removeAccessToken(accessTokenValue);
        if(JudgeUtils.isNotNull(accessToken.getRefreshToken())) {
            this.tokenStore.removeRefreshToken(accessToken.getRefreshToken());
        }
        Optional.ofNullable(session).ifPresent(s -> s.removeAttribute(SESSION_ATTRIBUTE_KEY_ACCESS_TOKEN));
    }
    
}
