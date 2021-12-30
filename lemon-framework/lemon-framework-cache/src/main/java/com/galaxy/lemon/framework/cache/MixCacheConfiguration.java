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

import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.utils.LemonUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 混合缓存配置
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class MixCacheConfiguration extends CachingConfigurerSupport {
    public static final String CACHE_KEY_PREFIX = "CACHE:";
    public static final String CACHE_KEY_SEPARATOR = ".";

    private List<CacheManager> cacheManagers;
    private List<MixCacheResolver> mixCacheResolvers;

    public MixCacheConfiguration(List<CacheManager> cacheManagers, List<MixCacheResolver> mixCacheResolvers) {
        this.cacheManagers = cacheManagers;
        this.mixCacheResolvers = mixCacheResolvers;
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        return new MixCacheManager(this.cacheManagers, this.mixCacheResolvers);
    }
    
    @Override
    @Bean  
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder(CACHE_KEY_PREFIX);
            sb.append(getApplicationName()).append(CACHE_KEY_SEPARATOR)
               .append(target.getClass().getSimpleName()).append(CACHE_KEY_SEPARATOR)
               .append(method.getName());
            if(null != params) {
                for (Object param : params) {
                    sb.append(CACHE_KEY_SEPARATOR);
                    if (null != param) {
                        if (param instanceof String) {
                            sb.append(param);
                        } else if(param instanceof CacheKeyParamExtractor){
                            sb.append(((CacheKeyParamExtractor) param).extract());
                            //sb.append(((CacheKeyParamExtractor) param).extract(param));
                        } else {
                            sb.append(StringUtils.toString(param));
                        }
                    } else {
                        sb.append("null");
                    }
                }
            }
            return sb.toString();
        };
    }
    
    private String getApplicationName() {
        return LemonUtils.getApplicationName();
    }
}
