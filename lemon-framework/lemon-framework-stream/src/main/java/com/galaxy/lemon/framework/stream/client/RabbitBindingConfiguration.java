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

package com.galaxy.lemon.framework.stream.client;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.stream.client.property.ExtendedProducerPropertiesChangingListener;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class RabbitBindingConfiguration {
    public static final String BINDER_RABBIT = "rabbit";
    /**
     * rabbit 异步接口初始化绑定关系
     * @param bindingMetadataHolder
     * @param bindingServiceProperties
     * @return
     */
    @Bean
    public CommandLineRunner rabbitBindingCommandLineRunner(BindingMetadataHolder bindingMetadataHolder,
                                                            BindingServiceProperties bindingServiceProperties) {
        return args -> {
            bindingMetadataHolder.getBindingMetadatas().stream().filter(m -> StringUtils.isNotBlank(m.getGroup()))
                    .forEach(m -> {
                        String bindingName = m.getBindingName();
                        BindingProperties bindingProperties = bindingServiceProperties.getBindingProperties(bindingName);
                        if (isRabbitBinder(bindingServiceProperties, bindingProperties.getBinder())) {
                            ProducerProperties producerProperties = bindingServiceProperties.getProducerProperties(bindingName);
                            if (JudgeUtils.isBlankAll(producerProperties.getRequiredGroups())) {
                                producerProperties.setRequiredGroups(m.getGroup());
                            }
                            bindingProperties.setProducer(producerProperties);
                            bindingServiceProperties.getBindings().put(bindingName, bindingProperties);
                        }
                    });
        };

    }

    private boolean isRabbitBinder(BindingServiceProperties bindingServiceProperties, String binderName) {
        return Optional.ofNullable(bindingServiceProperties.getBinders()).map(m -> m.get(binderName)).map(b
                -> StringUtils.equals(BINDER_RABBIT, b.getType())).orElse(Boolean.FALSE);
    }

    @Bean
    public ExtendedProducerPropertiesChangingListener extendedProducerPropertiesChangingListener(BindingMetadataHolder bindingMetadataHolder) {
        return new ExtendedProducerPropertiesChangingListener(bindingMetadataHolder);
    }

}
