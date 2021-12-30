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

package com.galaxy.lemon.framework.utils;

import com.galaxy.lemon.common.HttpMethod;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class WebUtils {
    public static final String REQUEST_ATTRIBUTE_REQUEST_ID = "REQUEST_ATTRIBUTE_REQUEST_ID";
    public static final String LEMON_CONTEXT_REQUEST = "LEMON_CONTEXT_REQUEST";

    /**
     * @return  HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes()).filter(s -> s instanceof ServletRequestAttributes).map(a -> (ServletRequestAttributes)a).map(s -> s.getRequest())
                .orElseGet(() -> Optional.of(LemonContext.getCurrentContext()).map(c -> c.get(LEMON_CONTEXT_REQUEST)).map(r -> (HttpServletRequest)r).orElse(null));
    }

    /**
     * set HttpServletRequest to LemonContext
     * @param request
     */
    public static void setLemonContextRequest(HttpServletRequest request) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_REQUEST, request);
    }

    /**
     * 校验请求数据
     * @param baseDTO
     */
    public static void validateRequestData(BaseDTO<?> baseDTO){
        validateRequestData(baseDTO, getHttpServletRequest());
    }

    /**
     * 校验请求数据
     * @param baseDTO
     * @param request
     * @return
     */
    public static void validateRequestData(BaseDTO<?> baseDTO, HttpServletRequest request) {
        String userIdFromHeader = request.getHeader(LemonConstants.HTTP_HEADER_USER_ID);
        if(JudgeUtils.isBlankAll(userIdFromHeader)) {
            return;
        }
        
        if(JudgeUtils.isNotBlankAll(userIdFromHeader, baseDTO.getUserId()) && JudgeUtils.notEquals(baseDTO.getUserId(), userIdFromHeader)) {
            LemonException.throwLemonException(ErrorMsgCode.ILLEGAL_PARAMETER.getMsgCd());
        }
    }

    /**
     * 获取Ip
     * @return
     */
    public static String resolveClientIP() {
        return resolveClientIP(getHttpServletRequest());
    }
    
    /**
     * 获取Ip
     * @param request
     * @return
     */
    public static String resolveClientIP(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(LemonConstants.HTTP_HEADER_CLIENT_IP)).filter(JudgeUtils::isNotBlank)
                .orElseGet( () -> Optional.ofNullable(request.getHeader(LemonConstants.HTTP_HEADER_FOR)).filter(StringUtils::isNotBlank)
                .map(x -> x.split(",")).map(a -> a[0]).orElseGet(() -> request.getRemoteAddr()));
    }

    /**
     * 获取Locale
     * @return
     */
    public static Locale resolveLocale() {
        return resolveLocale(getHttpServletRequest());
    }
    
    /**
     * 获取Locale
     * @param request
     * @return
     */
    public static Locale resolveLocale(HttpServletRequest request) {
        return Optional.ofNullable(request).map(r -> r.getHeader(LemonConstants.HTTP_HEADER_LOCALE)).map(l -> l.split("_"))
            .map(ls -> new Locale(ls[0], ls.length == 2 ? ls[1] : "" )).orElseGet(() -> LemonUtils.getLemonEnvironment().getDefaultLocale() );
    }

    /**
     * 获取requestId
     * @return
     */
    public static String resolveRequestId() {
        return resolveRequestId(getHttpServletRequest());
    }
    
    /**
     * 获取requestId
     * @param request
     * @return
     */
    public static String resolveRequestId(HttpServletRequest request) {
        return resolveRequestId(request, true);
    }

    /**
     * 获取requestId
     * @param request
     * @param create 没有获取到时是否创建
     * @return
     */
    public static String resolveRequestId(HttpServletRequest request, boolean create) {
        String requestId = Optional.ofNullable(request.getAttribute(REQUEST_ATTRIBUTE_REQUEST_ID)).map(StringUtils::toString).orElseGet(() ->
                Optional.ofNullable(request.getHeader(LemonConstants.HTTP_HEADER_REQUEST_ID)).orElseGet(() -> Optional.of(create).filter(c -> c).map(c -> IdGenUtils.generateRequestId()).orElse(null)  )
        );
        request.setAttribute(REQUEST_ATTRIBUTE_REQUEST_ID, requestId);
        return requestId;
    }

    /**
     * 是否GET请求
     * @param request
     * @return
     */
    public static boolean isGetRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getMethod()).filter(m -> HttpMethod.GET.toString().equals(m)).map(m -> Boolean.TRUE).orElse(Boolean.FALSE);
    }

}
