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

package com.galaxy.lemon.framework.id;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;

import java.util.Optional;

import static com.galaxy.lemon.common.exception.LemonException.throwLemonExceptionIfNecessary;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractIdGenerator implements IdGenerator {
    private IdGenProperties idGenProperties;

    public AbstractIdGenerator(IdGenProperties idGenProperties) {
        this.idGenProperties = idGenProperties;
    }

    protected Long getMaxValue(String idName) {
        Long maxValue = Optional.ofNullable(this.getIdGenProperties().getMaxValue()).map(m -> m.get(idName)).orElse(null);
        if (null != maxValue) {
            return maxValue;
        }
        if (permitUnlimited()) {
            return -1L;
        }
        throw LemonException.create(ErrorMsgCode.SYS_ERROR,
                "Id with key\"{1}\" must defined the max value, please configure it with property key \"{2}\"",
                new String[]{idName, "lemon.idgen.max-value.XXX=maxValue"});
    }

    /**
     * 最小值必须大于等于1
     * @param idName
     * @return
     */
    protected Long getMinValue(String idName) {
        Long minValue = Optional.ofNullable(this.getIdGenProperties().getMinValue()).map(m -> m.get(idName)).orElse(null);
        throwLemonExceptionIfNecessary(null != minValue && minValue < 1, ErrorMsgCode.SYS_ERROR,
                "Idgen min value must be large than or equals to 1, illegal Idgen key {1}.", new String[]{idName});
        return null != minValue ? (minValue  <= 0 ? 0 : minValue - 1 ) : 0;
    }

    protected boolean permitUnlimited() {
        return this.idGenProperties.isPermitUnlimited();
    }

    public IdGenProperties getIdGenProperties() {
        return this.idGenProperties;
    }

    public void setIdGenProperties(IdGenProperties idGenProperties) {
        this.idGenProperties = idGenProperties;
    }

}
