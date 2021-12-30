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

package com.galaxy.lemon.framework.stream.client;

import com.galaxy.lemon.common.ConfigurableBeanName;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.OrderUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.data.instantiator.CommandDTOInstantiator;
import com.galaxy.lemon.framework.stream.BindingNameDecorator;
import com.galaxy.lemon.framework.stream.Source;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class StreamClientFactoryBean implements MethodInterceptor, FactoryBean<Object>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(StreamClientFactoryBean.class);
    //default channel name
    private String defaultBinding;
    private Class<?> type;

    private Object proxy;

    @Autowired
    private BinderAwareChannelResolver binderAwareChannelResolver;
    @Autowired
    private CommandDTOInstantiator commandDTOInstantiator;
    @Autowired
    private InternalDataHelper internalDataHelper;
    @Autowired
    private BindingMetadataHolder bindingMetadataHolder;
    @Autowired(required = false)
    private List<RequestInterceptor> requestInterceptors;
    @Autowired(required = false)
    private BindingNameDecorator bindingNameDecorator;

    private Map<Method, StreamMetadata> streamMetadataMap = new HashMap<>();

    private Map<Method, MessageChannel> messageChannelCache = new ConcurrentHashMap<>();

    public StreamClientFactoryBean(Class<?> type) {
        this.type = type;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking async method {}", method);
        }
        if (! messageChannelCache.containsKey(method)) {
            StreamMetadata streamMetadata = this.streamMetadataMap.get(method);
            if (null == streamMetadata) {
                LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "@Source could not specified on method " + method);
            }
            MessageChannel messageChannel = this.binderAwareChannelResolver.resolveDestination(streamMetadata.bindingName);
            if (null == messageChannel) {
                LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "No Binding \"" + streamMetadata.bindingName + "\" found on method " + method);
            }
            this.messageChannelCache.put(method, messageChannel);
        }
        Object[] arguments = invocation.getArguments();
        if (null == arguments || arguments.length != 1) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "There must be one and only one argument on method \"" + method + "\" which annotation by @Source. ");
        }
        if (null == arguments[0]) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Argument must not be null in method " + method);
        }
        BaseDTO baseDTO = resolveCommandDTO(this.streamMetadataMap.get(method), arguments[0]);
        processPayload(baseDTO, method);
        sendMessage(method, baseDTO);
        return baseDTO;
    }

    private <C extends BaseDTO & ConfigurableBeanName> C resolveCommandDTO(StreamMetadata streamMetadata, Object rawDTO) {
        C commandDTO = Optional.of(rawDTO).filter(r -> r instanceof BaseDTO && r instanceof ConfigurableBeanName)
                .map(r -> (C) r).orElseGet(() -> (C) this.commandDTOInstantiator.newInstanceCommandDTO());
        this.internalDataHelper.copyLemonDataToDTO(LemonDataHolder.getLemonData(), commandDTO);
        commandDTO.setBeanName(streamMetadata.getHandlerBeanName());
        commandDTO.setBody(rawDTO);
        return commandDTO;
    }

    private void processPayload(BaseDTO baseDTO, Method method) {
        Optional.ofNullable(this.requestInterceptors).ifPresent(s -> {
            s.stream().forEachOrdered(i -> i.apply(baseDTO, method, this.streamMetadataMap.get(method)));
        });
    }

    private void sendMessage(Method method, BaseDTO commandDTO) {
        this.messageChannelCache.get(method).send(MessageBuilder.withPayload(commandDTO).build());
    }

    @Override
    public synchronized Object getObject() throws Exception {
        if (this.proxy == null) {
            ProxyFactory factory = new ProxyFactory(this.type, this);
            this.proxy = factory.getProxy();
        }
        return this.proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ReflectionUtils.doWithMethods(type, method -> {
            Source input = AnnotationUtils.findAnnotation(method, Source.class);
            if (input != null) {
                if (StringUtils.isBlank(input.value()) && StringUtils.isBlank(defaultBinding)) {
                    throw new IllegalArgumentException("@StreamClient and @Source binding are all blank in method " + method);
                }
                if (StringUtils.isBlank(input.handlerBeanName())) {
                    throw new IllegalArgumentException("@Source handlerBeanName is blank in method " + method);
                }
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("There must be one and only one argument on method \"" + method + "\" which annotation by @Source. ");
                }
                String bindingName = decorateChannelName(StringUtils.getDefaultIfEmpty(input.value(), defaultBinding));
                this.streamMetadataMap.put(method, new StreamMetadata(bindingName, input.handlerBeanName()));
                this.bindingMetadataHolder.addBindingMetadata(input.prefix(), input.group(), bindingName);
            }
        });
        List<RequestInterceptor> requestInterceptors = this.requestInterceptors;
        this.requestInterceptors = OrderUtils.sortByOrder(requestInterceptors);
    }

    private String decorateChannelName(String channelName) {
        return this.bindingNameDecorator == null ? channelName : this.bindingNameDecorator.decorate(channelName);
    }

    public String getDefaultBinding() {
        return defaultBinding;
    }

    public void setDefaultBinding(String defaultBinding) {
        this.defaultBinding = defaultBinding;
    }

    public BinderAwareChannelResolver getBinderAwareChannelResolver() {
        return binderAwareChannelResolver;
    }

    public void setBinderAwareChannelResolver(BinderAwareChannelResolver binderAwareChannelResolver) {
        this.binderAwareChannelResolver = binderAwareChannelResolver;
    }

    public static class StreamMetadata {
        private String bindingName;
        private String handlerBeanName;

        public StreamMetadata(String bindingName, String handlerBeanName) {
            this.bindingName = bindingName;
            this.handlerBeanName = handlerBeanName;
        }

        public String getBindingName() {
            return bindingName;
        }

        public String getHandlerBeanName() {
            return handlerBeanName;
        }
    }

}
