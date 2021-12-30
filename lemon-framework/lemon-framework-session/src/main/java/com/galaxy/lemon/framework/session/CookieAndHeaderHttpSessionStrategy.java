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

package com.galaxy.lemon.framework.session;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.framework.session.match.CookieHttpSessionRequestMatcher;
import com.galaxy.lemon.framework.session.match.HeaderHttpSessionRequestMatcher;
import com.galaxy.lemon.framework.session.match.RequestMatcher;
import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 同时支持header和cookie
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class CookieAndHeaderHttpSessionStrategy implements HttpSessionStrategy {

    private HttpSessionStrategy cookieHttpSessionStrategy;
    private HttpSessionStrategy headerHttpSessionStrategy;
    private RequestMatcher headerStrategyMatcher;
    private RequestMatcher cookieStrategyMatcher;
    //    private boolean onlyCookie = false;
//    private boolean onlyHeader = false;
    private SessionIdStrategy sessionIdStrategy;

    public CookieAndHeaderHttpSessionStrategy() {
        this(LemonConstants.COOKIE_SESSION_ID, LemonConstants.HTTP_HEADER_TOKEN, SessionIdStrategy.HeaderOrCookie);
    }

    public CookieAndHeaderHttpSessionStrategy(String cookieName, String headName, SessionIdStrategy sessionIdStrategy) {
        CookieHttpSessionStrategy cookieHttpSessionStrategy = new CookieHttpSessionStrategy();
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookieName(cookieName);
        cookieHttpSessionStrategy.setCookieSerializer(cookieSerializer);
        this.cookieHttpSessionStrategy = cookieHttpSessionStrategy;
        HeaderHttpSessionStrategy headerHttpSessionStrategy = new HeaderHttpSessionStrategy();
        headerHttpSessionStrategy.setHeaderName(headName);
        this.headerHttpSessionStrategy = headerHttpSessionStrategy;
        this.headerStrategyMatcher = new HeaderHttpSessionRequestMatcher(headName);
        this.cookieStrategyMatcher = new CookieHttpSessionRequestMatcher(cookieName);
        this.sessionIdStrategy = sessionIdStrategy;
//        this.onlyCookie = onlyCookie;
//        this.onlyHeader = onlyHeader;
//        if (this.onlyCookie && this.onlyHeader) {
//            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Could not are true for \"onlyCookie\" and \"onlyHeader\".");
//        }
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        return this.getHttpSessionStrategy(request).getRequestedSessionId(request);
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request,
                             HttpServletResponse response) {
        this.getHttpSessionStrategy(request).onNewSession(session, request, response);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request,
                                    HttpServletResponse response) {
        this.getHttpSessionStrategy(request).onInvalidateSession(request, response);
    }

    /**
     * 获取恰当的策略
     *
     * @param request
     * @return
     */
    public HttpSessionStrategy getHttpSessionStrategy(HttpServletRequest request) {
        switch (this.sessionIdStrategy) {
            case Cookie:
                return this.cookieHttpSessionStrategy;
            case Header:
                return this.headerHttpSessionStrategy;
            case CookieOrHeader:
                return this.cookieStrategyMatcher.matches(request) ? this.cookieHttpSessionStrategy : this.headerHttpSessionStrategy;
            case HeaderOrCookie:
                return this.headerStrategyMatcher.matches(request) ? this.headerHttpSessionStrategy : this.cookieHttpSessionStrategy;
            default:
                throw LemonException.create(ErrorMsgCode.SYS_ERROR, "This session id strategy is not supported.");
        }
//        if (this.onlyHeader) {
//            return this.headerHttpSessionStrategy;
//        }
//        if (this.onlyCookie) {
//            return this.cookieHttpSessionStrategy;
//        }
//
//        if (preferredCookie) {
//            return cookieStrategyMatcher.matches(request) ? this.cookieHttpSessionStrategy : this.headerHttpSessionStrategy;
//        }
//        return headerStrategyMatcher.matches(request) ? this.headerHttpSessionStrategy : this.cookieHttpSessionStrategy;
    }
}
