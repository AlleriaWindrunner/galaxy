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

package com.galaxy.lemon.gateway.zuul.hystrix.fallback;

import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SimpleZuulFallbackProvider extends AbstractZuulFallbackProvider {
    private static final String REQUEST_CONTEXT_KEY_RESPONSE_BODY = "REQUEST_CONTEXT_KEY_RESPONSE_BODY";
    private static final String STATUS_TEXT = "Server not available.";

    public SimpleZuulFallbackProvider(ResponseMessageResolver responseMessageResolver) {
        super(responseMessageResolver);
    }

    @Override
    protected void doClose() {

    }

    @Override
    protected String returnStatusText() {
        return STATUS_TEXT;
    }

    @Override
    protected int returnRawStatusCode() {
        return 200;
    }

    @Override
    protected HttpStatus returnStatusCode() {
        return HttpStatus.OK;
    }

    @Override
    protected HttpHeaders createHeader() {
        return new HttpHeaders();
    }

    @Override
    protected InputStream createBody() throws IOException {
        byte[] body = getContextResponseBody();
        return null == body ? null : new ByteArrayInputStream(body);
    }

    /**
     * set fallback response body to current context
     * @param body
     */
    public static void setContextResponseBody(byte[] body) {
        RequestContext.getCurrentContext().set(REQUEST_CONTEXT_KEY_RESPONSE_BODY, body);
    }

    public static byte[] getContextResponseBody() {
        Object body = RequestContext.getCurrentContext().get(REQUEST_CONTEXT_KEY_RESPONSE_BODY);
        return body != null ? (byte[]) body : null;
    }

}
