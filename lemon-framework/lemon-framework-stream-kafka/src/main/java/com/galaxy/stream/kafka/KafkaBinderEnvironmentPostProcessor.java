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

package com.galaxy.stream.kafka;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.bind.RelaxedNames;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 解决spring cloud stream kafka
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Order(Ordered.LOWEST_PRECEDENCE -1)
public class KafkaBinderEnvironmentPostProcessor implements EnvironmentPostProcessor {

    public final static String SPRING_KAFKA = "spring.kafka";

    public final static String SPRING_KAFKA_PRODUCER = SPRING_KAFKA + ".producer";

    public final static String SPRING_KAFKA_CONSUMER = SPRING_KAFKA + ".consumer";

    public final static String SPRING_KAFKA_PRODUCER_KEY_SERIALIZER = SPRING_KAFKA_PRODUCER + "." + "keySerializer";

    public final static String SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER = SPRING_KAFKA_PRODUCER + "." + "valueSerializer";

    public final static String SPRING_KAFKA_CONSUMER_KEY_DESERIALIZER = SPRING_KAFKA_CONSUMER + "." + "keyDeserializer";

    public final static String SPRING_KAFKA_CONSUMER_VALUE_DESERIALIZER = SPRING_KAFKA_CONSUMER + "." + "valueDeserializer";

    private static final String KAFKA_BINDER_DEFAULT_PROPERTIES = "kafkaBinderDefaultProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!environment.getPropertySources().contains(KAFKA_BINDER_DEFAULT_PROPERTIES)) {
            Map<String, Object> kafkaBinderDefaultProperties = new HashMap<>();
            kafkaBinderDefaultProperties.put("logging.level.org.I0Itec.zkclient", resolvePropertyValue(environment, "logging.level.org.I0Itec.zkclient", "ERROR", false));
            kafkaBinderDefaultProperties.put("logging.level.kafka.server.KafkaConfig", resolvePropertyValue(environment, "logging.level.kafka.server.KafkaConfig", "ERROR", false));
            kafkaBinderDefaultProperties.put("logging.level.kafka.admin.AdminClient.AdminConfig", resolvePropertyValue(environment, "logging.level.kafka.admin.AdminClient.AdminConfig", "ERROR", false));
            kafkaBinderDefaultProperties.put(SPRING_KAFKA_PRODUCER_KEY_SERIALIZER, resolvePropertyValue(environment, SPRING_KAFKA_PRODUCER_KEY_SERIALIZER, ByteArraySerializer.class.getName()));
            kafkaBinderDefaultProperties.put(SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER, resolvePropertyValue(environment, SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER, ByteArraySerializer.class.getName()));
            kafkaBinderDefaultProperties.put(SPRING_KAFKA_CONSUMER_KEY_DESERIALIZER, resolvePropertyValue(environment, SPRING_KAFKA_CONSUMER_KEY_DESERIALIZER, ByteArrayDeserializer.class.getName()));
            kafkaBinderDefaultProperties.put(SPRING_KAFKA_CONSUMER_VALUE_DESERIALIZER, resolvePropertyValue(environment, SPRING_KAFKA_CONSUMER_VALUE_DESERIALIZER, ByteArrayDeserializer.class.getName()));
            environment.getPropertySources().addLast(new MapPropertySource(KAFKA_BINDER_DEFAULT_PROPERTIES, kafkaBinderDefaultProperties));

        }
    }

    public String resolvePropertyValue(ConfigurableEnvironment environment, String propertyName, String defalutValue, boolean relaxed) {
        String value = environment.getProperty(propertyName);
        if (null == value && relaxed) {
            RelaxedNames relaxedNames = RelaxedNames.forCamelCase(propertyName);
            for (String relaxedName : relaxedNames) {
                value = environment.getProperty(relaxedName);
                if (null != value) {
                    break;
                }
            }
        }
        return null == value ? defalutValue : value;
    }

    public String resolvePropertyValue(ConfigurableEnvironment environment, String propertyName, String defalutValue) {
        return resolvePropertyValue(environment, propertyName, defalutValue, true);
    }

}
