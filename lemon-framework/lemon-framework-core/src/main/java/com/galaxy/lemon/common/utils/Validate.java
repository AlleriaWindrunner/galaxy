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

package com.galaxy.lemon.common.utils;

import com.galaxy.lemon.common.exception.BusinessException;
import com.galaxy.lemon.common.exception.ErrorMsgCode;

import java.util.function.Supplier;

/**
 * 继承common validate
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class Validate extends org.apache.commons.lang3.Validate {
    
    /**
     * 非负
     * @param i
     * @param msg
     */
    public static void nonNegative(int i , String msg) {
        if(i < 0) {
            throw new IllegalArgumentException(msg);
        }
    }
    
    /**
     * 非负
     * @param d
     * @param msg
     */
    public static void nonNegative(double d , String msg) {
        if(d < 0) {
            throw new IllegalArgumentException(msg);
        }
    }
    /**
     * 非负
     * @param d
     * @param msg
     */
    public static void nonNegative(long d , String msg) {
        if(d < 0) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * false 为false抛出业务异常
     * @param flag
     * @param errorMsgCode
     */
    public static void validate(boolean flag , ErrorMsgCode errorMsgCode) {
        if (!flag) {
            BusinessException.throwBusinessException(errorMsgCode);
        }
    }

    /**
     * supplier.get() 为false抛出业务异常
     * @param supplier
     * @param errorMsgCode
     */
    public static void validate(Supplier<Boolean> supplier, ErrorMsgCode errorMsgCode) {
        if (!supplier.get()) {
            BusinessException.throwBusinessException(errorMsgCode);
        }
    }

    /**
     * false 为false抛出业务异常
     * @param flag
     * @param errorMsgCode
     * @param parameters
     */
    public static void validate(boolean flag , ErrorMsgCode errorMsgCode, String... parameters) {
        if (!flag) {
            BusinessException.throwBusinessException(errorMsgCode, parameters);
        }
    }

    /**
     * supplier.get() 为false抛出业务异常
     * @param supplier
     * @param errorMsgCode
     * @param parameters
     */
    public static void validate(Supplier<Boolean> supplier , ErrorMsgCode errorMsgCode, String... parameters) {
        if (!supplier.get()) {
            BusinessException.throwBusinessException(errorMsgCode, parameters);
        }
    }

}
