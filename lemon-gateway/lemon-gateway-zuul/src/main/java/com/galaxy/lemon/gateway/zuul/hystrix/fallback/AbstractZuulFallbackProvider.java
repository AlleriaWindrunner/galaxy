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

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractZuulFallbackProvider implements ZuulFallbackProvider {

    private String route;
    private ResponseMessageResolver responseMessageResolver;

    public AbstractZuulFallbackProvider(ResponseMessageResolver responseMessageResolver) {
        this(null, responseMessageResolver);
    }

    public AbstractZuulFallbackProvider(String route, ResponseMessageResolver responseMessageResolver) {
        this.route = route;
        this.responseMessageResolver = responseMessageResolver;
    }

    @Override
    public String getRoute() {
        return this.route;
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return createClientHttpResponse();
    }

    protected ClientHttpResponse createClientHttpResponse() {
        return new ClientHttpResponse() {

            @Override
            public InputStream getBody() throws IOException {
                InputStream inputStream = createBody();
                return inputStream == null ? new ByteArrayInputStream(getDefaultResponseBody()) : inputStream;
            }

            @Override
            public HttpHeaders getHeaders() {
                return createHeader();
            }

            @Override
            public HttpStatus getStatusCode() throws IOException {
                return returnStatusCode();
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return returnRawStatusCode();
            }

            @Override
            public String getStatusText() throws IOException {
                return returnStatusText();
            }

            @Override
            public void close() {
                doClose();
            }

        };
    }

    protected byte[] getDefaultResponseBody() throws IOException {
        return this.getResponseMessageResolver().generateBytes(ErrorMsgCode.SERVER_NOT_AVAILABLE);
    }

    /**
     * close the resource after client response
     */
    protected abstract void doClose();

    /**
     * the response status info
     * @return
     */
    protected abstract String returnStatusText();

    /**
     * the raw status code
     * @return
     */
    protected abstract int returnRawStatusCode();

    /**
     * the response http status
     * @return
     */
    protected abstract HttpStatus returnStatusCode();

    /**
     * the response http header
     * @return
     */
    protected abstract HttpHeaders createHeader();

    /**
     * the response body
     * @return
     * @throws IOException
     */
    protected abstract InputStream createBody() throws IOException;

    public ResponseMessageResolver getResponseMessageResolver() {
        return responseMessageResolver;
    }
}
