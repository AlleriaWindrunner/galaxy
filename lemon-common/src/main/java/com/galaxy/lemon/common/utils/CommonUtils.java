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

import com.galaxy.lemon.common.Callback0;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * 通用判断
 *
 * @author yuzhou
 * @date 2019/1/23
 * @time 11:47
 * @since 1.4.0
 */
/**
 * 通用判断
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class CommonUtils {
    public static <T> boolean isNull(T t) {
        return null == t;
    }

    public static <T> boolean isNotNull(T t) {
        return null != t;
    }

    @SafeVarargs
    public static <T> boolean isNullAny(T... ts) {
        if(null == ts) {
            return true;
        }

        for(T t: ts) {
            if(null == t) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean isNotNullAny(T... ts) {
        return !isNullAny(ts);
    }

    public static <T, L> boolean equals(T t, L l) {
        return t.equals(l);
    }

    public static <T, L> boolean notEquals(T t, L l) {
        return ! equals(t, l);
    }

    public static boolean equals(String str1, String str2) {
        return StringUtils.equals(str1, str2);
    }
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return StringUtils.equalsIgnoreCase(str1, str2);
    }

    public static boolean notEquals(String str1, String str2) {
        return ! equals(str1, str2);
    }

    public static boolean equalsAny(String str1, String... strs) {
        if(null == strs && null == str1) {
            return true;
        }
        if(null == strs) {
            return false;
        }
        boolean f = false;
        for(String s : strs) {
            if(equals(str1, s)) {
                f = true;
                break;
            }
        }
        return f;
    }

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(String str) {
        return ! isEmpty(str);
    }

    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isNotBlankAll(String... args) {
        if(null == args) {
            return false;
        }
        boolean f = true;
        for(String s : args) {
            if(isBlank(s)) {
                f = false;
                break;
            }
        }
        return f;
    }

    public static boolean isBlankAll(String... args) {
        if(null == args) {
            return true;
        }
        boolean f = true;
        for(String s : args) {
            if(isNotBlank(s)) {
                f = false;
                break;
            }
        }
        return f;
    }

    public static boolean isBlankAny(String... args) {
        if(null == args) {
            return true;
        }
        boolean f = false;
        for(String s : args) {
            if(isBlank(s)) {
                f = true;
                break;
            }
        }
        return f;
    }

    /**
     * 集合是否为空
     * @param c
     * @return
     */
    public static boolean isEmpty(Collection<?> c) {
        if(null == c) {
            return true;
        }
        if(c.size() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 集合是否不为空
     * @param c
     * @return
     */
    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    /**
     * 判断map是否为空
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        if(null == map) {
            return true;
        }
        if(map.size() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断map是否不为空
     * @param map
     * @return
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }


    public static <T> boolean isEmpty(T[] ts) {
        if(null == ts) {
            return true;
        }
        if(ts.length <= 0) {
            return true;
        }
        return false;
    }

    public static <T> boolean isNotEmpty(T[] ts) {
        return !isEmpty(ts);
    }

    /**
     * 包含
     * @param args
     * @param s
     * @return
     */
    public static boolean contain(String[] args, String s) {
        if(isEmpty(args)) return false;
        boolean f = false;
        for(String a : args) {
            if(equals(a, s)) {
                f = true;
                break;
            }
        }
        return f;
    }

    /**
     * 集合是否包含某个元素，当元素为空是不判断，直接返回false
     * @param collection
     * @param item
     * @param <T>
     * @return
     */
    public static <T> boolean contain(Collection<T> collection, T item) {
        if (isEmpty(collection)) {
            return false;
        }
        if (isNull(item)) {
            return false;
        }
        return collection.stream().anyMatch(c -> c.equals(item));
    }


    /**
     * 非
     * @param flag
     * @return
     */
    public static boolean not(boolean flag) {
        return ! flag;
    }

    /**
     *
     * @param flag
     * @param defaultFlag flag == null 时取该值
     * @return
     */
    public static boolean isTrue(Boolean flag, boolean defaultFlag) {
        return null == flag ? defaultFlag : flag;
    }

    /**
     *
     * @param flag
     * @param supplier
     * @param <T>
     * @return
     */
    public static <T> T callbackIfNecessary(boolean flag, Supplier<T> supplier) {
        return flag ? supplier.get() : null;
    }

    /**
     * @param flag
     * @param callback
     */
    public static void callbackIfNecessary(boolean flag, Callback0 callback) {
        if(flag) {
            callback.callback();
        }
    }

    /**
     * @param supplier
     * @param callback0
     */
    public static void callbackIfNecessary(Supplier<Boolean> supplier, Callback0 callback0) {
        if(supplier.get()) {
            callback0.callback();
        }
    }
}
