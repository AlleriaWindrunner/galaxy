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

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultKeywordsResolver extends AbstractExpressionEvaluateKeywordsResolver {

    private String[] defaultRequestExpression;
    private String[] defaultResponseExpression;

    public DefaultKeywordsResolver(KeywordsExpressionEvaluator keywordsExpressionEvaluator,
                                   KeywordsExpressionSource keywordsExpressionSource,
                                   String[] defaultExpressions) {
        super(keywordsExpressionEvaluator, keywordsExpressionSource);
        if (JudgeUtils.isNotEmpty(defaultExpressions)) {
            List<String> request = new ArrayList<>();
            List<String> response = new ArrayList<>();
            for (String expression : defaultExpressions) {
                if (isResponseExpression(expression)) {
                    response.add(expression);
                } else {
                    request.add(expression);
                }
            }
            this.defaultRequestExpression = request.toArray(new String[request.size()]);
            this.defaultResponseExpression = response.toArray(new String[response.size()]);
        }
    }

    @Override
    protected String[] processExpression(String[] expressions, KeywordsResolverInfo keywordsResolverInfo){
        return JudgeUtils.isEmpty(expressions) ? getDefaultExpression(keywordsResolverInfo.getType()) : expressions;
    }

    private String[] getDefaultExpression(Type type) {
        switch (type) {
            case REQUEST:
                return this.defaultRequestExpression;
            case RESPONSE:
                return this.defaultResponseExpression;
            default:
                return null;
        }
    }

}
