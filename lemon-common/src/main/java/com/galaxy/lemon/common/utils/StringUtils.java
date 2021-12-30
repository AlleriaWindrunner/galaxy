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

import org.springframework.core.env.Environment;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * 字符串处理
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static final String BRACE_LEFT = "{";
    public static final String BRACE_RIGHT = "}";
    /**
     * 如果字符串str 为空，则取默认值defaultStr
     *
     * @param str
     * @param defaultValueStr
     * @return
     */
    public static String getDefaultIfEmpty(String str, String defaultValueStr) {
        if (null == str || "".equals(str)) {
            return defaultValueStr;
        }
        return str;
    }

    /**
     * 对象转字符串
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return null; //之前返回null
        }
        return String.valueOf(obj);
    }

    public static String leftPadIfNecessary(String value, int length, char padChar) {
        if (-1 == length) {
            return value;
        }
        return leftPad(value, length, padChar);
    }

    /**
     * 右补充达到指定长度
     *
     * @param value
     * @param length
     * @param padChar
     * @return
     */
    public static String rightPadIfNecessary(String value, int length, char padChar) {
        if (-1 == length) {
            return value;
        }
        return rightPad(value, length, padChar);
    }

    /**
     * 删除左补充字符
     *
     * @param value
     * @param padChar
     * @return
     */
    public static String removeLeftPad(String value, char padChar) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        int index = 0;
        char[] valueChars = value.toCharArray();
        for (int i = 0; i < valueChars.length; i++) {
            if (valueChars[i] != padChar) {
                index = i;
                break;
            }
        }
        return value.substring(index);
    }

    /**
     * 删除右补充字符
     *
     * @param value
     * @param padChar
     * @return
     */
    public static String removeRightPad(String value, char padChar) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        int index = value.length() - 1;
        char[] valueChars = value.toCharArray();
        for (int i = value.length() - 1; i >= 0; i--) {
            if (valueChars[i] != padChar) {
                index = i + 1;
                break;
            }
        }
        return value.substring(0, index);
    }

    /**
     * 异常对象转化为字符串
     *
     * @param e
     * @return
     */
    public static String toString(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName() + ": ");
        if (e.getMessage() != null) {
            p.print(e.getMessage() + "\n");
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    /**
     * 解决占位符
     *
     * @param originalStr
     * @param environment
     * @deprecated as of lemon framework 2.0.0, in favor of using {@link # Environment.resolvePlaceholder}
     * @return
     */
    @Deprecated
    public static String parsePlaceHolder(String originalStr, Environment environment) {
        return Optional.ofNullable(originalStr).filter(StringUtils::isNotBlank).map(environment::resolvePlaceholders).orElse(originalStr);
//        if (JudgeUtils.isBlank(originalStr)) {
//            return originalStr;
//        }
        /*StringBuilder sb = new StringBuilder();
        String str = originalStr;
        while (true) {
            int start = str.indexOf(PropertySourcesPlaceholderConfigurer.DEFAULT_PLACEHOLDER_PREFIX);
            if (-1 == start) {
                break;
            }
            sb.append(str.substring(0, start));
            int end = str.indexOf(PropertySourcesPlaceholderConfigurer.DEFAULT_PLACEHOLDER_SUFFIX);
            sb.append(environment.getProperty(str.substring(start + 2, end)));
            sb.append(str.substring(end + 1));
            str = sb.toString();
            sb.delete(0, sb.length());
        }
        return str;*/
    }

    /**
     * redis tag key
     * @param prefix
     * @param key
     * @return
     */
    public static String toRedisTagKey(String prefix, String key) {
        StringBuilder sb = new StringBuilder();
        if (isNotBlank(prefix)) {
            sb.append(prefix);
        }
        sb.append(BRACE_LEFT).append(key).append(BRACE_RIGHT);
        return sb.toString();
    }

    /**
     * 
     * @param key
     * @return
     */
    public static String toRedisTagKey(String key) {
        return toRedisTagKey(null, key);
    }

    /**
     * xxx{1}xxx{2} format to xxxargs[0]xxxargs[1]
     * @param format
     * @param args
     * @return
     */
    public static String formatString(String format, String... args) {
        if (args == null || args.length == 0) {
            return format;
        }
        if (isBlank(format) || format.indexOf(BRACE_LEFT) == -1) {
            return format;
        }
        StringBuilder result = new StringBuilder();
        int off = 0, idx1 = 0, idx2 = 0;
        while (true) {
            idx1 = format.indexOf(BRACE_LEFT, off);
            if (idx1 == -1) {
                break;
            }
            idx2 = format.indexOf(BRACE_RIGHT, idx1 + 1);
            if (idx2 == -1) {
                break;
            }
            result.append(format.substring(off, idx1));
            int idx = NumberUtils.toInt(format.substring(idx1 + 1, idx2));
            if (idx <= 0 || idx > args.length
                    || args[idx - 1] == null) {
                result.append(format.substring(idx1, idx2 + 1));
            } else {
                result.append(args[idx - 1]);
            }

            off = idx2 + 1;
        }
        result.append(format.substring(off));
        return result.toString();
    }

}
