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

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.BaseDTO;

/**
 * 交易执行失败响应处理
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface FailureHandlerResponseResolver {
    /**
     *
     * @param throwable
     * @param responseType
     * @return
     */
    <R extends BaseDTO & ConfigurableAlerting> R handlerFailure(Throwable throwable, Class<R> responseType);

    /**
     *
     * @param alertCapable alert
     * @param responseType  alert type
     * @return
     */
    <R extends BaseDTO & ConfigurableAlerting> R handlerFailure(AlertCapable alertCapable, Class<R> responseType);
}
