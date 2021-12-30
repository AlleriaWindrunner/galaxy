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

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.framework.expression.AbstractHttpValueExpression;
import com.galaxy.lemon.framework.utils.WebUtils;

import javax.servlet.ServletRequest;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ValueSourceHttpValueExpression extends AbstractHttpValueExpression {
    
    public ValueSourceHttpValueExpression(String expression, ValueSource valueSource) {
        super(expression, valueSource);
    }

    @Override
    public String obtainValue() {
        switch (this.getValueSource()) {
            case HEADER:
                return Optional.ofNullable(WebUtils.getHttpServletRequest()).map(r -> r.getHeader(this.getExpression())).orElse(null);
            case REQUEST:
                return obtainValueFormRequest();
            case SESSION:
                return Optional.ofNullable(WebUtils.getHttpServletRequest()).map(r -> r.getSession(false)).map(s -> s.getAttribute(this.getExpression())).map(String::valueOf).orElse(null);
            default:
                throw LemonException.create(ErrorMsgCode.SYS_ERROR, "Unsupported http ValueSource {1}.", new String[]{this.getValueSource().toString()});
        }
    }


    /**
     * 优先取parameter，没有值则取attribute
     * @return
     */
    public String obtainValueFormRequest() {
        ServletRequest request = WebUtils.getHttpServletRequest();
        String value = null;
        if (null != request) {
            value = request.getParameter(this.getExpression());
            if (null == value) {
                value = Optional.ofNullable(request.getAttribute(this.getExpression())).map(String::valueOf).orElse(null);
            }
        }
        return value;
    }
}
