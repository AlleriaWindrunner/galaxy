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

package com.galaxy.lemon.common.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * 构造函数中的异常类及其子类的异常转换
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractExceptionConverter implements ExceptionConverter {
    private List<Class<? extends Throwable>> classes;

    protected AbstractExceptionConverter(Class<? extends Throwable> clazz) {
        this.classes = new ArrayList<>();
        this.classes.add(clazz);
    }

    protected AbstractExceptionConverter(List<Class<? extends Throwable>> classes) {
        this.classes = classes;
    }

    protected List<Class<? extends Throwable>> getSupportThrowableClasses() {
        return this.classes;
    }

    @Override
    public boolean support(Throwable throwable) {
        boolean flag = false;
        for (Class<? extends Throwable> clazz : classes) {
            if (clazz.isAssignableFrom(throwable.getClass())) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
