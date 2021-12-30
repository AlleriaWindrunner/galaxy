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

package com.galaxy.lemon.framework.interceptor;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.SimpleAlert;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.log.logback.MDCUtil;
import com.galaxy.lemon.common.utils.ExceptionUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.alerting.AlertingResolver;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.alerting.FallbackAlertingResolver;
import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.response.FailureHandlerResponseResolver;
import com.galaxy.lemon.framework.utils.WebUtils;
import com.galaxy.lemon.framework.validation.BeanValidationInvalidHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * 全局异常处理
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ControllerAdvice
public class GlobalExceptionHandler<R extends BaseDTO & ConfigurableAlerting> {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private AlertingResolver alertingResolver;
    private FailureHandlerResponseResolver failureHandlerResponseResolver;

    public GlobalExceptionHandler(AlertingResolver alertingResolver,
                                  FailureHandlerResponseResolver failureHandlerResponseResolver) {
        this.alertingResolver = new FallbackAlertingResolver(alertingResolver);
        this.failureHandlerResponseResolver = failureHandlerResponseResolver;
    }

    @PostConstruct
    public void afterPropertySet() {
        if(this.alertingResolver == null && logger.isWarnEnabled()) {
            logger.warn("Bean 'AlertingResolver' not autowired into bean 'GlobalExceptionHandler', please check the configuration if necessary.");
        }
    }
    /**
     * 404 异常
     * @param noHandlerFoundException
     * @param request
     * @return
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseBody
    public BaseDTO<?> handNoHandlerFoundException(NoHandlerFoundException noHandlerFoundException, HttpServletRequest request) {
        if(logger.isErrorEnabled()) {
            logger.error("There is unexpected error (type=Not Found, status=404), request uri {}.", request.getRequestURI());
        }
        return processResponse(createResponseDTO(ErrorMsgCode.NO_HANDLER_FOUND_ERROR));
    }
    
    /**
     * 在进入ControllerAspect前bean validation就已经执行了，所以bean validation校验错误会抛出该异常
     * 
     * @param error
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public BaseDTO<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException error, HttpServletRequest request) {
        BaseDTO<?> baseDTO = extractResponseByFieldError(Optional.of(error).map(e -> e.getBindingResult()).map(r -> r.getAllErrors()).orElse(null));
        putMDC(request);
        BeanValidationInvalidHelper.resolveBeanValidationInvalidIfNecessary(error);
        return baseDTO;
    }

    /**
     * 在进入ControllerAspect前bean validation就已经执行了，所以bean validation校验错误会抛出该异常
     *
     * @param error
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public BaseDTO<?> handleBindException(BindException error, HttpServletRequest request) {
        BaseDTO<?> baseDTO = extractResponseByFieldError(Optional.of(error).map(e -> e.getBindingResult()).map(r -> r.getAllErrors()).orElse(null));
        putMDC(request);
        BeanValidationInvalidHelper.resolveBeanValidationInvalidIfNecessary(error);
        return baseDTO;
    }

    /**
     * 框架异常处理，一般业务抛出LemonException不会进到该方法
     * @param lemonException
     * @param request
     * @return
     */
    @ExceptionHandler(LemonException.class)
    @ResponseBody
    public BaseDTO<?> handLemonException(LemonException lemonException, HttpServletRequest request) {
        printError(lemonException, request);
        return processResponse(createResponseDTO((Throwable) lemonException));
    }
    
    /**
     * 其他异常处理
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseDTO<?> handException(Exception exception, HttpServletRequest request) {
        printError(exception, request);
        return processResponse(createResponseDTO(exception));
    }

    private BaseDTO<?> extractResponseByFieldError(List<ObjectError> objectErrors) {
        AlertCapable alertCapable = ErrorMsgCode.BEAN_VALIDATION_ERROR;
        if(null != objectErrors && objectErrors.size() > 0) {
            //只显示第一条
            alertCapable = Optional.ofNullable(objectErrors.get(0)).map(ObjectError::getDefaultMessage).filter(StringUtils::isNotBlank)
                    .map(SimpleAlert::newInstance).orElse(ErrorMsgCode.BEAN_VALIDATION_ERROR);
        }
        return processResponse(createResponseDTO(alertCapable));

    }

    private void printError(Exception e, HttpServletRequest request) {
        if(logger.isErrorEnabled() && !ExceptionUtils.isBusinessException(e)) {
            putMDC(request);
            logger.error("Unexpected error occurred during requesting uri {}", request.getRequestURI());
            logger.error("==>", e);
        }
    }

    /**
     *
     * @param alertCapable
     * @return
     */
    private R createResponseDTO(AlertCapable alertCapable) {
        return this.failureHandlerResponseResolver.handlerFailure(alertCapable, (Class<R>) LemonContextUtils.getResponseDTOType());
    }

    /**
     *
     * @param throwable
     * @return
     */
    private R createResponseDTO(Throwable throwable) {
        return this.failureHandlerResponseResolver.handlerFailure(throwable, (Class<R>) LemonContextUtils.getResponseDTOType());
    }

    /**
     * 加工响应结果
     * @param responseDTO
     */
    private R processResponse(R responseDTO) {
        Optional.ofNullable(this.alertingResolver).ifPresent(r -> r.resolve(responseDTO));
        return responseDTO;
    }

    private void putMDC(HttpServletRequest request) {
        Optional.ofNullable(WebUtils.resolveRequestId(request, false)).filter(StringUtils::isNotBlank).ifPresent(MDCUtil::putMDCKey);
    }
}
