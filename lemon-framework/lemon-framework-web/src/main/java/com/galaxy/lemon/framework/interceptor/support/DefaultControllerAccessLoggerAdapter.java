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

package com.galaxy.lemon.framework.interceptor.support;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.interceptor.ControllerAccessLoggerAdapter;
import com.galaxy.lemon.framework.utils.LemonUtils;
import com.galaxy.lemon.framework.utils.WebUtils;
import com.galaxy.lemon.common.log.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Controller Access logger adapter
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultControllerAccessLoggerAdapter extends ControllerAccessLoggerAdapter {
    private boolean logRequest;
    private boolean logResponse;

    public DefaultControllerAccessLoggerAdapter(AccessLogger accessLogger) {
        this(accessLogger, true, true);
    }

    public DefaultControllerAccessLoggerAdapter(AccessLogger accessLogger, boolean logRequest, boolean logResponse) {
        super(accessLogger);
        this.logRequest = logRequest;
        this.logResponse = logResponse;
    }

    @Override
    public void request(ProceedingJoinPoint pjp) {

        if (this.logRequest) {
            RequestInfo requestInfo = RequestInfo.builder()
                    .requestId(LemonUtils.getRequestId())
                    .msgId(LemonUtils.getMsgId())
                    .keywords(extractRawExpressionKeywords(pjp))
                    .method(WebUtils.getHttpServletRequest().getMethod())
                    .uri(WebUtils.getHttpServletRequest().getRequestURI())
                    .clientIp(LemonUtils.getClientIp())
                    .reqeustTime(LemonContextUtils.getTradeStartTime())
                    .target(pjp.getArgs())
                    .build();
            this.getAccessLogger().request(requestInfo);
        }

    }

    @Override
    public void response(ProceedingJoinPoint pjp, Object responseObject, long durationMillis) {
        if (this.logResponse) {
            ResponseInfo responseInfo = ResponseInfo.builder()
                    .requestId(LemonUtils.getRequestId())
                    .msgId(LemonUtils.getMsgId())
                    .duration(durationMillis)
                    .status(Optional.ofNullable(responseObject).filter(r -> r instanceof AlertCapable).map(AlertCapable.class::cast).map(AlertCapable::getMsgCd).orElse(null))
                    .result(responseObject)
                    .keywords(extractRawExpressionKeywords(pjp))
                    .build();
            this.getAccessLogger().response(responseInfo);
        }

    }

    private RawExpressionKeywords extractRawExpressionKeywords(ProceedingJoinPoint pjp) {
        Class<?> targetClass = pjp.getTarget().getClass();
        Method method = extractMethod(pjp.getSignature(), targetClass);
        return null == method ? null : new RawExpressionKeywords(createExpressionsKey(targetClass, method));
    }

    private KeywordsExpressionSource.ExpressionKey createExpressionsKey(Class<?> targetClass, Method method) {
        return new KeywordsExpressionSource.ExpressionKey(targetClass, method);
    }

    private Method extractMethod(Signature signature, Class<?> clazz) {
        return Optional.of(signature).filter(s -> s instanceof MethodSignature).map(m -> (MethodSignature) m).
                map(m -> safeGetMethod(m, clazz)).orElse(null);
    }

    private Method safeGetMethod(MethodSignature methodSignature, Class<?> clazz) {
        try {
            return clazz.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
