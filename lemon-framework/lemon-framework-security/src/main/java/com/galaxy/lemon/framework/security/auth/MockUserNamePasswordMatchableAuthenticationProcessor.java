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

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.codec.CodecException;
import com.galaxy.lemon.common.codec.ObjectDecoder;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.security.LemonAuthenticationException;
import com.galaxy.lemon.framework.security.SimpleUserInfo;
import com.galaxy.lemon.framework.security.UserInfoBase;

import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class MockUserNamePasswordMatchableAuthenticationProcessor extends AbstractGenericMatchableAuthenticationProcessor<GenericAuthenticationToken> {
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    private ObjectDecoder objectDecoder;

    public MockUserNamePasswordMatchableAuthenticationProcessor(String filterProcessesUrl,
                                                                ObjectDecoder objectDecoder) {
        super(filterProcessesUrl);
        this.objectDecoder = objectDecoder;
    }

    @Override
    protected UserInfoBase doProcessAuthentication(GenericAuthenticationToken genericAuthenticationToken) {
        AuthenticationRequest authenticationRequest = genericAuthenticationToken.getAuthenticationRequest();
        Map<String, String> authenticationRequestParameters = null;
        try {
            authenticationRequestParameters = this.objectDecoder.readValue(getRequestInputStream(authenticationRequest), Map.class);
        } catch (CodecException e) {
            LemonException.throwLemonException(ErrorMsgCode.AUTHENTICATION_FAILURE, e);
        }
        if (JudgeUtils.isEmpty(authenticationRequestParameters)) {
            LemonException.throwLemonException(ErrorMsgCode.AUTHENTICATION_FAILURE, "No authentication parameter found in request body.");
        }
        if (JudgeUtils.equals(authenticationRequestParameters.get(USER_NAME), "mock") &&
                JudgeUtils.equals(authenticationRequestParameters.get(PASSWORD), "mock123")) {
            return new SimpleUserInfo("mock123456", "mock", "12345678900");

        }
        throw new LemonAuthenticationException(MockMsgCode.USER_NAME_OR_PASSWORD_INVALID);
    }

    public enum MockMsgCode implements AlertCapable {
        USER_NAME_OR_PASSWORD_INVALID("AGW00001", "Invalid username or password.");

        private String msgCd;
        private String msgInfo;

        MockMsgCode(String msgCd, String msgInfo) {
            this.msgCd = msgCd;
            this.msgInfo = msgInfo;
        }

        @Override
        public String getMsgCd() {
            return this.msgCd;
        }

        @Override
        public String getMsgInfo() {
            return this.msgInfo;
        }
    }
}
