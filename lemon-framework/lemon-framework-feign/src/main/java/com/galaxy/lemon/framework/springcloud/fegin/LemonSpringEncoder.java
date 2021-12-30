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

package com.galaxy.lemon.framework.springcloud.fegin;

import com.galaxy.lemon.common.utils.AnnotationUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.data.NoBody;
import com.galaxy.lemon.framework.remoting.RemoteInvocationDataProcessor;
import com.galaxy.lemon.framework.springcloud.fegin.QueryBodyParameterResolver;
import com.galaxy.lemon.framework.utils.ValidationUtils;
import com.galaxy.lemon.framework.validation.ClientValidated;
import feign.Param;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract.ConvertingExpander;
import org.springframework.core.convert.ConversionService;

import javax.validation.Validator;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lemon扩展fegin encoder
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonSpringEncoder extends SpringEncoder {
    private static final Logger logger = LoggerFactory.getLogger(LemonSpringEncoder.class);
    
    private Validator validator;
    private boolean requireClientValidate;
    private Param.Expander expander;
    private QueryBodyParameterResolver queryBodyParameterResolver;
    private Map<Class<?>, Boolean> requireValidateCaches = new ConcurrentHashMap<>();
    private RemoteInvocationDataProcessor remoteInvocationDataProcessor;
    
    public LemonSpringEncoder(
            ObjectFactory<HttpMessageConverters> messageConverters,
            QueryBodyParameterResolver queryBodyParameterResolver,
            ConversionService conversionService,
            RemoteInvocationDataProcessor remoteInvocationDataProcessor,
            Validator validator,
            boolean requireClientValidate) {
        super(messageConverters);
        this.queryBodyParameterResolver = queryBodyParameterResolver;
        this.validator = validator;
        this.requireClientValidate = requireClientValidate;
        this.expander = new ConvertingExpander(conversionService);
        this.remoteInvocationDataProcessor = remoteInvocationDataProcessor;
    }
    
    @Override
    public void encode(Object requestBody, Type bodyType, RequestTemplate request)
            throws EncodeException {
        boolean continueEncode = true;
        if (null != requestBody && !(requestBody instanceof NoBody)) {
            continueEncode = encodeRequestBody(requestBody, bodyType, request);
        }
        if(continueEncode) {
            super.encode(requestBody, bodyType, request);
        }
    }

    private boolean encodeRequestBody(Object requestBody, Type bodyType, RequestTemplate request) {
        if (requestBody instanceof BaseDTO) {
            BaseDTO baseDTO = (BaseDTO)requestBody;
            this.remoteInvocationDataProcessor.processBeforeInvocation(LemonDataHolder.getLemonData(), baseDTO);
        }
        if (requireClientValidate) {
            clientValidate(requestBody);
        }
        return ! queryBodyParameterResolver.resolveQueryBodyParameter(request, requestBody, expander);
    }

    private void clientValidate(Object requestBody) {
        if(requireValidate(requestBody.getClass())) {
            ClientValidated validated = AnnotationUtils.findAnnotation(requestBody.getClass(), ClientValidated.class);
            JudgeUtils.callbackIfNecessary(validated != null, () -> {
                ValidationUtils.validateWithException(validator, requestBody, validated.value());
            });
        }
    }

    private boolean requireValidate(Class<?> clazz) {
        Boolean flag = requireValidateCaches.get(clazz);
        if (null == flag) {
            ClientValidated validated = AnnotationUtils.findAnnotation(clazz, ClientValidated.class);
            if (null != validated) {
                flag = true;
            } else {
                flag = false;
            }
            requireValidateCaches.putIfAbsent(clazz, flag);
        }
        return flag;
    }

}
