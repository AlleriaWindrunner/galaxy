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

package com.galaxy.lemon.framework.idgenerate.redis;

import com.galaxy.lemon.common.utils.ResourceUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.id.IdGenProperties;
import com.galaxy.lemon.framework.redis.serializer.LongRedisSerializer;
import com.galaxy.lemon.framework.utils.LemonUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RedisStringIdGenerator extends AbstractRedisIdGenerator {

    private static final String DEFAULT_IDGEN_LUA_FILE_PATH = "lua" + ResourceUtils.JAR_PACKAGE_PATH_SEPARATOR + "stringIdGen.lua";
    private static final String REDIS_KEY_NAME_SEPARATOR = ".";
    private static final Map<String, List<String>> CACHE_REDIS_SCRIPT_KEYS = new ConcurrentHashMap<>();

    public RedisStringIdGenerator(RedisTemplate<String, Long> redisTemplate, IdGenProperties idGenProperties) {
        super(redisTemplate, idGenProperties, DEFAULT_IDGEN_LUA_FILE_PATH);
    }

    @Override
    protected Long acquireLocalMaxValue(String idName, boolean global, Long maxValue, Long minValue, Integer delta) {
        List<String> keys = CACHE_REDIS_SCRIPT_KEYS.get(idName);
        if (null == keys) {
            keys = new ArrayList<>();
            keys.add(resolveIdGenRedisKey(global, idName));
            CACHE_REDIS_SCRIPT_KEYS.put(idName, keys);
        }
        return this.getRedisTemplate().execute(new DefaultRedisScript<>(this.getRedisIdGenScript(), Long.class),
                new StringRedisSerializer(), new LongRedisSerializer(), keys, String.valueOf(maxValue), String.valueOf(delta), String.valueOf(minValue));
    }

    /**
     *
     * @param global 是否全局共享的ID，如果是，生成redis key 为PREFIX，否则为PREFIX+AppName
     * @return redis key（数据结构为Hash）
     */
    private String resolveIdGenRedisKey(boolean global, String idName) {
        String prefix = getRedisKeyPrefix();
        switch (getIdGenProperties().getMode()) {
            case CLUSTER:
                if (!global) {
                    return StringUtils.toRedisTagKey(prefix, LemonUtils.getApplicationName() + REDIS_KEY_NAME_SEPARATOR + idName);
                } else {
                    return StringUtils.toRedisTagKey(prefix, getGlobalRedisKey() + REDIS_KEY_NAME_SEPARATOR + idName);
                }
            default:
                StringBuilder idGenKey = new StringBuilder(prefix);
                if (!global) {
                    idGenKey.append(LemonUtils.getApplicationName()).append(REDIS_KEY_NAME_SEPARATOR).append(idName);
                } else {
                    idGenKey.append(getGlobalRedisKey()).append(REDIS_KEY_NAME_SEPARATOR).append(idName);
                }
                return idGenKey.toString();
        }
    }

}
