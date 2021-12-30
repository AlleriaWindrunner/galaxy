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

package com.galaxy.lemon.framework.data;

import com.galaxy.lemon.common.utils.BeanUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.framework.annotation.NestQueryBody;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Optional;

/**
 * DTO 基础类
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class BaseDTO<T> extends BaseLemonData {

    /**
     * 消息体
     */
    @Valid
    @ApiModelProperty(value = "业务传输对象; 非嵌套业务传输对象，忽略该属性")
    @NestQueryBody
    private T body;


    /**
     * 创建BaseDTO的子类，并将other对象属性拷贝到BaseDTO子类
     *
     * @param clazz
     * @param other
     * @return
     */
    public static <T extends BaseDTO<NoBody>, O> T newChildInstance(Class<T> clazz, O other) {
        T dto = ReflectionUtils.newInstance(clazz);
        Optional.ofNullable(other).ifPresent(o -> BeanUtils.copyProperties(dto, o));
        return dto;
    }

    /**
     * 创建BaseDTO的子类,并设置Body对象
     *
     * @param clazz
     * @param body
     * @param <T>
     * @param <B>
     * @return
     */
    public static <T extends BaseDTO<B>, B> T newChildInstanceWithBody(Class<T> clazz, B body) {
        T dto = ReflectionUtils.newInstance(clazz);
        dto.setBody(body);
        return dto;
    }


    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
