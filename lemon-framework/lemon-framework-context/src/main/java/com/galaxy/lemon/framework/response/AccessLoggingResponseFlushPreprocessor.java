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

package com.galaxy.lemon.framework.response;

import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.logger.http.HttpAccessLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class AccessLoggingResponseFlushPreprocessor implements ResponseFlushPreprocessor {

    private HttpAccessLogger accessLogger;

    public AccessLoggingResponseFlushPreprocessor(HttpAccessLogger accessLogger) {
        this.accessLogger = accessLogger;
    }

    @Override
    public void processBeforeResponseFlush(HttpServletRequest request, HttpServletResponse response, BaseDTO rspDTO) {
        this.accessLogger.response(request, response, rspDTO);
    }
}
