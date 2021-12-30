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

import javax.servlet.http.HttpSession;

/**
 * 刷新session，保持session
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface RefreshTokenService {
    /**
     * resolve refresh token
     * @param httpSession
     * @return refresh token
     */
    String resolveRefreshToken(HttpSession httpSession);

    /**
     * extract refresh token
     * @param httpSession
     * @return
     */
    String extractRefreshToken(HttpSession httpSession);

    /**
     * revoke refresh token
     * @param sessionId current session id
     */
    void revokeRefreshToken(String sessionId);

    /**
     * is the current object a dummy RefreshTokenService
     * @return
     */
    default boolean isDummy() {
        return this instanceof DummyRefreshTokenService;
    }

    class DummyRefreshTokenService implements RefreshTokenService {

        @Override
        public String resolveRefreshToken(HttpSession httpSession) {
            return null;
        }

        @Override
        public String extractRefreshToken(HttpSession httpSession) {
            return null;
        }

        @Override
        public void revokeRefreshToken(String sessionId) {
        }
    }
}
