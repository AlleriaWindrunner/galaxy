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

package com.galaxy.lemon.common.codec;

import java.io.Writer;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface ObjectEncoder {
    /**
     * write value as string
     * @param object
     * @return
     * @throws CodecException
     */
    String writeValueAsString(Object object) throws CodecException;

    /**
     * write value as bytes
     * @param object
     * @return
     * @throws CodecException
     */
    byte[] writeValueAsBytes(Object object) throws CodecException;

    /**
     * write value to Writer
     * @param writer
     * @param value
     * @throws CodecException
     */
    void writeValue(Writer writer, Object value) throws CodecException;
}
