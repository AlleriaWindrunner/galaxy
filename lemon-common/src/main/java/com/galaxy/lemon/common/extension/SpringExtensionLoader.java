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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.galaxy.lemon.common.utils.AnnotationUtils;

/**
 * Spring Bean
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SpringExtensionLoader implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(SpringExtensionLoader.class);
    
    private static ApplicationContext applicationContext;
    
    /**
     * 获取激活的bean
     * 激活方式：类用Activate注解
     * @param clazz
     * @return
     */
    @Deprecated
    public static <T> T getActivateSpringBean(Class<T> clazz){
        if(! AnnotationUtils.isAnnotationPresent(clazz, Activate.class)) {
            throw new IllegalStateException("The class is not annotated by \"@Activate\". class = "+clazz);
        }
        String activateBeanName = AnnotationUtils.findAnnotation(clazz, Activate.class).value();
        return applicationContext.getBean(activateBeanName, clazz);
    }
    
    public static <T> T getSpringBean(String beanName, Class<T> clazz) {
        Object obj = applicationContext.getBean(beanName);
        if(null == obj) {
            return null;
        }
        return clazz.cast(obj);
    }
    
    public static <T> T getSpringBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> Map<String, T> getSpringBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }
    
    public static <T> Map<String, T> getSpringBeansOfType(ApplicationContext applicationContext, Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringExtensionLoader.applicationContext = applicationContext;
        if (logger.isInfoEnabled()) {
            logger.info("Aware bean \"{}\" to bean of type SpringExtensionLoader. ", applicationContext);
        }
    }

}
