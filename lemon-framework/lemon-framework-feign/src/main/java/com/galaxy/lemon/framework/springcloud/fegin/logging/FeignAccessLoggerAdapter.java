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

import com.galaxy.lemon.common.log.AccessLogger;
import feign.RequestTemplate;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class FeignAccessLoggerAdapter {
    private AccessLogger accessLogger;

    public FeignAccessLoggerAdapter(AccessLogger accessLogger) {
        this.accessLogger = accessLogger;
    }

    public abstract void request(RequestTemplate template);

    public abstract <R> void response(R rspDTO);

    public abstract void response(Exception e);

    public AccessLogger getAccessLogger() {
        return this.accessLogger;
    }
}
