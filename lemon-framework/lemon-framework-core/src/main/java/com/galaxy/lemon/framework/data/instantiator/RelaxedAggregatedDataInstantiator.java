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

package com.galaxy.lemon.framework.data.instantiator;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.ExposeBeanName;
import com.galaxy.lemon.common.Holder;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.ClassUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.framework.config.CoreProperties;
import com.galaxy.lemon.framework.data.*;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.DefaultCmdDTO;
import com.galaxy.lemon.framework.data.DefaultDTO;
import com.galaxy.lemon.framework.data.DefaultLemonData;

import java.util.Optional;

/**
 * 根据规则查找相关的类进行实例化
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RelaxedAggregatedDataInstantiator implements AggregatedDataInstantiator {
    public static final String CLASS_NAME_CMD_DTO = "com.galaxy.lemon.framework.data.GenericCmdDTO";
    public static final String CLASS_NAME_DTO = "com.galaxy.lemon.framework.data.GenericDTO";
    public static final String CLASS_NAME_LEMON_DATA = "com.galaxy.lemon.framework.data.LemonData";
    public static final String CLASS_NAME_RSP_DTO = "com.galaxy.lemon.framework.data.GenericRspDTO";

    private CoreProperties coreProperties;
    private Holder<Class<? extends BaseDTO>> genericDTOClassHolder = new Holder<>();
    private Holder<Class<? extends BaseDTO>> responseDTOClassHolder = new Holder<>();
    private Holder<Class<? extends BaseDTO>> commandDTOClassHolder = new Holder<>();
    private Holder<Class<? extends BaseLemonData>> lemonDataClassHolder = new Holder<>();

    public RelaxedAggregatedDataInstantiator(CoreProperties coreProperties) {
        this.coreProperties = coreProperties;
    }

    @Override
    public BaseDTO newInstanceCommandDTO() {
        if (null != commandDTOClassHolder.get()) {
            return ReflectionUtils.newInstance(commandDTOClassHolder.get());
        }

        Class<?> commandDtoClass = Optional.ofNullable(this.coreProperties.getCommandDTO()).map(d -> d.getClassName()).map(ReflectionUtils::forNameThrowRuntimeExceptionIfNecessary).orElseGet(() -> {
            if (ClassUtils.isPresent(CLASS_NAME_CMD_DTO, Thread.currentThread().getContextClassLoader())) {
                return (Class)ReflectionUtils.forNameThrowRuntimeExceptionIfNecessary(CLASS_NAME_CMD_DTO);
            } else {
                return (Class)DefaultCmdDTO.class;
            }
        });
        LemonException.throwLemonExceptionIfNecessary( !BaseDTO.class.isAssignableFrom(commandDtoClass) ||
                        !ExposeBeanName.class.isAssignableFrom(commandDtoClass), ErrorMsgCode.SYS_ERROR.getMsgCd(),
                "Class name of \"" + commandDtoClass.getName() + "\" must be assignable form \"BaseDTO\" and implements interface \"ExposeBeanName\".");
        commandDTOClassHolder.set((Class<? extends BaseDTO>)commandDtoClass);
        return ReflectionUtils.newInstance(commandDTOClassHolder.get());
    }

    @Override
    public BaseDTO newInstanceGenericDTO() {
        if (null != genericDTOClassHolder.get()) {
            return ReflectionUtils.newInstance(genericDTOClassHolder.get());
        }

        Class<?> genericDtoClass = Optional.ofNullable(this.coreProperties.getGenericDTO()).map(d -> d.getClassName()).map(ReflectionUtils::forNameThrowRuntimeExceptionIfNecessary).orElseGet(() -> {
            if (ClassUtils.isPresent(CLASS_NAME_DTO, Thread.currentThread().getContextClassLoader())) {
                return (Class)ReflectionUtils.forNameThrowRuntimeExceptionIfNecessary(CLASS_NAME_DTO);
            } else {
                return (Class)DefaultDTO.class;
            }
        });
        LemonException.throwLemonExceptionIfNecessary( !BaseDTO.class.isAssignableFrom(genericDtoClass), ErrorMsgCode.SYS_ERROR.getMsgCd(),
                "Class name of \"" + genericDtoClass.getName() + "\" must be assignable form \"BaseDTO\".");
        genericDTOClassHolder.set((Class<? extends BaseDTO>)genericDtoClass);
        return ReflectionUtils.newInstance(genericDTOClassHolder.get());
    }

    @Override
    public BaseLemonData newInstanceLemonData() {
        if (null != lemonDataClassHolder.get()) {
            return ReflectionUtils.newInstance(lemonDataClassHolder.get());
        }

        Class<?> dtoClassName = Optional.ofNullable(this.coreProperties.getLemonData()).map(d -> d.getClassName()).map(ReflectionUtils::forNameThrowRuntimeExceptionIfNecessary).orElseGet(() -> {
            if (ClassUtils.isPresent(CLASS_NAME_LEMON_DATA, Thread.currentThread().getContextClassLoader())) {
                return (Class)ReflectionUtils.forNameThrowRuntimeExceptionIfNecessary(CLASS_NAME_LEMON_DATA);
            } else {
                return (Class)DefaultLemonData.class;
            }
        });
        LemonException.throwLemonExceptionIfNecessary( !BaseLemonData.class.isAssignableFrom(dtoClassName), ErrorMsgCode.SYS_ERROR.getMsgCd(),
                "Class name of \"" + dtoClassName.getName() + "\" must be assignable form \"BaseDTO\".");
        lemonDataClassHolder.set((Class<? extends BaseLemonData>)dtoClassName);
        return ReflectionUtils.newInstance(lemonDataClassHolder.get());
    }

    @Override
    public BaseDTO newInstanceResponseDTO() {
        if (null != responseDTOClassHolder.get()) {
            return ReflectionUtils.newInstance(responseDTOClassHolder.get());
        }

        Class<?> rspDtoClass = Optional.ofNullable(this.coreProperties.getResponseDTO()).map(d -> d.getClassName()).map(ReflectionUtils::forNameThrowRuntimeExceptionIfNecessary).orElseGet(() -> {
            if (ClassUtils.isPresent(CLASS_NAME_RSP_DTO, Thread.currentThread().getContextClassLoader())) {
                return (Class)ReflectionUtils.forNameThrowRuntimeExceptionIfNecessary(CLASS_NAME_RSP_DTO);
            } else {
                return (Class)DefaultRspDTO.class;
            }
        });
        LemonException.throwLemonExceptionIfNecessary( !BaseDTO.class.isAssignableFrom(rspDtoClass) || !AlertCapable.class.isAssignableFrom(rspDtoClass), ErrorMsgCode.SYS_ERROR.getMsgCd(),
                "Class name of \"" + rspDtoClass.getName() + "\" must be assignable form \"BaseDTO\" and implements interface \"AlertCapable\".");
        responseDTOClassHolder.set((Class<? extends BaseDTO>)rspDtoClass);
        return ReflectionUtils.newInstance(responseDTOClassHolder.get());
    }
}
