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
import com.galaxy.lemon.framework.cache.CacheNameResolver;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class NameResolvedCacheResolver extends AbstractCacheResolver {
    private CacheNameResolver cacheNameResolver;

    public NameResolvedCacheResolver(CacheManager cacheManager) {
        this(cacheManager, null);
    }

    public NameResolvedCacheResolver(CacheManager cacheManager, CacheNameResolver cacheNameResolver) {
        super(cacheManager);
        this.cacheNameResolver = cacheNameResolver;
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = context.getOperation().getCacheNames();
        return JudgeUtils.isNotEmpty(cacheNames) ? cacheNames.stream().map(this::resolveCacheName).collect(Collectors.toList()) : cacheNames;
    }

    private String resolveCacheName(String originalCacheName) {
        return Optional.ofNullable(this.cacheNameResolver).map(r -> r.resolve(originalCacheName)).orElse(originalCacheName);
    }
}
