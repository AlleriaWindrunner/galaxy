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

package com.galaxy.lemon.framework.data.support;

import com.galaxy.lemon.common.KVPair;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.framework.data.PropertyWrapper;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DTO 对象属性描述
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RelaxedPropertyWrapper implements PropertyWrapper {

    private Map<String, Method> propertyNameSetMethods;
    private Map<String, Method> propertyNameGetMethods;
    private String [] excludeProperties = new String[] {"class"};
    private PropertyDescriptor[] propertyDescriptors;
    private String[] properties;
    private Map<String, TypeDescriptor> propertyNameTypeDescriptorMapper;
    private Class<?> clazz;

    public RelaxedPropertyWrapper(Class<?> clazz) {
        this.clazz = clazz;
        this.propertyNameSetMethods = ReflectionUtils.getAllDeclareWriteMethods(clazz);
        this.propertyNameGetMethods = ReflectionUtils.getAllDeclareReadMethods(clazz);
        List<PropertyDescriptor> propertyDescriptorList = Stream.of(ReflectionUtils.getPropertyDescriptors(clazz)).filter(p -> ! isExcludeProperty(p.getName())).collect(Collectors.toList());
        int propertiesSize = propertyDescriptorList.size();
        this.propertyDescriptors = propertyDescriptorList.toArray(new PropertyDescriptor[propertiesSize]);
        this.properties = propertyDescriptorList.stream().map(PropertyDescriptor::getName).collect(Collectors.toList()).toArray(new String[propertiesSize]);
        this.propertyNameTypeDescriptorMapper = propertyDescriptorList.stream().map(p -> KVPair.instance(p.getName(), TypeDescriptor.valueOf(p.getPropertyType()) )).collect(Collectors.toMap(KVPair::getK, KVPair::getV));
    }

    private boolean isExcludeProperty(String propertyName) {
        return Stream.of(this.excludeProperties).anyMatch(s -> s.equals(propertyName));
    }

    @Override
    public Class<?> getWrappedClass() {
        return this.clazz;
    }

    @Override
    public boolean hasProperty(String propertyName) {
        return Stream.of(this.properties).anyMatch(s -> s.equals(propertyName));
    }

    @Override
    public String[] getProperties() {
        return this.properties;
    }

    @Override
    public TypeDescriptor getTypeDescriptor(String propertyName) {
        return this.propertyNameTypeDescriptorMapper.get(propertyName);
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.propertyDescriptors;
    }

    @Override
    public void setValue(Object target, Object value, String propertyName) {
        Optional.ofNullable(propertyNameSetMethods.get(propertyName)).ifPresent(m -> {
            try {
                m.invoke(target, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LemonException.throwLemonException(e);
            }
        });
    }

    @Override
    public Object getValue(Object target, String propertyName) {
        LemonException.throwLemonExceptionIfNecessary(!propertyNameGetMethods.containsKey(propertyName), ErrorMsgCode.SYS_ERROR.getMsgCd(), "Cloud not found read method for property \"" + propertyName + "\" at class \"" + this.clazz + "\".");
        return Optional.ofNullable(propertyNameGetMethods.get(propertyName)).map(m -> {
            try {
                return m.invoke(target);
            } catch (IllegalAccessException |InvocationTargetException e) {
                throw LemonException.create(e);
            }
        }).orElse(null);
    }
}
