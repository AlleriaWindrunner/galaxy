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

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.exception.BusinessException;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 业务断言
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class Assert {

    public static <T>  void isNull(T t, AlertCapable alertCapable, String... parameters) {
        if (t != null) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static <T>  void isNotNull(T t, AlertCapable alertCapable, String... parameters) {
        if (t == null) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isEmpty(String str, AlertCapable alertCapable, String... parameters) {
        if (! StringUtils.isEmpty(str)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isNotEmpty(String str, AlertCapable alertCapable, String... parameters) {
        if (StringUtils.isEmpty(str)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isBlank(String str, AlertCapable alertCapable, String... parameters) {
        if (! StringUtils.isBlank(str)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isNotBlank(String str, AlertCapable alertCapable, String... parameters) {
        if (StringUtils.isBlank(str)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isTrue(boolean flag, AlertCapable alertCapable, String... parameters) {
        if (!flag) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isNotTrue(boolean flag, AlertCapable alertCapable, String... parameters) {
        if (flag) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isTrue(Supplier<Boolean> supplier, AlertCapable alertCapable, String... parameters) {
        if (!supplier.get()) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isNotTrue(Supplier<Boolean> supplier, AlertCapable alertCapable, String... parameters) {
        if (supplier.get()) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static <T> void isEmpty(T[] array, AlertCapable alertCapable, String... parameters) {
        if (! JudgeUtils.isEmpty(array)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static <T> void isNotEmpty(T[] array, AlertCapable alertCapable, String... parameters) {
        if (JudgeUtils.isEmpty(array)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isEmpty(Collection<?> collection, AlertCapable alertCapable, String... parameters) {
        if (! JudgeUtils.isEmpty(collection)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isNotEmpty(Collection<?> collection, AlertCapable alertCapable, String... parameters) {
        if (JudgeUtils.isEmpty(collection)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isEmpty(Map<?, ?> map, AlertCapable alertCapable, String... parameters) {
        if (! JudgeUtils.isEmpty(map)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isNotEmpty(Map<?, ?> map, AlertCapable alertCapable, String... parameters) {
        if (JudgeUtils.isEmpty(map)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static <T,L> void equals(T t, L l, AlertCapable alertCapable, String... parameters) {
        if (! t.equals(l)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static <T,L> void notEquals(T t, L l, AlertCapable alertCapable, String... parameters) {
        if (t.equals(l)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void contain(String textToSearch, String substring, AlertCapable alertCapable, String... parameters) {
        if (! StringUtils.contains(textToSearch, substring)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void notContain(String textToSearch, String substring, AlertCapable alertCapable, String... parameters) {
        if (StringUtils.contains(textToSearch, substring)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void contain(String[] arrayToSearch, String item, AlertCapable alertCapable, String... parameters) {
        if (! JudgeUtils.contain(arrayToSearch, item)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void notContain(String[] arrayToSearch, String item, AlertCapable alertCapable, String... parameters) {
        if (JudgeUtils.contain(arrayToSearch, item)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static <T> void contain(Collection<T> collectionToSearch, T item, AlertCapable alertCapable, String... parameters) {
        if (! JudgeUtils.contain(collectionToSearch, item)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

    public static void isSuccess(AlertCapable alertCapable, String... parameters) {
        if (!JudgeUtils.isSuccess(alertCapable)) {
            BusinessException.throwBusinessException(alertCapable, parameters);
        }
    }

}
