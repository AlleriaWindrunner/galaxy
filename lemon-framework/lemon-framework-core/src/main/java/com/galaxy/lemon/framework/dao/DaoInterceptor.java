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

package com.galaxy.lemon.framework.dao;

import com.galaxy.lemon.framework.data.DOBasicOperation;
import com.galaxy.lemon.framework.id.GeneratedValueResolver;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.PatternMatchUtils;

import java.util.stream.Stream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DaoInterceptor implements MethodInterceptor {
    private static final String INSERT_METHOD_NAME = "insert*";
    private static final String UPDATE_METHOD_NAME = "update*";

    private GeneratedValueResolver generatedValueResolver;

    public DaoInterceptor(GeneratedValueResolver generatedValueResolver) {
        this.generatedValueResolver = generatedValueResolver;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();

        if (isInsertOperation(invocation)) {
            Stream.of(arguments).filter(arg -> arg instanceof DOBasicOperation).forEach(baseDO ->{
                generatedValueResolver.resolve(baseDO);
                ((DOBasicOperation)baseDO).preInsert();
            });
        }

        if (isUpdateOperation(invocation)) {
            Stream.of(arguments).filter(arg -> arg instanceof DOBasicOperation).forEach(baseDao ->{
                ((DOBasicOperation)baseDao).preUpdate();
            });
        }
        return invocation.proceed();
    }

    private boolean isInsertOperation(MethodInvocation invocation) {
        return PatternMatchUtils.simpleMatch(INSERT_METHOD_NAME, invocation.getMethod().getName());
    }

    private boolean isUpdateOperation(MethodInvocation invocation) {
        return PatternMatchUtils.simpleMatch(UPDATE_METHOD_NAME, invocation.getMethod().getName());
    }

}
