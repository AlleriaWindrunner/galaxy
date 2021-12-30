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

import java.util.List;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */


@Deprecated
public class ExceptionConverterComposite implements ExceptionConverter {
    private List<ExceptionConverter> exceptionConverters = null;

    public ExceptionConverterComposite(List<ExceptionConverter> exceptionConverters) {
        this.exceptionConverters = exceptionConverters;
    }
    
    @Override
    public boolean support(Throwable throwable) {
        return true;
    }

    @Override
    public AlertCapable convert(Throwable throwable) {
        return resolveErrorMsgCd(throwable);
    }

    /**
     * 获取错误码
     * @param e
     * @return
     */
    private AlertCapable resolveErrorMsgCd(Throwable e) {
        if (null == e) return ErrorMsgCode.SYS_ERROR;
        if (LemonException.isLemonException(e)) {
            LemonException le = (LemonException) e;
            String msgCd = Optional.ofNullable(le.getMsgCd()).orElseGet(null);
            if (JudgeUtils.isNotBlank(msgCd)) {
                return SimpleAlert.newInstance(msgCd);
            }
            if(null == le.getCause()) {
                return ErrorMsgCode.SYS_ERROR;
            }
            e = le.getCause();
        }

        AlertCapable errorMsgCode= resolveErrorMsgCode(e, 1);
        if (! requireConvertByNestException(errorMsgCode)) {
            return errorMsgCode;
        }

        return ErrorMsgCode.SYS_ERROR;
    }

    private AlertCapable resolveErrorMsgCode(Throwable e, int count) {
        if(count >= 3) {
            return ErrorMsgCode.SYS_ERROR;
        }
        if(ExceptionConverter.isBasicError(e)) {
            return ErrorMsgCode.SYS_ERROR;
        }
        if(JudgeUtils.isEmpty(this.exceptionConverters)) return ErrorMsgCode.SYS_ERROR;

        AlertCapable errorMsgCode = convertByNestException();
        for(ExceptionConverter converter : this.exceptionConverters) {
            if (converter.support(e)) {
                errorMsgCode = converter.convert(e);
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
                    errorMsgCode = resolveErrorMsgCode(e, count + 1);
                }
                break;
            }
        }
        return errorMsgCode;
    }
}
