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

package com.galaxy.lemon.common.log;

import com.galaxy.lemon.common.KVPair;
import com.galaxy.lemon.common.utils.AnnotatedElementUtils;
import com.galaxy.lemon.common.utils.AnnotationUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.galaxy.lemon.common.utils.ReflectionUtils.getAllFields;


/**
 * parser for {@link LogIgnore}
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LogIgnoreConfigurer {

    private static final Map<Class<?>, LogIgnoreOperation> LOG_IGNORE_METADATA_CACHED
            = new ConcurrentHashMap<>();
    private Map<Field, LogIgnoreDefinition> logIgnoreCached = new ConcurrentReferenceHashMap<>();

    public <R> R configure(Class<?> parsedClass, Function<LogIgnoreOperation, R> function) {
        return function.apply(this.resolveLogIgnore(parsedClass));
    }

    private LogIgnoreOperation resolveLogIgnore(Class<?> clazz) {
        if (LOG_IGNORE_METADATA_CACHED.containsKey(clazz)) {
            return LOG_IGNORE_METADATA_CACHED.get(clazz);
        }
        Field[] allFields = ReflectionUtils.getAllFields(clazz);
        if (JudgeUtils.isNotEmpty(allFields)) {
            Map<Field, LogIgnoreDefinition> logIgnoreDefinitions = Stream.of(allFields).filter(LogIgnoreConfigurer::requiredLogIgnore)
                    .map(f -> KVPair.instance(f, createLogIgnoreDefinition(clazz, f))).collect(Collectors.toMap(KVPair::getK, KVPair::getV));
            LOG_IGNORE_METADATA_CACHED.put(clazz, new LogIgnoreOperation(logIgnoreDefinitions, clazz));
        } else {
            LOG_IGNORE_METADATA_CACHED.put(clazz, new LogIgnoreOperation(Collections.emptyMap(), clazz ));
        }
        return LOG_IGNORE_METADATA_CACHED.get(clazz);
    }

    private static boolean requiredLogIgnore(Field field) {
        return AnnotatedElementUtils.isAnnotated(field, LogIgnore.class);
    }

    private LogIgnoreDefinition createLogIgnoreDefinition(Class beanClass, Field field) {
        if (this.logIgnoreCached.containsKey(field)) {
            return this.logIgnoreCached.get(field);
        }
        LogIgnore logIgnore = AnnotationUtils.findAnnotation(field, LogIgnore.class);
        LogIgnoreDefinition logIgnoreDefinition = new LogIgnoreDefinition(logIgnore.condition(), field, field.getDeclaringClass());
        this.logIgnoreCached.put(field, logIgnoreDefinition);
        return logIgnoreDefinition;
    }

    public static class LogIgnoreOperation implements Iterable<LogIgnoreDefinition>{
        private Map<Field, LogIgnoreDefinition> logIgnoreDefinitions;
        private Class<?> beanClass;

        public LogIgnoreOperation(Map<Field, LogIgnoreDefinition> logIgnoreDefinitions, Class<?> beanClass) {
            this.logIgnoreDefinitions = logIgnoreDefinitions;
            this.beanClass = beanClass;
        }

        public boolean isEmpty() {
            return JudgeUtils.isEmpty(logIgnoreDefinitions);
        }


        @Override
        public Iterator<LogIgnoreDefinition> iterator() {
            return this.logIgnoreDefinitions.values().iterator();
        }

        public boolean contain(Class<?> declaringClass, String fieldName) {
            return this.logIgnoreDefinitions.keySet().stream().anyMatch(f ->
                declaringClass.equals(f.getDeclaringClass()) && f.getName().equals(fieldName)
            );
        }

        public LogIgnoreDefinition extractLogIgnoreDefinition(Class<?> declaringClass, String fieldName) {
            return this.logIgnoreDefinitions.values().stream().filter(d -> declaringClass.equals(d.getDeclaredClass())
                    && d.getField().getName().equals(fieldName)).findFirst().orElse(null);
        }

        public Map<Field, LogIgnoreDefinition> getLogIgnoreDefinitions() {
            return logIgnoreDefinitions;
        }

        public Class<?> getBeanClass() {
            return beanClass;
        }
    }

    public static class LogIgnoreDefinition {
        private LogIgnore.Condition condition;
        private Field field;
        private Class declaredClass;

        public LogIgnoreDefinition(LogIgnore.Condition condition, Field field, Class declaredClass) {
            this.condition = condition;
            this.field = field;
            this.declaredClass = declaredClass;
        }

        public LogIgnore.Condition getCondition() {
            return condition;
        }

        public Field getField() {
            return field;
        }

        public Class getDeclaredClass() {
            return declaredClass;
        }
    }
}
