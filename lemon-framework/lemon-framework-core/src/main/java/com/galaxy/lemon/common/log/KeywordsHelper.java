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

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.StringUtils;
import org.springframework.expression.Expression;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class KeywordsHelper {

    private static final String SPEL_VARIABLE_KEY_PREFIX = "#";
    private static final String[] SPEL_VARIABLE_KEYS = {"#request", "#response"};
    private static final String RESPONSE_EXPRESSION = "response";

    public static String extractVariableKey(Expression expression) {
        return extractVariableKey(expression.getExpressionString());
    }

    public static String extractVariableKey(String expStr) {
        if (expStr.length() > 0) {
            String variableKey = null;
            for (String vk : SPEL_VARIABLE_KEYS) {
                if (expStr.contains(vk)) {
                    if (null != variableKey) {
                        LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Illegal spel expression {1}", new String[]{expStr});
                    }
                    variableKey = vk.replace(SPEL_VARIABLE_KEY_PREFIX, LemonConstants.EMPTY_STRING);
                }
            }
            return variableKey;
        }
        throw LemonException.create(ErrorMsgCode.SYS_ERROR, "Spel expression can not be empty.");
    }

    public static boolean isResponseExpression(String expression) {
        return StringUtils.equals(RESPONSE_EXPRESSION, extractVariableKey(expression));
    }
}
