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
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.LemonDataMessageConverter;
import com.galaxy.lemon.framework.data.instantiator.LemonDataInstantiator;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */


public class DefaultLemonDataMessageConverter<L extends BaseLemonData> implements LemonDataMessageConverter<L> {

    private Class<L> lemonDataClass;
    private ObjectCodec objectCodec;

    public DefaultLemonDataMessageConverter(LemonDataInstantiator<L> lemonDataInstantiator,
                                            ObjectCodec objectCodec) {
        this.lemonDataClass = (Class<L>)lemonDataInstantiator.newInstanceLemonData().getClass();
        this.objectCodec = objectCodec;
    }


    @Override
    public L fromMessage(String message) {
        try {
            return this.objectCodec.readValue(message, lemonDataClass);
        } catch (CodecException e) {
            throw LemonException.create(e);
        }
    }

    @Override
    public String toMessage(L lemonData) {
        try {
            return this.objectCodec.writeValueAsString(lemonData);
        } catch (CodecException e) {
            throw LemonException.create(e);
        }
    }
}
