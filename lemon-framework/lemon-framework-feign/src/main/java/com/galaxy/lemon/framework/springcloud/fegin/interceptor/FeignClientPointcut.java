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

import feign.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.cloud.netflix.feign.FeignClient;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class FeignClientPointcut extends StaticMethodMatcherPointcut {
    private static final Logger logger = LoggerFactory.getLogger(FeignClientPointcut.class);

    //private static final Map<ClientKey, Boolean> matchCaches = new ConcurrentHashMap<>();

    @Override
    public boolean matches(Method method, Class<?> targetClass) {

        boolean matches =  matchesFeignClient(targetClass);

        if (matches) {
            matches = matchesClientMethod(method);
        }

        if (matches && logger.isInfoEnabled()) {
            logger.info("Intercepted feign client {}, class name is {}.", targetClass.getInterfaces()[0], targetClass);
        }

        return matches;

    }

    private boolean matchesFeignClient(Class<?> targetClass) {
        Class<?>[] interfaces = targetClass.getInterfaces();
        if(interfaces == null | interfaces.length != 1) {
            return false;
        }
        boolean b = interfaces[0].isAnnotationPresent(FeignClient.class);
        return b;
    }

    private boolean matchesClientMethod(Method method) {
        if (method.getDeclaringClass() == Object.class ||
                (method.getModifiers() & Modifier.STATIC) != 0 ||
                Util.isDefault(method)) {
            return false;
        }
        return true;
    }

    static class ClientKey {
        private Class<?> clazz;
        private Method method;

        public ClientKey(Class<?> clazz, Method method) {
            this.clazz = clazz;
            this.method = method;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClientKey clientKey = (ClientKey) o;

            if (clazz != null ? !clazz.equals(clientKey.clazz) : clientKey.clazz != null) return false;
            return method != null ? method.equals(clientKey.method) : clientKey.method == null;
        }

        @Override
        public int hashCode() {
            int result = clazz != null ? clazz.hashCode() : 0;
            result = 31 * result + (method != null ? method.hashCode() : 0);
            return result;
        }
    }
}
