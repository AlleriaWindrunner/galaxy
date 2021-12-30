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

package com.galaxy.lemon.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Optional;

/**
 * IO 相关工具类
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class IOUtils extends org.apache.commons.io.IOUtils{
    public static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * InputStreamReader 转 String
     * @param reader
     * @return
     * @throws IOException
     */
    public static String toString(Reader reader) throws IOException {
        StringBuilder sb = null;
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        while((line = br.readLine()) != null) {
            if(sb == null) {
                sb = new StringBuilder(line);
            } else {
                sb.append(ResourceUtils.ENTER_NEW_LINE).append(line);
            }
        }
        return Optional.ofNullable(sb).map(sbb -> sbb.toString()).orElse(null);
    }

    public static String toStringIgnoreException(InputStream input) {
        return toStringIgnoreException(input, DEFAULT_CHARSET);
    }

    public static String toStringIgnoreException(InputStream input, String encoding) {
        try {
            return toString(input, encoding);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     *
     * @param inputStream
     * @return
     */
    public static boolean available(InputStream inputStream) throws IOException {
        return null != inputStream && inputStream.available() > 0;
    }
}
