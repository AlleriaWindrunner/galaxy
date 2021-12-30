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

import com.galaxy.lemon.common.extension.SPIExtensionLoader;
import com.galaxy.lemon.common.utils.JudgeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class BeanValidationInvalidHelper {
    private static final Logger logger = LoggerFactory.getLogger(BeanValidationInvalidHelper.class);
    private static final String FIELD_ERROR_SEPARATOR = ":";
    private static List<BeanValidationErrorResolver> beanValidationErrorResolvers;
    
    static {
        beanValidationErrorResolvers = SPIExtensionLoader.getExtensionServices(BeanValidationErrorResolver.class);
        if(logger.isInfoEnabled()) {
            logger.info("List SPI service 'BeanValidationErrorResolver' : {}", beanValidationErrorResolvers);
        }
    }
    
    /**
     * 校验结果详情
     * @param throwable
     */
    public static void resolveBeanValidationInvalidIfNecessary(Throwable throwable) {
        if (null == throwable) {
            return;
        }
        if(throwable instanceof Exception) {
            resolveBeanValidationInvalidIfNecessary((Exception) throwable);
        }
    }
    
    /**
     * 校验结果详情
     * @param exception
     */
    public static void resolveBeanValidationInvalidIfNecessary(Exception exception) {
        if (null == exception) {
            return;
        }
        boolean resolved = false;
        if(JudgeUtils.isNotEmpty(beanValidationErrorResolvers)) {
            for(BeanValidationErrorResolver resolver : beanValidationErrorResolvers) {
                if(resolver.support(exception)) {
                    resolver.resolve(exception);
                    resolved = true;
                    break;
                }
            }
        }
        if(resolved) {
            return;
        }
        if (exception instanceof BindException) {
            BindException error = (BindException) exception;
            if(logger.isErrorEnabled()) {
                String message = error.getBindingResult().getAllErrors().stream().map(BeanValidationInvalidHelper::resolveErrorInfo).collect(Collectors.joining("~"));
                logger.error("Bind exception : {} ", message);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Bind exception cause :", exception);
            }
            return;
        }
        if(null != exception.getCause() && exception.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception.getCause();
            Set<ConstraintViolation<?>> cvs = cve.getConstraintViolations();
            if(JudgeUtils.isNotNull(cvs)) {
                if(logger.isErrorEnabled()) {
                    String msg = cvs.stream().map(cv -> cv.getRootBeanClass().getSimpleName() + "."+ cv.getPropertyPath()+":"+cv.getMessage()).collect(Collectors.joining("~"));
                    logger.error("Constraint violation exception : {}", msg);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Constraint Violation exception cause :", exception);
                }
            }
            
        }
    }

    private static String resolveErrorInfo(ObjectError objectError) {
        if (objectError instanceof FieldError) {
            FieldError fieldError = (FieldError) objectError;
            return fieldError.getField() + FIELD_ERROR_SEPARATOR + fieldError.getDefaultMessage();
        }
        return objectError.getDefaultMessage();
    }
}
