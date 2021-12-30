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

package com.galaxy.lemon.framework.springcloud.fegin;

import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.springcloud.fegin.support.QueryBodyParameterQueryStringResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Deprecated
public class LemonBodyParameterResolver {
    
    private Map<Class<?>,Map<String, Method>> readMethodsGenericClass = null;
    private static volatile LemonBodyParameterResolver lemonBodyParameterResolver;
    
    private LemonBodyParameterResolver() {
        this.readMethodsGenericClass = new HashMap<>();
    }
    
    public static LemonBodyParameterResolver getInstance() {
        if(null == lemonBodyParameterResolver) {
            synchronized(LemonBodyParameterResolver.class) {
                if(null == lemonBodyParameterResolver) {
                    lemonBodyParameterResolver = new LemonBodyParameterResolver();
                }
            }
        }
        return lemonBodyParameterResolver;
    }
    
    public void resolverClass(Class<?> clazz) {
        if(! BaseDTO.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class \""+clazz+"\" must be assignable from \"BaseDTO\".");
        }
        readMethodsGenericClass.put(clazz, ReflectionUtils.getDeclareReadMethods(clazz));
    }
    
    public Object readParameterValue(Object object, Class<?> clazz, String parameterName) {
        Map<String, Method> readMethods = readMethodsGenericClass.get(clazz);
        if(JudgeUtils.isEmpty(readMethods)) {
            throw new IllegalStateException("Class \""+clazz+"\" does not resolve during spring boot.");
        }
        Method readMethod = readMethods.get(parameterName);
        if(JudgeUtils.isNull(readMethod)) {
            throw new IllegalStateException("Could not found Read method for property \""+parameterName+"\" in class \""+clazz+"\".");
        }
        try {
            Object[] args = null;
            return readMethod.invoke(object, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw LemonException.create(e);
        }
    }
}
