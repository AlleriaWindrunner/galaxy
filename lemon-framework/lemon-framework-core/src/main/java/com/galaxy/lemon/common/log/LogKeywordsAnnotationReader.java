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

import com.galaxy.lemon.common.utils.AnnotationUtils;
import org.springframework.core.MethodIntrospector;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LogKeywordsAnnotationReader {

    private Map<Class<?>, Map<String, LogKeywordsMetadata>> nameLogKeywordsMetadataMap;
    private Map<Class<?>, Map<Method, LogKeywordsMetadata>> methodLogKeywordsMetadataMap;

    public LogKeywordsAnnotationReader() {
        this.nameLogKeywordsMetadataMap = new ConcurrentHashMap<>(16);
        this.methodLogKeywordsMetadataMap = new ConcurrentHashMap<>(16);
    }

    public void read(Class<?> clazz) {
        if (methodLogKeywordsMetadataMap.containsKey(clazz)){
            return;
        }
        Map<Method, LogKeywordsMetadata> methodLogKeywordsMetadataMap = MethodIntrospector.selectMethods(clazz, (MethodIntrospector.MetadataLookup) method -> {
            LogKeywords logKeywords = AnnotationUtils.findAnnotation(method, LogKeywords.class);
            return null != logKeywords ? new LogKeywordsMetadata(logKeywords.value()) : null;
        });
        methodLogKeywordsMetadataMap.entrySet().stream().filter(e -> null != e.getValue()).forEach( e -> {
            Map<String, LogKeywordsMetadata> nameLogKeywords = this.nameLogKeywordsMetadataMap.get(clazz);
            if (null == nameLogKeywords) {
                nameLogKeywords = new HashMap<>(16);
                this.nameLogKeywordsMetadataMap.put(clazz, nameLogKeywords);
            }
            Map<Method, LogKeywordsMetadata> methodLogKeywords = this.methodLogKeywordsMetadataMap.get(clazz);
            if (null == methodLogKeywords) {
                methodLogKeywords = new HashMap<>(16);
                this.methodLogKeywordsMetadataMap.put(clazz, methodLogKeywords);
            }
            Method method = e.getKey();
            LogKeywordsMetadata logKeywordsMetadata = e.getValue();
            methodLogKeywords.put(method, logKeywordsMetadata);
            nameLogKeywords.put(parseKeyName(method), logKeywordsMetadata);
        });
        methodLogKeywordsMetadataMap.clear();
    }

    public Map<Class<?>, Map<String, LogKeywordsMetadata>> getNameLogKeywordsMetadataMap() {
        return nameLogKeywordsMetadataMap;
    }

    public Map<Class<?>, Map<Method, LogKeywordsMetadata>> getMethodLogKeywordsMetadataMap() {
        return methodLogKeywordsMetadataMap;
    }

    private String parseKeyName(Method method) {
        return method.getName();
    }

    public static class LogKeywordsMetadata {
        private String[] value;

        public LogKeywordsMetadata(String[] value) {
            this.value = value;
        }

        public String[] getValue() {
            return value;
        }
    }

}
