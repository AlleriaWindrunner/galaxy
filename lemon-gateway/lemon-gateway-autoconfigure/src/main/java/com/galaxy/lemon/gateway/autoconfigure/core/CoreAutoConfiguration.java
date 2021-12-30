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

package com.galaxy.lemon.gateway.autoconfigure.core;

import com.galaxy.lemon.common.codec.ObjectEncoder;
import com.galaxy.lemon.common.log.AccessLogger;
import com.galaxy.lemon.common.log.FormattingAccessLogger;
import com.galaxy.lemon.common.log.logback.MDCUtil;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.alerting.AlertingResolver;
import com.galaxy.lemon.framework.alerting.MessageResourceSourceAlertingResolver;
import com.galaxy.lemon.framework.config.LemonEnvironment;
import com.galaxy.lemon.framework.data.LemonDataInitializer;
import com.galaxy.lemon.framework.data.instantiator.AggregatedDataInstantiator;
import com.galaxy.lemon.framework.i18n.LocaleMessageSource;
import com.galaxy.lemon.framework.jackson.message.DefaultResponseIgnorePredicate;
import com.galaxy.lemon.framework.jackson.message.ResponseIgnorePredicate;
import com.galaxy.lemon.framework.utils.WebUtils;
import com.galaxy.lemon.framework.web.filter.TradeContextCustomizer;
import com.galaxy.lemon.gateway.core.GatewayHelper;
import com.galaxy.lemon.gateway.core.log.GatewayAccessLoggerAdapter;
import com.galaxy.lemon.gateway.core.log.SimpleGatewayAccessLoggerAdapter;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class CoreAutoConfiguration {

    @Bean
    public LocaleResolver localeResolver(LemonEnvironment lemonEnvironment) {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(lemonEnvironment.getDefaultLocale());
        List<Locale> supports = lemonEnvironment.getSupportLocales();
        if(JudgeUtils.isNotEmpty(supports)) {
            localeResolver.setSupportedLocales(supports);
        }
        return localeResolver;
    }

    @Bean
    @ConditionalOnMissingBean(name = "lemonDataInitializer")
    public LemonDataInitializer lemonDataInitializer(AggregatedDataInstantiator aggregatedDataInstantiator) {
        return new DefaultGatewayLemonDataInitializer(aggregatedDataInstantiator);
    }

    @Bean
    @ConditionalOnMissingBean(name = "gatewayAccessLogger")
    public AccessLogger gatewayAccessLogger(ObjectEncoder objectEncoder) {
        return new FormattingAccessLogger(objectEncoder, LoggerFactory.getLogger("com.galaxy.lemon.gateway.access"));
    }

    @Bean
    @ConditionalOnMissingBean(name = "simpleGatewayAccessLoggerAdapter")
    public GatewayAccessLoggerAdapter simpleGatewayAccessLoggerAdapter(AccessLogger gatewayAccessLogger) {
        return new SimpleGatewayAccessLoggerAdapter(gatewayAccessLogger);
    }

    @Bean
    @ConditionalOnProperty(prefix = "lemon.alerting", name = "resolver", havingValue = "messageResource", matchIfMissing = true)
    public AlertingResolver alertingResolver(LocaleMessageSource localeMessageSource) {
        return new MessageResourceSourceAlertingResolver(localeMessageSource);
    }

    @Bean
    public TradeContextCustomizer tradeContextCustomizer(LemonDataInitializer lemonDataInitializer, LocaleResolver localeResolver) {
        return request -> {
            MDCUtil.putMDCKey(WebUtils.resolveRequestId(request));
            GatewayHelper.setLemonContextLocaleResolver(localeResolver);
            lemonDataInitializer.initialLemonData();
        };
    }

    /**
     * Force Channel Response ignore
     * @return
     */
    @Bean
    public ResponseIgnorePredicate responseIgnorePredicate() {
        return new DefaultResponseIgnorePredicate(true);
    }

}
