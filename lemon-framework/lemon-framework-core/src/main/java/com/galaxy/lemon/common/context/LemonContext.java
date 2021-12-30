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

package com.galaxy.lemon.common.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lemon context
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonContext extends ConcurrentHashMap<Object, Object>{
    private static final long serialVersionUID = 2729826264846708794L;
    private static final Logger logger = LoggerFactory.getLogger(LemonContext.class);
    
    private static final ThreadLocal<LemonContext> currentContextHolder = new ThreadLocal<LemonContext>(){
        protected LemonContext initialValue() {
            if (logger.isDebugEnabled()) {
                logger.debug("Initializing 'LemonContext' by thread-bound.");
            }
            return new LemonContext();
        }
    };
    
    public static LemonContext getCurrentContext() {
        return currentContextHolder.get();
    }
    
    public static void setCurrentContext(LemonContext lemonContext) {
        if (logger.isDebugEnabled()) {
            logger.debug("Bound lemon context {} to thread.", lemonContext);
        }
        currentContextHolder.set(lemonContext);
    }

    public static void putToCurrentContext(Object key, Object value) {
        getCurrentContext().put(key, value);
    }

    public static Object getFromCurrentContext(Object key) {
        return getCurrentContext().get(key);
    }

    public static void removeFromCurrentContext(Object key) {
        getCurrentContext().remove(key);
    }

    public static void clearCurrentContext() {
        currentContextHolder.remove();
        if (logger.isDebugEnabled()) {
            logger.debug("Cleared thread-bound lemon context.");
        }
    }

    /**
     * clear form thread locale
     */
    public void clear() {
        currentContextHolder.remove();
        if (logger.isDebugEnabled()) {
            logger.debug("Cleared thread-bound lemon context.");
        }
    }

    /**
     *
     * @param key
     * @param defaultVal
     * @return
     * @deprecated as of lemon 2.0.0, in favor of using {@link #getBoolean}
     */
    @Deprecated
    public boolean getBooleanOrDefault(Object key, boolean defaultVal) {
        return Optional.ofNullable(get(key)).map(Boolean.class::cast).orElse(defaultVal);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Boolean getBoolean(Object key, Boolean defaultValue) {
        return Optional.ofNullable(get(key)).map(Boolean.class::cast).orElse(defaultValue);
    }

    /**
     *
     * @param key
     * @return
     */
    public Boolean getBoolean(Object key) {
        return Optional.ofNullable(get(key)).map(Boolean.class::cast).orElse(null);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(Object key, String defaultValue) {
        return Optional.ofNullable(get(key)).map(String::valueOf).orElse(defaultValue);
    }

    /**
     *
     * @param key
     * @return
     */
    public String getString(Object key) {
        return Optional.ofNullable(get(key)).map(String::valueOf).orElse(null);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Integer getInteger(Object key, Integer defaultValue) {
        return Optional.ofNullable(get(key)).map(Integer.class::cast).orElse(defaultValue);
    }

    /**
     *
     * @param key
     * @return
     */
    public Integer getInteger(Object key) {
        return Optional.ofNullable(get(key)).map(Integer.class::cast).orElse(null);
    }

    /**
     *
     * @param key
     * @return
     */
    public Long getLong(Object key) {
        return Optional.ofNullable(get(key)).map(Long.class::cast).orElse(null);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Long getLong(Object key, Long defaultValue) {
        return Optional.ofNullable(get(key)).map(Long.class::cast).orElse(defaultValue);
    }

    /**
     *
     * @param key
     * @return
     */
    public Double getDouble(Object key) {
        return Optional.ofNullable(get(key)).map(Double.class::cast).orElse(null);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Double getDouble(Object key, Double defaultValue) {
        return Optional.ofNullable(get(key)).map(Double.class::cast).orElse(defaultValue);
    }

    /**
     *
     * @param key
     * @return
     */
    public LocalDateTime getLocalDataTime(Object key) {
        return Optional.ofNullable(get(key)).map(LocalDateTime.class::cast).orElse(null);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public LocalDateTime getLocalDataTime(Object key, LocalDateTime defaultValue) {
        return Optional.ofNullable(get(key)).map(LocalDateTime.class::cast).orElse(defaultValue);
    }

}
