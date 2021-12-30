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

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.ConfigurableBeanName;
import com.galaxy.lemon.common.Holder;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.framework.data.instantiator.AggregatedDataInstantiator;
import com.galaxy.lemon.framework.data.support.RelaxedPropertyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 仅限平台框架层使用
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public final class InternalDataHelper {
    private static final Logger logger = LoggerFactory.getLogger(InternalDataHelper.class);

    public static final String FRAMEWORK_BASE_PACKAGE = "com.galaxy.lemon";

    private Holder<PropertyWrapper> genericDTOPropertyWrapper = new Holder<>();
    private Holder<PropertyWrapper> lemonDataPropertyWrapper = new Holder<>();
    private Set<CopyPropertyValueWrapper> copyPropertyValueWrapperSet = new HashSet<>();
    private Holder<LemonDataCopier> lemonDataCopier = new Holder<>();

    public InternalDataHelper(AggregatedDataInstantiator aggregatedDataInstantiator) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[2];
        if (!stackTraceElement.getClassName().startsWith(FRAMEWORK_BASE_PACKAGE)) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "No privilege instance object \"InternalDataHelper\".");
        }
        parseClass(aggregatedDataInstantiator);
    }

    private void parseClass(AggregatedDataInstantiator aggregatedDataInstantiator) {
        this.lemonDataPropertyWrapper.set(new RelaxedPropertyWrapper(aggregatedDataInstantiator.newInstanceLemonData().getClass()));
        List<String> lemonDataPropertyNameList = Arrays.asList(this.getLemonDataPropertyWrapper().getProperties());
        List<String> baseDtoPropertyNameList = Arrays.asList(new RelaxedPropertyWrapper(BaseDTO.class).getProperties());
        CopyPropertyValueWrapper genericDTOCopyPropertyValueWrapper = parseCopyPropertyValueWrapper(aggregatedDataInstantiator.newInstanceGenericDTO().getClass(), lemonDataPropertyNameList, baseDtoPropertyNameList);
        this.copyPropertyValueWrapperSet.add(genericDTOCopyPropertyValueWrapper);
        this.copyPropertyValueWrapperSet.add(parseCopyPropertyValueWrapper(aggregatedDataInstantiator.newInstanceResponseDTO().getClass(), lemonDataPropertyNameList, baseDtoPropertyNameList));
        this.copyPropertyValueWrapperSet.add(parseCopyPropertyValueWrapper(aggregatedDataInstantiator.newInstanceCommandDTO().getClass(), lemonDataPropertyNameList, baseDtoPropertyNameList));
        this.genericDTOPropertyWrapper.set(genericDTOCopyPropertyValueWrapper.getPropertyWrapper());
        this.lemonDataCopier.set(parseLemonDataCopier(this.lemonDataPropertyWrapper.get(), lemonDataPropertyNameList, baseDtoPropertyNameList));
    }

    /**
     * parse the class ${@code type} to data processing
     * @param type
     * @param requiredPropertyNames
     * @param nonRequiredPropertyNames
     * @return
     */
    private CopyPropertyValueWrapper parseCopyPropertyValueWrapper(Class<? extends BaseDTO> type, List<String> requiredPropertyNames, List<String> nonRequiredPropertyNames) {
        PropertyWrapper propertyWrapper = new RelaxedPropertyWrapper(type);
        List<String> requiredCopyPropertyNameList = Stream.of(propertyWrapper.getProperties()).filter(p -> !nonRequiredPropertyNames.contains(p)).filter(p -> requiredPropertyNames.contains(p)).collect(Collectors.toList());
        String[] requiredCopyPropertyNames = requiredCopyPropertyNameList.toArray(new String[requiredCopyPropertyNameList.size()]);
        if (logger.isInfoEnabled()) {
            logger.info("Founding the DTO properties [{}] for DTO \"{}\", which required synchronize with \"LemonData\" context.", Stream.of(requiredCopyPropertyNames).collect(Collectors.joining(",")), type);
        }
        return new CopyPropertyValueWrapper(propertyWrapper, requiredCopyPropertyNames);
    }

    private LemonDataCopier parseLemonDataCopier(PropertyWrapper propertyWrapper, List<String> requiredPropertyNames, List<String> nonRequiredPropertyNames) {
        List<String> requiredCopyPropertyNameList = Stream.of(propertyWrapper.getProperties()).filter(p -> !nonRequiredPropertyNames.contains(p)).filter(p -> requiredPropertyNames.contains(p)).collect(Collectors.toList());
        String[] requiredCopyPropertyNames = requiredCopyPropertyNameList.toArray(new String[requiredCopyPropertyNameList.size()]);
        if (logger.isInfoEnabled()) {
            logger.info("Founding the LemonData properties [{}] for LemonData \"{}\".", Stream.of(requiredCopyPropertyNames).collect(Collectors.joining(",")), propertyWrapper.getWrappedClass());
        }
        return new LemonDataCopier(propertyWrapper, requiredCopyPropertyNames);
    }

    /**
     * 将lemondata ==》 dto
     * @param lemonData
     * @param dto
     */
    public <D extends BaseDTO, L extends BaseLemonData> void copyLemonDataToDTO(L lemonData, D dto) {
        dto.setRequestId(lemonData.getRequestId());
        dto.setMsgId(lemonData.getMsgId());
        dto.setStartDateTime(lemonData.getStartDateTime());
        dto.setUserId(lemonData.getUserId());
        dto.setClientIp(lemonData.getClientIp());
        dto.setLocale(lemonData.getLocale());
        dto.setBusiness(lemonData.getBusiness());
        dto.setUri(lemonData.getUri());
        dto.setToken(lemonData.getToken());
        dto.setLoginName(lemonData.getLoginName());
        dto.setEntryTx(lemonData.getEntryTx());
        dto.setVersionId(lemonData.getVersionId());
        copyLemonDataToDTOExtension(lemonData, dto);
    }

    private void copyLemonDataToDTOExtension(BaseLemonData lemonData, BaseDTO<?> baseDTO) {
        this.copyPropertyValueWrapperSet.stream().filter(c -> c.match(baseDTO.getClass())).forEach(c -> c.copyLemonDataToDTO(lemonData, baseDTO));
    }

    /**
     * genericDTO --> lemonData
     * @param dto
     * @param lemonData
     */
    public <D extends BaseDTO, L extends BaseLemonData> void copyDTOToLemonData(D dto, L lemonData) {
        lemonData.setRequestId(dto.getRequestId());
        lemonData.setMsgId(dto.getMsgId());
        lemonData.setStartDateTime(dto.getStartDateTime());
        lemonData.setUserId(dto.getUserId());
        lemonData.setClientIp(dto.getClientIp());
        lemonData.setLocale(dto.getLocale());
        lemonData.setBusiness(dto.getBusiness());
        lemonData.setUri(dto.getUri());
        lemonData.setToken(dto.getToken());
        lemonData.setLoginName(dto.getLoginName());
        lemonData.setEntryTx(dto.getEntryTx());
        lemonData.setVersionId(dto.getVersionId());
        copyDTOToLemonDataExtension(dto, lemonData);
    }

    private void copyDTOToLemonDataExtension(BaseDTO<?> baseDTO, BaseLemonData lemonData) {
        this.copyPropertyValueWrapperSet.stream().filter(c -> c.match(baseDTO.getClass())).forEach(c -> c.copyDTOToLemonData(baseDTO, lemonData));
    }

    /**
     * copy lemon data ${@code source} to lemon data ${@code dest}
     * @param source
     * @param dest
     * @param <L> lemonData
     */
    public <L extends BaseLemonData> void copeLemonData(L source, L dest) {
        dest.setRequestId(source.getRequestId());
        dest.setMsgId(source.getMsgId());
        dest.setStartDateTime(source.getStartDateTime());
        dest.setUserId(source.getUserId());
        dest.setClientIp(source.getClientIp());
        dest.setLocale(source.getLocale());
        dest.setBusiness(source.getBusiness());
        dest.setUri(source.getUri());
        dest.setToken(source.getToken());
        dest.setLoginName(source.getLoginName());
        dest.setEntryTx(source.getEntryTx());
        dest.setVersionId(source.getVersionId());
        this.lemonDataCopier.get().copyLemonData(source, dest);
    }

    /**
     * 清洗响应数据
     * clean response DTO
     * @param dto
     * @return
     */
    public <D extends BaseDTO> void cleanResponseDTO(D dto) {
        dto.setUserId(null);
        dto.setLoginName(null);
        dto.setToken(null);
    }

    /**
     *
     * @param baseDTO
     * @param requestId
     */
    public void setRequestId(BaseDTO<?> baseDTO, String requestId) {
        baseDTO.setRequestId(requestId);
    }

    public void setRequestId(BaseLemonData lemonData, String requestId) {
        lemonData.setRequestId(requestId);
    }

    /**
     * LemonData PropertyWrapper
     * @return
     */
    public PropertyWrapper getLemonDataPropertyWrapper() {
        return this.lemonDataPropertyWrapper.get();
    }

    /**
     * GenericDTO PropertyWrapper
     * @return
     */
    public PropertyWrapper getGenericDTOPropertyWrapper() {
        return this.genericDTOPropertyWrapper.get();
    }

    private class CopyPropertyValueWrapper {
        private Class<?> type;
        private PropertyWrapper propertyWrapper;
        private String[] requiredCopyPropertyNames;

        CopyPropertyValueWrapper(PropertyWrapper propertyWrapper, String[] requiredCopyPropertyNames) {
            this.type = propertyWrapper.getWrappedClass();
            this.propertyWrapper = propertyWrapper;
            this.requiredCopyPropertyNames = requiredCopyPropertyNames;
        }

        public void copyDTOToLemonData(BaseDTO<?> baseDTO, BaseLemonData lemonData) {
            Stream.of(requiredCopyPropertyNames).forEach(p -> Optional.ofNullable(propertyWrapper.getValue(baseDTO, p)).ifPresent(v -> getLemonDataPropertyWrapper().setValue(lemonData, v, p)));
        }

        public void copyLemonDataToDTO(BaseLemonData lemonData, BaseDTO<?> baseDTO) {
            Stream.of(requiredCopyPropertyNames).forEach(p -> Optional.ofNullable(getLemonDataPropertyWrapper().getValue(lemonData, p)).ifPresent(v -> this.getPropertyWrapper().setValue(baseDTO, v, p)));
        }

        public boolean match(Class<?> dtoClass) {
            if (dtoClass == type) {
                return true;
            }
            if (AlertCapable.class.isAssignableFrom(dtoClass) && !AlertCapable.class.isAssignableFrom(this.type)) {
                return false;
            }
            if (ConfigurableBeanName.class.isAssignableFrom(dtoClass) && ! ConfigurableBeanName.class.isAssignableFrom(this.type)) {
                return false;
            }
            return this.type.isAssignableFrom(dtoClass);
        }

        public PropertyWrapper getPropertyWrapper() {
            return this.propertyWrapper;
        }
    }

    private static class LemonDataCopier {
        private PropertyWrapper propertyWrapper;
        private String[] requiredCopyPropertyNames;

        public LemonDataCopier(PropertyWrapper propertyWrapper,
                               String[] requiredCopyPropertyNames) {
            this.propertyWrapper = propertyWrapper;
            this.requiredCopyPropertyNames = requiredCopyPropertyNames;
        }

        public void copyLemonData(BaseLemonData sourceLemonData, BaseLemonData destLemonData) {
            Stream.of(this.getRequiredCopyPropertyNames()).forEach(p ->
                    Optional.ofNullable(this.getPropertyWrapper().getValue(sourceLemonData, p)).ifPresent(v -> this.getPropertyWrapper().setValue(destLemonData, v, p)));
        }

        public PropertyWrapper getPropertyWrapper() {
            return propertyWrapper;
        }

        public String[] getRequiredCopyPropertyNames() {
            return requiredCopyPropertyNames;
        }
    }

}
