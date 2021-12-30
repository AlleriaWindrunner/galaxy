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

import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.framework.signature.SignatureMetadataExtractor;
import com.galaxy.lemon.gateway.zuul.ZuulHelper;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultZuulSignatureVerifier extends AbstractZuulSignatureVerifier {
    public static final String REQUIRES_UNSET_REQUEST_CONTEXT = "REQUIRES_UNSET_REQUEST_CONTEXT";

    public DefaultZuulSignatureVerifier(ZuulHelper zuulHelper, SignatureMetadataExtractor<SignatureDataSource<HttpServletRequest>, String> signatureMetadataExtractor){
        super(zuulHelper, signatureMetadataExtractor);
    }

    @Override
    public boolean verify(SignatureDataSource<HttpServletRequest> signatureDataSource) {
        try {
            return super.verify(signatureDataSource);
        } finally {
            if(requiresUnsetRequestContext()) {
                RequestContext.getCurrentContext().unset();
            }
        }
    }

    private boolean requiresUnsetRequestContext() {
        return LemonContext.getCurrentContext().getBoolean(REQUIRES_UNSET_REQUEST_CONTEXT, Boolean.FALSE);
    }

}
