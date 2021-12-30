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

package com.galaxy.lemon.framework.autoconfigure.signature;

import com.galaxy.lemon.framework.signature.SignatureVerifier;
import com.galaxy.lemon.framework.signature.filter.SignatureFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({SignatureFilter.class, SignatureVerifier.class})
public class SignatureAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SignatureAutoConfiguration.class);

    @Configuration
    @ConditionalOnBean(IndicateSignatureFilterUrlPattern.class)
    public static class SignatureFilterConfiguration {

        private SignatureVerifier signatureVerifier;
        private String[] signatureVerifierFilterUrlPatterns;

        public SignatureFilterConfiguration(SignatureVerifier signatureVerifier,
                                            ObjectProvider<List<IndicateSignatureFilterUrlPattern>> signatureVerifierFilterUrlPatterns) {
            this.signatureVerifier = signatureVerifier;
            if(this.signatureVerifier == null && logger.isWarnEnabled()) {
                logger.warn("Bean \"signatureVerifier\" do not defined in spring application.");
            }
            List<String> urlList = new ArrayList();
            Optional.ofNullable(signatureVerifierFilterUrlPatterns.getIfAvailable()).ifPresent(s ->
                    s.stream().map(IndicateSignatureFilterUrlPattern::getUrlPatterns).forEach(urlList::addAll));
            this.signatureVerifierFilterUrlPatterns = urlList.toArray(new String[urlList.size()]);
        }

        @Bean
        @ConditionalOnBean(SignatureVerifier.class)
        public SignatureFilter signatureFilter() {
            SignatureVerifier delegationSignatureVerifier = new SignatureVerifier() {
                public boolean shouldVerify(SignatureDataSource signatureDataSource) {
                    return true;
                }
                public boolean verify(SignatureDataSource signatureDataSource) {
                    return signatureVerifier.verify(signatureDataSource);
                }
            };

            return new SignatureFilter(delegationSignatureVerifier);
        }

        @Bean
        @ConditionalOnBean(SignatureFilter.class)
        public FilterRegistrationBean signatureFilterRegistration() {
            FilterRegistrationBean registration = new FilterRegistrationBean();
            registration.setFilter(signatureFilter());
            registration.addUrlPatterns(this.signatureVerifierFilterUrlPatterns);
            registration.setName("signatureFilter");
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
            return registration;
        }
    }
}
