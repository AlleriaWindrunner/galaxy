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

package com.galaxy.lemon.gateway.zuul.filter;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.galaxy.lemon.framework.signature.SignatureVerifier;
import com.galaxy.lemon.framework.signature.SignatureVerifier.SignatureDataSource;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SignatureZuulFilter extends ZuulFilter {
    private static final Logger logger = LoggerFactory.getLogger(SignatureZuulFilter.class);
    
    public static final int SIGNATURE_ORDER = FilterConstants.ORDER_PRE_SIGNATURE;
    
    private SignatureVerifier signatureVerifier;
    
    private ResponseMessageResolver responseMessageResolver;
    
    public SignatureZuulFilter(SignatureVerifier signatureVerifier, ResponseMessageResolver responseMessageResolver) {
        this.signatureVerifier = signatureVerifier;
        this.responseMessageResolver = responseMessageResolver;
    }
    
    @Override
    public boolean shouldFilter() {
        return signatureVerifier.shouldVerify(new SignatureDataSource(RequestContext.getCurrentContext().getRequest()));
    }

    @Override
    public Object run() {
        try{
            if (JudgeUtils.not(this.signatureVerifier.verify(new SignatureDataSource(RequestContext.getCurrentContext().getRequest())))) {
                RequestContext ctx = RequestContext.getCurrentContext();
                ctx.setSendZuulResponse(false);
                ctx.setResponseBody(this.responseMessageResolver.generateString(ErrorMsgCode.SIGNATURE_EXCEPTION));
                return null;
            }
        } catch (LemonException le) {
            if(logger.isErrorEnabled()) {
                logger.error("Failed to verify signature", le);
            }
            RequestContext ctx = RequestContext.getCurrentContext();
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(this.responseMessageResolver.generateString(Optional.ofNullable(le.getMsgCd()).orElse(ErrorMsgCode.SYS_ERROR.getMsgCd())));
        } catch (Exception e) {
            if(logger.isErrorEnabled()) {
                logger.error("Failed to verify signature", e);
            }
            RequestContext ctx = RequestContext.getCurrentContext();
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(this.responseMessageResolver.generateString(ErrorMsgCode.SYS_ERROR));
        }
        return null;
    }

    @Override
    public String filterType() {
        return FilterType.PRE.lowerCaseName();
    }

    @Override
    public int filterOrder() {
        return SIGNATURE_ORDER;
    }

}
