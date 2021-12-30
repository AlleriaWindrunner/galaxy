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

package com.galaxy.lemon.framework.config;

import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.env.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.galaxy.lemon.common.LemonConstants.FALSE;
import static com.galaxy.lemon.common.LemonConstants.TRUE;

/**
 * lemon default configuration
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor {
    public static final Logger logger = LoggerFactory.getLogger(DefaultPropertiesEnvironmentPostProcessor.class);

    public static final String DEFAULT_PROPERTIES = "defaultProperties";
    public static final String LEMON_DEFAULT_CONFIG_ENABLED = "lemon.config.default.enabled";
    public static final String LEMON_DEFAULT_CONFIG_LOCATION = "lemon.config.default.location";
    public static final String LEMON_DEFAULT_CONFIG_DEFAULT_LOCATION = "classpath:/META-INF/";
    public static final String LEMON_DEFAULT_CONFIG_FILE_NAME = "lemon.yml";
    public static final String LEMON_EXTENSION_CONFIG_FILE_NAME = "extension.yml";
    public static final String LEMON_DEFAULT_CONFIG_LOADED_MARK = "lemon.config.default.loaded";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        if (requiredLoad(environment)) {
            ResourceLoader resourceLoader = Optional.ofNullable(application.getResourceLoader()).orElseGet(() -> new DefaultResourceLoader());
            PropertySourcesLoader propertySourcesLoader = new PropertySourcesLoader();

            String location = Optional.ofNullable(environment.getProperty(LEMON_DEFAULT_CONFIG_LOCATION)).filter(StringUtils::isNotBlank).orElse(LEMON_DEFAULT_CONFIG_DEFAULT_LOCATION);
            String lemonConfigFile = (StringUtils.endsWith(location, "/") ? location : location + "/") + LEMON_DEFAULT_CONFIG_FILE_NAME;
            loadResource(lemonConfigFile, resourceLoader, propertySourcesLoader);

            String extensionConfigFile = (StringUtils.endsWith(location, "/") ? location : location + "/") + LEMON_EXTENSION_CONFIG_FILE_NAME;
            loadResource(extensionConfigFile, resourceLoader, propertySourcesLoader);

            MutablePropertySources propertySources = propertySourcesLoader.getPropertySources();
            for (Iterator<PropertySource<?>> iterator = propertySources.iterator(); iterator.hasNext();) {
                MapPropertySource propertySource = (MapPropertySource) iterator.next();
                addDefaultProperty(environment, propertySource);
            }
            markLoaded(environment);
        }
    }

    private void loadResource(String location, ResourceLoader resourceLoader, PropertySourcesLoader propertySourcesLoader) {
        Resource resource = resourceLoader.getResource(location);
        if (resource.exists()) {
            try {
                logger.debug("loading default config {}", location);
                propertySourcesLoader.load(resource);
            } catch (IOException e) {
                LemonException.throwLemonException(e);
            }
        }
    }

    private void addDefaultProperty(ConfigurableEnvironment environment, MapPropertySource propertySource) {
        MutablePropertySources sources = environment.getPropertySources();
        Map<String, Object> map = null;
        if (sources.contains(DEFAULT_PROPERTIES)) {
            PropertySource<?> source = sources.get(DEFAULT_PROPERTIES);
            if (source instanceof MapPropertySource) {
                map = ((MapPropertySource) source).getSource();
            }
        }
        else {
            map = new LinkedHashMap<>();
            sources.addLast(new MapPropertySource(DEFAULT_PROPERTIES, map));
        }
        if (map != null) {
            map.putAll(propertySource.getSource());
        }
    }

    private boolean requiredLoad(Environment environment) {
        return TRUE.equals(environment.getProperty(LEMON_DEFAULT_CONFIG_ENABLED, TRUE))
                && FALSE.equals(environment.getProperty(LEMON_DEFAULT_CONFIG_LOADED_MARK, FALSE));
    }

    private void markLoaded(ConfigurableEnvironment environment) {
        MutablePropertySources sources = environment.getPropertySources();
        Map<String, Object> map = null;
        if (sources.contains(DEFAULT_PROPERTIES)) {
            PropertySource<?> source = sources.get(DEFAULT_PROPERTIES);
            if (source instanceof MapPropertySource) {
                map = ((MapPropertySource) source).getSource();
            }
        }
        if (null != map) {
            map.put(LEMON_DEFAULT_CONFIG_LOADED_MARK, TRUE);
        }
    }
}
