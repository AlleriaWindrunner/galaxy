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

package com.galaxy.lemon.gateway.core.validation;

import com.galaxy.lemon.common.HttpMethod;
import com.galaxy.lemon.common.codec.CodecException;
import com.galaxy.lemon.common.codec.ObjectDecoder;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.gateway.core.GatewayHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * 输入参数检查
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultInputDataValidator implements InputDataValidator {
    private static final Logger logger = LoggerFactory.getLogger(DefaultInputDataValidator.class);

    private ObjectDecoder objectDecoder;

    public DefaultInputDataValidator(ObjectDecoder objectDecoder) {
        this.objectDecoder = objectDecoder;
    }

    @Override
    public boolean validateInputData(HttpServletRequest httpServletRequest) {
        return validateInputParams(httpServletRequest);
    }

    /**
     * 输入参数检查
     * @param request
     * @return
     */
    public boolean validateInputParams(HttpServletRequest request) {
        String[] sensitiveHeaders = this.getDefaultSensitiveHeaderNames();
        if(JudgeUtils.isNotEmpty(sensitiveHeaders)) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()) {
                String headName = headerNames.nextElement();
                if(StringUtils.isNotBlank(headName) && JudgeUtils.contain(sensitiveHeaders, headName)) {
                    if(logger.isErrorEnabled()) {
                        logger.error("Illegal http request header \"{}\" .", headName);
                    }
                    return false;
                }
            }
        }

        String[] sensitiveParameters = this.getDefaultSensitiveParameterNames();
        if(JudgeUtils.isNotEmpty(sensitiveParameters)) {
            Map<String, ?> parameterMap = request.getParameterMap();
            if (hasIllegalParameter(sensitiveParameters, parameterMap)) {
                return false;
            }
            if (HttpMethod.GET.name().equals(request.getMethod()) && request.getContentLength() <= 0) {
                return true;
            }
            try {
                parameterMap = GatewayHelper.getRequestBodyForMap(request, this.objectDecoder);
            } catch (IOException ioException) {
                LemonException.throwLemonException(ioException);
            } catch (CodecException codecException) {
                if (logger.isDebugEnabled()) {
                    try {
                        logger.debug("Ignored validating non Json input data. ==> {}", GatewayHelper.getRequestBody(request));
                    } catch (Exception e) {
                    }
                }
                //非json格式不做检查
                return true;
            }
            if (hasIllegalParameter(sensitiveParameters, parameterMap)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasIllegalParameter(String[] sensitiveParameters, Map<String, ?> parameterMap) {
        if(JudgeUtils.isNotEmpty(parameterMap)) {
            for(String paramKey : parameterMap.keySet()) {
                if(JudgeUtils.isNotBlank(paramKey) && JudgeUtils.contain(sensitiveParameters, paramKey)) {
                    if(logger.isErrorEnabled()) {
                        logger.error("Illegal param \"{}\".", paramKey);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
