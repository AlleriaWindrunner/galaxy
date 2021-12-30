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

package com.galaxy.lemon.framework.data;

import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface PropertyWrapper {

    /**
     * wrapped class
     * @return
     */
    Class<?> getWrappedClass();

    /**
     * 是否Wrapper对象的属性
     * @param propertyName
     * @return
     */
    boolean hasProperty(String propertyName);

    /**
     * 获取封装对象所有属性
     * @return
     */
    String[] getProperties();

    /**
     *
     * @param propertyName
     * @return
     */
    TypeDescriptor getTypeDescriptor(String propertyName);

    /**
     * get all propertyDescriptors
     * @return
     */
    PropertyDescriptor[] getPropertyDescriptors();

    /**
     * set value to property of target object
     * @param target
     * @param value
     * @param propertyName
     */
    void setValue(Object target, Object value, String propertyName);

    /**
     * 获取Value
     * @param target
     * @param propertyName
     * @return
     */
    Object getValue(Object target, String propertyName);
}
