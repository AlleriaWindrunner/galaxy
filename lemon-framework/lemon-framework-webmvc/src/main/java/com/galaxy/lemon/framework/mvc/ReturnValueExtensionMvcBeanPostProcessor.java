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

package com.galaxy.lemon.framework.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持 @ResponseIgnore
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ReturnValueExtensionMvcBeanPostProcessor implements BeanPostProcessor {
    private ObjectMapper objectMapper;

    public ReturnValueExtensionMvcBeanPostProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            processRequestMappingHandlerAdapter((RequestMappingHandlerAdapter)bean);
        }
        return bean;
    }

    private void processRequestMappingHandlerAdapter(RequestMappingHandlerAdapter bean) {
        List<HandlerMethodReturnValueHandler> handlerMethodReturnValueHandlers = bean.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();
        for (HandlerMethodReturnValueHandler handlerMethodReturnValueHandler : handlerMethodReturnValueHandlers) {
            if (handlerMethodReturnValueHandler instanceof RequestResponseBodyMethodProcessor) {
                RequestMappingHandlerAdapterPropertyAccessor requestMappingHandlerAdapterPropertyAccessor = new RequestMappingHandlerAdapterPropertyAccessor(bean);
                handlers.add(createExtendsResponseBodyReturnValueHandler(bean.getMessageConverters(),
                        requestMappingHandlerAdapterPropertyAccessor.getContentNegotiationManager(),
                        requestMappingHandlerAdapterPropertyAccessor.getRequestResponseBodyAdvice()));
            }
            handlers.add(handlerMethodReturnValueHandler);
        }
        bean.setReturnValueHandlers(handlers);
    }

    private HandlerMethodReturnValueHandler createExtendsResponseBodyReturnValueHandler(List<HttpMessageConverter<?>> messageConverters, ContentNegotiationManager contentNegotiationManager, List<Object> requestResponseBodyAdvice) {
        List<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<>();
        for (HttpMessageConverter<?> httpMessageConverter : messageConverters) {
            HttpMessageConverter<?> httpMessageConverterTemp = httpMessageConverter;
            if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
                httpMessageConverterTemp = new MappingJackson2HttpMessageConverter(this.objectMapper);
            }
            httpMessageConverters.add(httpMessageConverterTemp);
        }

        return new ExtendsResponseBodyReturnValueHandler(httpMessageConverters, contentNegotiationManager, requestResponseBodyAdvice);
    }

    private static class RequestMappingHandlerAdapterPropertyAccessor {
        private PropertyAccessor propertyAccessor;

        public RequestMappingHandlerAdapterPropertyAccessor(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
            this.propertyAccessor = new DirectFieldAccessor(requestMappingHandlerAdapter);
        }

        public ContentNegotiationManager getContentNegotiationManager() {
            return (ContentNegotiationManager)this.getPropertyValue("contentNegotiationManager");
        }

        public List<Object> getRequestResponseBodyAdvice() {
            return (List<Object>)this.getPropertyValue("requestResponseBodyAdvice");
        }

        private Object getPropertyValue(String propertyName) {
            return this.propertyAccessor.getPropertyValue(propertyName);
        }
    }
}
