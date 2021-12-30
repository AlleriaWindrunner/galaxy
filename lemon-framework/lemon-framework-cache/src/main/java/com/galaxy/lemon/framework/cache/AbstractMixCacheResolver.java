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

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractMixCacheResolver implements MixCacheResolver {

    private CacheType cacheType;

    public AbstractMixCacheResolver(CacheType cacheType) {
        this.cacheType = cacheType;
    }

    @Override
    public CacheType getCacheType() {
        return this.cacheType;
    }

    @Override
    public String decorateCacheName(String cacheName) {
        StringBuilder appendCN = new StringBuilder(this.cacheType.getCacheType());
        return appendCN.append(CACHE_NAME_SEPARATOR).append(cacheName).toString();
    }

    @Override
    public boolean match(String decoratedCacheName) {
        return StringUtils.startsWith(decoratedCacheName, getCacheType().getCacheType());
    }

    @Override
    public String undecorateCacheName(String decoratedCacheName) {
        if(decoratedCacheName.startsWith(cacheType.getCacheType())) {
            return decoratedCacheName.replaceFirst(this.cacheType.getCacheType() + CACHE_NAME_SEPARATOR, "");
        }
        return decoratedCacheName;
    }
}
