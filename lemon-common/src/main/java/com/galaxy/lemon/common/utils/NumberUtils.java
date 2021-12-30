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

/**
 * Number utils
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {
    
    /**
     * @param value
     * @param defaultVal
     * @return
     */
    public static Integer getDefaultIfNull(Integer value, Integer defaultVal) {
        if(null == value) {
            return defaultVal;
        }
        return value;
    }
    
    /**
     * @param value
     * @param defaultVal
     * @return
     */
    public static Long getDefaultIfNull(Long value, Long defaultVal) {
        if(null == value) {
            return defaultVal;
        }
        return value;
    }
    
}
