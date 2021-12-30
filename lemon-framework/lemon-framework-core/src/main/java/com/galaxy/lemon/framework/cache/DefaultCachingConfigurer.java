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

package com.galaxy.lemon.framework.cache;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;

import java.util.Map;


/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultCachingConfigurer extends CachingConfigurerSupport {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCachingConfigurer.class);
    public static final String CACHE_KEY_SEPARATOR = ".";
    public static final String NO_PARAM = "NP";

    private String[] priorities;
    private Map<String, CacheResolver> cacheResolvers;

    public DefaultCachingConfigurer(Map<String, CacheResolver> cacheResolvers) {
        this(cacheResolvers, new String[]{CacheConstants.CACHE_RESOLVER_REDIS,
                CacheConstants.CACHE_RESOLVER_JCACHE,
                CacheConstants.CACHE_RESOLVER_EHCACHE});
    }

    public DefaultCachingConfigurer(Map<String, CacheResolver> cacheResolvers,
                                    String[] priorities) {
        this.cacheResolvers = cacheResolvers;
        this.priorities = priorities;
    }

    @Override
    public CacheResolver cacheResolver() {
        if (JudgeUtils.isEmpty(this.cacheResolvers)) {
            return null;
        }
        CacheResolver cacheResolver = null;
        for(String priority : priorities) {
            if (cacheResolvers.containsKey(priority)) {
                cacheResolver = cacheResolvers.get(priority);
                if (logger.isInfoEnabled()) {
                    logger.info("Found default CacheResolver {}.", cacheResolver);
                }
                break;
            }
        }
        return cacheResolver;
    }

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName()).append(CACHE_KEY_SEPARATOR).append(method.getName());
            if(null != params) {
                for (Object param : params) {
                    sb.append(CACHE_KEY_SEPARATOR);
                    if (null != param) {
                        if (param instanceof String) {
                            sb.append(param);
                        } else if(param instanceof CacheKeyExtractor){
                            sb.append(((CacheKeyExtractor) param).extract());
                        } else {
                            sb.append(StringUtils.toString(param));
                        }
                    } else {
                        sb.append(NO_PARAM);
                    }
                }
            }
            return sb.toString();
        };
    }
}
