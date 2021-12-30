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

import com.galaxy.lemon.common.log.Keywords;
import com.galaxy.lemon.common.log.LoggingCodec;
import com.galaxy.lemon.framework.desensitization.DesensitizationConfigurer;
import com.galaxy.lemon.framework.jackson.ObjectMapperObjectCodec;
import com.galaxy.lemon.framework.jackson.sensitive.DesensitizationBeanSerializerModifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.galaxy.lemon.framework.jackson.log.KeywordsJsonSerializer;
import com.galaxy.lemon.framework.jackson.log.LogIgnoreBeanSerializerModifier;
import org.springframework.beans.factory.BeanFactory;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultLoggingCodec extends ObjectMapperObjectCodec implements LoggingCodec {

    public DefaultLoggingCodec(ObjectMapper objectMapper, BeanFactory beanFactory) {
        super(objectMapper);
        SimpleModule logJsonModule = new SimpleModule("logJsonModule");
        logJsonModule.addSerializer(Keywords.class, new KeywordsJsonSerializer());
        objectMapper.registerModule(logJsonModule);
        SerializerFactory serializerFactory = objectMapper.getSerializerFactory()
                .withSerializerModifier(new DesensitizationBeanSerializerModifier(new DesensitizationConfigurer(beanFactory)))
                .withSerializerModifier(new LogIgnoreBeanSerializerModifier());
        objectMapper.setSerializerFactory(serializerFactory);
    }
}
