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

package com.galaxy.lemon.common.cglib;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * cglib proxy.
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class Proxy {
    private Enhancer enhancer;

    protected Proxy() {
        this.enhancer = new Enhancer();
    }

    public static Proxy getProxy(Class<?>... classes) {
        Proxy proxy = new Proxy();
        Enhancer enhancer = proxy.getEnhancer();
        if (classes.length > 1) {
            for (Class<?> clazz : classes) {
                if (!clazz.isInterface()) {
                    throw new IllegalArgumentException("Class must be a interface. ");
                }
            }
            enhancer.setInterfaces(classes);
        } else {
            if (classes[0].isInterface()) {
                enhancer.setInterfaces(classes);
            } else {
                enhancer.setSuperclass(classes[0]);
            }
        }
        return proxy;
    }

    public Object newInstance(MethodInterceptor callback) {
        this.enhancer.setCallback(callback);
        return enhancer.create();
    }

    protected Enhancer getEnhancer() {
        return this.enhancer;
    }

}
