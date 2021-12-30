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

package com.galaxy.lemon.framework.jackson.message;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.annotation.ResponseIgnore;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ResponseIgnoreBeanSerializerModifier extends BeanSerializerModifier {
    
    private ResponseIgnorePredicate responseIgnorePredicate;

    public ResponseIgnoreBeanSerializerModifier(ResponseIgnorePredicate responseIgnorePredicate){
        this.responseIgnorePredicate = responseIgnorePredicate;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        if (JudgeUtils.isEmpty(beanProperties)) {
            return beanProperties;
        }
        List<BeanPropertyWriter> newBeanProperties = new ArrayList<>();
        for (BeanPropertyWriter beanPropertyWriter : beanProperties) {
            if (predicate(beanPropertyWriter)) {
                newBeanProperties.add(beanPropertyWriter);
            }
        }
        return newBeanProperties;
    }

    private boolean predicate(BeanPropertyWriter beanPropertyWriter) {
        if (beanPropertyWriter.getMember().hasAnnotation(ResponseIgnore.class)) {
            ResponseIgnore responseIgnore = beanPropertyWriter.getMember().getAnnotation(ResponseIgnore.class);
            return !this.responseIgnorePredicate.predicate(new ResponseIgnorePredicate.ResponseIgnoreMetadata(responseIgnore));
        }
        return true;
    }
}
