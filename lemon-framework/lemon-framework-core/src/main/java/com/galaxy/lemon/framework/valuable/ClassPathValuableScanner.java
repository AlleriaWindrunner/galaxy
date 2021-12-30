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

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.LemonFramework;
import com.galaxy.lemon.common.scanner.ClassPathClassFileScanner;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ClassPathValuableScanner extends ClassPathClassFileScanner {
    private static final Logger logger = LoggerFactory.getLogger(ClassPathValuableScanner.class);

    private static final String VALUABLE_INTERFACE_NAME = "com.galaxy.lemon.framework.valuable.Valuable";

    private List<Class<?>> valuableClasses = new ArrayList<>();

    public void scan(String... basePackages) {
        scan((mr, mrf) -> doCallback(mr, mrf), resolveBasePackages(basePackages));
    }

    private void doCallback(MetadataReader mr, MetadataReaderFactory mrf) {
        if (isValuableClass(mr)) {
            try {
                Class<?> valuableClass = ReflectionUtils.forName(mr.getAnnotationMetadata().getClassName());
                valuableClasses.add(valuableClass);
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }
    }

    private boolean isValuableClass(MetadataReader mr) {
        String[] interfaceNames = mr.getAnnotationMetadata().getInterfaceNames();
        if (JudgeUtils.isEmpty(interfaceNames)) {
            return false;
        }
        return Stream.of(interfaceNames).anyMatch(s -> StringUtils.equals(s, VALUABLE_INTERFACE_NAME));
    }

    private String[] resolveBasePackages(String... basePackages) {
        String[] scanPackages = doResolveBasePackages(basePackages);
        if (logger.isInfoEnabled()) {
            logger.info("Scanning Valuable classes with packages {}", JudgeUtils.isEmpty(scanPackages) ? "ALL_OF_PATH" : scanPackages);
        }
        return scanPackages;
    }

    private String[] doResolveBasePackages(String... basePackages) {
        if (basePackages == null || basePackages.length == 0
                || (basePackages.length == 1 && StringUtils.isBlank(basePackages[0]))) {
            String scanPackage = LemonFramework.getScanPackage();
            if (StringUtils.isBlank(scanPackage)) {
                return new String[]{LemonConstants.EMPTY_STRING};
            }
            String[] scanPackageSplits = StringUtils.split(scanPackage, LemonConstants.DOT);
            if (scanPackageSplits.length > 2) {
                return new String[]{scanPackageSplits[0] + LemonConstants.DOT + scanPackageSplits[1]};
            }
            return new String[]{scanPackage};
        } else {
            return basePackages;
        }
    }

    public List<Class<?>> getValuableClasses() {
        return valuableClasses;
    }

}
