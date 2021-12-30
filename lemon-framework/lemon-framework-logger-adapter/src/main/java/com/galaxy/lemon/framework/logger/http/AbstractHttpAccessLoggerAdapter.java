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

package com.galaxy.lemon.framework.logger.http;

import com.galaxy.lemon.common.log.AccessLogger;
import com.galaxy.lemon.common.log.RequestInfo;
import com.galaxy.lemon.common.log.ResponseInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractHttpAccessLoggerAdapter implements HttpAccessLogger {

    private static final String ALREADY_PRINT_REQUEST_LOG = "ALREADY_PRINT_REQUEST_LOG";
    private static final String ALREADY_PRINT_RESPONSE_LOG = "ALREADY_PRINT_RESPONSE_LOG";

    protected AccessLogger accessLogger;

    public AbstractHttpAccessLoggerAdapter(AccessLogger accessLogger) {

        this.accessLogger = accessLogger;
    }

    public void request(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        request(httpServletRequest, httpServletResponse, resolveRequestTarget(httpServletRequest, httpServletResponse));
    }

    public void request(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object target) {
        if(!hasAlreadyPrintRequestLog(httpServletRequest)) {
            RequestInfo.Builder builder = createRequestInfoBuilder(httpServletRequest, httpServletResponse, target);
            this.accessLogger.request(builder.build());
            setAlreadyPrintRequestLog(httpServletRequest);
        }
    }

    protected abstract RequestInfo.Builder createRequestInfoBuilder(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object target);

    public void response(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        response(httpServletRequest, httpServletResponse, resolveResponseTarget(httpServletRequest, httpServletResponse));
    }

    public void response(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object target) {
        if (!hasAlreadyPrintResponseLog(httpServletRequest)) {
            ResponseInfo.Builder builder = createResponseInfoBuilder(httpServletRequest, httpServletResponse, target);
            this.accessLogger.response(builder.build());
            setAlreadyPrintResponseLog(httpServletRequest);
        }
    }

    protected abstract ResponseInfo.Builder createResponseInfoBuilder(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object target);

    private boolean hasAlreadyPrintRequestLog(HttpServletRequest httpServletRequest) {
        return Optional.ofNullable(httpServletRequest.getAttribute(ALREADY_PRINT_REQUEST_LOG)).map(f -> (Boolean) f).orElse(Boolean.FALSE);
    }

    private void setAlreadyPrintRequestLog(HttpServletRequest httpServletRequest) {
        httpServletRequest.setAttribute(ALREADY_PRINT_REQUEST_LOG, Boolean.TRUE);
    }

    protected abstract Object resolveRequestTarget(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    private boolean hasAlreadyPrintResponseLog(HttpServletRequest httpServletRequest) {
        return Optional.ofNullable(httpServletRequest.getAttribute(ALREADY_PRINT_RESPONSE_LOG)).map(f -> (Boolean) f).orElse(Boolean.FALSE);
    }

    private void setAlreadyPrintResponseLog(HttpServletRequest httpServletRequest) {
        httpServletRequest.setAttribute(ALREADY_PRINT_RESPONSE_LOG, Boolean.TRUE);
    }

    protected abstract Object resolveResponseTarget(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
