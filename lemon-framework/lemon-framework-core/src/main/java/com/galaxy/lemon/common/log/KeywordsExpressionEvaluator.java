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

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.spring.spel.SpelFunctionConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class KeywordsExpressionEvaluator extends CachedExpressionEvaluator {
    public static final Logger logger = LoggerFactory.getLogger(KeywordsExpressionEvaluator.class);

    private SpelFunctionConfigurer spelFunctionConfigurer = new SpelFunctionConfigurer.DefaultSpelFunctionConfigurer();
    /**
     * default keywords expressions
     */
    private Expression[] expressions;

    private Map<ExpressionKey, Expression> cachedExpression = new ConcurrentHashMap<>(16);

    private AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(KeywordsExpressionEvaluator.class, KeywordsExpressionEvaluator.class);

    private boolean ignoreError = true;

    public KeywordsExpressionEvaluator() {
        this(new SpelExpressionParser());
    }

    public KeywordsExpressionEvaluator(ExpressionParser expressionParser) {
        this(expressionParser, null);
    }

    public KeywordsExpressionEvaluator(String[] expressions) {
        this(new SpelExpressionParser(), expressions);
    }

    public KeywordsExpressionEvaluator(ExpressionParser expressionParser, String[] expressions) {
        super((SpelExpressionParser) expressionParser);
        if (JudgeUtils.isNotEmpty(expressions)) {
            this.expressions = new Expression[expressions.length];
            for (int i = 0; i < expressions.length; i++) {
                this.expressions[i] = expressionParser.parseExpression(expressions[i]);
            }
        }
    }

    /**
     * evaluate by default expression
     * @param target
     * @return
     */
    public Keywords evaluate(Object target) {
        return evaluateWithVariable(null, target);
    }

    /**
     * evalute with expression
     * @param expressions
     * @param target
     * @return
     */
    public Keywords evaluate(String[] expressions, Object target) {
        return evaluate(expressions, target, false);
    }

    /**
     * evaluate with expression
     * @param expressions
     * @param target
     * @param withDefaultExpression required evaluate by defalut expression
     * @return
     */
    public Keywords evaluate(String[] expressions, Object target, boolean withDefaultExpression) {
        if (JudgeUtils.isEmpty(expressions)) {
            return null;
        }
        Keywords keywords = new Keywords();
        for (String expressionStr : expressions) {
            if (StringUtils.isBlank(expressionStr)) {
                continue;
            }
            Expression expression = getExpression(this.cachedExpression, this.annotatedElementKey, expressionStr);
            try {
                keywords.append(doEvaluate(expression, null, target));
            } catch (Exception e) {
                if (ignoreError) {
                    logger.debug("Failed to evaluate expression " + expressionStr, e);
                } else {
                    throw e;
                }
            }
        }
        if (withDefaultExpression) {
            keywords.safeCombine(evaluate(target));
        }
        return keywords;
    }

    /**
     * evaluate with default expression
     * @param variableKey expression variable key
     * @param target variable object
     * @return
     */
    protected Keywords evaluateWithVariable(String variableKey, Object target) {
        if (JudgeUtils.isEmpty(this.expressions)) {
            return null;
        }
        Keywords keywords = new Keywords();
        for (Expression expression : this.expressions) {
            try {
                keywords.append(doEvaluate(expression, variableKey, target));
            } catch (Exception e) {
                if (ignoreError) {
                    logger.debug("Failed to evaluate expression " + expression.getExpressionString(), e);
                } else {
                    throw e;
                }
            }
        }
        return keywords;
    }

    public EvaluationContext createEvaluationContext(String key, Object target) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable(key, target);
        this.spelFunctionConfigurer.configure(evaluationContext);
        return evaluationContext;
    }

    public void setIgnoreError(boolean ignoreError) {
        this.ignoreError = ignoreError;
    }

    protected String doEvaluate(Expression expression, String variableKey, Object target) {
        if (null == target || target instanceof String) {
            return null;
        }
        String key = variableKey == null ? extractVariableKey(expression) : variableKey;
        return expression.getValue(createEvaluationContext(key, target), String.class);
    }

    private String extractVariableKey(Expression expression) {
        return KeywordsHelper.extractVariableKey(expression);
    }

}
