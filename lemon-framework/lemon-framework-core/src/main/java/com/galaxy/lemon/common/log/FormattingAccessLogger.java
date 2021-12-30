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
import com.galaxy.lemon.framework.context.LemonContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 格式化日志打印
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class FormattingAccessLogger extends OncePerRequestAccessLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormattingAccessLogger.class);

    private ObjectEncoder objectEncoder;

    public FormattingAccessLogger(ObjectEncoder objectEncoder, Logger logger) {
        super(logger);
        this.objectEncoder = objectEncoder;
    }

    public FormattingAccessLogger(ObjectEncoder objectEncoder, Class<?> logClass) {
        super(LoggerFactory.getLogger(logClass));
        this.objectEncoder = objectEncoder;
    }

    @Override
    public void logRequest(RequestInfo<?> requestInfo) {

        try {
            logger.info("[{}] - {}", LemonContextUtils.getAccessConcurrent(), encodeObject(requestInfo));
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Failed to formatting \"RequestInfo\".", e);
            }
        }
    }

    @Override
    public void logResponse(ResponseInfo<?> responseInfo) {
        try {
            logger.info(encodeObject(responseInfo));
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Failed to formatting \"ResponseInfo\".", e);
            }
        }
    }

    protected String encodeObject(Object object) {
        try {
            return this.objectEncoder.writeValueAsString(object);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Failed to formatting object using ObjectEncoder.", e);
            }
        }
        return object.toString();
    }
}
