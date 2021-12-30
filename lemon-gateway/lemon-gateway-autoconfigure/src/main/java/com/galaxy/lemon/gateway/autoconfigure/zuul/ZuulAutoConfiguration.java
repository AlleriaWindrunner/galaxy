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

package com.galaxy.lemon.gateway.autoconfigure.zuul;

import com.galaxy.lemon.common.codec.ObjectDecoder;
import com.galaxy.lemon.common.log.AccessLogger;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.galaxy.lemon.framework.signature.Algorithm;
import com.galaxy.lemon.framework.signature.SignatureMetadataExtractor;
import com.galaxy.lemon.framework.signature.SignatureVerifier;
import com.galaxy.lemon.framework.signature.SignatureVerifier.SignatureDataSource;
import com.galaxy.lemon.gateway.core.log.GatewayAccessLoggerAdapter;
import com.galaxy.lemon.gateway.core.validation.DefaultInputDataValidator;
import com.galaxy.lemon.gateway.core.validation.InputDataValidator;
import com.galaxy.lemon.gateway.zuul.ZuulExtensionProperties;
import com.galaxy.lemon.gateway.zuul.ZuulFilterConfiguration;
import com.galaxy.lemon.gateway.zuul.ZuulGatewayAccessLoggerAdapter;
import com.galaxy.lemon.gateway.zuul.ZuulHelper;
import com.galaxy.lemon.gateway.zuul.hystrix.fallback.SimpleZuulFallbackProvider;
import com.galaxy.lemon.gateway.zuul.signature.DefaultZuulSignatureVerifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@EnableZuulProxy
@Import(ZuulFilterConfiguration.class)
@EnableConfigurationProperties({ ZuulExtensionProperties.class })
public class ZuulAutoConfiguration {
    
    @Configuration
    public static class ZuulHelperConfiguration {
        @Bean
        public ZuulHelper zuulHelper(ZuulExtensionProperties zuulExtensionProperties,
                                     ZuulProperties properties,
                                     RouteLocator routeLocator) {
            UrlPathHelper urlPathHelper = new UrlPathHelper();
            urlPathHelper.setRemoveSemicolonContent(properties.isRemoveSemicolonContent());
            return new ZuulHelper(urlPathHelper, routeLocator, zuulExtensionProperties);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SignatureMetadataExtractor<SignatureDataSource<HttpServletRequest>, String> defaultSignatureMetadataExtractor() {
        return index -> new SignatureMetadataExtractor.SignatureMetadata<>(Algorithm.MD5, "mock");
    }
    
    @Bean
    public SignatureVerifier zuulSignatureVerifier(ZuulHelper zuulHelper, SignatureMetadataExtractor<SignatureDataSource<HttpServletRequest>, String> signatureMetadataExtractor) {
        return new DefaultZuulSignatureVerifier(zuulHelper, signatureMetadataExtractor);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ZuulFallbackProvider simpleZuulFallbackProvider(ResponseMessageResolver responseMessageResolver) {
        return new SimpleZuulFallbackProvider(responseMessageResolver);
    }

    @Bean
    public GatewayAccessLoggerAdapter zuulGatewayAccessLoggerAdapter(AccessLogger gatewayAccessLogger, ZuulHelper zuulHelper) {
        return new ZuulGatewayAccessLoggerAdapter(gatewayAccessLogger, zuulHelper);
    }

    @Bean
    @ConditionalOnMissingBean
    public InputDataValidator inputDataValidator(ObjectDecoder objectDecoder) {
        return new DefaultInputDataValidator(objectDecoder);
    }
}
