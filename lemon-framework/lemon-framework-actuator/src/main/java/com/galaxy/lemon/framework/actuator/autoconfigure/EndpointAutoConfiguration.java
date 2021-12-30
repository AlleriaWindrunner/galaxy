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

package com.galaxy.lemon.framework.actuator.autoconfigure;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.OrderUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.actuator.endpoint.OfflineEndpoint;
import com.galaxy.lemon.framework.actuator.endpoint.StatusEndpoint;
import com.galaxy.lemon.framework.offline.OfflineListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class EndpointAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(EndpointAutoConfiguration.class);

    private List<OfflineListener> offlineListeners;

    public EndpointAutoConfiguration(ObjectProvider<List<OfflineListener>> offlineListeners) {
        List<OfflineListener> offlineListenerList = offlineListeners.getIfAvailable();
        if (JudgeUtils.isNotEmpty(offlineListenerList)) {
            this.offlineListeners = OrderUtils.sortByOrder(offlineListenerList);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Offline listeners {}.", Optional.ofNullable(this.offlineListeners).map(s -> s.stream().map(StringUtils::toString).collect(Collectors.joining(LemonConstants.COMMA))).orElse(" none"));
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public OfflineEndpoint offlineEndpoint() {
        return new OfflineEndpoint(this.offlineListeners);
    }

    @Bean
    @ConditionalOnMissingBean
    public StatusEndpoint statusEndpoint() {
        return new StatusEndpoint();
    }
}
