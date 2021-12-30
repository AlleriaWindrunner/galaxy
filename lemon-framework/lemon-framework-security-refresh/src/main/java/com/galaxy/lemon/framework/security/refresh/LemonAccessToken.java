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
import com.galaxy.lemon.common.utils.JudgeUtils;

import java.io.Serializable;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonAccessToken implements AccessToken, Serializable {
    private static final long serialVersionUID = -1658867699790775874L;
    
    private String value;
    private RefreshToken refreshToken;

    public LemonAccessToken(){}

    public LemonAccessToken(String value) {
        this.value = value;
        if(JudgeUtils.isBlank(this.value)) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR.getMsgCd(), "the access token is blank.");
        }
    }
    @Override
    public String getValue() {
        return this.value;
    }
    public RefreshToken getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }
    public void setValue(String value) {
        this.value = value;
    }

}
