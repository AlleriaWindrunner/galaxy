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

import com.galaxy.lemon.common.codec.ObjectEncoder;
import com.galaxy.lemon.common.exception.ExceptionConversionService;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.instantiator.ResponseDTOInstantiator;
import com.galaxy.lemon.framework.response.RelaxedFailureHandlerResponseFlusherResolver;
import com.galaxy.lemon.framework.utils.LemonUtils;
import com.galaxy.lemon.framework.utils.WebUtils;

import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */


public class WebFailureHandlerResponseFlusherResolver
        extends RelaxedFailureHandlerResponseFlusherResolver {

    public WebFailureHandlerResponseFlusherResolver(ExceptionConversionService exceptionConversionService,
                                                    ResponseDTOInstantiator responseDTOInstantiator,
                                                    InternalDataHelper internalDataHelper,
                                                    ObjectEncoder objectEncoder) {
        super(exceptionConversionService, responseDTOInstantiator, internalDataHelper, objectEncoder);
    }

    @Override
    protected <R extends BaseDTO & ConfigurableAlerting> void preProcessResponseDTO(R responseDTO) {
        Optional.ofNullable(Optional.ofNullable(LemonUtils.getRequestId()).orElseGet(() -> getRequestIdFormHttpRequest()))
                .filter(JudgeUtils::isNotBlank).ifPresent(i -> this.internalDataHelper.setRequestId(responseDTO, i));
    }

    private String getRequestIdFormHttpRequest() {
        return Optional.ofNullable(WebUtils.getHttpServletRequest()).map(r -> WebUtils.resolveRequestId(r, false)).orElse(null);
    }
}
