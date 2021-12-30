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

package com.galaxy.lemon.framework.spring.spel;

import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.common.utils.StringUtils;

import java.lang.reflect.Method;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */


public abstract class SpelFunction {

    public static final String STRCAT       = "STRCAT";
    public static final String STRCAT3       = "STRCAT3";
    public static final String STRCAT4       = "STRCAT4";
    public static final String SUBSTR       = "SUBSTR";
    public static final String SUBSTRE      = "SUBSTRE";
    public static final String STRLEN       = "STRLEN";
    public static final String LEFTPAD      = "LEFTPAD";
    public static final String RIGHTPAD     = "RIGHTPAD";


    public static final Method STRCAT_METHOD    = ReflectionUtils.getDeclaredMethodByName(SpelFunction.class,"concat");
    public static final Method STRCAT3_METHOD    = ReflectionUtils.getDeclaredMethodByName(SpelFunction.class,"concat3");
    public static final Method STRCAT4_METHOD    = ReflectionUtils.getDeclaredMethodByName(SpelFunction.class,"concat4");
    public static final Method SUBSTR_METHOD    = getMethod(StringUtils.class, "substring", new Class[]{String.class, int.class, int.class});
    public static final Method SUBSTRE_METHOD   = getMethod(StringUtils.class, "substring", new Class[]{String.class, int.class});
    public static final Method STRLEN_METHOD    = ReflectionUtils.getDeclaredMethodByName(StringUtils.class,"length");
    public static final Method LEFTPAD_METHOD   = getMethod(StringUtils.class, "leftPad", new Class[]{String.class, int.class, char.class});
    public static final Method RIGHTPAD_METHOD  = getMethod(StringUtils.class, "rightPad", new Class[]{String.class, int.class, char.class});

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw LemonException.create(e);
        }
    }

    public static String concat(String str1, String str2) {
        return StringUtils.join(str1, str2);
    }

    public static String concat3(String str1, String str2, String str3) {
        return StringUtils.join(str1, str2, str3);
    }

    public static String concat4(String str1, String str2, String str3, String str4) {
        return StringUtils.join(str1, str2, str3, str4);
    }

}
