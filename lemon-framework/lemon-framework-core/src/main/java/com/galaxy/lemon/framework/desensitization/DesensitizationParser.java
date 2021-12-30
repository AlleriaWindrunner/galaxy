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

import com.galaxy.lemon.common.utils.AnnotatedElementUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface DesensitizationParser {

    List<DesensitizationMetadata> parse(Class<?> targetClass);

    class AnnotationDesensitizationParser implements DesensitizationParser {

        private static final Map<Class<?>, List<DesensitizationMetadata>> METADATA_CACHED = new ConcurrentHashMap<>();
        private static final List<DesensitizationMetadata> EMPTY_METADATA = Collections.emptyList();

        @Override
        public List<DesensitizationMetadata> parse(Class<?> targetClass) {
            List<DesensitizationMetadata> desensitizationMetadataList = getFromCache(targetClass);
            if (null != desensitizationMetadataList) {
                return desensitizationMetadataList;
            }
            desensitizationMetadataList = doParse(targetClass);
            putToCache(targetClass, desensitizationMetadataList);
            return desensitizationMetadataList;
        }

        public List<DesensitizationMetadata> doParse(Class<?> targetClass) {
            List<DesensitizationMetadata> desensitizationMetadataList = null;
            Field[] fields = ReflectionUtils.getAllFields(targetClass);
            if (null != fields) {
                for (Field field : fields) {
                    Desensitization desensitization = AnnotatedElementUtils.findMergedAnnotation(field, Desensitization.class);
                    if (null != desensitization) {
                        desensitizationMetadataList = Optional.ofNullable(desensitizationMetadataList).orElseGet(ArrayList::new);
                        desensitizationMetadataList.add(new DesensitizationMetadata(targetClass, field, desensitization.type(), desensitization.expr()));
                    }
                }
            }
            return desensitizationMetadataList == null ? EMPTY_METADATA : desensitizationMetadataList;
        }

        private List<DesensitizationMetadata> getFromCache(Class<?> targetClass) {
            return METADATA_CACHED.get(targetClass);
        }

        public void putToCache(Class<?> targetClass, List<DesensitizationMetadata> desensitizationMetadataList) {
            METADATA_CACHED.put(targetClass, desensitizationMetadataList);
        }
    }

    class DesensitizationMetadata {
        private Class<?> targetClass;
        private Field field;
        private Type type;
        private String expression;

        public DesensitizationMetadata(Class<?> targetClass, Field field, Type type, String expression) {
            this.targetClass = targetClass;
            this.field = field;
            this.type = type;
            this.expression = expression;
        }

        public DesensitizationMetadata(Class<?> targetClass, Field field, Type type) {
            this.targetClass = targetClass;
            this.field = field;
            this.type = type;
        }

        public Class<?> getTargetClass() {
            return targetClass;
        }

        public void setTargetClass(Class<?> targetClass) {
            this.targetClass = targetClass;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }
}
