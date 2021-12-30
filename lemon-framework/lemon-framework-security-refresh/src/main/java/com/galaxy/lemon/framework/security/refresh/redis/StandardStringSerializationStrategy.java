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

package com.galaxy.lemon.framework.security.refresh.redis;

import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class StandardStringSerializationStrategy extends BaseRedisTokenStoreSerializationStrategy {

    private static final StringRedisSerializer STRING_SERIALIZER = new StringRedisSerializer();

    @Override
    protected String deserializeStringInternal(byte[] bytes) {
        return STRING_SERIALIZER.deserialize(bytes);
    }

    @Override
    protected byte[] serializeInternal(String string) {
        return STRING_SERIALIZER.serialize(string);
    }

}
