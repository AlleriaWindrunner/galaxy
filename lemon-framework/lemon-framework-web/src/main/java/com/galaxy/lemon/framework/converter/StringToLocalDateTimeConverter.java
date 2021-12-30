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

package com.galaxy.lemon.framework.converter;

import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;

import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;

/**
 * String 转 LocalDateTime
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
    private static final int DATE_TIME_LENGTH = 14;
    
    @Override
    public LocalDateTime convert(String source) {
        if (JudgeUtils.isBlank(source)) {
            return null;
        }
        source = StringUtils.trim(source);
        if(StringUtils.length(source) != DATE_TIME_LENGTH) {
            throw new LemonException(LemonException.SYS_ERROR_MSGCD, "Date time string length must equals 14.");
        }
        return DateTimeUtils.parseLocalDateTime(source);
    }

}
