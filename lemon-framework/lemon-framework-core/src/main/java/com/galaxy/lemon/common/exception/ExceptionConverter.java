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
import com.galaxy.lemon.common.extension.SPI;

import java.net.SocketTimeoutException;

/**
 * SPI
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@SPI
public interface ExceptionConverter {
    AlertCapable CONVERT_BY_NEST_EXCEPTION = new SimpleAlert(null, null);

    boolean support(Throwable throwable);
    AlertCapable convert(Throwable throwable);

    default AlertCapable convertByNestException() {
        return CONVERT_BY_NEST_EXCEPTION;
    }

    default boolean requireConvertByNestException(AlertCapable alertCapable) {
        return CONVERT_BY_NEST_EXCEPTION == alertCapable;
    }

    static boolean isBasicError(Throwable throwable) {
        Class<?> clazz = throwable.getClass();
        return clazz.equals(Exception.class) || clazz.equals(Throwable.class) || clazz.equals(Error.class)
                || (clazz.equals(RuntimeException.class) && throwable.getCause() == null);
    }

    static boolean isTimeOutException(Throwable t) {
        if(t == null) return false;

        if(t instanceof SocketTimeoutException) {
            return true;
        }

        Throwable t2 = t.getCause();
        if(null == t2) {
            return false;
        }
        int count = 0;
        while(null != t2 && count <= 4) {
            if(t2 instanceof SocketTimeoutException) {
                return true;
            }
            t2 = t2.getCause();
            count ++;
        }
        return false;
    }

}
