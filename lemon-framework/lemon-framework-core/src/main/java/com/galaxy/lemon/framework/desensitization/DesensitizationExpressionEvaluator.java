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

import com.galaxy.lemon.framework.spring.spel.SpelFunctionConfigurer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DesensitizationExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> desensitizationCache = new ConcurrentHashMap<ExpressionKey, Expression>(64);
    private final static SpelFunctionConfigurer SPEL_FUNCTION_CONFIGURER = new SpelFunctionConfigurer.DefaultSpelFunctionConfigurer();

    public EvaluationContext createEvaluationContext(Field field, Object target, BeanFactory beanFactory) {
        DesensitizationExpressionRootObject root = new DesensitizationExpressionRootObject(field, target);
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(root);
        evaluationContext.setVariable(field.getName(), target);
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        SPEL_FUNCTION_CONFIGURER.configure(evaluationContext);
        return evaluationContext;

    }

    public Object desensitize(String expression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return getExpression(this.desensitizationCache, methodKey, expression).getValue(evalContext);
    }

    void clear() {
        this.desensitizationCache.clear();
    }
}
