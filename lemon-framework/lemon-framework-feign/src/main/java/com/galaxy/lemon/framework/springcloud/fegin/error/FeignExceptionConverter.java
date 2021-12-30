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

package com.galaxy.lemon.framework.springcloud.fegin.error;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.ExceptionConverter;
import feign.FeignException;
import feign.RetryableException;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class FeignExceptionConverter implements ExceptionConverter {
    @Override
    public boolean support(Throwable throwable) {
        return (!(throwable instanceof RetryableException)) && throwable instanceof FeignException;
    }

    @Override
    public AlertCapable convert(Throwable throwable) {
        FeignException fe = (FeignException) throwable;
        if(fe.status() == 404) {
            return ErrorMsgCode.SERVER_RESOURCE_NOT_FOUND;
        }
        if(ExceptionConverter.isTimeOutException(fe.getCause())) {
            return ErrorMsgCode.CLIENT_TIMEOUT;
        }
        if(null == fe.getCause() || ExceptionConverter.isBasicError(fe.getCause())) {
            return ErrorMsgCode.SYS_ERROR;
        }
        return convertByNestException();
    }
}
