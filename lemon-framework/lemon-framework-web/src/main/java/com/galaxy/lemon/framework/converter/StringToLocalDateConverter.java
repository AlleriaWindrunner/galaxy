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

import java.time.LocalDate;

import org.springframework.core.convert.converter.Converter;

import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;

/**
 * String 转 localDate
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    private static final int DATE_LENGTH = 8;
    @Override
    public LocalDate convert(String source) {
        if (JudgeUtils.isBlank(source)) {
            return null;
        }
        if(StringUtils.length(source) != DATE_LENGTH) {
            throw new LemonException(LemonException.SYS_ERROR_MSGCD, "Date string length must equals 8.");
        }
        source = StringUtils.trim(source);
        return DateTimeUtils.parseLocalDate(source);
    }

}
