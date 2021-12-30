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
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.PropertyWrapper;
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
 * 解决GET传GenericDTO的问题；
 * 因FeignGET请求用Json传对象，这样服务端必须要用@RequestBody注解，但服务端有可能需要其他客户端用param GET请求，与用@RequestBody注解冲突
 * 现GET请求传GenericDTO必须用@LemonBody注解，解决上述冲突
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Deprecated
public class LemonBodyServletModelAttributeMethodProcessor implements HandlerMethodArgumentResolver{

    private ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor;
    private PropertyWrapper propertyWrapper;
    
    public LemonBodyServletModelAttributeMethodProcessor(PropertyWrapper propertyWrapper) {
        this.servletModelAttributeMethodProcessor = new ServletModelAttributeMethodProcessor(false);
        this.propertyWrapper = propertyWrapper;
    }
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LemonBody.class);
    }
    
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        LemonWebDataBinderFactory lemonWebDataBinderFactory = new LemonWebDataBinderFactory(binderFactory, this.propertyWrapper);
        Object object = this.servletModelAttributeMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, lemonWebDataBinderFactory);
        if(object instanceof BaseDTO){
            BaseDTO<?> genericDTO = (BaseDTO<?>) object;
            ConversionService conversionService = lemonWebDataBinderFactory.getConversionService();
            ServletRequest servletRequest = webRequest.getNativeRequest(ServletRequest.class);
            Map<String, String[]> parameterMap =  servletRequest.getParameterMap();
            if(JudgeUtils.isNotEmpty(parameterMap)) {
                parameterMap.entrySet().stream().filter(e -> propertyWrapper.hasProperty(e.getKey()))
                        .forEach(e -> this.setPropertyValueConvertIfNecessary(genericDTO, e.getKey(), Optional.ofNullable(e.getValue()).map(a -> a[0]).orElse(null), conversionService) ) ;
            }
        }
        return object;
    }

    public void setPropertyValueConvertIfNecessary(BaseDTO<?> genericDTO, String propertyName, Object propertyValue, ConversionService conversionService) {
        Object targetValue = propertyValue;
        if (null != targetValue && conversionService.canConvert(TypeDescriptor.forObject(propertyValue), this.propertyWrapper.getTypeDescriptor(propertyName))) {
            targetValue = conversionService.convert(propertyValue, TypeDescriptor.forObject(propertyValue), this.propertyWrapper.getTypeDescriptor(propertyName));
        }
        this.propertyWrapper.setValue(genericDTO, targetValue, propertyName);
    }
    

    /**
     * 除了Lemon fields外其他属性的数据绑定
     * @author yuzhou
     * @date 2017年9月15日
     * @time 下午1:54:18
     *
     */
    public static class LemonWebDataBinderFactory implements WebDataBinderFactory {
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
            conversionService = webDataBinder.getConversionService();
            return webDataBinder;
        }
        
        public ConversionService getConversionService() {
            return conversionService;
        }
        
    }

}
