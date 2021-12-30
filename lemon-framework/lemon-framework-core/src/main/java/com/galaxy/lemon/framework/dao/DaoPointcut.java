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

package com.galaxy.lemon.framework.dao;

import com.galaxy.lemon.common.utils.AnnotationUtils;
import com.galaxy.lemon.common.utils.ClassUtils;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DaoPointcut extends StaticMethodMatcherPointcut {
    private final List<Class<? extends Annotation>> annotationTypes;
    private final List<Pattern> patterns;

    public DaoPointcut() {
        this.annotationTypes = new ArrayList<>(5);
        this.patterns = new ArrayList<>(5);
    }

    public void addDaoAnnotation(Class<? extends Annotation> annotation) {
        this.annotationTypes.add(annotation);
    }

    public void addDaoPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    public void addDaoPattern(String pattern) {
        this.addDaoPattern(Pattern.compile(pattern));
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        String qualifiedMethodName =  ClassUtils.getQualifiedMethodName(method, targetClass);
        return this.annotationTypes.stream().anyMatch(a -> AnnotationUtils.findAnnotation(targetClass, a) != null)
                || this.patterns.stream().map(p -> p.matcher(qualifiedMethodName)).anyMatch(Matcher::matches);
    }



}
