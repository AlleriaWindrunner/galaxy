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
import com.galaxy.lemon.common.BusinessObjectCapable;
import com.galaxy.lemon.common.exception.ExceptionConversionService;
import com.galaxy.lemon.common.utils.BeanUtils;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.BaseDTO;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractExceptionConverterFailureHandlerResponseResolver implements FailureHandlerResponseResolver {

    private ExceptionConversionService exceptionConversionService;

    public AbstractExceptionConverterFailureHandlerResponseResolver(ExceptionConversionService exceptionConversionService) {
        this.exceptionConversionService = exceptionConversionService;
    }

    @Override
    public <R extends BaseDTO & ConfigurableAlerting> R handlerFailure(Throwable throwable, Class<R> responseType) {
        AlertCapable alertCapable = this.exceptionConversionService.convert(throwable);
        R r = doCreateResponseDTO(alertCapable, responseType);
        if (throwable instanceof BusinessObjectCapable) {
            Object businessObject = ((BusinessObjectCapable) throwable).getBusinessObject();
            if (null != businessObject) {
                BeanUtils.copyProperties(r, businessObject);
            }
        }
        return r;
    }

    @Override
    public <R extends BaseDTO & ConfigurableAlerting> R handlerFailure(AlertCapable alertCapable, Class<R> responseType) {
        return doCreateResponseDTO(alertCapable, responseType);
    }

    /**
     * create response dto
     * @param alertCapable
     * @param responseType
     * @param <R>
     * @return
     */
    public abstract <R extends BaseDTO & ConfigurableAlerting> R doCreateResponseDTO(AlertCapable alertCapable, Class<R> responseType);
}
