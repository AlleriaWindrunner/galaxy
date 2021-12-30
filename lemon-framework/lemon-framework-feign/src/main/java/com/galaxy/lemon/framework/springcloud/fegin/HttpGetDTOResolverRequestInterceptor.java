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
import com.galaxy.lemon.common.codec.CodecException;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.DTOMessageConverter;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 没有DTO对象的GET方法采用Head 传送GenericDTO对象
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Deprecated
public class HttpGetDTOResolverRequestInterceptor<D extends BaseDTO> implements RequestInterceptor {

    private DTOMessageConverter<D> dtoMessageConverter;

    public HttpGetDTOResolverRequestInterceptor(DTOMessageConverter<D> dtoMessageConverter) {
        this.dtoMessageConverter = dtoMessageConverter;
    }
    @Override
    public void apply(RequestTemplate template) {
        if (HttpMethod.GET.toString().equalsIgnoreCase(template.method())) {
            try {
                template.header(LemonConstants.HTTP_HEADER_DTO, this.dtoMessageConverter.toMessage());
            } catch (CodecException e) {
                LemonException.throwLemonException(e);
            }
        }
    }
}
