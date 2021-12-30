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

package com.galaxy.lemon.framework.data.interceptor;

import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataInitializer;
import com.galaxy.lemon.framework.data.instantiator.AggregatedDataInstantiator;
import com.galaxy.lemon.framework.data.interceptor.BeanFactoryInitLemonDataAdvisor;
import com.galaxy.lemon.framework.data.interceptor.EnableInitialLemonData;
import com.galaxy.lemon.framework.data.interceptor.InitialLemonDataInterceptor;
import com.galaxy.lemon.framework.data.interceptor.InitialLemonDataSource;
import com.galaxy.lemon.framework.data.support.DefaultContextLemonDataInitializer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * aop for initial lemon data
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class ProxyInitialLemonDataConfiguration implements ImportAware {
    private static final String SUPPORT_ANNOTATIONS = "supportAnnotations";
    public static final String SUPPORT_ANNOTATION_CLASS_NAMES = "supportAnnotationClassNames";
    public static final String BEAN_NAME_BACKGROUND_LEMON_DATA_INITIALIZER = "backgroundLemonDataInitializer";

    private AnnotationAttributes enableInitLemonData;
    private InternalDataHelper internalDataHelper;

    public ProxyInitialLemonDataConfiguration(InternalDataHelper internalDataHelper) {
        this.internalDataHelper = internalDataHelper;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableInitLemonData = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableInitialLemonData.class.getName(), false));
        if (this.enableInitLemonData == null) {
            throw new IllegalArgumentException(
                    "@EnableInitLemonData is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean
    public InitialLemonDataSource initialLemonDataSource() {
        List<Class<? extends Annotation>> supportInitialLemonDataAnnotations = new ArrayList<>();
        Class<? extends Annotation>[] classes = (Class<? extends Annotation>[])this.enableInitLemonData.getClassArray(SUPPORT_ANNOTATIONS);
        Stream.of(classes).forEach(supportInitialLemonDataAnnotations::add);

        String[] supportAnnotationsClassNames = this.enableInitLemonData.getStringArray(SUPPORT_ANNOTATION_CLASS_NAMES);
        Stream.of(supportAnnotationsClassNames).filter(ReflectionUtils::isPresent).map(ReflectionUtils::forNameThrowRuntimeExceptionIfNecessary)
                .filter(Class::isAnnotation).map(c -> (Class<? extends Annotation>) c).forEach(supportInitialLemonDataAnnotations::add);

        return new AnnotationInitialLemonDataSource(BEAN_NAME_BACKGROUND_LEMON_DATA_INITIALIZER,
                this.internalDataHelper, supportInitialLemonDataAnnotations);
    }

    @SuppressWarnings("unchecked")
    @Bean(name = "initLemonDataAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryInitLemonDataAdvisor initLemonDataAdvisor() {
        BeanFactoryInitLemonDataAdvisor advisor = new BeanFactoryInitLemonDataAdvisor(initialLemonDataSource());
        advisor.setAdvice(initLemonDataInterceptor());
        advisor.setOrder(this.enableInitLemonData.<Integer>getNumber("order"));
        return advisor;
    }
    
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public InitialLemonDataInterceptor initLemonDataInterceptor() {
        InitialLemonDataInterceptor interceptor = new InitialLemonDataInterceptor(initialLemonDataSource());
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(name = BEAN_NAME_BACKGROUND_LEMON_DATA_INITIALIZER)
    public LemonDataInitializer backgroundLemonDataInitializer(AggregatedDataInstantiator aggregatedDataInstantiator) {
        return new DefaultContextLemonDataInitializer(aggregatedDataInstantiator);
    }

}
