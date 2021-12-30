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

package com.galaxy.lemon.framework.random;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.utils.LemonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 绑定token随机数，实际与session绑定
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class BindingTokenRandomTemplate implements RandomTemplate {
    private static final Logger logger = LoggerFactory.getLogger(BindingTokenRandomTemplate.class);
    
    private static final String REDIS_KEY_PREFIX = "random.";
    private StringRedisTemplate stringRedisTemplate;
    
    public BindingTokenRandomTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    
    @Override
    public String apply(String key, int leaseTime, RandomType randomType, int length) {
        if(JudgeUtils.isNull(LemonUtils.getToken())) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR.getMsgCd(), "Token is null during acquire random.");
        }
        String random = genRandom(randomType, length);
        stringRedisTemplate.boundValueOps(resolveRandomKey(key)).set(random, leaseTime, TimeUnit.MILLISECONDS);
        if(logger.isDebugEnabled()) {
            logger.debug("apply binding token random {} , key is {}", random, resolveRandomKey(key));
        }
        return random;
    }

    @Override
    public boolean validateOnce(String key, String random) {
        String randomFromRedis = acquireOnce(key);
        if(JudgeUtils.equals(random, randomFromRedis)) {
            return true;
        }
        return false;
    }

    @Override
    public String acquireOnce(String key) {
        if(JudgeUtils.isNull(LemonUtils.getToken())) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR.getMsgCd(), "Token is null during acquire random.");
        }
        String randomFromRedis = stringRedisTemplate.boundValueOps(resolveRandomKey(key)).getAndSet("");
        stringRedisTemplate.delete(resolveRandomKey(key));
        if(logger.isDebugEnabled()) {
            logger.debug("acquire once binding token random {} , key is {}", randomFromRedis, resolveRandomKey(key));
        }
        return randomFromRedis;
    }
    
    private String resolveRandomKey(String key) {
        StringBuilder keyBuilder = new StringBuilder(REDIS_KEY_PREFIX);
        keyBuilder.append(LemonUtils.getToken()).append(".").append(key);
        return keyBuilder.toString();
    }
    
}
