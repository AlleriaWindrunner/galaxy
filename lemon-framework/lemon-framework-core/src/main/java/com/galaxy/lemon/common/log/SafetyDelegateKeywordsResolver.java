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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SafetyDelegateKeywordsResolver implements KeywordsResolver {
    private static final Logger logger = LoggerFactory.getLogger(SafetyDelegateKeywordsResolver.class);

    private KeywordsResolver keywordsResolver;

    public SafetyDelegateKeywordsResolver(KeywordsResolver keywordsResolver) {
        this.keywordsResolver = keywordsResolver;
    }


    @Override
    public <T> Keywords resolve(T obj) {
        try {
            return this.keywordsResolver.resolve(obj);
        } catch (Exception e) {
            logger.debug("Failed to resolve keywords by " + this.keywordsResolver, e);
        }
        return null;
    }
}
