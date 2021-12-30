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

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

/**
 * SPI
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class MethodArgumentBeanValidationErrorResolver implements BeanValidationErrorResolver {

    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof MethodArgumentNotValidException;
    }

    @Override
    public void resolve(Throwable throwable) {
        if(logger.isErrorEnabled()) {
            MethodArgumentNotValidException error = (MethodArgumentNotValidException) throwable;
            String msg = error.getBindingResult().getFieldErrors().stream().map(fieldError -> fieldError.getField()+":"+fieldError.getDefaultMessage())
                    .collect(Collectors.joining("~"));
            logger.error("MethodArgumentNotValidException", msg);
        }
    }

}
