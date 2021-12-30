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

package com.galaxy.lemon.gateway.core.log;

import com.galaxy.lemon.common.log.AccessLogger;
import com.galaxy.lemon.common.log.RequestInfo;
import com.galaxy.lemon.common.log.ResponseInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SimpleGatewayAccessLoggerAdapter extends GatewayAccessLoggerAdapter {

    public SimpleGatewayAccessLoggerAdapter(AccessLogger accessLogger) {
        super(accessLogger);
    }

    @Override
    protected void customize(RequestInfo.Builder builder) {

    }

    @Override
    protected Object resolveRequestTarget(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return "UNKNOWN";
    }

    @Override
    protected void customize(ResponseInfo.Builder builder) {

    }

    @Override
    protected Object resolveResponseTarget(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return "UNKNOWN";
    }
}
