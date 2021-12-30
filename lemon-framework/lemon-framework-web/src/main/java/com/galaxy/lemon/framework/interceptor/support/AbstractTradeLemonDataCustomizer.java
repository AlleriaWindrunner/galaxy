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

package com.galaxy.lemon.framework.interceptor.support;

import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.*;
import com.galaxy.lemon.framework.data.instantiator.LemonDataInstantiator;
import com.galaxy.lemon.framework.interceptor.LemonDataCustomizer;

/**
 * 实现了基本的DTO <<===>> LemonData 拷贝
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractTradeLemonDataCustomizer implements LemonDataCustomizer {

    private LemonDataInstantiator<?> lemonDataInstantiator;

    private LemonDataInitializer lemonDataInitializer;

    private InternalDataHelper internalDataHelper;

    public AbstractTradeLemonDataCustomizer(LemonDataInstantiator<?> lemonDataInstantiator,
                                            LemonDataInitializer lemonDataInitializer,
                                            InternalDataHelper internalDataHelper) {
        this.lemonDataInstantiator = lemonDataInstantiator;
        this.lemonDataInitializer = lemonDataInitializer;
        this.internalDataHelper = internalDataHelper;
    }

    @Override
    public <D extends BaseDTO> void customizeRequestEntry(D requestDTO) {
        this.lemonDataInitializer.initialLemonData();
        if (null != requestDTO) {
            this.internalDataHelper.copyLemonDataToDTO(LemonDataHolder.getLemonData(), requestDTO);
        }
        doCustomizeRequestEntry(requestDTO, LemonDataHolder.getLemonData());
    }

    @Override
    public <D extends BaseDTO, L extends BaseLemonData> void customizeRequestNonEntry(D requestDTO, L lemonData) {
        boolean hasLemonData = (lemonData != null);
        if (! hasLemonData) {
            lemonData = (L)this.lemonDataInstantiator.newInstanceLemonData();
        }
        LemonDataHolder.setLemonData(lemonData);
        if (null != requestDTO) {
            if (hasLemonData) {
                this.internalDataHelper.copyLemonDataToDTO(lemonData, requestDTO);
            } else {
                this.internalDataHelper.copyDTOToLemonData(requestDTO, lemonData);
            }
        }
        doCustomizeRequestNonEntry(requestDTO, lemonData);
    }

    @Override
    public <R extends BaseDTO & ConfigurableAlerting> void customizeResponse(R responseDTO) {
        if (null == responseDTO || null == LemonDataHolder.getLemonData()) {
            return;
        }
        this.internalDataHelper.copyLemonDataToDTO(LemonDataHolder.getLemonData(), responseDTO);
        doCustomizeResponse(responseDTO, LemonDataHolder.getLemonData());
        this.internalDataHelper.cleanResponseDTO(responseDTO);
    }

    /**
     * 客户化平台参数处理
     * @param baseDTO
     * @param lemonData
     */
    protected abstract <D extends BaseDTO, L extends BaseLemonData> void doCustomizeRequestNonEntry(D baseDTO, L lemonData);

    /**
     * 客户化平台参数处理
     * @param baseDTO
     * @param lemonData
     */
    public abstract <D extends BaseDTO, L extends BaseLemonData> void doCustomizeRequestEntry(D baseDTO, L lemonData);

    /**
     * 客户化平台参数处理
     * @param responseDTO
     * @param lemonData
     */
    protected abstract <R extends BaseDTO & ConfigurableAlerting, L extends BaseLemonData> void doCustomizeResponse(R responseDTO, L lemonData);
}
