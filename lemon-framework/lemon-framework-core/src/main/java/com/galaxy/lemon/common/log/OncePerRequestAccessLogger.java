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

package com.galaxy.lemon.common.log;

import com.galaxy.lemon.common.context.LemonContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class OncePerRequestAccessLogger implements AccessLogger {
    private static final String CONTEXT_KEY_ALREADY_PRINT_RESPONSE_LOG = "CONTEXT_KEY_ALREADY_PRINT_RESPONSE.";
    private static final String CONTEXT_KEY_ALREADY_PRINT_REQUEST_LOG = "CONTEXT_KEY_ALREADY_PRINT_REQUEST.";

    protected Logger logger;

    public OncePerRequestAccessLogger(Logger logger) {
        this.logger = logger;
    }

    public OncePerRequestAccessLogger(Class<?> logClass) {
        this.logger = LoggerFactory.getLogger(logClass);
    }

    @Override
    public <T, REQ extends RequestInfo<T>> void request(REQ requestInfo) {
        if (alreadyPrintRequestLog()) {
            return;
        }
        logRequest(requestInfo);
        setAlreadyPrintRequestLog();
    }

    public abstract void logRequest(RequestInfo<?> requestInfo);

    @Override
    public <T, RSP extends ResponseInfo<T>> void response(RSP responseInfo) {
        if (alreadyPrintResponseLog()) {
            return;
        }
        logResponse(responseInfo);
        setAlreadyPrintResponseLog();
    }

    public abstract void logResponse(ResponseInfo<?> responseInfo);

    public boolean alreadyPrintResponseLog() {
        return LemonContext.getCurrentContext().getBoolean(resolveContextKey(CONTEXT_KEY_ALREADY_PRINT_RESPONSE_LOG), Boolean.FALSE);
    }

    public void setAlreadyPrintResponseLog() {
        LemonContext.getCurrentContext().put(resolveContextKey(CONTEXT_KEY_ALREADY_PRINT_RESPONSE_LOG), Boolean.TRUE);
    }

    public boolean alreadyPrintRequestLog(){
        return LemonContext.getCurrentContext().getBoolean(resolveContextKey(CONTEXT_KEY_ALREADY_PRINT_REQUEST_LOG), Boolean.FALSE);
    }

    public void setAlreadyPrintRequestLog() {
        LemonContext.getCurrentContext().put(resolveContextKey(CONTEXT_KEY_ALREADY_PRINT_REQUEST_LOG), Boolean.TRUE);
    }

    public String resolveContextKey(String prefix) {
        return prefix.concat(this.getClass().getName());
    }
}
