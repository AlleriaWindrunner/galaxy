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

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.AnnotatedElementUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.common.utils.Validate;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataInitializer;
import com.galaxy.lemon.framework.data.interceptor.InitialLemonData;
import com.galaxy.lemon.framework.data.interceptor.InitialLemonData.LemonDataSource;
import com.galaxy.lemon.framework.data.interceptor.InitialLemonDataSource;
import com.galaxy.lemon.framework.data.interceptor.LemonDataInitializerAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class AnnotationInitialLemonDataSource implements InitialLemonDataSource, BeanFactoryAware {
    public static final String BEAN_NAME_LEMON_DATA_INSTANCE = "onlyInstantiationContextLemonDataInitializer";
    public static final String BEAN_NAME_LEMON_DATA_SIMPLE_INITIALIZER = "backgroundLemonDataInitializer";

    private BeanFactory beanFactory;
    private String defaultLemonDataInitializer;
    private InternalDataHelper internalDataHelper;

    /**
     * beanName -> LemonDataInitializer
     */
    private Map<LemonDataInitializerKey, LemonDataInitializer> cachingLemonDataInitializer = new HashMap<>();
    private LemonDataInitializer CACHING_NULL = () -> {};
    private List<Class<? extends Annotation>> supportLemonDataInitializationAnnotations = new ArrayList<>();

    public AnnotationInitialLemonDataSource(String defaultLemonDataInitializer,
                                            InternalDataHelper internalDataHelper) {
        this.defaultLemonDataInitializer = defaultLemonDataInitializer;
        this.internalDataHelper = internalDataHelper;
        this.supportLemonDataInitializationAnnotations.add(InitialLemonData.class);
    }

    public AnnotationInitialLemonDataSource(String defaultLemonDataInitializer,
                                            InternalDataHelper internalDataHelper,
                                            List<Class<? extends Annotation>> supportLemonDataInitializationAnnotations) {
        this(defaultLemonDataInitializer,internalDataHelper);
        Optional.ofNullable(supportLemonDataInitializationAnnotations).ifPresent(s -> s.stream().forEach(this::addInitLemonDataAnnotation));
    }

    @Override
    public LemonDataInitializer getLemonDataInitializer(Method method, Class<?> targetClass) {
        LemonDataInitializerKey lemonDataInitializerKey = new LemonDataInitializerKey(method, targetClass);
        if (null != this.getCachingLemonDataInitializer(lemonDataInitializerKey)) {
            return this.getCachingLemonDataInitializer(lemonDataInitializerKey);
        }

        boolean matched = false;
        InitialLemonData initialLemonData = null;
        for(Class<? extends Annotation> initLemonDataAnnotation : this.supportLemonDataInitializationAnnotations) {
            Collection<? extends Annotation> matchedAnnotations = AnnotatedElementUtils.getAllMergedAnnotations(method, initLemonDataAnnotation);
            if(JudgeUtils.isNotEmpty(matchedAnnotations)) {
                matched = true;
            }
            if (null == initialLemonData && JudgeUtils.isNotEmpty(matchedAnnotations)) {
                initialLemonData = matchedAnnotations.stream().filter(a -> a instanceof InitialLemonData).map(a -> (InitialLemonData) a).findFirst().orElse(null);
            }
            if (matched && null != initialLemonData) {
                break;
            }
        }
        if (!matched) {
            this.addCachingLemonDataInitializer(lemonDataInitializerKey, null);
            return null;
        }

        LemonDataInitializer lemonDataInitializer = null;

        if (null == initialLemonData) {
            lemonDataInitializer = getBeanOfLemonDataInitializer(this.defaultLemonDataInitializer);
        } else {
            switch (initialLemonData.source()) {
                case LEMON_DATA_INSTANCE:
                    lemonDataInitializer = createLemonDataInitializerAdapter(this.getBeanOfLemonDataInitializer(BEAN_NAME_LEMON_DATA_INSTANCE),
                            initialLemonData.source(), initialLemonData.requiredClearContext(), targetClass, method);
                    break;
                case SIMPLE_LEMON_DATA_INITIALIZER:
                    lemonDataInitializer = createLemonDataInitializerAdapter(this.getBeanOfLemonDataInitializer(BEAN_NAME_LEMON_DATA_SIMPLE_INITIALIZER),
                            initialLemonData.source(), initialLemonData.requiredClearContext(), targetClass, method);
                    break;
                case LEMON_DATA_INITIALIZER:
                    String beanNameOfLemonDataInitializer = StringUtils.getDefaultIfEmpty(initialLemonData.lemonDataInitializer(), this.defaultLemonDataInitializer);
                    lemonDataInitializer = createLemonDataInitializerAdapter(this.getBeanOfLemonDataInitializer(beanNameOfLemonDataInitializer),
                            initialLemonData.source(), initialLemonData.requiredClearContext(), targetClass, method);
                    break;
                case COPY_FROM_ARGUMENT:
                    lemonDataInitializer = createLemonDataInitializerAdapter(this.getBeanOfLemonDataInitializer(BEAN_NAME_LEMON_DATA_INSTANCE),
                            initialLemonData.source(), initialLemonData.requiredClearContext(), targetClass, method);
                    break;
                case COPY_SOME_FROM_ARGUMENT:
                    lemonDataInitializer = createLemonDataInitializerAdapter(this.getBeanOfLemonDataInitializer(BEAN_NAME_LEMON_DATA_INSTANCE),
                            initialLemonData.source(), initialLemonData.requiredClearContext(), targetClass, method);
                    break;
                default:
                    LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Unsupported lemon data initialization source {1}", new String[]{initialLemonData.source().toString()});
                    break;
            }
        }
        addCachingLemonDataInitializer(lemonDataInitializerKey, lemonDataInitializer);
        return lemonDataInitializer;
    }

    protected void addInitLemonDataAnnotation(Class<? extends Annotation> initLemonDataAnnotation) {
        if (! containInitLemonDataAnnotation(initLemonDataAnnotation)) {
            this.supportLemonDataInitializationAnnotations.add(initLemonDataAnnotation);
        }
    }

    public boolean containInitLemonDataAnnotation(Class<? extends Annotation> clazz) {
        return this.supportLemonDataInitializationAnnotations.contains(clazz);
    }

    public void addCachingLemonDataInitializer(LemonDataInitializerKey lemonDataInitializerKey, LemonDataInitializer lemonDataInitializer) {
        this.cachingLemonDataInitializer.put(lemonDataInitializerKey, lemonDataInitializer == null ? CACHING_NULL : lemonDataInitializer);
    }

    public void addCachingLemonDataInitializer(Method method, Class<?> targetClass, LemonDataInitializer lemonDataInitializer) {
        this.cachingLemonDataInitializer.put(new LemonDataInitializerKey(method, targetClass), lemonDataInitializer == null ? CACHING_NULL : lemonDataInitializer);
    }

    public LemonDataInitializer getCachingLemonDataInitializer(LemonDataInitializerKey lemonDataInitializerKey) {
        LemonDataInitializer lemonDataInitializer =  this.cachingLemonDataInitializer.get(lemonDataInitializerKey);
        return CACHING_NULL == lemonDataInitializer ? null : lemonDataInitializer;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     *
     * @param lemonDataInitializer
     * @param lemonDataSource
     * @return
     */
    private LemonDataInitializerAdapter createLemonDataInitializerAdapter(LemonDataInitializer lemonDataInitializer,
                                                                          LemonDataSource lemonDataSource,
                                                                          boolean requiredClearContext,
                                                                          Class<?> annotatedMethodClass,
                                                                          Method annotatedMethod) {
        return new LemonDataInitializerAdapter(lemonDataInitializer, lemonDataSource, requiredClearContext, annotatedMethodClass, annotatedMethod, this.internalDataHelper);
    }

    /**
     *
     * @param beanName
     * @return
     */
    private LemonDataInitializer getBeanOfLemonDataInitializer(String beanName) {
        Validate.notBlank(beanName, "Bean name of LemonDataInitializer can not be empty.");
        return Optional.ofNullable(this.beanFactory.getBean(beanName, LemonDataInitializer.class)).orElseThrow(()
                -> LemonException.create(ErrorMsgCode.SYS_ERROR.getMsgCd(), "Cloud not found bean \"" + beanName + "\" from bean factory \"" + this.beanFactory + "\""));
    }

    private static class LemonDataInitializerKey {
        private Class<?> targetClass;
        private Method method;

        public LemonDataInitializerKey(Method method, Class<?> targetClass) {
            this.targetClass = targetClass;
            this.method = method;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            LemonDataInitializerKey that = (LemonDataInitializerKey) object;

            if (targetClass != null ? !targetClass.equals(that.targetClass) : that.targetClass != null) return false;
            return method != null ? method.equals(that.method) : that.method == null;
        }

        @Override
        public int hashCode() {
            int result = targetClass != null ? targetClass.hashCode() : 0;
            result = 31 * result + (method != null ? method.hashCode() : 0);
            return result;
        }
    }
}
