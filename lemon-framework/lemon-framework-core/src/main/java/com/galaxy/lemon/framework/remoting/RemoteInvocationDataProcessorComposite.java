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

package com.galaxy.lemon.framework.remoting;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.utils.OrderUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.remoting.RemoteInvocationDataProcessor;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RemoteInvocationDataProcessorComposite<L extends BaseLemonData, D extends BaseDTO, R extends BaseDTO & AlertCapable> implements RemoteInvocationDataProcessor<L ,D , R> {

    private List<RemoteInvocationDataProcessor<L, D, R>> remoteInvocationDataProcessors;

    public RemoteInvocationDataProcessorComposite(List<RemoteInvocationDataProcessor<L, D, R>> remoteInvocationDataProcessors) {
        if (null != remoteInvocationDataProcessors) {
            this.remoteInvocationDataProcessors = OrderUtils.sortByOrder(remoteInvocationDataProcessors);
        }
    }

    @Override
    public void processBeforeInvocation(L lemonData, D baseDto) {
        Optional.ofNullable(this.remoteInvocationDataProcessors).ifPresent(s ->
        s.stream().forEach(c -> c.processBeforeInvocation(lemonData, baseDto)));
    }

    @Override
    public void processAfterInvocation(L lemonData, R responseDto) {
        Optional.ofNullable(this.remoteInvocationDataProcessors).ifPresent(s ->
        s.stream().forEach(c -> c.processAfterInvocation(lemonData, responseDto)));
    }

    @Override
    public void processAfterInvocation(L lemonData, Throwable throwable) {
        Optional.ofNullable(this.remoteInvocationDataProcessors).ifPresent(s ->
        s.stream().forEach(c -> c.processAfterInvocation(lemonData, throwable)));
    }
}
