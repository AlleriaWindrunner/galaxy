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

package com.galaxy.lemon.common.exception;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.SimpleAlert;
import com.galaxy.lemon.common.utils.JudgeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class GenericExceptionConversionService implements ConfigurableExceptionConversionService {
    private static final Logger logger = LoggerFactory.getLogger(GenericExceptionConversionService.class);

    private List<ExceptionConverter> exceptionConverters = null;
    private final static Class<?> RUNTIME_EXCEPTION_CLASS = RuntimeException.class;
    private final int MAX_RESOLVE_EXCEPTION_DEEP = 3;

    public GenericExceptionConversionService() {
        this.exceptionConverters = new ArrayList<>();
    }

    public GenericExceptionConversionService(List<ExceptionConverter> exceptionConverters) {
        this.exceptionConverters = exceptionConverters;
    }

    @Override
    public AlertCapable convert(Throwable throwable) {
        AlertCapable alertCapable = resolveThrowable(throwable);
        if (logger.isDebugEnabled()) {
            logger.debug("Converted exception \"{}\" to message code \"{}\".", throwable, alertCapable);
        }
        return alertCapable;
    }

    @Override
    public void addConverter(ExceptionConverter exceptionConverter) {
        this.exceptionConverters.add(exceptionConverter);
    }

    public List<ExceptionConverter> getExceptionConverters() {
        return this.exceptionConverters;
    }

    /**
     * 获取错误码
     * @param throwable
     * @return
     */
    private AlertCapable resolveThrowable(Throwable throwable) {
        Throwable e = throwable;
        if (null == e) return ErrorMsgCode.SYS_ERROR;

        if (RUNTIME_EXCEPTION_CLASS.equals(e.getClass()) && e.getCause() != null) {
            e = e.getCause();
        }

        if (LemonException.isLemonException(e)) {
            LemonException le = (LemonException) e;
            if (JudgeUtils.isNotBlank(le.getMsgCd())) {
                return SimpleAlert.newInstance(le);
            }
            if(null == le.getCause()) {
                return ErrorMsgCode.SYS_ERROR;
            }
            e = le.getCause();
        }

        if (e instanceof AlertCapable) {
            return SimpleAlert.newInstance((AlertCapable) e);
        }

        AlertCapable errorMsgCode= resolveThrowable(e, 1);

        return !requireConvertByNestException(errorMsgCode) ? errorMsgCode : ErrorMsgCode.SYS_ERROR;
    }

    private AlertCapable resolveThrowable(Throwable e, int count) {
        if(count >= getMaxResolveExceptionDeep()) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not resolve exception conversion because of cause too deep. ===> ", e);
            }
            return ErrorMsgCode.SYS_ERROR;
        }
        if(ExceptionConverter.isBasicError(e)) {
            return ErrorMsgCode.SYS_ERROR;
        }
        if(JudgeUtils.isEmpty(this.exceptionConverters)){
            return ErrorMsgCode.SYS_ERROR;
        }

        AlertCapable errorMsgCode = convertByNestException();
        for(ExceptionConverter converter : this.exceptionConverters) {
            if (converter.support(e)) {
                errorMsgCode = converter.convert(e);
                if (logger.isDebugEnabled()) {
                    logger.debug("Conversion exception {} to {} by {}.", e, Optional.ofNullable(errorMsgCode).map(AlertCapable::getMsgCd).orElse(null), converter);
                }
                if (null == errorMsgCode) {
                    errorMsgCode = ErrorMsgCode.SYS_ERROR;
                    break;
                }
                if (requireConvertByNestException(errorMsgCode)) {
                    e = e.getCause();
                    if (null == e) {
                        errorMsgCode = ErrorMsgCode.SYS_ERROR;
                        break;
                    }
                    errorMsgCode = resolveThrowable(e, count + 1);
                }
                break;
            }
        }
        return errorMsgCode;
    }

    private int getMaxResolveExceptionDeep() {
        return MAX_RESOLVE_EXCEPTION_DEEP;
    }
}

