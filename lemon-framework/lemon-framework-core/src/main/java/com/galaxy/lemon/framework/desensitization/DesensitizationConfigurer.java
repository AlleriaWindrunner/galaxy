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

package com.galaxy.lemon.framework.desensitization;

import com.galaxy.lemon.framework.desensitization.DesensitizationParser.DesensitizationMetadata;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DesensitizationConfigurer {

    private BeanFactory beanFactory;
    private DesensitizationParser desensitizationParser;
    private Map<Type, Desensitizer<?, ?>> desensitizationMap;

    public DesensitizationConfigurer() {
        this(null);
    }

    public DesensitizationConfigurer(BeanFactory beanFactory) {
        this(new DesensitizationParser.AnnotationDesensitizationParser(), beanFactory);
    }

    public DesensitizationConfigurer(DesensitizationParser desensitizationParser, BeanFactory beanFactory) {
        this.desensitizationParser = desensitizationParser;
        this.beanFactory = beanFactory;
        loadInternalDesensitizer();
    }

    public void addDesensitizer(Type type, Desensitizer<?, ?> desensitization) {
        this.desensitizationMap = Optional.ofNullable(this.desensitizationMap).orElseGet(ConcurrentHashMap::new);
        this.desensitizationMap.put(type, desensitization);
    }

    public void configure(Class<?> targetClass, Consumer<DesensitizationContext> callback) {
        List<DesensitizationMetadata> desensitizationMetadataList = this.getDesensitizationParser().parse(targetClass);
        callback.accept(new DesensitizationContext(desensitizationMetadataList, this.desensitizationMap, this.beanFactory));
    }

    public DesensitizationParser getDesensitizationParser() {
        return desensitizationParser;
    }

    private void loadInternalDesensitizer() {
        addDesensitizer(Type.CHINESE_NAME, new Desensitizer.ChineseNameDesensitizer());
        addDesensitizer(Type.ADDRESS, new Desensitizer.AddressDesensitizer());
        addDesensitizer(Type.ID_CARD, new Desensitizer.IdCardDesensitizer());
        addDesensitizer(Type.BANK_CARD, new Desensitizer.BankCardDesensitizer());
        addDesensitizer(Type.CNAPS_CODE, new Desensitizer.CNAPSCodeDesensitizer());
        addDesensitizer(Type.EMAIL, new Desensitizer.EmailDesensitizer());
        addDesensitizer(Type.MOBILE_NO, new Desensitizer.MobileNoDesensitizer());
        addDesensitizer(Type.PHONE_NO, new Desensitizer.PhoneNoDesensitizer());
        addDesensitizer(Type.LEFT, new Desensitizer.LeftDesensitizer());
        addDesensitizer(Type.MIDDLE, new Desensitizer.MiddleDesensitizer());
        addDesensitizer(Type.RIGHT, new Desensitizer.RightDesensitizer());
        addDesensitizer(Type.ALL, new Desensitizer.AllDesensitizer());
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public static class DesensitizationContext {
        private List<DesensitizationMetadata> desensitizationMetadatas;
        private Map<Type, Desensitizer<?, ?>> desensitizationMap;
        private BeanFactory beanFactory;

        public DesensitizationContext(List<DesensitizationMetadata> desensitizationMetadatas,
                                      Map<Type, Desensitizer<?, ?>> desensitizationMap,
                                      BeanFactory beanFactory) {
            this.desensitizationMetadatas = desensitizationMetadatas;
            this.desensitizationMap = desensitizationMap;
            this.beanFactory = beanFactory;
        }

        public List<DesensitizationMetadata> getDesensitizationMetadatas() {
            return this.desensitizationMetadatas;
        }

        public Desensitizer<?, ?> getDesensitizer(DesensitizationMetadata metadata) {
            if (Type.SPEL.equals(metadata.getType())) {
                return new SpelDesensitizer(metadata.getExpression(), metadata.getTargetClass(), metadata.getField(), this.beanFactory);
            }
            return this.desensitizationMap.get(metadata.getType());
        }
    }
}
