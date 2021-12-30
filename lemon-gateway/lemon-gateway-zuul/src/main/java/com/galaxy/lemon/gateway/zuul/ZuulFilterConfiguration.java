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

package com.galaxy.lemon.gateway.zuul;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.galaxy.lemon.framework.signature.SignatureVerifier;
import com.galaxy.lemon.gateway.core.log.GatewayAccessLoggerAdapter;
import com.galaxy.lemon.gateway.core.validation.InputDataValidator;
import com.galaxy.lemon.gateway.zuul.filter.*;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zuul fillter configuration
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class ZuulFilterConfiguration extends ZuulFilterCreatingSupport {

    private GatewayAccessLoggerAdapter gatewayAccessLoggerAdapter;

    public ZuulFilterConfiguration(GatewayAccessLoggerAdapter zuulGatewayAccessLoggerAdapter) {
        this.gatewayAccessLoggerAdapter = zuulGatewayAccessLoggerAdapter;
    }
    
    @Bean
    public ZuulFilter requestLogZuulFilter() {
        return createZuulFilter(true, FilterType.PRE, FilterConstants.ORDER_PRE_REQUEST_LOG,
            () -> {
                this.gatewayAccessLoggerAdapter.request(RequestContext.getCurrentContext().getRequest(), RequestContext.getCurrentContext().getResponse());
                return null;
            });
    }

    @Bean
    public ZuulFilter validationInputDataZuulFilter(ResponseMessageResolver responseMessageResolver, InputDataValidator inputDataValidator) {
        return createZuulFilter(true, FilterType.PRE, FilterConstants.ORDER_PRE_VALIDATION_INPUT_DATA,
                () ->{
                    RequestContext ctx = RequestContext.getCurrentContext();
                    if(JudgeUtils.not(inputDataValidator.validateInputData(ctx.getRequest()))) {
                        ctx.setSendZuulResponse(false);
                        ctx.setResponseBody(responseMessageResolver.generateString(ErrorMsgCode.ILLEGAL_PARAMETER));
                    }
                    return null;
                });
    }
    
    @Bean
    public ZuulFilter signatureZuulFilter(SignatureVerifier zuulSignatureVerifier, ResponseMessageResolver responseMessageResolver) {
        return new SignatureZuulFilter(zuulSignatureVerifier, responseMessageResolver);
    }
    
    @Bean
    public ZuulFilter requestHeaderAddingZuulFilter(ZuulHelper zuulHelper) {
        return new RequestHeaderAddingZuulFilter(zuulHelper);
    }

    @Bean
    public ZuulFilter responseLogZuulFilter() {
        return createZuulFilter(() -> RequestContext.getCurrentContext().getThrowable() == null,
            FilterType.POST, FilterConstants.ORDER_POST_LOG,
            () -> {
                this.gatewayAccessLoggerAdapter.response(RequestContext.getCurrentContext().getRequest(), RequestContext.getCurrentContext().getResponse());
                return null;
            });
    }
    
    @Bean
    public ZuulFilter errorLogZuulFilter() {
        return createZuulFilter(() -> RequestContext.getCurrentContext().getThrowable() != null,
            FilterType.ERROR, FilterConstants.ORDER_ERROR_LOG,
            () -> {
                RequestContext ctx = RequestContext.getCurrentContext();
                this.gatewayAccessLoggerAdapter.response(ctx.getRequest(), ctx.getResponse());
                return null;
            });
    }
    
}
