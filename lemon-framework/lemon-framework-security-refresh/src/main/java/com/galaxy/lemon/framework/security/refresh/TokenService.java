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

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;

import javax.servlet.http.HttpSession;


/**
 * token service
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface TokenService {
    AccessToken createAccessToken(HttpSession session);
    
    AccessToken getAccessToken(HttpSession session);
    
    AccessToken refreshAccessToken(HttpSession session, String refreshTokenValue);
    
    void revokeToken(HttpSession session, String accessTokenValue);

    class InvalidRefreshTokenException extends LemonException {
        private static final long serialVersionUID = -2429093621333533895L;
        public InvalidRefreshTokenException(String msgInfo) {
            super(ErrorMsgCode.REFRESH_TOKEN_INVALID.getMsgCd(), msgInfo);
        }
    }
}
