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

package com.galaxy.lemon.gateway.autoconfigure.feign;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.framework.utils.LemonUtils;
import com.galaxy.lemon.framework.utils.WebUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
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
public class GatewayFeignAutoConfiguration {
    @Bean
    public RequestInterceptor httpHeaderRequestInterceptor() {
        return template -> {
            template.header(LemonConstants.HTTP_HEADER_REQUEST_ID, LemonUtils.getRequestId());
            template.header(LemonConstants.HTTP_HEADER_SOURCE, LemonUtils.getApplicationName());
            template.header(LemonConstants.HTTP_HEADER_LOCALE, LemonUtils.getLocale().toString());
            template.header(LemonConstants.HTTP_HEADER_TOKEN, LemonUtils.getToken());
            template.header(LemonConstants.HTTP_HEADER_CLIENT_IP, LemonUtils.getClientIp());
            copyOriginalHeader(template, LemonConstants.HTTP_HEADER_SECURE);
        };
    }

    public void copyOriginalHeader(RequestTemplate restTemplate, String headerName) {
        Optional.ofNullable(WebUtils.getHttpServletRequest()).map(r -> r.getHeader(headerName)).ifPresent(v -> restTemplate.header(headerName, v));
    }
}
