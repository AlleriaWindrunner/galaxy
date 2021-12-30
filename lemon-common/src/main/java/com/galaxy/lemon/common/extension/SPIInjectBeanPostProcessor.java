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

package com.galaxy.lemon.common.extension;

import com.galaxy.lemon.common.utils.AnnotationUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring bean 注入SPI服务
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SPIInjectBeanPostProcessor implements Ordered, ApplicationContextAware, SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent>, BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SPIInjectBeanPostProcessor.class);
    private ApplicationContext applicationContext;
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));
    private List<SPIInjectMetadata> spiInjectMetedataList = new CopyOnWriteArrayList<>();
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            if(logger.isInfoEnabled()) {
                logger.info("Starting to inject SPI service. ");
            }
            if(this.spiInjectMetedataList != null && this.spiInjectMetedataList.size() > 0) {
                injectSPI(this.spiInjectMetedataList);
                this.spiInjectMetedataList.clear();
            }
        }
    }

    private void injectSPI(List<SPIInjectMetadata> spiInjectMetedataList) {
        spiInjectMetedataList.stream().parallel().forEach(s -> injectSPI(s));
    }
    
    private void injectSPI(SPIInjectMetadata spiInjectMetedata) {
        Object spiService = null;
        switch (spiInjectMetedata.getInject().type()) {
        case LIST:
            spiService = SPIExtensionLoader.getExtensionServices(spiInjectMetedata.getServiceType());
            break;
        case ADAPTIVE:
            spiService = SPIExtensionLoader.getExtensionAdaptiveService(spiInjectMetedata.getServiceType());
            break;
        case ACTIVATE:
            spiService = SPIExtensionLoader.getExtensionActivateService(spiInjectMetedata.getServiceType());
            break;
        case RANDOM:
            spiService = Optional.ofNullable(SPIExtensionLoader.getExtensionServices(spiInjectMetedata.getServiceType())).map(l -> l.get(0)).orElse(null);
            break;
        default:
            break;
        }
        if(null != spiService) {
            ReflectionUtils.setFieldValue(spiInjectMetedata.getField(), spiInjectMetedata.getBean(), spiService);
            if(logger.isInfoEnabled()) {
                logger.info("Injected SPI service {} to object {} property {}.", spiService, spiInjectMetedata.getBean(), spiInjectMetedata.getField().getName());
            }
        }
    }
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {
            Field[] fields = targetClass.getDeclaredFields();
            if(fields != null && fields.length > 0) {
                List<SPIInjectMetadata> spiInjectMetadataForBean = Stream.of(fields).parallel().filter(f -> AnnotationUtils.isAnnotationPresent(f, Inject.class))
                    .map(f -> resolveSPIInjectMetadata(bean, f)).collect(Collectors.toCollection(ArrayList::new));
                if (null != spiInjectMetadataForBean && spiInjectMetadataForBean.size() > 0) {
                    this.spiInjectMetedataList.addAll(spiInjectMetadataForBean);
                } else {
                    this.nonAnnotatedClasses.add(targetClass);
                }
            }
        }
        return bean;
    }
    

    private SPIInjectMetadata resolveSPIInjectMetadata(Object bean, Field f) {
        Class<?> spiServiceClass = f.getType();
        if(List.class.isAssignableFrom(f.getType())) {
            spiServiceClass = ReflectionUtils.getGenericClass(f);
        }
        return SPIInjectMetadata.instance(bean ,f, AnnotationUtils.findAnnotation(f, Inject.class), spiServiceClass);
    }

    @Override
    public void afterSingletonsInstantiated() {
        
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    public static class SPIInjectMetadata {
        private Object bean;
        private Field field;
        private Inject inject;
        private Class<?> serviceType;
        
        public SPIInjectMetadata(Object bean, Field field, Inject inject, Class<?> serviceType) {
            this.bean = bean;
            this.field = field;
            this.inject = inject;
            this.serviceType = serviceType;
        }
        public Object getBean() {
            return bean;
        }
        public void setBean(Object bean) {
            this.bean = bean;
        }
        public Field getField() {
            return field;
        }
        public void setField(Field field) {
            this.field = field;
        }
        public Inject getInject() {
            return inject;
        }
        public void setInject(Inject inject) {
            this.inject = inject;
        }
        public Class<?> getServiceType() {
            return serviceType;
        }
        public void setServiceType(Class<?> serviceType) {
            this.serviceType = serviceType;
        }

        public static SPIInjectMetadata instance(Object bean, Field field, Inject inject, Class<?> serviceType) {
            return new SPIInjectMetadata(bean, field, inject, serviceType);
        }
    }
}
