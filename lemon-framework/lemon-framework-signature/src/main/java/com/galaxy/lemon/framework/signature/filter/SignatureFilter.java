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

package com.galaxy.lemon.framework.signature.filter;

import com.galaxy.lemon.framework.signature.SignatureVerifier;
import com.galaxy.lemon.framework.signature.SignatureVerifyException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 签名验签
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SignatureFilter extends OncePerRequestFilter {

    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 50;

    private int maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;
    private SignatureVerifier signatureVerifier;

    public SignatureFilter(SignatureVerifier signatureVerifier) {
        this.signatureVerifier = signatureVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest requestToUse = request;
        if (!(request instanceof ContentCachingRequestWrapper)) {
            requestToUse = new ContentCachingRequestWrapper(request, getMaxPayloadLength());
        }
        SignatureVerifier.SignatureDataSource signatureDataSource = new SignatureVerifier.SignatureDataSource(requestToUse);
        if(this.signatureVerifier.shouldVerify(signatureDataSource) && ! this.signatureVerifier.verify(signatureDataSource)) {
            throw new SignatureVerifyException("signature error");
        }
        filterChain.doFilter(requestToUse, response);
    }

    /**
     * Return the maximum length of the payload body to be included in the log message.
     * @since 3.0
     */
    protected int getMaxPayloadLength() {
        return this.maxPayloadLength;
    }
}
