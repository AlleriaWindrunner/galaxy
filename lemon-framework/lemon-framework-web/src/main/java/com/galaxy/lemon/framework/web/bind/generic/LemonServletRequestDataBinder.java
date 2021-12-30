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

package com.galaxy.lemon.framework.web.bind.generic;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.PropertyWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.Map;
import java.util.Optional;

/**
 * GenericDTO 对象数据绑定
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonServletRequestDataBinder extends ExtendedServletRequestDataBinder {

    private PropertyWrapper propertyWrapper;

    public LemonServletRequestDataBinder(Object target, PropertyWrapper propertyWrapper) {
        super(target);
        setDisallowedFields();
        this.propertyWrapper = propertyWrapper;
    }

    public LemonServletRequestDataBinder(Object target, String objectName) {
        super(target, objectName);
        setDisallowedFields();
    }

    protected void setDisallowedFields() {
        if(requireDirectBind()) {
            setDisallowedFields(this.propertyWrapper.getProperties());
        }
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        super.addBindValues(mpvs, request);
        if (requireDirectBind()) {
            Map<String, String[]> parameterMap =  request.getParameterMap();
            Optional.ofNullable(parameterMap).filter(JudgeUtils::isNotEmpty).ifPresent(m -> {
                m.entrySet().stream().filter(e -> this.propertyWrapper.hasProperty(e.getKey())).forEach(e -> {
                    setPropertyValueConvertIfNecessary((BaseDTO)this.getTarget(), e.getKey(), Optional.ofNullable(e.getValue()).map(a -> a[0]).orElse(null), this.getConversionService());
                });
            });
        }
    }

    protected boolean requireDirectBind() {
        return this.getTarget() instanceof BaseDTO;
    }

    public void setPropertyValueConvertIfNecessary(BaseDTO<?> genericDTO, String propertyName, Object propertyValue, ConversionService conversionService) {
        if (null != propertyValue && conversionService.canConvert(TypeDescriptor.forObject(propertyValue), this.propertyWrapper.getTypeDescriptor(propertyName))) {
            propertyValue = conversionService.convert(propertyValue, TypeDescriptor.forObject(propertyValue), this.propertyWrapper.getTypeDescriptor(propertyName));
        }
        this.propertyWrapper.setValue(genericDTO, propertyValue, propertyName);
    }
}
