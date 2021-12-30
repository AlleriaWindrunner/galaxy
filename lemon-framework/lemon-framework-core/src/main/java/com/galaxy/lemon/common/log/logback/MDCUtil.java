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

package com.galaxy.lemon.common.log.logback;

import org.slf4j.MDC;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class MDCUtil {
    /**
     * 默认的日志MDC Key
     */
    public static final String LOG_MDC_KEY = "requestId";
    /**
     * 默认的日志MDC Value
     */
    public static final String LOG_MDC_DEFAULT = "UNKNOWN";

    /**
     * 设置MDC属性
     *
     * @param key   MDC Key
     * @param value MDC Value
     */
    public static void putMDCKey(String key, String value) {
        MDC.put(key, value != null ? value : LOG_MDC_DEFAULT);
    }

    /**
     * 设置MDC默认Key属性
     *
     * @param value MDC Value
     */
    public static void putMDCKey(String value) {
        putMDCKey(LOG_MDC_KEY, value);
    }

    /**
     * 删除MDC Key
     *
     * @param key MDC Key
     */
    public static void removeMDCKey(String key) {
        MDC.remove(key);
    }

    /**
     * 删除MDC默认的Key
     */
    public static void removeMDCKey() {
        removeMDCKey(LOG_MDC_KEY);
    }
}
