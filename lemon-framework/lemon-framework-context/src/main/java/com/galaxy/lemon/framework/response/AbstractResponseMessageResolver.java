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
import com.galaxy.lemon.common.codec.CodecException;
import com.galaxy.lemon.common.codec.ObjectEncoder;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.framework.alerting.AlertingResolver;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.alerting.FallbackAlertingResolver;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractResponseMessageResolver<R extends BaseDTO & ConfigurableAlerting> implements ResponseMessageResolver<R> {

    private ObjectEncoder objectEncoder;
    private AlertingResolver alertingResolver;
    private ResponseFlushPreprocessor<R> responseFlushPreprocessor;
    private InternalDataHelper internalDataHelper;
    private RequestIdExtractor requestIdExtractor;

    public AbstractResponseMessageResolver(AlertingResolver alertingResolver,
                                           ResponseFlushPreprocessor<R> responseFlushPreprocessor,
                                           InternalDataHelper internalDataHelper,
                                           RequestIdExtractor requestIdExtractor,
                                           ObjectEncoder objectEncoder) {
        this.objectEncoder = objectEncoder;
        this.alertingResolver = new FallbackAlertingResolver(alertingResolver);
        this.internalDataHelper = internalDataHelper;
        this.responseFlushPreprocessor = responseFlushPreprocessor;
        this.requestIdExtractor = requestIdExtractor;
    }

    @Override
    public void resolve(HttpServletRequest request, HttpServletResponse response, AlertCapable alertCapable) throws IOException {
        resolve(request, response, alertCapable.getMsgCd());
    }

    @Override
    public void resolve(HttpServletRequest request, HttpServletResponse response, String msgCode) throws IOException {
        R responseDTO = doCreateResponseDTO(msgCode);
        resolveResponse(request, response, responseDTO);
    }

    @Override
    public void resolveResponse(HttpServletRequest request, HttpServletResponse response, R responseDTO)
            throws IOException {
        processResponseDTO(responseDTO);
        this.responseFlushPreprocessor.processBeforeResponseFlush(request, response, responseDTO);
        this.internalDataHelper.cleanResponseDTO(responseDTO);
        PrintWriter writer = response.getWriter();
        try {
            this.objectEncoder.writeValue(writer, responseDTO);
        } catch (CodecException e) {
            throw LemonException.create(e.getCause());
        }
        writer.flush();
    }

    @Override
    public byte[] generateBytes(AlertCapable alertCapable) throws IOException {
        return generateBytes(alertCapable.getMsgCd());
    }

    @Override
    public byte[] generateBytes(String msgCode) throws IOException {
        try {
            return this.objectEncoder.writeValueAsBytes(createResponseDTO(msgCode));
        } catch (CodecException e) {
            throw LemonException.create(e.getCause());
        }
    }

    @Override
    public String generateString(AlertCapable alertCapable){
        return generateString(alertCapable.getMsgCd());
    }

    @Override
    public String generateString(String msgCode) {
        try {
            return this.objectEncoder.writeValueAsString(createResponseDTO(msgCode));
        } catch (CodecException e) {
            throw LemonException.create(e.getCause());
        }
    }

    protected R createResponseDTO(String msgCode) {
        R responseDTO = doCreateResponseDTO(msgCode);
        processResponseDTO(responseDTO);
        this.internalDataHelper.cleanResponseDTO(responseDTO);
        return responseDTO;
    }

    protected void processResponseDTO(R responseDTO) {
        if (null != LemonDataHolder.getLemonData()) {
            this.internalDataHelper.copyLemonDataToDTO(LemonDataHolder.getLemonData(), responseDTO);
        } else {
            this.internalDataHelper.setRequestId(responseDTO, this.requestIdExtractor.extract());
        }
        this.alertingResolver.resolve(responseDTO);
    }

    protected abstract R doCreateResponseDTO(String msgCode);


}
