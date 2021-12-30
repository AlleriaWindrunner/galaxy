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
import com.galaxy.lemon.framework.context.LemonContextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractExpressionEvaluateKeywordsResolver implements KeywordsResolver {

    private KeywordsExpressionEvaluator keywordsExpressionEvaluator;
    private KeywordsExpressionSource keywordsExpressionSource;

    public AbstractExpressionEvaluateKeywordsResolver(KeywordsExpressionEvaluator keywordsExpressionEvaluator,
                                                      KeywordsExpressionSource keywordsExpressionSource) {
        this.keywordsExpressionEvaluator = keywordsExpressionEvaluator;
        this.keywordsExpressionSource = keywordsExpressionSource;
    }

    /**
     *
     * @param obj KeywordsResolverInfo or Object type
     * @param <T>
     * @return
     */
    @Override
    public <T> Keywords resolve(T obj) {
        Keywords keywords = LemonContextUtils.getKeywords();
        if (null == keywords && JudgeUtils.isNotNullAny(this.getKeywordsExpressionEvaluator())) {
            keywords = obj instanceof KeywordsResolverInfo ? doResolve((KeywordsResolverInfo) obj) : evaluateKeywords(obj);
        }
        return keywords == null ? Keywords.EMPTY : keywords;
    }

    /**
     * resolve keywords by KeywordsResolverInfo
     * @param keywordsResolverInfo
     * @return
     */
    protected Keywords doResolve(KeywordsResolverInfo keywordsResolverInfo) {
        String[] expressions = keywordsResolverInfo.getExpressionKey() == null ? null :
                getExpression(this.getExpressionSource().getExpression(keywordsResolverInfo.getExpressionKey()), keywordsResolverInfo.getType());
        expressions = processExpression(expressions, keywordsResolverInfo);
        return JudgeUtils.isEmpty(expressions) ? null : evaluateKeywords(expressions, keywordsResolverInfo.getTarget());
    }

    /**
     * keywords 表达式加工
     * @param expressions
     * @param keywordsResolverInfo
     * @return
     */
    protected String[] processExpression(String[] expressions, KeywordsResolverInfo keywordsResolverInfo){
        return expressions;
    }


    protected boolean isResponseExpression(String expression) {
        return KeywordsHelper.isResponseExpression(expression);
    }

    protected KeywordsExpressionEvaluator getKeywordsExpressionEvaluator() {
        return keywordsExpressionEvaluator;
    }

    protected KeywordsExpressionSource getExpressionSource() {
        return this.keywordsExpressionSource;
    }

    protected String[] getExpression(String[] expressions, Type type) {
        if (JudgeUtils.isEmpty(expressions)) {
            return null;
        }
        if (expressions.length == 1) {
            String[] expressions_ = null;
            switch (type) {
                case REQUEST:
                    if (!isResponseExpression(expressions[0])) {
                        expressions_ = expressions;
                    }
                    break;
                case RESPONSE:
                    if (isResponseExpression(expressions[0])) {
                        expressions_ = expressions;
                    }
                    break;
                default:
                    break;
            }
            return expressions_;
        }
        List<String> expressionList = new ArrayList<>();
        for (String expression : expressions) {
            switch (type) {
                case REQUEST:
                    if (!isResponseExpression(expression)) {
                        expressionList.add(expression);
                    }
                    break;
                case RESPONSE:
                    if (isResponseExpression(expression)) {
                        expressionList.add(expression);
                    }
                    break;
            }
        }
        return JudgeUtils.isEmpty(expressionList) ? null : expressionList.toArray(new String[expressionList.size()]);
    }

    protected  <T> Keywords evaluateKeywords(T obj) {
        return getKeywordsExpressionEvaluator().evaluate(obj);
    }

    protected <T> Keywords evaluateKeywords(String[] expression, T obj) {
        return getKeywordsExpressionEvaluator().evaluate(expression, obj);
    }

    public static class KeywordsResolverInfo {
        private Object target;
        private Type type;
        private KeywordsExpressionSource.ExpressionKey expressionKey;


        public KeywordsResolverInfo(Object target, KeywordsExpressionSource.ExpressionKey expressionKey, Type type) {
            this.target = target;
            this.expressionKey = expressionKey;
            this.type = type;
        }

        public Object getTarget() {
            return target;
        }

        public Type getType() {
            return type;
        }

        public KeywordsExpressionSource.ExpressionKey getExpressionKey() {
            return expressionKey;
        }
    }

    enum Type {
        REQUEST, RESPONSE
    }

}
