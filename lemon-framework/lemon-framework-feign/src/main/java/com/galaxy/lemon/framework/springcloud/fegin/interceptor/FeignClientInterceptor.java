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

package com.galaxy.lemon.framework.springcloud.fegin.interceptor;

import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.remoting.RemoteInvocationDataProcessor;
import com.galaxy.lemon.framework.springcloud.fegin.logging.FeignAccessLoggerAdapter;
import com.galaxy.lemon.framework.utils.IdGenUtils;
import com.galaxy.lemon.framework.utils.LemonUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class FeignClientInterceptor implements MethodInterceptor {
    private RemoteInvocationDataProcessor remoteInvocationDataProcessor;
    private FeignAccessLoggerAdapter feignAccessLoggerAdapter;

    public FeignClientInterceptor(RemoteInvocationDataProcessor remoteInvocationDataProcessor,
                                  FeignAccessLoggerAdapter feignAccessLoggerAdapter) {
        this.remoteInvocationDataProcessor = remoteInvocationDataProcessor;
        this.feignAccessLoggerAdapter = feignAccessLoggerAdapter;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String originalMsgId = LemonUtils.getMsgId();
        try{
            Optional.ofNullable(LemonDataHolder.getLemonData()).ifPresent(l -> l.setMsgId(IdGenUtils.generateMsgId()));
            LemonContextUtils.setRemoteInvocationStartTime();
            Object result =  invocation.proceed();
            this.feignAccessLoggerAdapter.response(result);
            return result;
        } catch (Exception e){
            this.remoteInvocationDataProcessor.processAfterInvocation(LemonDataHolder.getLemonData(), e);
            this.feignAccessLoggerAdapter.response(e);
            throw e;
        }finally {
            LemonContextUtils.clearRemoteInvocationStartTime();
            Optional.ofNullable(LemonDataHolder.getLemonData()).ifPresent(l -> l.setMsgId(originalMsgId));
        }
    }

}
