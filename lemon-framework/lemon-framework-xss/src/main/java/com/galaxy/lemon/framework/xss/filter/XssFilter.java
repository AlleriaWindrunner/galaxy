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

package com.galaxy.lemon.framework.xss.filter;

import com.galaxy.lemon.framework.xss.request.XssHttpServletRequestWrapper;
import com.galaxy.lemon.framework.xss.resolver.EncodingXssResolver;
import com.galaxy.lemon.framework.xss.jackson.XssStringDeserializer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 防xss攻击filter
 * 针对multipart/form-data和application/x-www-form-urlencoded的请求做相应的处理
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * raw json(json字符串)格式的数据，content-type为application/json的请求参看{@link XssStringDeserializer}
 * @see
 * @since 1.0.0
 */

public class XssFilter implements Filter {

    private EncodingXssResolver xssResolver;

    private String encoding;

    public XssFilter(EncodingXssResolver xssResolver, String encoding) {
        this.xssResolver = xssResolver;
        this.encoding = encoding;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request, xssResolver, encoding), response);
    }

    @Override
    public void destroy() {

    }
}
