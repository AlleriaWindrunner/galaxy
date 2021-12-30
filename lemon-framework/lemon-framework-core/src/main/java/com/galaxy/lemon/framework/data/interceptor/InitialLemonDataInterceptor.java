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

package com.galaxy.lemon.framework.data.interceptor;

import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.data.LemonDataInitializer;
import com.galaxy.lemon.framework.data.LemonDataLifecycle;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class InitialLemonDataInterceptor implements MethodInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(InitialLemonDataInterceptor.class);

    private InitialLemonDataSource initialLemonDataSource;

    public InitialLemonDataInterceptor(InitialLemonDataSource initialLemonDataSource) {
        this.initialLemonDataSource = initialLemonDataSource;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        LemonDataInitializer lemonDataInitializer = this.getLemonDataInitializer(invocation);
        LemonException.throwLemonExceptionIfNecessary(null == lemonDataInitializer, ErrorMsgCode.SYS_ERROR.getMsgCd(),
                "Bean \"LemonDataInitializer\" is null at method \""+invocation.getMethod().getName()+"\"");
        try{
            if (lemonDataInitializer instanceof LemonDataInitializerAdapter) {
                Object[] arguments = invocation.getArguments();
                BaseLemonData baseLemonData = JudgeUtils.isEmpty(arguments) ? null :
                        Stream.of(arguments).filter(a -> a instanceof BaseLemonData).findFirst().map(b -> (BaseLemonData) b).orElse(null);
                LemonDataInitializerAdapter lemonDataInitializerAdapter = (LemonDataInitializerAdapter) lemonDataInitializer;
                if (null == baseLemonData) {
                    lemonDataInitializerAdapter.initialLemonData();
                } else {
                    lemonDataInitializerAdapter.initialLemonData(baseLemonData);
                }
            } else {
                lemonDataInitializer.initialLemonData();
            }
            return invocation.proceed();
        } finally {
            if (lemonDataInitializer instanceof LemonDataLifecycle) {
                ((LemonDataLifecycle)lemonDataInitializer).clear();
            } else {
                LemonDataHolder.clear();
                LemonContext.clearCurrentContext();
                if (logger.isDebugEnabled()) {
                    logger.debug("Cleared lemon data context by {}.", this);
                    logger.debug("Cleared LemonContext by {}.", this);
                }
            }
        }
    }

    protected LemonDataInitializer getLemonDataInitializer(MethodInvocation invocation) {
        return this.initialLemonDataSource.getLemonDataInitializer(invocation.getMethod(), invocation.getMethod().getDeclaringClass());
    }
}
