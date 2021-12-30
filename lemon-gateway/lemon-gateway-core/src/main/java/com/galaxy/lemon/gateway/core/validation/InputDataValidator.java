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

package com.galaxy.lemon.gateway.core.validation;

import com.galaxy.lemon.common.LemonConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * 输入参数检查
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface InputDataValidator {
    //default sensitive parameter names
    String[] SENSITIVE_PARAMETER_NAMES = new String[]{"userId", "entryTx", "requestId", "msgId", "loginName"};
    String[] SENSITIVE_HEADER_NAMES = new String[]{LemonConstants.HTTP_HEADER_USER_ID,
            LemonConstants.HTTP_HEADER_REQUEST_ID,
            LemonConstants.HTTP_HEADER_CLIENT_IP,
            LemonConstants.HTTP_HEADER_SOURCE,
            LemonConstants.HTTP_HEADER_BUSINESS,
            LemonConstants.HTTP_HEADER_URI,
            LemonConstants.HTTP_HEADER_LOGIN_NAME};

    boolean validateInputData(HttpServletRequest httpServletRequest);

    /**
     * 默认的敏感参数
     * @return
     */
    default String[] getDefaultSensitiveParameterNames() {
        return SENSITIVE_PARAMETER_NAMES;
    }

    /**
     * 默认的敏感http header
     * @return
     */
    default String[] getDefaultSensitiveHeaderNames() {
        return SENSITIVE_HEADER_NAMES;
    }
}
