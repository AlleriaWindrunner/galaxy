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

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.IOUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.gateway.core.GatewayHelper;
import com.galaxy.lemon.gateway.zuul.ZuulExtensionProperties.ZuulRoute;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * zuul helper
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ZuulHelper {
    private static final Logger logger = LoggerFactory.getLogger(ZuulHelper.class);
    
    public static final String CTX_CURRENT_REQUEST_URI = "LEMON_CURRENT_REQUEST_URI";
    public static final String CTX_CURRENT_ZUUL_ROUTE = "LEMON_CURRENT_ZUUL_ROUTE";
    public static final String CTX_CURRENT_PATH_MATCHERS = "LEMON_CURRENT_PATH_MATCHERS";
    public static final String CTX_CURRENT_BODY_STRING = "LEMON_CURRENT_BODY_STRING";
    

    private UrlPathHelper urlPathHelper;
    private RouteLocator routeLocator;
    private ZuulExtensionProperties zuulExtensionProperties;
    private PathMatcher pathMatcher = new AntPathMatcher();

    public ZuulHelper(UrlPathHelper urlPathHelper,
                      RouteLocator routeLocator,
                      ZuulExtensionProperties zuulExtensionProperties) {
        this.urlPathHelper = urlPathHelper;
        this.routeLocator = routeLocator;
        this.zuulExtensionProperties = zuulExtensionProperties;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, String> getCurrentPathMatchers(HttpServletRequest httpServletRequest){
        RequestContext ctx = RequestContext.getCurrentContext();
        return Optional.ofNullable(ctx.get(CTX_CURRENT_PATH_MATCHERS)).map(m -> (Map<String, String>) m)
            .orElseGet(() ->{
                String requestURI = this.getCurrentRequestURI(httpServletRequest);
                ZuulRoute zuulRoute = this.getCurrentZuulRoute(httpServletRequest);
                Map<String, String> matchedPaths = pathMatcher.extractUriTemplateVariables(zuulRoute.getPath(), requestURI);
                ctx.set(CTX_CURRENT_PATH_MATCHERS, matchedPaths);
                return matchedPaths;
            });
        
    }
    
    public String getCurrentRequestURI(HttpServletRequest httpServletRequest) {
        RequestContext ctx = RequestContext.getCurrentContext();
        return Optional.ofNullable(ctx.get(CTX_CURRENT_REQUEST_URI)).map(m -> (String)m)
            .orElseGet(() ->{
                String requestURI0 = this.urlPathHelper.getPathWithinApplication(httpServletRequest);
                ctx.set(CTX_CURRENT_REQUEST_URI, requestURI0);
                return requestURI0;
            });
    }
    
    public ZuulRoute getZuulRouteByPath(String path) {
        Route route = this.routeLocator.getMatchingRoute(path);
        if(JudgeUtils.isNull(route)) {
            return null;
        }
        return this.zuulExtensionProperties.getZuulRoute(route.getId());
    }
    
    public ZuulRoute getCurrentZuulRoute(HttpServletRequest httpServletRequest) {
        RequestContext ctx = RequestContext.getCurrentContext();
        return Optional.ofNullable(ctx.get(CTX_CURRENT_ZUUL_ROUTE)).map(m -> (ZuulRoute)m)
            .orElseGet(() -> {
                ZuulRoute zuulRoute = this.getZuulRouteByPath(this.getCurrentRequestURI(httpServletRequest));
                ctx.set(CTX_CURRENT_ZUUL_ROUTE, zuulRoute);
                return zuulRoute;
            });
    }
    
    public String getCurrentRequestBody(HttpServletRequest request) {
        RequestContext ctx = RequestContext.getCurrentContext();
        return Optional.ofNullable(ctx.get(CTX_CURRENT_BODY_STRING)).map(m -> (String)m)
            .orElseGet(() -> {
                String body = GatewayHelper.getRequestBody(request);
                ctx.set(CTX_CURRENT_BODY_STRING, body);
                return body;
            });
    }
    
    public Map<String, String[]> getParameterMap(HttpServletRequest request) {
        Map<String, String[]> maps = request.getParameterMap();
        return maps;
    }
    
    public byte[] getResponseDataStream() {
        InputStream inputStream = RequestContext.getCurrentContext().getResponseDataStream();
        if(null == inputStream) return null;
        byte[] bodyBytes;
        try {
            bodyBytes = IOUtils.toByteArray(inputStream);
            RequestContext.getCurrentContext().setResponseDataStream(new ByteArrayInputStream(bodyBytes));
            return bodyBytes;
        } catch (IOException e) {
            throw LemonException.create(ErrorMsgCode.SYS_ERROR);
        }
    }
}
