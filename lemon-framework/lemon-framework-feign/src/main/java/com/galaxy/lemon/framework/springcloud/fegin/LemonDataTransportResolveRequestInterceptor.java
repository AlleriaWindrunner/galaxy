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

package com.galaxy.lemon.framework.springcloud.fegin;

import com.galaxy.lemon.common.HttpMethod;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.Encodes;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.data.LemonDataMessageConverter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonDataTransportResolveRequestInterceptor<L extends BaseLemonData> implements RequestInterceptor {
    private static final String ENCODE_VALUE = Util.UTF_8.name();
    private LemonDataMessageConverter<L> lemonDataMessageConverter;

    public LemonDataTransportResolveRequestInterceptor(LemonDataMessageConverter<L> lemonDataMessageConverter) {
        this.lemonDataMessageConverter = lemonDataMessageConverter;
    }

    @Override
    public void apply(RequestTemplate template) {
        if (HttpMethod.GET.toString().equalsIgnoreCase(template.method())) {
            template.header(LemonConstants.HTTP_HEADER_DTO, urlEncode(toMessage()));
            template.header(LemonConstants.HTTP_HEADER_DTO_ENCODE, getEncoding());
        }
    }

    private String toMessage() {
        return this.lemonDataMessageConverter.toMessage((L) LemonDataHolder.getLemonData());
    }


    private String urlEncode(String arg) {
        try {
            return Encodes.urlEncode(arg, getEncoding());
        } catch (UnsupportedEncodingException e) {
            throw LemonException.create(e);
        }
    }

    private String getEncoding() {
        return ENCODE_VALUE;
    }
}
