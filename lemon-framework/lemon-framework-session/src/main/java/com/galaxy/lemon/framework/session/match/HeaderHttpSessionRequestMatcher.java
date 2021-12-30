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

package com.galaxy.lemon.framework.session.match;


import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class HeaderHttpSessionRequestMatcher implements RequestMatcher {
    private final static String INDICATED_HEADER_SESSION_ID = "header";
    private final String expectedHeaderName;

    public HeaderHttpSessionRequestMatcher(String expectedHeaderName) {
        this.expectedHeaderName = expectedHeaderName;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return StringUtils.isNotBlank(request.getHeader(expectedHeaderName))
                || StringUtils.equalsIgnoreCase(request.getHeader(LemonConstants.HTTP_HEADER_SESSION_ID_STRATEGY), INDICATED_HEADER_SESSION_ID);
    }

}
