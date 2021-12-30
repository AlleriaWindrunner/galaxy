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

package com.galaxy.lemon.framework.signature.support;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.framework.signature.AbstractSignatureVerifier;
import com.galaxy.lemon.framework.signature.SignatureMetadataExtractor;
import com.galaxy.lemon.framework.signature.SignatureVerifier.SignatureDataSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractIndexSignatureMetadataExtractor implements SignatureMetadataExtractor<SignatureDataSource<HttpServletRequest>, String> {

    public static final String REQUEST_HEADER_SECURE_INDEX = LemonConstants.HTTP_HEADER_SECURE;

    @Override
    public SignatureMetadata<String> extract(SignatureDataSource<HttpServletRequest> dataSource) {
        String index = resolveSecureIndex(dataSource);
        return doExtract(index);
    }

    protected abstract SignatureMetadata<String> doExtract(String index);

    protected String resolveSecureIndex(SignatureDataSource<HttpServletRequest> signatureDataSource) {
        return Optional.ofNullable(signatureDataSource.getSignatureData()).map(this::getSecureIndex).orElseThrow(() -> new AbstractSignatureVerifier.InvalidSignatureException("Could not found Secure index in http header."));
    }

    public String getSecureIndex(HttpServletRequest request) {
        return request.getHeader(REQUEST_HEADER_SECURE_INDEX);
    }
}
