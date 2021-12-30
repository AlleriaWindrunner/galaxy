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

package com.galaxy.lemon.framework.valuable;

import com.galaxy.lemon.common.Holder;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.utils.JudgeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ValuableHelper {
    private static final Logger logger = LoggerFactory.getLogger(ValuableHelper.class);

    private static final Holder<List<Class<?>>> VALUABLE_ENUM_CLASSES = new Holder<>();
    private static final Holder<List<Class<?>>> VALUABLE_CLASSES = new Holder<>();

    static {
        ClassPathValuableScanner classPathValuableScanner = new ClassPathValuableScanner();
        classPathValuableScanner.scan("");
        List<Class<?>> valuableClasses = classPathValuableScanner.getValuableClasses();
        VALUABLE_CLASSES.set(valuableClasses);
        List<Class<?>> valuableEnums = Optional.ofNullable(valuableClasses).map(cs -> cs.stream().filter(Class::isEnum).collect(Collectors.toList())).orElse(null);
        VALUABLE_ENUM_CLASSES.set(valuableEnums);
        if (logger.isInfoEnabled()) {
            logger.info("Found Valuable classes {}", Optional.ofNullable(getValuableClasses()).filter(JudgeUtils::isNotEmpty).map(s -> s.stream().map(String::valueOf).collect(Collectors.joining(LemonConstants.COMMA))).orElse("none"));
            logger.info("Found Valuable enum classes {}", Optional.ofNullable(getValuableEnumClasses()).filter(JudgeUtils::isNotEmpty).map(s -> s.stream().map(String::valueOf).collect(Collectors.joining(LemonConstants.COMMA))).orElse("none"));
        }
    }

    /**
     * 获取所有Valuable enum 类
     * @return
     */
    public static List<Class<?>> getValuableEnumClasses() {
        return VALUABLE_ENUM_CLASSES.get();
    }

    /**
     * 获取所有Valuable 类
     * @return
     */
    public static List<Class<?>> getValuableClasses() {
        return VALUABLE_CLASSES.get();
    }

}
