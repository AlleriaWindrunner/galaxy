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

package com.galaxy.lemon.framework.web.filter;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.data.LemonDataInitializer;
import com.galaxy.lemon.framework.data.LemonDataMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonDataInitializerFilter extends OncePerRequestFilter {

    private LemonDataMessageConverter lemonDataMessageConverter;
    private LemonDataInitializer lemonDataInitializer;

    public LemonDataInitializerFilter(LemonDataMessageConverter lemonDataMessageConverter,
                                      LemonDataInitializer lemonDataInitializer) {
        this.lemonDataMessageConverter = lemonDataMessageConverter;
        this.lemonDataInitializer = lemonDataInitializer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String lemonDataMessage = request.getHeader(LemonConstants.HTTP_HEADER_DTO);
            if (StringUtils.isEmpty(lemonDataMessage)) {
                this.lemonDataInitializer.initialLemonData();
            } else {
                BaseLemonData lemonData = this.lemonDataMessageConverter.fromMessage(lemonDataMessage);
                LemonDataHolder.setLemonData(lemonData);
            }
            filterChain.doFilter(request, response);
        } finally {
            response.setHeader(LemonConstants.HTTP_HEADER_DTO, this.lemonDataMessageConverter.toMessage(LemonDataHolder.getLemonData()));
        }
    }
}
