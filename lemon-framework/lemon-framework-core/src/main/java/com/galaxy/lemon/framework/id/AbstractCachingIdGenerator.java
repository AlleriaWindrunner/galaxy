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

package com.galaxy.lemon.framework.id;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.galaxy.lemon.common.exception.LemonException.throwLemonExceptionIfNecessary;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractCachingIdGenerator extends AbstractIdGenerator implements IdGenerator {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractCachingIdGenerator.class);

    private static final int DEFAULT_DELTA = 500;
    private static final String DEFAULT_DELTA_KEY = "default";
    private static final Map<String, IdStore> ID_STORE_MAP = new ConcurrentHashMap<>();

    private Lock lock = new ReentrantLock();

    public AbstractCachingIdGenerator(IdGenProperties idGenProperties) {
        super(idGenProperties);
    }

    /**
     *
     * 生成ID，key在application 范围内要唯一
     * 如果不需要多个应用共用ID生成器，建议使用此方法生成ID
     *
     * @param idName ID key
     * @return ID
     */
    @Override
    public String generateId(String idName) {
        return this.generateId(idName, false).toString();
    }

    /**
     * 针对所有应用生成唯一ID
     * @param idName ID key
     * @return ID
     */
    @Override
    public String generateGlobalId(String idName) {
        return this.generateId(idName, true).toString();
    }

    /**
     * 生成ID
     *
     * @param idName ID Name
     * @param global 是否全局的 true ：全局 false : 钟对application
     * @return ID
     */
    protected Long generateId(String idName, boolean global) {
        LemonException.throwLemonExceptionIfNecessary(JudgeUtils.isBlank(idName), LemonException.SYS_ERROR_MSGCD, "Id name cloud not be blank on IdGenerator.");
        IdStore idStore = this.getIdStoreFromCache(idName);
        if(null == idStore) {
            lock.lock();
            try {
                idStore = this.getIdStoreFromCache(idName);
                if(null == idStore) {
                    idStore = this.createIdStore(idName, global);
                    this.putIdStoreToCache(idName, idStore);
                }
            } finally {
                lock.unlock();
            }
        }
        while(true) {
            Long value = idStore.nextValue();
            if(value < 0) {
                lock.lock();
                try{
                    idStore = this.getIdStoreFromCache(idName);
                    value = idStore.nextValue();
                    if(value < 0) {
                        idStore = this.createIdStore(idName, global);
                        value = idStore.nextValue();
                        this.putIdStoreToCache(idName, idStore);
                    }
                } finally {
                    lock.unlock();
                }
                //这个判断应该不会进去，除非脚本没有对maxValue进行控制，防御性代码
                if(value < 0) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("IdGenerator script may be have a bug, generate id value is {}, defensive programming.", value);
                    }
                    idStore = this.getIdStoreFromCache(idName);
                    continue;
                }
            }
            if(logger.isDebugEnabled()) {
                logger.debug("Acquired id \"{}\" with name \"{}\".", value, idName);
            }
            return value;
        }
    }

    /**
     * 创建IDStore
     * @param idName
     * @param global
     * @return
     */
    protected IdStore createIdStore(String idName, boolean global) {
        IdStore oldIdStore = this.getIdStoreFromCache(idName);
        Long maxValue = null != oldIdStore ? oldIdStore.maxValue : this.getMaxValue(idName);
        if(null == maxValue) {
            maxValue = -1L;
        }
        Long minValue = this.getMinValue(idName);
        Integer delta = this.getDelta(idName);
        throwLemonExceptionIfNecessary((maxValue != -1 && minValue >= maxValue), ErrorMsgCode.SYS_ERROR,
                "The \"IdGen\" with name \"{1}\" min value must be less than the max value.",
                new String[]{idName});
        Long localMaxValue = this.acquireLocalMaxValue(idName, global, maxValue, minValue, delta);
        Long currentValue = localMaxValue - delta + 1;
        if(-1 != maxValue) {
            localMaxValue = localMaxValue <= maxValue ? localMaxValue : maxValue;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("New Id store with key \"{}\", currentId \"{}\", localMaxId \"{}\", maxValue \"{}\", delta \"{}\", minValue \"{}\".",
                    idName, currentValue, localMaxValue, maxValue, delta, minValue);
        }
        return IdStore.newInstance(currentValue, localMaxValue, maxValue);
    }

    protected IdStore getIdStoreFromCache(String idName) {
        return ID_STORE_MAP.get(idName);
    }

    protected void putIdStoreToCache(String idName, IdStore idStore) {
        ID_STORE_MAP.put(idName, idStore);
    }

    /**
     * 本地缓存的ID数量
     * @param idName
     * @return
     */
    protected Integer getDelta(String idName) {
        return Optional.ofNullable(this.getIdGenProperties().getDelta().get(idName)).orElseGet(
                () -> Optional.ofNullable(this.getIdGenProperties().getDelta().get(DEFAULT_DELTA_KEY)).orElse(DEFAULT_DELTA));
    }

    /**
     * 获取ID本地缓存最大值
     * @param idName ID 名称
     * @param global 是否全局唯一ID
     * @param maxValue ID最大值
     * @param minValue ID最小值
     * @param delta 每次在本地缓存的ID个数
     * @return
     */
    protected abstract Long acquireLocalMaxValue(String idName, boolean global, Long maxValue, Long minValue, Integer delta);

    /**
     * id store
     */
    protected static class IdStore {
        private AtomicLong localIdGen;
        private Long localMaxValue;
        //全局最大值，超过该值继续从minValue开始;该值为null则无穷自增，达到long最大值
        private Long maxValue;

        public IdStore(AtomicLong localIdGen, Long localMaxValue, Long maxValue) {
            this.localIdGen = localIdGen;
            this.localMaxValue = localMaxValue;
            this.maxValue = maxValue;
        }

        public static IdStore newInstance(Long currentValue, Long localMaxValue, Long maxValue) {
            return new IdStore(new AtomicLong(currentValue), localMaxValue, maxValue);
        }

        /**
         * 超出localMaxValue 返回-1
         * 超出maxValue返回-2
         * @return
         */
        public Long nextValue() {
            Long value = localIdGen.getAndIncrement();
            if(value > localMaxValue) {
                return -1L;
            }
            if(-1 != maxValue && value > maxValue) {
                return -2L;
            }
            return value;
        }
    }
}
