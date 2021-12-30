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

package com.galaxy.lemon.framework.data.interceptor;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.framework.context.LemonContextLifecycle;
import com.galaxy.lemon.framework.data.*;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.utils.IdGenUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.data.LemonDataInitializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * LemonDataInitializer adapter
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonDataInitializerAdapter implements LemonDataInitializer, LemonDataLifecycle, LemonContextLifecycle {
    public static final Logger logger = LoggerFactory.getLogger(LemonDataInitializerAdapter.class);
    private LemonDataInitializer lemonDataInitializer;
    private LemonDataOperationMetadata lemonDataOperationMetadata;
    private InternalDataHelper internalDataHelper;

    /**
     *
     * @param lemonDataInitializer
     * @param lemonDataSource
     * @param annotatedMethodClass
     * @param annotatedMethod
     */
    public LemonDataInitializerAdapter(LemonDataInitializer lemonDataInitializer,
                                       InitialLemonData.LemonDataSource lemonDataSource,
                                       boolean requiredClearContext,
                                       Class<?> annotatedMethodClass,
                                       Method annotatedMethod,
                                       InternalDataHelper internalDataHelper) {
        this(lemonDataInitializer,
                new LemonDataOperationMetadata(lemonDataSource, requiredClearContext, annotatedMethodClass, annotatedMethod),
                internalDataHelper);
    }

    public LemonDataInitializerAdapter(LemonDataInitializer lemonDataInitializer,
                                       LemonDataOperationMetadata lemonDataOperationMetadata,
                                       InternalDataHelper internalDataHelper) {
        this.lemonDataInitializer = lemonDataInitializer;
        this.lemonDataOperationMetadata = lemonDataOperationMetadata;
        this.internalDataHelper = internalDataHelper;
    }

    /**
     *
     * @param lemonData 拦截方法类型为BaseLemonData的输入参数
     * @param <T>
     */
    public <T extends BaseLemonData> void initialLemonData(T lemonData) {
        switch (this.lemonDataOperationMetadata.getLemonDataSource()) {
            case LEMON_DATA_INSTANCE:
                this.initialLemonData();
                break;
            case LEMON_DATA_INITIALIZER:
            case SIMPLE_LEMON_DATA_INITIALIZER:
                this.initialLemonData();
                this.resolveEntryTx(LemonDataHolder.getLemonData());
                break;
            case COPY_FROM_ARGUMENT:
                this.initialLemonData();
                if (lemonData instanceof BaseDTO) {
                    this.internalDataHelper.copyDTOToLemonData((BaseDTO)lemonData, LemonDataHolder.getLemonData());
                } else {
                    this.internalDataHelper.copeLemonData(lemonData, LemonDataHolder.getLemonData());
                }
                this.resolveEntryTx(LemonDataHolder.getLemonData());
                break;
            case COPY_SOME_FROM_ARGUMENT:
                this.initialLemonData();
                if (lemonData instanceof BaseDTO) {
                    this.internalDataHelper.copyDTOToLemonData((BaseDTO)lemonData, LemonDataHolder.getLemonData());
                } else {
                    this.internalDataHelper.copeLemonData(lemonData, LemonDataHolder.getLemonData());
                }
                LemonDataHolder.getLemonData().setRequestId(IdGenUtils.generateRequestId());
                LemonDataHolder.getLemonData().setMsgId(IdGenUtils.generateMsgId());
                LemonDataHolder.getLemonData().setEntryTx(null);
                this.resolveEntryTx(LemonDataHolder.getLemonData());
                break;
            default:
                LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Unsupported lemon data initializer source {1}", new String[]{this.lemonDataOperationMetadata.getLemonDataSource().toString()});
                break;
        }
    }

    @Override
    public void initialLemonData() {
        Optional.ofNullable(this.lemonDataInitializer).ifPresent(LemonDataInitializer::initialLemonData);
    }

    private void resolveEntryTx(BaseLemonData lemonData) {
        if (StringUtils.isBlank(lemonData.getEntryTx())) {
            lemonData.setEntryTx(this.lemonDataOperationMetadata.getAnnotatedMethodClass().getSimpleName() + LemonConstants.DOT + this.lemonDataOperationMetadata.getAnnotatedMethod().getName());
        }
    }

    @Override
    public void clear() {
        if (this.lemonDataOperationMetadata.isRequiredClearContext()) {
            LemonDataHolder.clear();
            LemonContext.clearCurrentContext();
            if (logger.isDebugEnabled()) {
                logger.debug("Cleared lemon data context by {}", this);
                logger.debug("Cleared LemonContext by {}", this);
            }
        }
    }

    static class LemonDataOperationMetadata {
        private InitialLemonData.LemonDataSource lemonDataSource;
        private boolean requiredClearContext;
        private Class<?> annotatedMethodClass;
        private Method annotatedMethod;

        public LemonDataOperationMetadata(InitialLemonData.LemonDataSource lemonDataSource,
                                          boolean requiredClearContext,
                                          Class<?> annotatedMethodClass,
                                          Method annotatedMethod) {
            this.lemonDataSource = lemonDataSource;
            this.requiredClearContext = requiredClearContext;
            this.annotatedMethod = annotatedMethod;
            this.annotatedMethodClass = annotatedMethodClass;
        }

        public InitialLemonData.LemonDataSource getLemonDataSource() {
            return lemonDataSource;
        }

        public Class<?> getAnnotatedMethodClass() {
            return annotatedMethodClass;
        }

        public Method getAnnotatedMethod() {
            return annotatedMethod;
        }

        public boolean isRequiredClearContext() {
            return requiredClearContext;
        }
    }
}
