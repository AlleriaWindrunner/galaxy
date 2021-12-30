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

package com.galaxy.lemon.framework.validation;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.SimpleAlert;
import com.galaxy.lemon.common.exception.AbstractExceptionConverter;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.utils.JudgeUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ConstraintViolationExceptionConverter extends AbstractExceptionConverter {

    public ConstraintViolationExceptionConverter() {
        super(ConstraintViolationException.class);
    }

    @Override
    public AlertCapable convert(Throwable throwable) {
        return constraintViolationException((ConstraintViolationException)throwable);
    }

    private static AlertCapable constraintViolationException(ConstraintViolationException cve) {
        String errorMsgCd = null;
        if(JudgeUtils.isNotEmpty(cve.getConstraintViolations())) {
            ConstraintViolation<?> constraintViolation = cve.getConstraintViolations().iterator().next();
            errorMsgCd = constraintViolation.getMessage();
        }
        return Optional.ofNullable(errorMsgCd).map(m -> SimpleAlert.newInstance(m)).orElse(ErrorMsgCode.BEAN_VALIDATION_ERROR);
    }
}
