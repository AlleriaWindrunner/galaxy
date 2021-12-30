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

package com.galaxy.lemon.framework.web.filter;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 固定IP访问过滤器
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class FixedIpAccessFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(FixedIpAccessFilter.class);

    private static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";
    public static final String ALLOW_ACCESS_IP_LIST = "allowAccessIpList";
    public static final String THROW_EXCEPTION_WHEN_FORBIDDEN_ACCESS = "throwExceptionWhenForbiddenAccess";
    public static final String SEPARATOR_FOR_IP_LIST = ",";
    protected static final String[] DEFAULT_ALLOW_ACCESS_IP_LIST = new String[]{"localhost", "127.0.0.1"};
    private String alreadyFilteredAttributeName = getClass().getName().concat(ALREADY_FILTERED_SUFFIX);

    private String[] allowAccessIpList = null;
    private boolean throwExceptionWhenForbiddenAccess = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String throwExceptionWhenForbiddenAccessStr = filterConfig.getInitParameter(THROW_EXCEPTION_WHEN_FORBIDDEN_ACCESS);
        if (JudgeUtils.isNotBlank(throwExceptionWhenForbiddenAccessStr) &&
                Boolean.parseBoolean(throwExceptionWhenForbiddenAccessStr)) {
            this.throwExceptionWhenForbiddenAccess = true;
        }
        String ipStr = filterConfig.getInitParameter(ALLOW_ACCESS_IP_LIST);
        if (ipStr != null && ipStr.length() > 0) {
            this.allowAccessIpList = ipStr.split(SEPARATOR_FOR_IP_LIST);
        } else {
            this.allowAccessIpList = DEFAULT_ALLOW_ACCESS_IP_LIST;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Allow access ip list is {}.", Stream.of(this.allowAccessIpList).collect(Collectors.joining(SEPARATOR_FOR_IP_LIST)));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (request.getAttribute(this.alreadyFilteredAttributeName) == null) {
            request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);
            if (logger.isInfoEnabled()) {
                logger.info("Request fixed ip access service \"{}\", remote address is \"{}\".", ((HttpServletRequest) request).getRequestURL(), request.getRemoteAddr());
            }
            if (JudgeUtils.not(doPermitCheck(request))) {
                if (this.throwExceptionWhenForbiddenAccess) {
                    LemonException.throwLemonException(ErrorMsgCode.FORBIDDEN_OPERATION.getMsgCd());
                } else {
                    response.getWriter().write(errorMsg());
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    private String errorMsg() {
        StringBuilder msg = new StringBuilder("{");
        msg.append("\"msgCd\"").append(":\"").append(ErrorMsgCode.FORBIDDEN_OPERATION.getMsgCd()).append("\",")
                .append("\"msgInfo\"").append(":\"").append("Access Forbidden.").append("\"}");
        return msg.toString();
    }

    private boolean doPermitCheck(ServletRequest request) {
        String remoteHost = request.getRemoteHost();
        return JudgeUtils.contain(allowAccessIpList, remoteHost);
    }

    @Override
    public void destroy() {
        this.allowAccessIpList = null;
    }

}
