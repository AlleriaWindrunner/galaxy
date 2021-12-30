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

package com.galaxy.lemon.framework.data.support;

import com.galaxy.lemon.common.codec.CodecException;
import com.galaxy.lemon.common.codec.ObjectCodec;
import com.galaxy.lemon.framework.data.*;
import com.galaxy.lemon.framework.data.instantiator.GenericDTOInstantiator;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DTOMessageConverterSupport<D extends BaseDTO, L extends BaseLemonData> implements DTOMessageConverter<D> {
    private ObjectCodec objectCodec;
    private GenericDTOInstantiator<D> genericDTOInstantiator;
    private InternalDataHelper internalDataHelper;

    public DTOMessageConverterSupport(ObjectCodec objectCodec,
                                      GenericDTOInstantiator<D> genericDTOInstantiator,
                                      InternalDataHelper internalDataHelper) {
        this.objectCodec = objectCodec;
        this.genericDTOInstantiator = genericDTOInstantiator;
        this.internalDataHelper = internalDataHelper;
    }

    @Override
    public String toMessage() throws CodecException {
        L lemonData = (L) LemonDataHolder.getLemonData();
        D dto = this.genericDTOInstantiator.newInstanceGenericDTO();
        this.internalDataHelper.copyLemonDataToDTO(lemonData, dto);
        return this.objectCodec.writeValueAsString(dto);
    }

    @Override
    public D fromMessage(String message) throws CodecException {
        return (D) this.objectCodec.readValue(message, this.internalDataHelper.getGenericDTOPropertyWrapper().getWrappedClass());
    }
}
