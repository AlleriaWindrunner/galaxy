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

package com.galaxy.lemon.framework.autoconfigure.core;

import com.galaxy.lemon.framework.autoconfigure.condition.ConditionalOnEntryPoint;
import com.galaxy.lemon.framework.autoconfigure.condition.ConditionalOnGateway;
import com.galaxy.lemon.framework.jackson.ObjectMapperHolder;
import com.galaxy.lemon.framework.jackson.message.DefaultResponseIgnorePredicate;
import com.galaxy.lemon.framework.jackson.message.ResponseIgnoreBeanSerializerModifier;
import com.galaxy.lemon.framework.jackson.message.ResponseIgnorePredicate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnBean(ObjectMapper.class)
public class ObjectMapperConfiguration {


    @Configuration
    @ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
    @Conditional(ResponseExtendsObjectMapperCondition.class)
    public static class ResponseObjectMapperConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ResponseIgnorePredicate responseIgnorePredicate() {
            return new DefaultResponseIgnorePredicate();
        }

        @Bean
        public ObjectMapperHolder responseObjectMapperHolder(Jackson2ObjectMapperBuilder builder,
                                                             ResponseIgnorePredicate responseIgnorePredicate) {
            ObjectMapper objectMapper = builder.build();
            SerializerFactory serializerFactory = objectMapper.getSerializerFactory()
                    .withSerializerModifier(new ResponseIgnoreBeanSerializerModifier(responseIgnorePredicate));
            objectMapper.setSerializerFactory(serializerFactory);
            return new ObjectMapperHolder(objectMapper);
        }
    }

    @Bean
    @ConditionalOnMissingBean(name="responseObjectMapperHolder")
    public ObjectMapperHolder responseObjectMapperHolder(ObjectMapper objectMapper) {
        return new ObjectMapperHolder(objectMapper);
    }


    public static class ResponseExtendsObjectMapperCondition extends AnyNestedCondition {

        public ResponseExtendsObjectMapperCondition( ) {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnEntryPoint
        static class EntryPonitCondition {

        }

        @ConditionalOnGateway
        static class GatewayCondtion {

        }
    }
}
