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

package com.galaxy.lemon.framework.response;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.exception.ExceptionConversionService;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.instantiator.ResponseDTOInstantiator;
import com.galaxy.lemon.framework.utils.LemonUtils;

import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RelaxedFailureHandlerResponseResolver extends AbstractExceptionConverterFailureHandlerResponseResolver {

    protected ResponseDTOInstantiator<?> responseDTOInstantiator;
    protected InternalDataHelper internalDataHelper;

    public RelaxedFailureHandlerResponseResolver(ExceptionConversionService exceptionConversionService,
                                                 ResponseDTOInstantiator<?> responseDTOInstantiator,
                                                 InternalDataHelper internalDataHelper) {
        super(exceptionConversionService);
        this.responseDTOInstantiator = responseDTOInstantiator;
        this.internalDataHelper = internalDataHelper;
    }

    /**
     * 当responseType为null时，responseDTOInstantiator 创建默认实例
     * @param alertCapable
     * @param responseType
     * @return
     */
    @Override
    public <R extends BaseDTO & ConfigurableAlerting> R doCreateResponseDTO(AlertCapable alertCapable, Class<R> responseType) {
        R responseDTO = doInternalCreateResponseDTO(responseType);
        preProcessResponseDTO(responseDTO);
        responseDTO.setMsgCd(alertCapable.getMsgCd());
        responseDTO.setMsgInfo(alertCapable.getMsgInfo());
        return this.doPreFlush(responseDTO);
    }

    protected <R extends BaseDTO & ConfigurableAlerting> R doPreFlush(R response) {
        return response;
    }

    protected <R extends BaseDTO & ConfigurableAlerting> void  preProcessResponseDTO(R responseDTO) {
        Optional.ofNullable(LemonUtils.getRequestId()).filter(JudgeUtils::isNotBlank).ifPresent(i -> this.internalDataHelper.setRequestId(responseDTO, i));
    }

    private <R extends BaseDTO & ConfigurableAlerting> R doInternalCreateResponseDTO(Class<R> responseType) {
        return Optional.ofNullable(responseType).map(ReflectionUtils::newInstance).orElseGet(() -> (R)this.responseDTOInstantiator.newInstanceResponseDTO());
    }
}
