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

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.validation.annotation.Validated;

import com.galaxy.lemon.common.utils.AnnotationUtils;
import com.galaxy.lemon.framework.utils.ValidationUtils;

/**
 * Bean validator
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class BeanValidator {
    
    private Validator validator;
    
    private ValidatorType validatorType;
    
    public BeanValidator(Validator validator, ValidatorType validatorType) {
        this.validator = validator;
        this.validatorType = validatorType;
    }
    
    public boolean validate(StringBuilder messages, Object object, Class<?>... groups) {
        Validated validated = AnnotationUtils.findAnnotation(object.getClass(), Validated.class);
        if( null == validated) {
            return true;
        }
        if(null == groups) {
            groups = validated.value();
        }
        return ValidationUtils.validate(validator, messages, object, groups);
    }
    
    /**
     * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException.
     */
    public void validateWithException(Object object, Class<?>... groups) throws ConstraintViolationException {
        ValidationUtils.validateWithException(validator, object, groups);
    }
    
    public ValidatorType getValidatorType() {
        return this.validatorType;
    }
    
    public enum ValidatorType {
        SPRING_BEAN, SPI
    }
}
