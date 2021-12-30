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

package com.galaxy.lemon.gateway.zuul.signature;

import com.galaxy.lemon.common.HttpMethod;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.signature.AbstractSignatureVerifier;
import com.galaxy.lemon.framework.signature.SignatureMetadataExtractor;
import com.galaxy.lemon.gateway.zuul.ZuulExtensionProperties.ZuulRoute;
import com.galaxy.lemon.gateway.zuul.ZuulHelper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractZuulSignatureVerifier extends AbstractSignatureVerifier<HttpServletRequest> {
    
    protected static final String REQUEST_HEADER_SIGN = "x-lemon-sign";
    protected static final String SIGNATURE_PATHS_SEPARATOR = ",";

    protected ZuulHelper zuulHelper;

    public AbstractZuulSignatureVerifier(ZuulHelper zuulHelper, SignatureMetadataExtractor<SignatureDataSource<HttpServletRequest>, String> signatureMetadataExtractor) {
        super(signatureMetadataExtractor);
        this.zuulHelper = zuulHelper;
    }

    @Override
    protected boolean doShouldVerify(SignatureDataSource<HttpServletRequest> signatureDataSource) {
        ZuulRoute zuulRoute = this.zuulHelper.getCurrentZuulRoute(signatureDataSource.getSignatureData());
        return JudgeUtils.isTrue(zuulRoute.getSignatured(), true);
    }

    @Override
    protected String resolveSignatureSignedValue(SignatureDataSource<HttpServletRequest> signatureDataSource) {
        return Optional.ofNullable(signatureDataSource.getSignatureData()).map(this::getSignatureMessage).orElseThrow(() -> new InvalidSignatureException("Could not found Signature value in http header."));
    }

    @Override
    protected String resolveSignatureContent(SignatureDataSource<HttpServletRequest> signatureDataSource) {
        return Optional.ofNullable(signatureDataSource.getSignatureData()).map(this::getContentMessage).orElseThrow(() -> new InvalidSignatureException("Failed to extract signature content."));
    }

    /**
     * 获取签名消息
     * @param request
     * @return
     */
    protected String getSignatureMessage(HttpServletRequest request) {
        return request.getHeader(REQUEST_HEADER_SIGN);
    }

    /**
     * 获取签名内容
     * @param request
     * @return
     */
    protected String getContentMessage(HttpServletRequest request) {
        String contentMessage;
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod().toUpperCase());
        switch (httpMethod) {
        case GET:
            return Optional.ofNullable(request.getQueryString()).filter(JudgeUtils::isNotBlank).orElseGet(() -> getPathVariable(request));
        default:
            contentMessage = this.zuulHelper.getCurrentRequestBody(request);
            if(JudgeUtils.isNotBlank(contentMessage)) {
                return contentMessage;
            }
            if(isFormRequest(request)) {
                contentMessage = this.getFormData(request);
                if(JudgeUtils.isNotBlank(contentMessage)) {
                    return contentMessage;
                }
            }
            return getPathVariable(request);
        }
    }
    
    protected boolean isFormRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        MediaType mediaType = MediaType.valueOf(contentType);
        return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType)
                || MediaType.MULTIPART_FORM_DATA.includes(mediaType);
    }
    
    private String getPathVariable(HttpServletRequest request) {
        String requestURI = this.zuulHelper.getCurrentRequestURI(request);
        if (JudgeUtils.isBlank(requestURI)) {
            return null;
        }
        
        ZuulRoute zuulRoute = this.zuulHelper.getCurrentZuulRoute(request);
        if(JudgeUtils.isNull(zuulRoute) || JudgeUtils.isBlank(zuulRoute.getPath())
            || zuulRoute.getPath().indexOf("{") == -1) {
            return null;
        }
        
        Map<String, String> matchedPaths = this.zuulHelper.getCurrentPathMatchers(request);
        if(JudgeUtils.isEmpty(matchedPaths)) {
            return null;
        }
        String signaturePaths = zuulRoute.getSignaturedParameters();
        if(JudgeUtils.isNotBlank(signaturePaths)) {
            List<String> signaturePathList = Arrays.asList(signaturePaths.split(SIGNATURE_PATHS_SEPARATOR));
            return matchedPaths.values().stream().filter(m -> signaturePathList.contains(m)).collect(Collectors.joining());
        }
        return matchedPaths.values().stream().collect(Collectors.joining());
    }
    
    protected String getFormData(HttpServletRequest request) {
        Map<String, String[]> parameterMap = this.zuulHelper.getParameterMap(request);
        if(JudgeUtils.isEmpty(parameterMap)) {
            return null;
        }
        ZuulRoute zuulRoute = this.zuulHelper.getCurrentZuulRoute(request);
        if(JudgeUtils.isNull(zuulRoute)) {
            return null;
        }
        String signatureParameters = zuulRoute.getSignaturedParameters();
        if(JudgeUtils.isBlank(signatureParameters)) {
            if(logger.isDebugEnabled()) {
                logger.debug("No signature parameters found in zuul configuration for request {},  please confirm it.", request.getRequestURL());
            }
            return null;
        }
        StringBuilder sb  = new StringBuilder();
        Stream.of(signatureParameters.split(SIGNATURE_PATHS_SEPARATOR)).forEachOrdered(s -> {
            String[] vs = parameterMap.get(s);
            if(JudgeUtils.isNotEmpty(vs)) {
                Stream.of(vs).forEachOrdered(sb::append);
            }
        });
        return sb.toString();
    }

}
