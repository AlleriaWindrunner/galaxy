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

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.config.LemonProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 当前系统环境变量及配置文件
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonEnvironment implements EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(LemonEnvironment.class);

    public static final String DEFAULT_APPLICATION_NAME = LemonConstants.DEFAULT_APPLICATION_NAME;
    public static final Locale DEFAULT_LOCALE = Locale.CHINESE;
    public static final Locale[] DEFAULT_SUPPORTED_LOCALES = {Locale.CHINESE, Locale.ENGLISH};
    public static final String[] DEFAULT_GATEWAYS = new String[]{"AGW","IGW"};
    public static final String PROPERTY_KEY_APPLICATION_NAME = LemonConstants.APPLICATION_NAME;
    
    private Environment environment;
    private String applicationName;
    private LemonProperties lemonProperties;

    public LemonEnvironment(LemonProperties lemonProperties) {
        this.lemonProperties = lemonProperties;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public String getProperty(String key) {
        return this.environment.getProperty(key);
    }
    
    public <T> T getProperty(String key, Class<T> targetType) {
        return this.environment.getProperty(key, targetType);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    /**
     * 系统默认的Locale
     * @return
     */
    public Locale getDefaultLocale() {
        return this.lemonProperties.getLocale().getDefaultLocale();
    }
    
    public List<Locale> getSupportLocales() {
        return this.lemonProperties.getLocale().getSupportLocales();
    }

    public LemonProperties getLemonProperties() {
        return this.lemonProperties;
    }

    @PostConstruct
    public void postConstruct() {
        String applicationNameTemp = this.environment.getProperty(PROPERTY_KEY_APPLICATION_NAME);
        if (JudgeUtils.isBlank(applicationNameTemp)) {
            applicationNameTemp = DEFAULT_APPLICATION_NAME;
            System.setProperty(PROPERTY_KEY_APPLICATION_NAME, applicationNameTemp);
            if (logger.isInfoEnabled()) {
                logger.info("No application name was found. set default application name \"{}\".", DEFAULT_APPLICATION_NAME);
            }
        }
        this.applicationName = applicationNameTemp;

        if(JudgeUtils.isEmpty(this.getLemonProperties().getGateways())) {
            this.getLemonProperties().setGateways(DEFAULT_GATEWAYS);
            if(logger.isInfoEnabled()) {
                logger.info("use default gateways {}, you can modify configuration property \"lemon.gateways\" to change it.", Arrays.toString(DEFAULT_GATEWAYS));
            }
        }
        initLocaleIfNecessary();

        if(logger.isInfoEnabled()) {
            logger.info("application name is {}.", this.applicationName);
            logger.info("default locale is {}.", this.lemonProperties.getLocale().getDefaultLocale());
            logger.info("support locales are {}", this.lemonProperties.getLocale().getSupportLocales());
            logger.info("support gateways are {}", Arrays.toString(this.getLemonProperties().getGateways()));
        }
    }

    private void initLocaleIfNecessary() {
        if(JudgeUtils.isNull(this.getLemonProperties().getLocale())) {
            LemonProperties.LocaleProperties localeConfig = new LemonProperties.LocaleProperties();
            localeConfig.setDefaultLocale(DEFAULT_LOCALE);
            localeConfig.setSupportLocales(Arrays.asList(DEFAULT_SUPPORTED_LOCALES));
            this.lemonProperties.setLocale(localeConfig);
            if(logger.isInfoEnabled()) {
                logger.info("use default locale {}，you can modify configuration property \"lemon.locale.defaultLocale\" to change it.", DEFAULT_LOCALE);
                logger.info("use default support locales {}，you can modify configuration property \"lemon.locale.supportLocales\" to change it.", Arrays.toString(DEFAULT_SUPPORTED_LOCALES));
            }
        }
        if(JudgeUtils.isNull(this.getLemonProperties().getLocale().getDefaultLocale())) {
            this.getLemonProperties().getLocale().setDefaultLocale(DEFAULT_LOCALE);
            if(logger.isInfoEnabled()) {
                logger.info("use default locale {}，you can modify configuration property \"lemon.locale.defaultLocale\" to change it.", DEFAULT_LOCALE);
            }
        }
        if(JudgeUtils.isNull(this.getLemonProperties().getLocale().getSupportLocales())) {
            this.getLemonProperties().getLocale().setSupportLocales(Arrays.asList(DEFAULT_SUPPORTED_LOCALES));
            if(logger.isInfoEnabled()) {
                logger.info("use default support locales {}，you can modify configuration property \"lemon.locale.supportLocales\" to change it.", Arrays.toString(DEFAULT_SUPPORTED_LOCALES));
            }
        }
    }
    
}
