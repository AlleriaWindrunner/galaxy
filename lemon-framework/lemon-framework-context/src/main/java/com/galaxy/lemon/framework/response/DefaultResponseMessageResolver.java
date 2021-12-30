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

import com.galaxy.lemon.common.codec.ObjectEncoder;
import com.galaxy.lemon.framework.alerting.AlertingResolver;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.instantiator.ResponseDTOInstantiator;

/**
 * 响应消息解决
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultResponseMessageResolver<R extends BaseDTO & ConfigurableAlerting> extends AbstractResponseMessageResolver<R> {

    private ResponseDTOInstantiator<R> responseDTOInstantiator;

    public DefaultResponseMessageResolver(AlertingResolver alertingResolver,
                                          ResponseFlushPreprocessor<R> responseFlushPreprocessor,
                                          InternalDataHelper internalDataHelper,
                                          RequestIdExtractor requestIdExtractor,
                                          ObjectEncoder objectEncoder,
                                          ResponseDTOInstantiator<R> responseDTOInstantiator) {
        super(alertingResolver, responseFlushPreprocessor, internalDataHelper, requestIdExtractor, objectEncoder);
        this.responseDTOInstantiator = responseDTOInstantiator;
    }

    @Override
    protected R doCreateResponseDTO(String msgCode) {
        R responseDTO =  this.responseDTOInstantiator.newInstanceResponseDTO();
        responseDTO.setMsgCd(msgCode);
        return responseDTO;
    }
}
