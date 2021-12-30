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

package com.galaxy.lemon.framework.spring.spel;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.galaxy.lemon.framework.spring.spel.SpelFunctionConfigurer.SpelFunctionMetadata.instance;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface SpelFunctionConfigurer {
    /**
     * configure function to spel context
     * @param evaluationContext
     */
    void configure(StandardEvaluationContext evaluationContext);

    /**
     * get spel function names
     * @return
     */
    List<String> getFunctions();

    /**
     * default spel function configurer
     */
    class DefaultSpelFunctionConfigurer implements SpelFunctionConfigurer {

        private SpelFunctionConfigurer spelFunctionConfigurer;

        public DefaultSpelFunctionConfigurer() {
            List<SpelFunctionMetadata> metadataList = loadExtension(loadInternal());
            this.spelFunctionConfigurer = new StaticSpelFunctionConfigurer(metadataList);
        }

        protected List<SpelFunctionMetadata> loadExtension(List<SpelFunctionMetadata> metadataList) {
            return metadataList;
        }

        protected List<SpelFunctionMetadata> loadInternal() {
            List<SpelFunctionMetadata> metadataList = new ArrayList<>();
            metadataList.add(instance(SpelFunction.STRCAT, SpelFunction.STRCAT_METHOD));
            metadataList.add(instance(SpelFunction.STRCAT3, SpelFunction.STRCAT3_METHOD));
            metadataList.add(instance(SpelFunction.STRCAT4, SpelFunction.STRCAT4_METHOD));
            metadataList.add(instance(SpelFunction.SUBSTR, SpelFunction.SUBSTR_METHOD));
            metadataList.add(instance(SpelFunction.SUBSTRE, SpelFunction.SUBSTRE_METHOD));
            metadataList.add(instance(SpelFunction.STRLEN, SpelFunction.STRLEN_METHOD));
            metadataList.add(instance(SpelFunction.LEFTPAD, SpelFunction.LEFTPAD_METHOD));
            metadataList.add(instance(SpelFunction.RIGHTPAD, SpelFunction.RIGHTPAD_METHOD));
            return metadataList;
        }

        @Override
        public void configure(StandardEvaluationContext evaluationContext) {
            this.spelFunctionConfigurer.configure(evaluationContext);
        }

        @Override
        public List<String> getFunctions() {
            return this.spelFunctionConfigurer.getFunctions();
        }
    }

    /**
     * static spel function configurer
     */
    class StaticSpelFunctionConfigurer implements SpelFunctionConfigurer {
        private List<SpelFunctionMetadata> spelFunctionMetadatas;
        private List<String> spelFunctions;

        public StaticSpelFunctionConfigurer(List<SpelFunctionMetadata> spelFunctionMetadatas) {
            this.spelFunctionMetadatas = spelFunctionMetadatas;
            this.spelFunctions = this.spelFunctionMetadatas.stream().map(SpelFunctionMetadata::getName).collect(Collectors.toList());
        }

        @Override
        public void configure(StandardEvaluationContext evaluationContext) {
            Optional.ofNullable(this.spelFunctionMetadatas).ifPresent(s -> doConfigure(evaluationContext, s));
        }

        @Override
        public List<String> getFunctions() {
            return this.spelFunctions;
        }

        protected void doConfigure(StandardEvaluationContext evaluationContext, List<SpelFunctionMetadata> spelFuncationMetadatas) {
            spelFuncationMetadatas.stream().forEach(m -> {
                evaluationContext.registerFunction(m.getName(), m.getMethod());
            });
        }
    }

    /**
     * MetaData for spel function
     */
    class SpelFunctionMetadata {
        private String name;
        private Method method;

        public SpelFunctionMetadata(String name, Method method) {
            this.name = name;
            this.method = method;
        }

        public static SpelFunctionMetadata instance(String name, Method method) {
            return new SpelFunctionMetadata(name, method);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }
}
