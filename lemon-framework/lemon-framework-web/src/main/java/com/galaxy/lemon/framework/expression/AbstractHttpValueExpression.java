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

package com.galaxy.lemon.framework.expression;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;

import static com.galaxy.lemon.common.utils.StringUtils.formatString;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractHttpValueExpression implements Expression {
    private String expression;
    private ValueSource valueSource;

    public AbstractHttpValueExpression(String expression, ValueSource valueSource) {
        checkExpression(expression);
        this.expression = expression.replaceFirst(valueSource.getKey() + LemonConstants.DOT, LemonConstants.EMPTY_STRING);
        this.valueSource = valueSource;
    }

    @Override
    public String getExpressionString() {
        return valueSource.getKey() + LemonConstants.DOT + this.expression;
    }

    @Override
    public Object getValue() throws EvaluationException {
        return this.obtainValue();
    }

    @Override
    public <T> T getValue(Class<T> desiredResultType) throws EvaluationException {
        return desiredResultType.cast(getValue());
    }

    @Override
    public Object getValue(Object rootObject) throws EvaluationException {
        return getValue();
    }

    @Override
    public <T> T getValue(Object rootObject, Class<T> desiredResultType) throws EvaluationException {
        return getValue(desiredResultType);
    }

    @Override
    public Object getValue(EvaluationContext context) throws EvaluationException {
        return getValue();
    }

    @Override
    public Object getValue(EvaluationContext context, Object rootObject) throws EvaluationException {
        throw new EvaluationException( "Cannot call getValue(EvaluationContext context, Object rootObject) on a HttpValueExpression");
    }

    @Override
    public <T> T getValue(EvaluationContext context, Class<T> desiredResultType) throws EvaluationException {
        return getValue(desiredResultType);
    }

    @Override
    public <T> T getValue(EvaluationContext context, Object rootObject, Class<T> desiredResultType) throws EvaluationException {
        throw new EvaluationException( "Cannot call getValue(EvaluationContext context, Object rootObject, Class<T> desiredResultType) on a HttpValueExpression");
    }

    @Override
    public Class<?> getValueType() throws EvaluationException {
        throw new EvaluationException( "Cannot call getValueType() on a HttpValueExpression");
    }

    @Override
    public Class<?> getValueType(Object rootObject) throws EvaluationException {
        throw new EvaluationException( "Cannot call getValueType() on a HttpValueExpression");
    }

    @Override
    public Class<?> getValueType(EvaluationContext context) throws EvaluationException {
        throw new EvaluationException( "Cannot call getValueType() on a HttpValueExpression");
    }

    @Override
    public Class<?> getValueType(EvaluationContext context, Object rootObject) throws EvaluationException {
        throw new EvaluationException( "Cannot call getValueType() on a HttpValueExpression");
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor() throws EvaluationException {
        throw new EvaluationException( "Cannot call getValueTypeDescriptor() on a HttpValueExpression");
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor(Object rootObject) throws EvaluationException {
        throw new EvaluationException( "Cannot call getValueTypeDescriptor() on a HttpValueExpression");
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) throws EvaluationException {
        throw new EvaluationException( "Cannot call getValueTypeDescriptor() on a HttpValueExpression");
    }

    @Override
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, Object rootObject) throws EvaluationException {
        throw new EvaluationException( "Cannot call getValueTypeDescriptor() on a HttpValueExpression");
    }

    @Override
    public boolean isWritable(Object rootObject) throws EvaluationException {
        return false;
    }

    @Override
    public boolean isWritable(EvaluationContext context) throws EvaluationException {
        return false;
    }

    @Override
    public boolean isWritable(EvaluationContext context, Object rootObject) throws EvaluationException {
        return false;
    }

    @Override
    public void setValue(Object rootObject, Object value) throws EvaluationException {
        throw new EvaluationException( "Cannot call setValue() on a HttpValueExpression");
    }

    @Override
    public void setValue(EvaluationContext context, Object value) throws EvaluationException {
        throw new EvaluationException( "Cannot call setValue() on a HttpValueExpression");
    }

    @Override
    public void setValue(EvaluationContext context, Object rootObject, Object value) throws EvaluationException {
        throw new EvaluationException( "Cannot call setValue() on a HttpValueExpression");
    }

    public String getExpression() {
        return expression;
    }

    public ValueSource getValueSource() {
        return valueSource;
    }

    public static boolean isHttpValueExpression(String expression){
        for (ValueSource valueSource : ValueSource.values()) {
            if (StringUtils.startsWith(expression, valueSource.getKey())) {
                return true;
            }
        }
        return false;
    }

    private void checkExpression(String expression) {
        LemonException.throwLemonExceptionIfNecessary(!isHttpValueExpression(expression), ErrorMsgCode.SYS_ERROR.getMsgCd(), formatString("Illegal http expression {}.", new String[]{expression}));
    }


    /**
     *  获取value
     * @return
     */
    public abstract String obtainValue();

    public enum ValueSource {
        HEADER("httpHeader"), REQUEST("httpRequest"), SESSION("httpSession");
        private String key;

        ValueSource(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public static ValueSource toValueSource(String key) {
            ValueSource result = null ;
            for (ValueSource source : values()) {
                if (source.key.equals(key)) {
                    result = source;
                    break;
                }
            }
            return result;
        }

    }
}
