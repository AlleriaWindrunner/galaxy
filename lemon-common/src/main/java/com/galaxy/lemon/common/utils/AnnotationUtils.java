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

package com.galaxy.lemon.common.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class AnnotationUtils extends org.springframework.core.annotation.AnnotationUtils{

    /**
     * Get the <em>repeatable</em> {@linkplain Annotation annotations} of
     * {@code annotationType} from the supplied {@link AnnotatedElement}, where
     * such annotations are either <em>present</em>, <em>indirectly present</em>,
     * or <em>meta-present</em> on the element.
     * @param field
     * @param annotationType
     * @param <T>
     * @return
     */
    public static <T extends Annotation> List<T> findAnnotations(Field field, Class<T> annotationType) {
        if (null == field || null == annotationType){
            return null;
        }
        Set<T> set = getRepeatableAnnotations(field, annotationType);
        return Optional.of(set).filter(s -> !s.isEmpty()).map(s -> s.stream().collect(Collectors.toList())).orElse(Collections.EMPTY_LIST);
    }

    /**
     * Get the <em>repeatable</em> {@linkplain Annotation annotations} of
     * {@code annotationType} from the supplied {@link AnnotatedElement}, where
     * such annotations are either <em>present</em>, <em>indirectly present</em>,
     * or <em>meta-present</em> on the element.
     * @param clazz
     * @param annotationType
     * @param <T>
     * @return
     */
    public static <T extends Annotation> List<T> findAnnotations(Class<?> clazz, Class<T> annotationType) {
        if (null == clazz || null == annotationType){
            return null;
        }
        Set<T> set = getRepeatableAnnotations(clazz, annotationType);
        return Optional.of(set).filter(s -> !s.isEmpty()).map(s -> s.stream().collect(Collectors.toList())).orElse(Collections.EMPTY_LIST);
    }

    public static <T extends Annotation> boolean isAnnotationPresent(Field field, Class<T> annotation) {
        return field.isAnnotationPresent(annotation);
    }

    public static <T extends Annotation> boolean isAnnotationPresent(Class<?> clazz, Class<T> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }

    /**
     *
     * @param element
     * @param annotationType
     * @param <A>
     * @deprecated as of lemon 3.0.0, in favor of using ${@AnnotatedElementUtils.getAllMergedAnnotations}
     * @return
     */
    @Deprecated
    public static <A extends Annotation> Collection<A> getAllMergedAnnotations(AnnotatedElement element, Class<A> annotationType) {
        return AnnotatedElementUtils.getAllMergedAnnotations(element, annotationType);
    }
    
}
