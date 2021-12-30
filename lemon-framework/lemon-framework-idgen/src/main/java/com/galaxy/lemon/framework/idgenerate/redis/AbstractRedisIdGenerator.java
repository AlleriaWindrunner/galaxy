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

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ResourceUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.id.AbstractCachingIdGenerator;
import com.galaxy.lemon.framework.id.IdGenProperties;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractRedisIdGenerator extends AbstractCachingIdGenerator {
    private static final String DEFAULT_PREFIX_ID_GEN = "IDGEN:";
    private static final String GLOBAL_REDIS_KEY = "GLOBAL";

    private RedisTemplate<String, Long> redisTemplate;
    private String idgenScript;

    public AbstractRedisIdGenerator(RedisTemplate<String, Long> redisTemplate,
                                    IdGenProperties idGenProperties,
                                    String redisScriptFilePath) {
        super(idGenProperties);
        this.redisTemplate = redisTemplate;
        loadRedisScript(redisScriptFilePath);
    }

    protected void loadRedisScript(String redisScriptFilePath) {
        try {
            this.idgenScript = ResourceUtils.getFileContent(redisScriptFilePath);
        } catch (IOException e) {
            LemonException.throwLemonException(e);
        }
        LemonException.throwLemonExceptionIfNecessary(JudgeUtils.isBlank(this.idgenScript), ErrorMsgCode.SYS_ERROR.getMsgCd(), "Redis hash id generator script " + redisScriptFilePath + " is not found.");
        if(logger.isDebugEnabled()) {
            logger.debug("Load IdGen lua script {}.", redisScriptFilePath);
            logger.debug("{}", this.idgenScript);
        }
    }

    public String getRedisIdGenScript() {
        return this.idgenScript;
    }

    public RedisTemplate<String, Long> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 非全局的用实例名
     * @return
     */
    public String getGlobalRedisKey() {
        return GLOBAL_REDIS_KEY;
    }

    public String getRedisKeyPrefix() {
        return StringUtils.getDefaultIfEmpty(this.getIdGenProperties().getPrefix(), DEFAULT_PREFIX_ID_GEN);
    }
}

