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

package com.galaxy.lemon.framework.web.bind.generic;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.annotation.LemonBody;
import com.galaxy.lemon.framework.annotation.QueryBody;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.PropertyWrapper;
import com.galaxy.lemon.framework.data.instantiator.GenericDTOInstantiator;
import com.galaxy.lemon.framework.data.support.RelaxedPropertyWrapper;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.ServletRequest;
import java.util.Map;
import java.util.Optional;

/**
 * DTO 属性为protected需要此Processor
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonServletModelAttributeMethodProcessor implements HandlerMethodArgumentResolver {

    private ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor;
    private PropertyWrapper propertyWrapper;

    public LemonServletModelAttributeMethodProcessor(GenericDTOInstantiator genericDTOInstantiator) {
        this.servletModelAttributeMethodProcessor = new ServletModelAttributeMethodProcessor(false);
        this.propertyWrapper = new RelaxedPropertyWrapper(genericDTOInstantiator.newInstanceGenericDTO().getClass());
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(LemonBody.class) || parameter.hasParameterAnnotation(QueryBody.class)) && BaseDTO.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        LemonWebDataBinderFactory lemonWebDataBinderFactory = new LemonWebDataBinderFactory(binderFactory, this.propertyWrapper);
        Object target = this.servletModelAttributeMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, lemonWebDataBinderFactory);
        return bindingLemonData(webRequest.getNativeRequest(ServletRequest.class), target, lemonWebDataBinderFactory.getConversionService());
    }

    protected Object bindingLemonData(ServletRequest request, Object target, ConversionService conversionService) {
        if (requireDirectLemonDataBind(target)) {
            Map<String, String[]> parameterMap =  request.getParameterMap();
            Optional.ofNullable(parameterMap).filter(JudgeUtils::isNotEmpty).ifPresent(m -> {
                m.entrySet().stream().filter(e -> this.propertyWrapper.hasProperty(e.getKey())).forEach(e -> {
                    setPropertyValueConvertIfNecessary((BaseDTO)target, e.getKey(), Optional.ofNullable(e.getValue()).map(a -> a[0]).orElse(null), conversionService);
                });
            });
        }
        return target;
    }

    public void setPropertyValueConvertIfNecessary(BaseDTO<?> genericDTO, String propertyName, Object propertyValue, ConversionService conversionService) {
        if (null != propertyValue && conversionService.canConvert(TypeDescriptor.forObject(propertyValue), this.propertyWrapper.getTypeDescriptor(propertyName))) {
            propertyValue = conversionService.convert(propertyValue, TypeDescriptor.forObject(propertyValue), this.propertyWrapper.getTypeDescriptor(propertyName));
        }
        this.propertyWrapper.setValue(genericDTO, propertyValue, propertyName);
    }

    protected boolean requireDirectLemonDataBind(Object target) {
        return target instanceof BaseDTO;
    }

    private static class LemonWebDataBinderFactory implements WebDataBinderFactory {
        private WebDataBinderFactory webDateBinderFactory;
        private ConversionService conversionService;
        private PropertyWrapper propertyWrapper;

        public LemonWebDataBinderFactory(WebDataBinderFactory webDateBinderFactory,
                                         PropertyWrapper propertyWrapper) {
            this.webDateBinderFactory = webDateBinderFactory;
            this.propertyWrapper = propertyWrapper;
        }

        @Override
        public WebDataBinder createBinder(NativeWebRequest webRequest, Object target, String objectName) throws Exception {
            WebDataBinder webDataBinder = webDateBinderFactory.createBinder(webRequest, target, objectName);
            webDataBinder.setDisallowedFields(this.propertyWrapper.getProperties());
            this.conversionService = webDataBinder.getConversionService();
            return webDataBinder;
        }

        public ConversionService getConversionService() {
            return this.conversionService;
        }
    }
}
