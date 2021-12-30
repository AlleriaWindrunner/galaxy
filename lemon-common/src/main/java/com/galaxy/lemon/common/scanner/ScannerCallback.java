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

package com.galaxy.lemon.common.scanner;

import com.galaxy.lemon.common.utils.StringUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.stream.Stream;

/**
 * 扫描到类名回调
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@FunctionalInterface
public interface ScannerCallback {
    String[] IGNORE_START_PACKAGES = {"java.", "javax.", "org.springframework", "sun.", "org.mybatis"};

    void callback(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory);

    default String getClassName(MetadataReader metadataReader) {
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        return annotationMetadata.getClassName();
    }

    static boolean isIgnoredClass(String className) {
        return StringUtils.isBlank(className) || Stream.of(IGNORE_START_PACKAGES).anyMatch(p -> StringUtils.startsWith(className, p));
    }
}
