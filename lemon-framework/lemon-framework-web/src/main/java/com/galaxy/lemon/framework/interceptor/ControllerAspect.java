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

import com.galaxy.lemon.common.AlertParameterizable;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.log.logback.MDCUtil;
import com.galaxy.lemon.framework.alerting.AlertingResolver;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.alerting.FallbackAlertingResolver;
import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.LemonDataMessageConverter;
import com.galaxy.lemon.framework.response.FailureHandlerResponseResolver;
import com.galaxy.lemon.framework.utils.LemonUtils;
import com.galaxy.lemon.framework.utils.WebUtils;
import com.galaxy.lemon.common.utils.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.galaxy.lemon.common.utils.JudgeUtils.callbackIfNecessary;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Aspect
@Configuration
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ControllerAspect {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAspect.class);

    private AlertingResolver alertingResolver;
    private LemonDataCustomizer lemonDataCustomizer;
    private ControllerAccessLoggerAdapter controllerAccessLoggerAdapter;
    private LemonDataMessageConverter lemonDataMessageConverter;
    private FailureHandlerResponseResolver failureHandlerResponseResolver;

    public ControllerAspect(
            LemonDataMessageConverter lemonDataMessageConverter,
            LemonDataCustomizer lemonDataCustomizer,
            AlertingResolver alertingResolver,
            ControllerAccessLoggerAdapter controllerAccessLoggerAdapter,
            FailureHandlerResponseResolver failureHandlerResponseResolver) {
        this.alertingResolver = new FallbackAlertingResolver(alertingResolver);
        this.lemonDataCustomizer = lemonDataCustomizer;
        this.lemonDataMessageConverter = lemonDataMessageConverter;
        this.controllerAccessLoggerAdapter = controllerAccessLoggerAdapter;
        this.failureHandlerResponseResolver = failureHandlerResponseResolver;
    }

    @Pointcut("execution (* com.galaxy..*Controller.*(..))")
    public void anyCmpayControllerMethod() {
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void anyRestControllerMethod() {
    }

    @Around("anyCmpayControllerMethod()||anyRestControllerMethod()")
    public Object doAroundController(ProceedingJoinPoint pjp) throws Throwable {
        Object responseObject = null;
        try {
            LemonContextUtils.setCurrentTxName(resolveTransactionName(pjp));
            beforeProceed(pjp);
            MDCUtil.putMDCKey(LemonUtils.getRequestId());
            this.controllerAccessLoggerAdapter.request(pjp);
            responseObject = pjp.proceed();
        } catch (Throwable e) {
            responseObject = throwableProceed(pjp, e);
        } finally {
            afterProceed(pjp, responseObject);
            this.controllerAccessLoggerAdapter.response(pjp, responseObject, DateTimeUtils.durationMillis(LemonContextUtils.getTradeStartTime(), DateTimeUtils.getCurrentLocalDateTime()));
        }
        return responseObject;
    }

    /**
     * controller 前处理
     *
     * @param pjp
     */
    private void beforeProceed(ProceedingJoinPoint pjp) {
        BaseDTO<?> genericDTO = extractRequestDTO(pjp);
        WebUtils.validateRequestData(genericDTO);
        BaseLemonData lemonData = extractLemonData();

        if (null == genericDTO && logger.isWarnEnabled()) {
            logger.warn("There is no \"GenericDTO\" type parameter in Controller method {}, such an mode is not recommended.", pjp.getSignature().getName());
        }

        //入口交易
        if (isEntryPointTrade(genericDTO, lemonData)) {
            if (logger.isDebugEnabled()) {
                logger.debug("This is trade entry point, request id is {}", LemonUtils.getRequestId());
            }
            LemonContextUtils.setRequiredProcessAlerting();
            this.lemonDataCustomizer.customizeRequestEntry(genericDTO);
        } else {
            this.lemonDataCustomizer.customizeRequestNonEntry(genericDTO, lemonData);
        }
    }

    /**
     * 异常处理
     *
     * @param pjp
     * @param throwable
     */
    private Object throwableProceed(ProceedingJoinPoint pjp, Throwable throwable) {
        if (logger.isErrorEnabled() && !ExceptionUtils.isBusinessException(throwable)) {
            logger.error("Unexpected error occurred during executing method \"{}\". ", pjp.getSignature().getName());
            logger.error("==>", throwable);
        }
        Optional.ofNullable(throwable).filter(t -> t instanceof AlertParameterizable).map(t -> (AlertParameterizable) t)
                .map(AlertParameterizable::getParameters).filter(JudgeUtils::isNotEmpty).ifPresent(LemonContextUtils::setAlertParameters);
        Optional.of(pjp.getSignature()).filter(s -> s instanceof MethodSignature).map(s -> (MethodSignature) s).map(MethodSignature::getReturnType).ifPresent(LemonContextUtils::setResponseDTOType);
        return this.failureHandlerResponseResolver.handlerFailure(throwable, Optional.of(pjp.getSignature()).filter(s -> s instanceof MethodSignature).map(s -> (MethodSignature) s).map(MethodSignature::getReturnType).orElse(null));

    }

    /**
     * Controller 后处理
     *
     * @param pjp
     * @param responseObject
     */
    private <R extends BaseDTO & ConfigurableAlerting> void afterProceed(ProceedingJoinPoint pjp, Object responseObject) {
        if (JudgeUtils.isNotNull(responseObject)) {
            if (responseObject instanceof BaseDTO && responseObject instanceof ConfigurableAlerting) {
                this.lemonDataCustomizer.customizeResponse((R) responseObject);
            }
            if (responseObject instanceof ConfigurableAlerting) {
                resolveAlerting((ConfigurableAlerting) responseObject);
            }
        }
    }

    private BaseDTO<?> extractRequestDTO(ProceedingJoinPoint pjp) {
        List<BaseDTO<?>> requestDTOList = Optional.ofNullable(pjp.getArgs()).filter(JudgeUtils::isNotEmpty).map(this::extractRequestDTOFromArgs).orElse(null);
        if (null != requestDTOList && requestDTOList.size() > 1) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR.getMsgCd(),
                    "There is only one parameter which assignable from \"BaseDTO\" in Controller method [" + pjp.getSignature().getName() + "].");
        }
        return JudgeUtils.isEmpty(requestDTOList) ? null : requestDTOList.get(0);
    }

    private List<BaseDTO<?>> extractRequestDTOFromArgs(Object[] args) {
        return Stream.of(args).filter(o -> o instanceof BaseDTO).map(m -> (BaseDTO<?>) m).collect(Collectors.toList());
    }

    /**
     * check returning GenericRspDTO
     *
     * @param configurableAlerting
     */
    private void checkAlerting(ConfigurableAlerting configurableAlerting) {
        if (JudgeUtils.isBlank(configurableAlerting.getMsgCd())) {
            configurableAlerting.setMsgCd(ErrorMsgCode.MSG_CD_NOT_EXISTS.getMsgCd());
            if (logger.isErrorEnabled()) {
                logger.error("Could not found \"MsgCd\" in ResponseDTO {}, set default MsgCd {} to it.", configurableAlerting, ErrorMsgCode.MSG_CD_NOT_EXISTS.getMsgCd());
            }
        }
    }

    /**
     * resolve alerting
     *
     * @param configurableAlerting
     */
    private void resolveAlerting(ConfigurableAlerting configurableAlerting) {
        checkAlerting(configurableAlerting);
        callbackIfNecessary(LemonContextUtils.requiredProcessAlerting(), () -> this.alertingResolver.resolve(configurableAlerting));
    }

    /**
     * judge entry trade
     *
     * @param baseDTO
     * @return
     */
    private boolean isEntryPointTrade(BaseDTO<?> baseDTO, BaseLemonData lemonData) {
        if (null != lemonData) {
            return JudgeUtils.isBlank(lemonData.getEntryTx());
        }
        if (null != baseDTO) {
            return JudgeUtils.isBlank(baseDTO.getEntryTx());
        }
        return true;
    }

    private BaseLemonData extractLemonData() {
        return Optional.of(WebUtils.getHttpServletRequest()).filter(WebUtils::isGetRequest).map(r -> r.getHeader(LemonConstants.HTTP_HEADER_DTO)).map(this::encodeDTOMessage).map(this::fromMessage).orElse(null);
    }

    private String resolveTransactionName(ProceedingJoinPoint pjp) {
        return StringUtils.substringBefore(pjp.getSignature().toShortString(), LemonConstants.LEFT_PARENTHESIS);
    }

    private BaseLemonData fromMessage(String message) {
        return this.lemonDataMessageConverter.fromMessage(message);
    }

    private String encodeDTOMessage(String dtoMessage) {
        return Optional.ofNullable(WebUtils.getHttpServletRequest()).map(r -> Optional.ofNullable(r.getHeader(LemonConstants.HTTP_HEADER_DTO_ENCODE))
                .map(e -> StringUtils.equals(LemonConstants.TRUE, e) ? Encodes.DEFAULT_URL_ENCODING : e).orElse(Encodes.DEFAULT_URL_ENCODING)).map(e -> urlDecode(dtoMessage, e)).orElse(dtoMessage);
    }

    private String urlDecode(String dtoMessage, String encoding) {
        try {
            return Encodes.urlDecode(dtoMessage, encoding);
        } catch (UnsupportedEncodingException e) {
            throw LemonException.create(e);
        }
    }
}
