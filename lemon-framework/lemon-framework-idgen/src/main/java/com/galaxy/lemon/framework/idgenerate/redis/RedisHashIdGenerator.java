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
import com.galaxy.lemon.framework.idgenerate.redis.AbstractRedisIdGenerator;
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

public class RedisHashIdGenerator extends AbstractRedisIdGenerator {

    private static final String DEFAULT_IDGEN_LUA_FILE_PATH = "lua" + ResourceUtils.JAR_PACKAGE_PATH_SEPARATOR + "hashIdGen.lua";
    private static final Map<String, List<String>> CACHE_REDIS_SCRIPT_KEYS = new ConcurrentHashMap<>();

    public RedisHashIdGenerator(RedisTemplate<String, Long> redisTemplate, IdGenProperties idGenProperties) {
        super(redisTemplate, idGenProperties, DEFAULT_IDGEN_LUA_FILE_PATH);
    }

    @Override
    protected Long acquireLocalMaxValue(String idName, boolean global, Long maxValue, Long minValue, Integer delta) {
        List<String> keys = CACHE_REDIS_SCRIPT_KEYS.get(idName);
        if (null == keys) {
            keys = new ArrayList<>();
            keys.add(resolveIdGenRedisKey(global));
            CACHE_REDIS_SCRIPT_KEYS.put(idName, keys);
        }
        return this.getRedisTemplate().execute(new DefaultRedisScript<>(this.getRedisIdGenScript(), Long.class),
                new StringRedisSerializer(), new LongRedisSerializer(), keys, idName, String.valueOf(maxValue), String.valueOf(delta), String.valueOf(minValue));
    }

    /**
     *
     * @param global ?????????????????????ID?????????????????????redis key ???PREFIX????????????PREFIX+AppName
     * @return redis key??????????????????Hash???
     */
    private String resolveIdGenRedisKey(boolean global) {
        String prefix = getRedisKeyPrefix();
        switch (getIdGenProperties().getMode()) {
            case CLUSTER:
                if (!global) {
                    return StringUtils.toRedisTagKey(prefix, LemonUtils.getApplicationName());
                } else {
                    return StringUtils.toRedisTagKey(prefix, getGlobalRedisKey());
                }
            default:
                StringBuilder idGenKey = new StringBuilder(prefix);
                if (!global) {
                    idGenKey.append(LemonUtils.getApplicationName());
                } else {
                    idGenKey.append(getGlobalRedisKey());
                }
                return idGenKey.toString();
        }
    }

}
