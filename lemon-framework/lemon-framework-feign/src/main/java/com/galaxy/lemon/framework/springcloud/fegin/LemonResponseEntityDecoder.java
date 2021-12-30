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

import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.remoting.RemoteInvocationDataProcessor;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * lemon 扩展fegin decoder
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonResponseEntityDecoder extends ResponseEntityDecoder {
    private RemoteInvocationDataProcessor remoteInvocationDataProcessor;

    public LemonResponseEntityDecoder(Decoder decoder,
                                      RemoteInvocationDataProcessor remoteInvocationDataProcessor) {
        super(decoder);
        this.remoteInvocationDataProcessor = remoteInvocationDataProcessor;
    }
    
    @Override
    public Object decode(final Response response, Type type) throws IOException,
            FeignException {
        Object result =  super.decode(response, type);
        if(result != null && result instanceof BaseDTO) {
            BaseDTO<?> baseDTO = (BaseDTO<?>) result;
            this.remoteInvocationDataProcessor.processAfterInvocation(LemonDataHolder.getLemonData(), baseDTO);
        }
        return result;
    }
}
