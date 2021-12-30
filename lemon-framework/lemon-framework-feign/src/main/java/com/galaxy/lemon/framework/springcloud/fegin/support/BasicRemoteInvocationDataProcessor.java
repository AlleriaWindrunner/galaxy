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

package com.galaxy.lemon.framework.springcloud.fegin.support;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.remoting.RemoteInvocationDataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
public class BasicRemoteInvocationDataProcessor<L extends BaseLemonData, D extends BaseDTO, R extends BaseDTO & AlertCapable> implements RemoteInvocationDataProcessor<L, D, R> {
    private static final Logger logger = LoggerFactory.getLogger(BasicRemoteInvocationDataProcessor.class);

    private InternalDataHelper internalDataHelper;

    public BasicRemoteInvocationDataProcessor(InternalDataHelper internalDataHelper) {
        this.internalDataHelper = internalDataHelper;
    }
    @Override
    public void processBeforeInvocation(L lemonData, D baseDTO) {
        if (null == lemonData) {
            if (logger.isDebugEnabled()) {
                logger.debug("LemonData context don't initial, maybe there is a bug.");
            }
            return;
        }
        this.internalDataHelper.copyLemonDataToDTO(lemonData, baseDTO);
    }

    @Override
    public void processAfterInvocation(L lemonData, R responseDto) {
    }

    @Override
    public void processAfterInvocation(L lemonData, Throwable throwable) {
    }
}
