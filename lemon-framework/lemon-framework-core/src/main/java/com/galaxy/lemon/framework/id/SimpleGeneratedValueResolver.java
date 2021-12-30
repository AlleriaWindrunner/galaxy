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

package com.galaxy.lemon.framework.id;

import com.galaxy.lemon.common.Invoker;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SimpleGeneratedValueResolver implements GeneratedValueResolver {

    private Map<Class<?>, List<Invoker>> invokers;

    public SimpleGeneratedValueResolver(Map<Class<?>, List<Invoker>> invokers) {
        this.invokers = invokers;
        if (JudgeUtils.isNull(this.invokers)) {
            this.invokers = new HashMap<>();
        }
    }

    @Override
    public void resolve(Object annotatedObject) {
        Class<?> clazz = annotatedObject.getClass();
        if(getInvokers().containsKey(clazz)) {
            getInvokers().get(clazz).stream().forEach(i -> i.invoke(annotatedObject, null));
        }
    }

    private Map<Class<?>, List<Invoker>> getInvokers() {
        return this.invokers;
    }

    static class GeneratedMetadata {
        private String key;
        private String prefix;
        private Method method;
        private GeneratorStrategy generatorStrategy;

        GeneratedMetadata(String key, String prefix, Method method, GeneratorStrategy generatorStrategy) {
            this.key = key;
            this.prefix = prefix;
            this.method = method;
            this.generatorStrategy = generatorStrategy;
        }

        public String getPrefix() {
            return this.prefix;
        }
        public Method getMethod() {
            return this.method;
        }
        GeneratorStrategy getGeneratorStrategy() {
            return this.generatorStrategy;
        }
        public String getKey() {
            return this.key;
        }
    }

    public static class GeneratedValueInvoker implements Invoker {

        private GeneratedMetadata generatedMetadata;

        public GeneratedValueInvoker(String key, String prefix, Method method, GeneratorStrategy generatorStrategy) {
            this.generatedMetadata = new GeneratedMetadata(key, prefix, method, generatorStrategy);
        }

        @Override
        public Object invoke(Object target, Object[] arguments) {
            try {
                generatedMetadata.getMethod().invoke(target, generatedMetadata.getGeneratorStrategy().generatedValue(generatedMetadata.getKey(), generatedMetadata.getPrefix()));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                LemonException.throwLemonException(e);
            }
            return null;
        }
    }
}
