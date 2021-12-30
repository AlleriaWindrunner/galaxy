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

package com.galaxy.lemon.framework.autoconfigure.xss;

import com.galaxy.lemon.framework.xss.filter.XssFilter;
import com.galaxy.lemon.framework.xss.jackson.XssStringDeserializer;
import com.galaxy.lemon.framework.xss.resolver.EncodingXssResolver;
import com.galaxy.lemon.framework.xss.resolver.HtmlEscapeXssResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.web.HttpEncodingProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ObjectMapper.class, Jackson2ObjectMapperBuilder.class, XssStringDeserializer.class})
public class XssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EncodingXssResolver htmlEscapeXssResolver() {
        return new HtmlEscapeXssResolver();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer xssObjectMapperBuilderCustomizer(EncodingXssResolver xssResolver) {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.deserializers(new XssStringDeserializer(xssResolver));
        } ;
    }

    @Configuration
    @ConditionalOnClass({ObjectMapper.class, Jackson2ObjectMapperBuilder.class, XssStringDeserializer.class})
    public static class XssFilterConfiguration {

        @Bean
        public FilterRegistrationBean xssFilterRegistration(EncodingXssResolver xssResolver, HttpEncodingProperties httpEncodingProperties) {
            FilterRegistrationBean registration = new FilterRegistrationBean();
            registration.setFilter(new XssFilter(xssResolver, httpEncodingProperties.getCharset().displayName()));
            registration.addUrlPatterns("/*");
            registration.setName("xssFilter");
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
            return registration;
        }

    }

}
