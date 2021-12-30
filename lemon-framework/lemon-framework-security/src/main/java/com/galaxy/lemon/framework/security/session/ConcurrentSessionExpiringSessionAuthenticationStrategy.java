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

package com.galaxy.lemon.framework.security.session;

import com.galaxy.lemon.framework.security.RefreshTokenService;
import com.galaxy.lemon.framework.security.session.RequiredExpireSessionExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 解决多终端登录、多session共存策略
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ConcurrentSessionExpiringSessionAuthenticationStrategy implements SessionAuthenticationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentSessionExpiringSessionAuthenticationStrategy.class);

    private RefreshTokenService refreshTokenService;
    private RequiredExpireSessionExtractor requiredExpireSessionExtractor;
    
    public ConcurrentSessionExpiringSessionAuthenticationStrategy(RequiredExpireSessionExtractor requiredExpireSessionExtractor,
                                                                  RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
        this.requiredExpireSessionExtractor = requiredExpireSessionExtractor;
    }
    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response)
        throws SessionAuthenticationException {
        Optional.ofNullable(this.requiredExpireSessionExtractor).map(e -> e.extractRequiredExpireSessions(authentication.getPrincipal(), request))
                .ifPresent(ss -> ss.stream().forEach(this::expireNow));
    }

    public void expireNow(SessionInformation sessionInformation) {
        if(logger.isDebugEnabled()) {
            logger.debug("expire session {}, principal {}", sessionInformation.getSessionId(), sessionInformation.getPrincipal());
        }
        sessionInformation.expireNow();
        this.refreshTokenService.revokeRefreshToken(sessionInformation.getSessionId());
    }

}
