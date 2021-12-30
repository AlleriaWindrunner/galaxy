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

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Field;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SpelDesensitizer implements Desensitizer<Object, Object> {

    private final DesensitizationExpressionEvaluator evaluator = new DesensitizationExpressionEvaluator();
    private String expression;
    private Field field;
    private BeanFactory beanFactory;
    private AnnotatedElementKey annotatedElementKey;

    public SpelDesensitizer(String expression, Class<?> targetClass, Field field, BeanFactory beanFactory) {
        this.expression = expression;
        this.field = field;
        this.beanFactory = beanFactory;
        this.annotatedElementKey = new AnnotatedElementKey(field, targetClass);
    }

    @Override
    public Object desensitize(Object object) {
        EvaluationContext evaluationContext = evaluator.createEvaluationContext(this.field, object, this.beanFactory);
        return evaluator.desensitize(this.expression, this.annotatedElementKey, evaluationContext);
    }
}
