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

import com.galaxy.lemon.common.KVPair;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 统一多缓存管理
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class MixCacheManager extends AbstractCacheManager{
    public static final Logger logger = LoggerFactory.getLogger(MixCacheManager.class);
    
    private Map<CacheType, CacheManager> cacheManagerMap;
    private List<CacheManager> cacheManagers;
    private List<MixCacheResolver> mixCacheResolvers;
    
    public MixCacheManager(List<CacheManager> cacheManagers, List<MixCacheResolver> mixCacheResolvers) {
        super();
        this.cacheManagers = cacheManagers;
        this.mixCacheResolvers = mixCacheResolvers;
        Validate.notEmpty(this.cacheManagers, "List 'cacheManagers' can not be empty.");
        Validate.notEmpty(mixCacheResolvers, "List 'mixCacheResolvers' can not be empty.");
        initCacheManagers();

    }
    
    public void initCacheManagers() {
        this.cacheManagerMap = this.cacheManagers.stream().filter(cm -> !(cm instanceof MixCacheManager)).map(cm -> KVPair.instance(this.resolveCacheType(cm), cm))
                .collect(Collectors.toMap(KVPair::getK, KVPair::getV));
        Validate.notNull(this.cacheManagerMap, "cache managers can not be null.");
        Validate.notEmpty(this.cacheManagerMap.values().toArray(), "cache managers can not be null.");
        if(logger.isInfoEnabled()) {
            logger.info("find cache managers {}.", this.cacheManagerMap);
        }
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Collection<Cache> caches = new ArrayList<>();
        this.cacheManagerMap.values().stream().forEach(cm -> {
            Optional.ofNullable(cm.getCacheNames()).filter(JudgeUtils::isNotEmpty).map( cns -> cns.stream().map(cm::getCache).collect(Collectors.toList()) ).ifPresent(caches::addAll);
        });
        if(logger.isInfoEnabled()) {
            logger.info("load caches '{}'", caches.stream().map(Cache::getName).collect(Collectors.joining(", ")));
        }
        return caches;
    }

    @Override
    protected Cache getMissingCache(String name) {
        Cache cache = null;
        for (MixCacheResolver resolver : this.mixCacheResolvers) {
            if(resolver.match(name)) {
                cache = this.cacheManagerMap.get(resolver.getCacheType()).getCache(resolver.undecorateCacheName(name));
                if(logger.isInfoEnabled()) {
                    logger.info("get missing cache '{}' with name '{}'", cache, resolver.undecorateCacheName(name));
                }
                break;
            }
        }
        LemonException.throwLemonExceptionIfNecessary(null == cache, ErrorMsgCode.SYS_ERROR.getMsgCd(), "Could not find cache with undecorate name " + name);
        return cache;
    }

    private CacheType resolveCacheType(CacheManager cacheManager) {
        return this.mixCacheResolvers.stream().filter(r -> r.match(cacheManager)).map(MixCacheResolver::getCacheType).findFirst()
                .orElseThrow(() -> LemonException.create(ErrorMsgCode.SYS_ERROR.getMsgCd(),"Not support cache manager " + cacheManager));
    }

}
