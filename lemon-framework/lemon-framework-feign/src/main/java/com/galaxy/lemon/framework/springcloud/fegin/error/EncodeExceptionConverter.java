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
import feign.codec.EncodeException;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class EncodeExceptionConverter implements ExceptionConverter {
    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof EncodeException;
    }

    @Override
    public AlertCapable convert(Throwable throwable) {
        Throwable t2 = throwable.getCause();
        if(null == t2 || ExceptionConverter.isBasicError(t2)) {
            return ErrorMsgCode.SYS_ERROR;
        }
        return convertByNestException();
    }
}
