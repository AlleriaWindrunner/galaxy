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

import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.session.match.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Cookie used for session id
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class CookieHttpSessionRequestMatcher implements RequestMatcher {
    private final String expectedCookieName;

    public CookieHttpSessionRequestMatcher(String expectedCookieName) {
        this.expectedCookieName = expectedCookieName;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return ! Optional.ofNullable(request.getCookies()).map(cookies -> Stream.of(cookies).noneMatch(c -> StringUtils.equals(c.getName(), expectedCookieName))).orElse(true);
    }

}
