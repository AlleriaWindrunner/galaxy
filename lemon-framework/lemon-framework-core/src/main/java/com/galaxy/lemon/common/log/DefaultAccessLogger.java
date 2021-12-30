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

package com.galaxy.lemon.common.log;

import com.galaxy.lemon.common.codec.ObjectEncoder;
import org.slf4j.Logger;

/**
 * 支持keywords,formatting,OncePerRequest
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultAccessLogger extends FormattingAccessLogger {

    private KeywordsResolver keywordsResolver;


    public DefaultAccessLogger(ObjectEncoder objectEncoder, Logger logger) {
        this(null, objectEncoder, logger);
    }

    public DefaultAccessLogger(KeywordsResolver keywordsResolver,
                               ObjectEncoder objectEncoder,
                               Logger logger) {
        super(objectEncoder, logger);
        this.keywordsResolver = wrapperSafetyKeywordsResolver(keywordsResolver);
    }

    public DefaultAccessLogger(ObjectEncoder objectEncoder, Class<?> logClass) {
        this(null, objectEncoder, logClass);
    }

    public DefaultAccessLogger(KeywordsResolver keywordsResolver,
                               ObjectEncoder objectEncoder,
                               Class<?> logClass) {
        super(objectEncoder, logClass);
        this.keywordsResolver = wrapperSafetyKeywordsResolver(keywordsResolver);
    }

    @Override
    public void logRequest(RequestInfo<?> requestInfo) {
        resolveKeywords(requestInfo);
        super.logRequest(requestInfo);
    }

    @Override
    public void logResponse(ResponseInfo<?> responseInfo) {
        resolveKeywords(responseInfo);
        super.logResponse(responseInfo);
    }

    private void resolveKeywords(ResponseInfo<?> responseInfo) {
        DefaultKeywordsResolver.KeywordsResolverInfo keywordsResolverInfo =
                new DefaultKeywordsResolver.KeywordsResolverInfo(responseInfo.getResult(), extractExpressionsKey(responseInfo.getKeywords()), DefaultKeywordsResolver.Type.RESPONSE);
        responseInfo.setKeywords(this.keywordsResolver.resolve(keywordsResolverInfo));
    }

    private void resolveKeywords(RequestInfo<?> requestInfo) {
        DefaultKeywordsResolver.KeywordsResolverInfo keywordsResolverInfo =
                new DefaultKeywordsResolver.KeywordsResolverInfo(requestInfo.getTarget(), extractExpressionsKey(requestInfo.getKeywords()), DefaultKeywordsResolver.Type.REQUEST);
        requestInfo.setKeywords(this.keywordsResolver.resolve(keywordsResolverInfo));
    }

    private KeywordsExpressionSource.ExpressionKey extractExpressionsKey(Keywords keywords) {
        return keywords instanceof RawExpressionKeywords ? ((RawExpressionKeywords) keywords).getExpressionKey() : null;
    }

    private KeywordsResolver wrapperSafetyKeywordsResolver(KeywordsResolver keywordsResolver) {
        return null == keywordsResolver ? new KeywordsResolver() {
            @Override
            public <T> Keywords resolve(T obj) {
                return null;
            }
        } : new SafetyDelegateKeywordsResolver(keywordsResolver);
    }

}
