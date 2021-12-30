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
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * cache name resolver
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class MixCacheCacheResolver extends AbstractCacheResolver implements EnvironmentAware {

    private MixCacheResolver mixCacheResolver;
    private Environment environment;
    
    public MixCacheCacheResolver(CacheManager cacheManager, MixCacheResolver mixCacheResolver) {
        super(cacheManager);
        this.mixCacheResolver = mixCacheResolver;
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = context.getOperation().getCacheNames();
        if(JudgeUtils.isNotEmpty(cacheNames)) {
            return cacheNames.stream().map(this::resolveCacheName).collect(Collectors.toList());
        }
        return cacheNames;
    }
    
    private String resolveCacheName(String originalCacheName) {
        return this.mixCacheResolver.decorateCacheName(this.environment.resolvePlaceholders(originalCacheName));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
