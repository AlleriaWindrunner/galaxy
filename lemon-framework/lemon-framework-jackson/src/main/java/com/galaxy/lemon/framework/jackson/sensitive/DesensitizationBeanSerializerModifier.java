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

package com.galaxy.lemon.framework.jackson.sensitive;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.desensitization.DesensitizationConfigurer;
import com.galaxy.lemon.framework.desensitization.DesensitizationConfigurer.DesensitizationContext;
import com.galaxy.lemon.framework.desensitization.Desensitizer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.galaxy.lemon.framework.jackson.sensitive.DesensitizationJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DesensitizationBeanSerializerModifier extends BeanSerializerModifier {
    private static final Logger logger = LoggerFactory.getLogger(DesensitizationBeanSerializerModifier.class);

    private DesensitizationConfigurer configurer;
    private Map<Desensitizer, DesensitizationJsonSerializer> desensitizationJsonSerializerMap = new ConcurrentHashMap<>();

    public DesensitizationBeanSerializerModifier(DesensitizationConfigurer configurer) {
        this.configurer = configurer;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        if (JudgeUtils.isEmpty(beanProperties)) {
            return beanProperties;
        }
        this.configurer.configure(beanDesc.getBeanClass(), ctx -> configureBeanPropertyWriter(ctx, beanProperties));
        return beanProperties;
    }

    private void configureBeanPropertyWriter(DesensitizationContext context, List<BeanPropertyWriter> beanProperties) {
        Optional.ofNullable(context.getDesensitizationMetadatas()).filter(JudgeUtils::isNotEmpty).ifPresent(s -> s.stream().forEach(
                m -> {
                    Desensitizer<?, ?> desensitizer =  context.getDesensitizer(m);
                    BeanPropertyWriter beanPropertyWriter = extractBeanPropertyWriter(beanProperties, m.getField());
                    if (null != beanPropertyWriter) {
                        beanPropertyWriter.assignSerializer(resolveDesensitizationJsonSerializer(desensitizer));
                        if (logger.isDebugEnabled()) {
                            logger.debug("Fetched desensitizer [{}] for field [{}] with type [{}].", desensitizer, m.getField(), m.getType());
                        }
                    } else {
                        if (logger.isWarnEnabled()) {
                            logger.warn("No BeanPropertyWriter found in {} with field {}. ", beanProperties, m.getField());
                        }
                    }
                }
        ));
    }

    private BeanPropertyWriter extractBeanPropertyWriter(List<BeanPropertyWriter> beanPropertyWriters, Field field) {
        BeanPropertyWriter beanPropertyWriter1 = null;
        String fieldName = field.getName();
        for (BeanPropertyWriter beanPropertyWriter : beanPropertyWriters) {
            if (StringUtils.equals(beanPropertyWriter.getName(), fieldName) &&
                    beanPropertyWriter.getMember().getDeclaringClass().equals(field.getDeclaringClass())) {
                beanPropertyWriter1 = beanPropertyWriter;
                break;
            }
        }
        return beanPropertyWriter1;
    }

    //TODO SpelDesensitizer 应该不需要缓存
    public DesensitizationJsonSerializer resolveDesensitizationJsonSerializer(Desensitizer desensitization) {
        if (null == desensitizationJsonSerializerMap.get(desensitization)) {
            synchronized (desensitizationJsonSerializerMap) {
                if (null == desensitizationJsonSerializerMap.get(desensitization)) {
                    desensitizationJsonSerializerMap.put(desensitization, new DesensitizationJsonSerializer(desensitization));
                }
            }
        }
        return desensitizationJsonSerializerMap.get(desensitization);
    }
}
