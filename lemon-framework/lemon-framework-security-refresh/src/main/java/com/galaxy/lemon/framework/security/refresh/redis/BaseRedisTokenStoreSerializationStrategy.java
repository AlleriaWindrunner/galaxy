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

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class BaseRedisTokenStoreSerializationStrategy implements
        RedisTokenStoreSerializationStrategy {

    private static final byte[] EMPTY_ARRAY = new byte[0];

    private static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (isEmpty(bytes)) {
            return null;
        }
        return deserializeInternal(bytes, clazz);
    }

    protected abstract <T> T deserializeInternal(byte[] bytes, Class<T> clazz);

    @Override
    public String deserializeString(byte[] bytes) {
        if (isEmpty(bytes)) {
            return null;
        }
        return deserializeStringInternal(bytes);
    }

    protected abstract String deserializeStringInternal(byte[] bytes);

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return EMPTY_ARRAY;
        }
        return serializeInternal(object);
    }

    protected abstract byte[] serializeInternal(Object object);

    @Override
    public byte[] serialize(String data) {
        if (data == null) {
            return EMPTY_ARRAY;
        }
        return serializeInternal(data);
    }

    protected abstract byte[] serializeInternal(String data);


}
