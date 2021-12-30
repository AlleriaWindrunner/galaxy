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

package com.galaxy.lemon.framework.autoconfigure.gray;

import com.galaxy.lemon.framework.gray.GrayZoneAvoidanceRule;
import com.galaxy.lemon.framework.gray.configuration.GrayConfiguration;
import com.galaxy.lemon.framework.gray.constant.GrayConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static com.galaxy.lemon.framework.gray.constant.GrayConstants.*;

import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(GrayZoneAvoidanceRule.class)
@ConditionalOnProperty(value = "lemon.gray.enabled", matchIfMissing = true)
@RibbonClients(defaultConfiguration = GrayConfiguration.class)
@Import(GrayStreamConfiguration.class)
public class GrayAutoConfiguration {

    @Configuration
    @ConditionalOnClass(EurekaInstanceConfigBean.class)
    static class EurekaConditionalProperty {
        @Bean
        public EurekaPropertyModifierBeanPostProcessor eurekaPropertyModifierBeanPostProcessor() {
            return new EurekaPropertyModifierBeanPostProcessor();
        }
    }

    @Configuration
    @ConditionalOnClass(BindingServiceProperties.class)
    static class BindingConditionalProperty {
        @Bean
        public BindingPropertyModifierBeanPostProcessor bindingPropertyModifierBeanPostProcessor() {
            return new BindingPropertyModifierBeanPostProcessor();
        }
    }

    static class EurekaPropertyModifierBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof EurekaInstanceConfigBean) {
                EurekaInstanceConfigBean eurekaInstanceConfigBean = (EurekaInstanceConfigBean) bean;
                String versionId = System.getenv(VERSION_ID);
                if (isGrayEnv(versionId)) {
                    //未配置
                    Map<String, String> metadataMap = eurekaInstanceConfigBean.getMetadataMap();
                    if (StringUtils.isEmpty(metadataMap.get(VERSION_ID))) {
                        metadataMap.put(VERSION_ID, versionId);
                    }
                }
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }

    static class BindingPropertyModifierBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof BindingServiceProperties) {
                BindingServiceProperties bindingServiceProperties = (BindingServiceProperties) bean;
                String versionId = System.getenv(VERSIONID);
                if (isGrayEnv(versionId)) {
                    bindingServiceProperties.getBindings().forEach((k, v) -> {
                        if (StringUtils.equals(k, INPUT)){
                            v.setDestination(v.getDestination() + HYPHEN + GRAY_TAG);
                        }
                    });
                }
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }

    /**
     * 判断当前环境是否为灰度环境
     *
     * @param versionId
     * @return
     */
    private static boolean isGrayEnv(String versionId) {
        return StringUtils.isNotBlank(versionId) && versionId.endsWith(GRAY_TAG);
    }
}
