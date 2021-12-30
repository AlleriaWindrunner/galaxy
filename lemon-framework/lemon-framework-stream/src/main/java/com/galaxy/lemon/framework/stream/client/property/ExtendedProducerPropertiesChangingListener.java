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

package com.galaxy.lemon.framework.stream.client.property;

import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.stream.client.BindingMetadataHolder;
import org.springframework.cloud.stream.binder.DefaultBinderFactory;
import org.springframework.cloud.stream.binder.rabbit.properties.RabbitBindingProperties;
import org.springframework.cloud.stream.binder.rabbit.properties.RabbitExtendedBindingProperties;
import org.springframework.cloud.stream.binder.rabbit.properties.RabbitProducerProperties;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ExtendedProducerPropertiesChangingListener implements DefaultBinderFactory.Listener {
    private BindingMetadataHolder bindingMetadataHolder;

    public ExtendedProducerPropertiesChangingListener(BindingMetadataHolder bindingMetadataHolder) {
        this.bindingMetadataHolder = bindingMetadataHolder;
    }

    @Override
    public void afterBinderContextInitialized(String configurationName, ConfigurableApplicationContext binderContext) {
        RabbitExtendedBindingProperties rabbitExtendedBindingProperties = binderContext.getBean(RabbitExtendedBindingProperties.class);
        bindingMetadataHolder.getBindingMetadatas().stream().filter(m -> StringUtils.isNoneBlank(m.getGroup(), m.getPrefix()))
                .forEach(m -> {
                    String bindingName = m.getBindingName();
                    RabbitProducerProperties rabbitProducerProperties = rabbitExtendedBindingProperties.getExtendedProducerProperties(bindingName);
                    if (StringUtils.isNotBlank(rabbitProducerProperties.getPrefix())) {
                        return;
                    }
                    rabbitProducerProperties.setPrefix(m.getPrefix());
                    RabbitBindingProperties rabbitBindingProperties = rabbitExtendedBindingProperties.getBindings().get(bindingName);
                    if (rabbitBindingProperties == null) {
                        rabbitBindingProperties = new RabbitBindingProperties();
                        rabbitExtendedBindingProperties.getBindings().put(bindingName, rabbitBindingProperties);
                    }
                    rabbitBindingProperties.setProducer(rabbitProducerProperties);
                });
    }
}
