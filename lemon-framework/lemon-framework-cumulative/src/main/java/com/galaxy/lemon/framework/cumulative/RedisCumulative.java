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

package com.galaxy.lemon.framework.cumulative;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.config.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ResourceUtils;
import com.galaxy.lemon.framework.redis.serializer.LongRedisSerializer;

/**
 * Redis 实现累计
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RedisCumulative implements Cumulative {
    private static final Logger logger = LoggerFactory.getLogger(RedisCumulative.class);
    
    public static final String HASH_KEY_PREFIX = "CUMULATIVE.";
    public static final String REDIS_CUMULATIVE_LUA_FILE_PATH = "lua" + ResourceUtils.JAR_PACKAGE_PATH_SEPARATOR + "cumulative.lua";;

    private String script;
    private RedisTemplate<String ,String> redisTemplate;

    @Value("${lemon.cumulative.redis.mode:SINGLE}")
    private Mode redisMode;

    /**
     * @param redisTemplate
     */
    public RedisCumulative(RedisTemplate<String ,String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        try {
            script = ResourceUtils.getFileContent(REDIS_CUMULATIVE_LUA_FILE_PATH);
            if(logger.isDebugEnabled()) {
                logger.debug("loaded cumulative script:");
                logger.debug("{}", script);
            }
        } catch (IOException e) {
            LemonException.throwLemonException(e);
        }
    }
    
    @Override
    public void countByDay(String key, Dimension... dimensions) {
        if(JudgeUtils.isBlank(key) || null == dimensions || dimensions.length <= 0) {
            LemonException.throwLemonException(ErrorMsgCode.CUMULATIVE_ERROR.getMsgCd(), "param key or dimensions is null.");
        }
        List<String> keys = new ArrayList<>();
        keys.add(getKey(HASH_KEY_PREFIX, key, CumulativeMode.DAY));
        keys.add("");
        List<String> args = new ArrayList<>();
        args.add(CumulativeMode.DAY.getMode());
        for(Dimension d : dimensions) {
            args.add(d.getKey());
            args.add(d.getValue());
        }
        Object[] argsArray = args.toArray(new String[]{});
        if(logger.isDebugEnabled()) {
            logger.debug("redis day cumulative with keys {} ~~~ args {}", keys, argsArray);
        }
        Long rst = this.redisTemplate.execute(new DefaultRedisScript<Long>(this.script, Long.class),
            new StringRedisSerializer(), new LongRedisSerializer(), keys, argsArray);
        if(rst != 0) {
            LemonException.throwLemonException(ErrorMsgCode.CUMULATIVE_ERROR.getMsgCd(), 
                "redis day cumulative occured error, keys "+keys + ", args "+ argsArray);
        }
    }

    @Override
    public void countByMonth(String key, Dimension... dimensions) {
        if(JudgeUtils.isBlank(key) || null == dimensions || dimensions.length <= 0) {
            LemonException.throwLemonException(ErrorMsgCode.CUMULATIVE_ERROR.getMsgCd(), "param key or dimensions is null.");
        }
        List<String> keys = new ArrayList<>();
        keys.add("");
        keys.add(getKey(HASH_KEY_PREFIX, key, CumulativeMode.MONTH));
        List<String> args = new ArrayList<>();
        args.add(CumulativeMode.MONTH.getMode());
        for(Dimension d : dimensions) {
            args.add(d.getKey());
            args.add(d.getValue());
        }
        Object[] argsArray = args.toArray(new String[]{});
        if(logger.isDebugEnabled()) {
            logger.debug("redis month cumulative with keys {} ~~~ args {}", keys, argsArray);
        }
        Long rst = this.redisTemplate.execute(new DefaultRedisScript<Long>(this.script, Long.class),
            new StringRedisSerializer(), new LongRedisSerializer(), keys, argsArray);
        if(rst != 0) {
            LemonException.throwLemonException(ErrorMsgCode.CUMULATIVE_ERROR.getMsgCd(), 
                "redis month cumulative occured error, keys "+keys + ", args "+ argsArray);
        }
    }

    @Override
    public void countByDayAndMonth(String key, Dimension... dimensions) {
        if(JudgeUtils.isBlank(key) || null == dimensions || dimensions.length <= 0) {
            LemonException.throwLemonException(ErrorMsgCode.CUMULATIVE_ERROR.getMsgCd(), "param key or dimensions is null.");
        }
        List<String> keys = new ArrayList<>();
        keys.add(getKey(HASH_KEY_PREFIX, key, CumulativeMode.DAY));
        keys.add(getKey(HASH_KEY_PREFIX, key, CumulativeMode.MONTH));
        List<String> args = new ArrayList<>();
        args.add(CumulativeMode.DAY_AND_MONTH.getMode());
        for(Dimension d : dimensions) {
            args.add(d.getKey());
            args.add(d.getValue());
        }
        Object[] argsArray = args.toArray(new String[]{});
        if(logger.isDebugEnabled()) {
            logger.debug("redis day and month cumulative with keys {} ~~~ args {}", keys, argsArray);
        }
        Long rst = this.redisTemplate.execute(new DefaultRedisScript<Long>(this.script, Long.class),
            new StringRedisSerializer(), new LongRedisSerializer(), keys, argsArray);
        if(rst != 0) {
            LemonException.throwLemonException(ErrorMsgCode.CUMULATIVE_ERROR.getMsgCd(), 
                "redis day and month cumulative occured error, keys "+keys + ", args "+ argsArray);
        }
    }

    @Override
    public String queryByDay(String key, String dimensionKey) {
        Object obj = this.redisTemplate.opsForHash().get(getKey(HASH_KEY_PREFIX, key, CumulativeMode.DAY), dimensionKey);
        return String.valueOf(obj);
    }

    @Override
    public String queryByMonth(String key, String dimensionKey) {
        Object obj = this.redisTemplate.opsForHash().get(getKey(HASH_KEY_PREFIX, key, CumulativeMode.MONTH), dimensionKey);
        return String.valueOf(obj);
    }

    public String getKey(String prefix, String key, CumulativeMode mode, Mode redisMode) {
        switch (redisMode) {
            case CLUSTER:
                StringBuilder sb = new StringBuilder(prefix);
                switch (mode) {
                    case DAY:
                        sb.append(StringUtils.toRedisTagKey(null, DateTimeUtils.getCurrentMonthStr())).append(DateTimeUtils.getCurrentDayStr());
                        break;
                    case MONTH:
                        sb.append(StringUtils.toRedisTagKey(null, DateTimeUtils.getCurrentMonthStr()));
                        break;
                    default:
                        LemonException.throwLemonException(ErrorMsgCode.CUMULATIVE_ERROR.getMsgCd(),"can not get cumulative key with mode "+mode);
                        break;
                }
                sb.append(".").append(key);
                return sb.toString();
            default:
                return getKey(prefix, key, mode);
        }

    }
    
}
