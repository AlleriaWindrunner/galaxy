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

package com.galaxy.lemon.framework.session.support;

import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class RequiredExpireSessionSupport extends PrincipalNameIndexNameSessionRepositorySupport {

    private SessionRepository<ExpiringSession> sessionRepository;

    public RequiredExpireSessionSupport(SessionRepository<ExpiringSession> sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * 判断sessionInformation是否需要过期处理
     *
     * @param sessionInformation 判断session是否需要过期处理
     * @param currentPrincipal 当前session中的principal
     * @return
     */
    public boolean requireExpire(SessionInformation sessionInformation, Object currentPrincipal) {
        ExpiringSession eSession = this.sessionRepository.getSession(sessionInformation.getSessionId());
        if(null == eSession || eSession.isExpired()) {
            return false;
        }

        return requireExpireConditionalOn(eSession, currentPrincipal);
    }

    /**
     * 根据业务情况判断是否要过期session
     *
     * @param expiringSession  是否需要过期的session
     * @param currentPrincipal 当前session principal
     * @return
     */
    protected abstract boolean requireExpireConditionalOn(ExpiringSession expiringSession, Object currentPrincipal);

    public static class SessionInformation {
        private String sessionId;
        private Object principal;

        public SessionInformation(String sessionId, Object principal) {
            this.sessionId = sessionId;
            this.principal = principal;
        }

        public String getSessionId() {
            return sessionId;
        }

        public Object getPrincipal() {
            return principal;
        }

        public static SessionInformation newInstance(String sessionId, Object principal) {
            return new SessionInformation(sessionId, principal);
        }
    }

}
