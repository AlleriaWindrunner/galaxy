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

package com.galaxy.lemon.framework.jackson.log;

import com.galaxy.lemon.common.log.LogIgnore;
import com.galaxy.lemon.common.utils.JudgeUtils;
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

public class LogIgnoreBeanSerializerModifier extends BeanSerializerModifier {

    private LogIgnorePredicate logIgnorePredicate = new LogIgnorePredicate.RequiredLogIgnorePredicate();

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
        if (beanPropertyWriter.getMember().hasAnnotation(LogIgnore.class)) {
            LogIgnore logIgnore = beanPropertyWriter.getMember().getAnnotation(LogIgnore.class);
            return !this.logIgnorePredicate.predicate(new LogIgnorePredicate.LogIgnoreMetadata(logIgnore.condition()), beanPropertyWriter);
        }
        return true;
    }

}
