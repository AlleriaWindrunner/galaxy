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

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.security.session.PrincipalNameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 提取需要过期的session
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractRequiredExpireSessionExtractor implements RequiredExpireSessionExtractor {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractRequiredExpireSessionExtractor.class);

    private SessionRegistry sessionRegistry;
    private PrincipalNameResolver principalNameResolver;

    public AbstractRequiredExpireSessionExtractor(SessionRegistry sessionRegistry,
                                                  PrincipalNameResolver principalNameResolver) {
        this.sessionRegistry = sessionRegistry;
        this.principalNameResolver = principalNameResolver;
    }

    @Override
    public List<SessionInformation> extractRequiredExpireSessions(Object principal, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object principalNameIndexName = this.resolvePrincipalNameIndexName(principal, request);
        if (null == principalNameIndexName) {
            //getAllSessions 自己解决principalNameIndexName
            principalNameIndexName = principal;
        }
        List<SessionInformation> sessionInformations = this.sessionRegistry.getAllSessions(principalNameIndexName, false);

        return Optional.ofNullable(sessionInformations).map(s -> s.stream().filter(si -> JudgeUtils.notEquals(session.getId(), si.getSessionId()))
                .filter(si -> requireExpire(si, principal)).collect(Collectors.toList())).orElse(null);
    }

    protected Object resolvePrincipalNameIndexName(Object principal, HttpServletRequest request){
        return this.principalNameResolver.resolve(principal);
    }

    public abstract boolean requireExpire(SessionInformation sessionInformation, Object currentPrincipal);

}
