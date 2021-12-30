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

package com.galaxy.lemon.framework.xss.request;

import com.galaxy.lemon.framework.xss.resolver.EncodingXssResolver;
import com.galaxy.lemon.framework.xss.utils.EncodingUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 防止xss注入功能的request
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger logger = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);

    private static final String DEFAULT_ENCODING = "UTF-8";

    private String encoding;

    private EncodingXssResolver xssResolver;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request, EncodingXssResolver xssResolver, String encoding) {
        super(request);
        this.xssResolver = xssResolver;
        this.encoding = EncodingUtils.getEncoding(request, encoding);
    }

    @Override
    public String getRequestURI() {
        return Optional.ofNullable(super.getRequestURI()).map(this::resolveXss).orElse(null);
    }

    @Override
    public StringBuffer getRequestURL() {
        return Optional.ofNullable(super.getRequestURL()).map(StringBuffer::toString).map(this::resolveXss).map(StringBuffer::new).orElse(null);
    }

    @Override
    public String getServletPath() {
        return Optional.ofNullable(super.getServletPath()).map(this::resolveXss).orElse(null);
    }

    @Override
    public String getQueryString() {

        return Optional.ofNullable(super.getQueryString()).map(this::resolveQueryString).orElse(null);

    }

    @Override
    public String getHeader(String name) {
        return Optional.ofNullable(super.getHeader(name)).map(this::resolveXss).orElse(null);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if(super.getParameterMap() == null) {
            return null;
        }

        Map<String, String[]> pmap = new HashMap<String, String[]>();
        super.getParameterMap().forEach((key, values)->{
            for(int i=0; i<values.length; i++) {
                values[i] = Optional.ofNullable(values[i]).map(this::resolveXss).orElse(null);
            }

            pmap.put(key, values);
        });

        return pmap;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        return resolveArray(values);
    }

    @Override
    public String getParameter(String parameter) {
        return Optional.ofNullable(super.getParameter(parameter)).map(this::resolveXss).orElse(null);
    }

    private String resolveQueryString(String queryString) {
        StringBuilder xssResolveQueryString = new StringBuilder();
        StringTokenizer st = new StringTokenizer(queryString, "&");
        int i;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            i = s.indexOf("=");
            String name = null;
            String value = null;
            if (i > 0 && s.length() >= i + 1) {
                name = s.substring(0, i);
                value = s.substring(i + 1);
            }
            else if (i == -1)
            {
                name=s;
                value="";

            }
            if (xssResolveQueryString.length() != 0) {
                xssResolveQueryString.append("&");
            }
            xssResolveQueryString.append(this.resolveXss(name)).append("=").append(this.resolveXss(value));
        }
        return xssResolveQueryString.toString();

    }

    private String[] resolveArray(String[] values) {
        if(values == null) {
            return null;
        }

        return Stream.of(values).filter(StringUtils::isNotBlank).map(this::resolveXss).collect(Collectors.toList()).toArray(new String[]{});
    }

    private String resolveXss(String text) {
        return xssResolver.resolveXss(text, this.encoding);
    }
}
