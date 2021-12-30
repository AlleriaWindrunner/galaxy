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

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;

/**
 * 扫描class文件
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ClassPathClassFileScanner {
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final String PATH_SEPARATOR = "/";
    private static final String EMPTY_STRING = "";
    
    private Environment environment = new StandardEnvironment();
    private ResourcePatternResolver resourcePatternResolver;
    private MetadataReaderFactory metadataReaderFactory;
    private String resourcePattern = DEFAULT_RESOURCE_PATTERN;
    
    public ClassPathClassFileScanner() {
        this(null);
    }

    public ClassPathClassFileScanner(ResourceLoader resourceLoader) {
        setResourceLoader(resourceLoader);
    }
    
    private void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }
    
    public void scan(ScannerCallback callback, String... basePackages) {
        doScan(callback, basePackages);
    }
    
    private void doScan(ScannerCallback callback, String... basePackages) {
        for (String basePackage : basePackages) {
            scanAndCallback(callback, basePackage);
        }
    }

    private void scanAndCallback(ScannerCallback callback, String basePackage) {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + this.resourcePattern;
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    callback.callback(this.metadataReaderFactory.getMetadataReader(resource), this.metadataReaderFactory);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O failure during class file scanning on classpath.", e);
        }
        
    }

    private String resolveBasePackage(String basePackage) {
        if(StringUtils.isBlank(basePackage)) return EMPTY_STRING;
        String resourcePath = ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
        return StringUtils.endsWith(resourcePath, PATH_SEPARATOR) ? resourcePath : resourcePath + PATH_SEPARATOR;
    }
}
