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

package com.galaxy.lemon.framework.springcloud.fegin.logging;

import com.galaxy.lemon.common.codec.CodecException;
import com.galaxy.lemon.common.codec.ObjectEncoder;
import com.galaxy.lemon.common.log.AccessLogger;
import com.galaxy.lemon.common.log.RequestInfo;
import com.galaxy.lemon.common.log.ResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * feign 日志打印
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class FeignAccessLogger implements AccessLogger {
    private static final Logger logger = LoggerFactory.getLogger(FeignAccessLogger.class);

    private ObjectEncoder objectEncoder;

    public FeignAccessLogger(ObjectEncoder objectEncoder) {
        this.objectEncoder = objectEncoder;
    }

    @Override
    public <T, REQ extends RequestInfo<T>> void request(REQ requestInfo) {
        try {
            logger.info(this.objectEncoder.writeValueAsString(requestInfo));
        } catch (CodecException e) {
            logger.error("", e);
        }
    }

    @Override
    public <T, RSP extends ResponseInfo<T>> void response(RSP responseInfo) {
        try {
            logger.info(this.objectEncoder.writeValueAsString(responseInfo));
        } catch (CodecException e) {
            logger.error("", e);
        }
    }
}
