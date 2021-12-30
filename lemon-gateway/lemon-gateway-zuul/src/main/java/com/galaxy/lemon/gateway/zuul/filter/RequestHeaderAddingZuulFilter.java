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

package com.galaxy.lemon.gateway.zuul.filter;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.framework.utils.LemonUtils;
import com.galaxy.lemon.gateway.zuul.ZuulHelper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * set parameters for lemon framework
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RequestHeaderAddingZuulFilter extends ZuulFilter {

    private static final String SYS_CHANNEL = "x-ics-sysCnl";
    private static final String BUS_CHANNEL = "x-ics-busCnl";

    private ZuulHelper zuulHelper;
    
    public RequestHeaderAddingZuulFilter(ZuulHelper zuulHelper) {
        this.zuulHelper = zuulHelper;
    }
    
    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        ctx.addZuulRequestHeader(LemonConstants.HTTP_HEADER_REQUEST_ID, LemonUtils.getRequestId());
        ctx.addZuulRequestHeader(LemonConstants.HTTP_HEADER_LOCALE, String.valueOf(LemonUtils.getLocale()));
        ctx.addZuulRequestHeader(LemonConstants.HTTP_HEADER_SOURCE, LemonUtils.getApplicationName());
        ctx.addZuulRequestHeader(LemonConstants.HTTP_HEADER_BUSINESS, this.zuulHelper.getCurrentZuulRoute(request).getId());
        ctx.addZuulRequestHeader(LemonConstants.HTTP_HEADER_URI, this.zuulHelper.getCurrentRequestURI(request));
        ctx.addZuulRequestHeader(LemonConstants.HTTP_HEADER_TOKEN, LemonUtils.getToken());
        if (null != request) {
            Optional.ofNullable(request.getHeader(SYS_CHANNEL)).ifPresent(s -> ctx.addZuulRequestHeader(SYS_CHANNEL, s));
            Optional.ofNullable(request.getHeader(BUS_CHANNEL)).ifPresent(b -> ctx.addZuulRequestHeader(BUS_CHANNEL, b));
        }
        return null;
    }

    @Override
    public String filterType() {
        return FilterType.PRE.lowerCaseName();
    }

    @Override
    public int filterOrder() {
        return FilterConstants.ORDER_PRE_ADDING_HTTP_HEADER;
    }

}
