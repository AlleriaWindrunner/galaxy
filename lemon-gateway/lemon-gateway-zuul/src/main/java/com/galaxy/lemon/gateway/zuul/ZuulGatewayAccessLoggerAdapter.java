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

package com.galaxy.lemon.gateway.zuul;

import com.galaxy.lemon.common.log.AccessLogger;
import com.galaxy.lemon.common.log.RequestInfo;
import com.galaxy.lemon.common.log.ResponseInfo;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.gateway.core.log.GatewayAccessLoggerAdapter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ZuulGatewayAccessLoggerAdapter extends GatewayAccessLoggerAdapter {

    private ZuulHelper zuulHelper;

    public ZuulGatewayAccessLoggerAdapter(AccessLogger accessLogger, ZuulHelper zuulHelper) {
        super(accessLogger);
        this.zuulHelper = zuulHelper;
    }
    @Override
    protected void customize(RequestInfo.Builder builder) {

    }

    @Override
    protected Object resolveRequestTarget(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return target(httpServletRequest);
    }

    @Override
    protected void customize(ResponseInfo.Builder builder) {

    }

    @Override
    protected Object resolveResponseTarget(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return Optional.ofNullable(RequestContext.getCurrentContext().getResponseBody()).orElseGet(()
                -> Optional.ofNullable(zuulHelper.getResponseDataStream()).map(String::new).orElse(null));
    }

    private Object target(HttpServletRequest httpServletRequest) {
        Map<String,String[]> parameterMap = httpServletRequest.getParameterMap();
        String requestBody = zuulHelper.getCurrentRequestBody(httpServletRequest);
        if (JudgeUtils.isNotEmpty(parameterMap) && JudgeUtils.isNotBlank(requestBody)) {
            Map<String, Object> target = new HashMap<>();
            target.put("ParametersMap", parameterMap);
            target.put("Body", requestBody);
            return target;
        } else {
            if (JudgeUtils.isNotBlank(requestBody)) {
                return requestBody;
            } else if (JudgeUtils.isNotEmpty(parameterMap)){
                return parameterMap;
            }
        }
        return null;
    }
}
