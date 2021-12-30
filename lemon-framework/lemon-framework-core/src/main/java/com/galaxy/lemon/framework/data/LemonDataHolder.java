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

package com.galaxy.lemon.framework.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;

import java.util.Optional;

/**
 * lemon data holder
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonDataHolder {
    private static final Logger logger = LoggerFactory.getLogger(LemonDataHolder.class);
    
    private static final ThreadLocal<BaseLemonData> lemonDataHolder = new NamedThreadLocal<>("LemonDataHolder");
 
    public static BaseLemonData getLemonData() {
        return lemonDataHolder.get();
    }

    public static <T extends BaseLemonData>  T getLemonData(Class<T> clazz) {
        return Optional.ofNullable(getLemonData()).map(clazz::cast).orElse(null);
    }
    
    public static void setLemonData(BaseLemonData lemonData) {
        lemonDataHolder.set(lemonData);
        if (logger.isDebugEnabled()) {
            logger.debug("Bound lemon data {} to thread.", lemonData);
        }
    }
    
    public static void clear() {
        lemonDataHolder.remove();
        if (logger.isDebugEnabled()) {
            logger.debug("Cleared thread-bound lemon data.");
        }
    }
    
}
