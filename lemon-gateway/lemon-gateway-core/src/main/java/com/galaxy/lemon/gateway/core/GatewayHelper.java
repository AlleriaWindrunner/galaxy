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

package com.galaxy.lemon.gateway.core;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.codec.CodecException;
import com.galaxy.lemon.common.codec.ObjectDecoder;
import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.IOUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class GatewayHelper {

    public static final String X_FORWARDED_FOR_HEADER = LemonConstants.HTTP_HEADER_FOR;

    public static final String REQUEST_ATTRIBUTE_BODY = "REQUEST_ATTRIBUTE_BODY";
    public static final String REQUEST_ATTRIBUTE_BODY_MAP = "REQUEST_ATTRIBUTE_BODY_MAP";
    public static final String LEMON_CONTEXT_LOCALE_RESOLVER = "LEMON_CONTEXT_LOCALE_RESOLVER";

    public static final Object NULL_OBJECT = new Object();
    public static final Map NULL_MAP =  Collections.emptyMap();

    public static Locale resolveLocale(HttpServletRequest request) {
        LocaleResolver localeResolver = Optional.ofNullable(LemonContext.getCurrentContext().get(LEMON_CONTEXT_LOCALE_RESOLVER)).map(l -> (LocaleResolver) l).orElse(null);
        if (JudgeUtils.isNotNull(localeResolver)) {
            return localeResolver.resolveLocale(request);
        }
        return RequestContextUtils.getLocale(request);
    }

    public static String xForwardedFor(HttpServletRequest request) {
        String xforwardedfor = request.getHeader(X_FORWARDED_FOR_HEADER);
        String remoteAddr = request.getRemoteAddr();
        if (xforwardedfor == null) {
            xforwardedfor = remoteAddr;
        } else if (!xforwardedfor.contains(remoteAddr)) { // Prevent duplicates
            xforwardedfor += ", " + remoteAddr;
        }
        return xforwardedfor;
    }

    public static String getToken(HttpServletRequest request) {
        String token = Optional.ofNullable(request.getSession(false)).map(HttpSession::getId).orElse(null);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        token = Optional.ofNullable(request.getHeader(LemonConstants.HTTP_HEADER_TOKEN)).orElse(null);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        return Optional.ofNullable(request.getCookies()).map(s -> Stream.of(s).filter(c -> StringUtils.equals(c.getName(), LemonConstants.COOKIE_SESSION_ID)).findFirst().map(c -> c.getValue()).orElse(null)).orElse(null);
    }

    public static InputStream getRequestInputStream(HttpServletRequest request) {
        try {
            return request.getInputStream();
        } catch (IOException ex) {
            throw LemonException.create(ex);
        }
    }

    public static String getRequestBody(HttpServletRequest request) {
        if (null != request.getAttribute(REQUEST_ATTRIBUTE_BODY)) {
            return request.getAttribute(REQUEST_ATTRIBUTE_BODY) == NULL_OBJECT ? null : (String) request.getAttribute(REQUEST_ATTRIBUTE_BODY);
        }
        String bodyString = Optional.ofNullable(getRequestInputStream(request)).map(IOUtils::toStringIgnoreException).orElse(null);
        request.setAttribute(REQUEST_ATTRIBUTE_BODY, bodyString == null ? NULL_OBJECT : bodyString);
        return bodyString;
    }

    public static Map<String, Object> getRequestBodyForMap(HttpServletRequest request, ObjectDecoder objectDecoder) throws IOException, CodecException {
        if (null != request.getAttribute(REQUEST_ATTRIBUTE_BODY_MAP)) {
            return request.getAttribute(REQUEST_ATTRIBUTE_BODY_MAP) == NULL_MAP ? null : (Map<String, Object>) request.getAttribute(REQUEST_ATTRIBUTE_BODY_MAP);
        }
        Map<String, Object> bodyForMap = null;
        InputStream inputStream = getRequestInputStream(request);
        if(null != inputStream) {
            bodyForMap = objectDecoder.readValue(inputStream, Map.class);
        }
        if (null == bodyForMap) {
            bodyForMap = NULL_MAP;
        }
        request.setAttribute(REQUEST_ATTRIBUTE_BODY_MAP, bodyForMap);
        return bodyForMap;
    }

    public static String resolveClientIp(HttpServletRequest request) {
        return Optional.ofNullable(xForwardedFor(request)).map(i -> i.split(",")).map(a -> StringUtils.trim(a[0])).orElseGet(() -> request.getRemoteAddr());
    }

    public static void setLemonContextLocaleResolver(LocaleResolver localeResolver) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_LOCALE_RESOLVER, localeResolver);
    }
}
