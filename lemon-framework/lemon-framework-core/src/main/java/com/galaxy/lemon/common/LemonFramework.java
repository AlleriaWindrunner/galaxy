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

package com.galaxy.lemon.common;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.NetUtils;
import com.galaxy.lemon.common.utils.ResourceUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * lemon framework runner
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonFramework {
    private static final Logger logger = LoggerFactory.getLogger(LemonFramework.class);
    
    public static final String BATCH_ENV = "lemon.batch.enabled";
    public static final String REGISTER_WITH_EUREKA = "registerWithEureka";
    public static final Holder<String> scanPackage = new Holder<>();

    /**
     * @param clazz
     * @param args
     */
    public static void run(Class<?> clazz, String[] args) {
        scanPackage.set(clazz.getPackage().getName());
        configureApplicationProperty(args);
        showProperties(args);
        SpringApplication.run(clazz, args);
    }

    /**
     * 
     * @param clazz
     * @param args
     *
     * @deprecated as of Lemon 2.0.0, in favor of using {@link #run}
     */
    @Deprecated
    public static void processApplicationArgs(Class<?> clazz, String[] args) {
        scanPackage.set(clazz.getPackage().getName());
        configureApplicationProperty(args);
        showProperties(args);
        SpringApplication.run(clazz, args);
    }
    
    /**
     * 处理lemon 平台参数
     * @param args
     */
    private static void configureApplicationProperty(String[] args) {
        //lemon home
        configureLemonHome();
        //batch env
        configureBatch();
        //profiles
        configureProfile(args);
        //lemon logging path
        configureLog();
        //lemon node name
        configureNodeName();
    }

    private static void configureLemonHome() {
        if (JudgeUtils.isBlank(System.getProperty(LemonConstants.LEMON_HOME))) {
            if (logger.isWarnEnabled()) {
                logger.warn("Env variable \"lemon.home\" was not defined.");
            }
        }
    }

    private static void configureBatch() {
        System.setProperty(REGISTER_WITH_EUREKA, isBatchEnv() ? LemonConstants.FALSE : LemonConstants.TRUE);
    }

    private static void configureLog() {
        if(JudgeUtils.isBlank(System.getProperty(LemonConstants.LOGGING_PATH))) {
            System.setProperty(LemonConstants.LOGGING_PATH, "../logs");
        }
        //show sql,lemon.sql.level
        if (JudgeUtils.isNotBlank(System.getProperty(LemonConstants.SHOW_SQL))
                && LemonConstants.TRUE.equalsIgnoreCase(System.getProperty(LemonConstants.SHOW_SQL))) {
            System.setProperty(LemonConstants.SQL_LEVEL, "DEBUG");
        }
    }

    private static void configureNodeName() {
        if (JudgeUtils.isBlank(System.getProperty(LemonConstants.NODE_NAME))) {
            String hostName = NetUtils.getLocalHostName();
            int maxNodeNameLength = 8;
            if (JudgeUtils.isNotEmpty(hostName)) {
                if (hostName.length() > maxNodeNameLength) {
                    hostName = hostName.substring(0, maxNodeNameLength + 1);
                }
                System.setProperty(LemonConstants.NODE_NAME, hostName );
            }
        }
    }

    private static void configureProfile(String[] args) {
        if (JudgeUtils.isNotBlank(System.getProperty(LemonConstants.PROFILES_ACTIVE))) {
            return;
        }
        String envFromArgs = Optional.ofNullable(args).map(s -> Stream.of(s).filter(a -> StringUtils.startsWith(a, "--spring.profiles.active=")).findFirst().orElse(null)).orElse(null);
        String profilesActive = Optional.ofNullable(envFromArgs).map(e -> e.split("=")[1]).orElse(Env.getProfile(Env.getDefaultEnv()));
        System.setProperty(LemonConstants.PROFILES_ACTIVE, profilesActive);
        if (logger.isInfoEnabled()) {
            logger.info("Current env and active profile is {}.", profilesActive);
            if (StringUtils.isBlank(envFromArgs)) {
                logger.info("Could not found env variable with key {} from command line , using default env.", LemonConstants.PROFILES_ACTIVE);
            }
        }
    }

    private static void showProperties(String[] args) {
        if(logger.isInfoEnabled()) {
            StringBuilder showProperties = new StringBuilder();
            //show command line properties
            if(args != null) {
                Stream.of(args).parallel().forEach(arg -> showProperties.append(arg).append(ResourceUtils.ENTER_NEW_LINE));
                logger.info("Show command line arguments: {} {}",ResourceUtils.ENTER_NEW_LINE, showProperties.toString());
                showProperties.delete(0, showProperties.length());
            }

            //show system properties
            System.getProperties().stringPropertyNames().forEach(key -> showProperties.append(key).append(" : ")
                    .append(System.getProperty(key)).append(ResourceUtils.ENTER_NEW_LINE));
            logger.info("Show system properties: {} {}", ResourceUtils.ENTER_NEW_LINE, showProperties.toString());
            showProperties.delete(0, showProperties.length());
        }
    }

    /**
     *
     * @return 默认扫描路径
     */
    public static String getScanPackage() {
        return scanPackage.get();
    }

    /**
     * 是否为batch环境
     * @return
     */
    public static boolean isBatchEnv() {
        return JudgeUtils.equalsIgnoreCase(System.getProperty(BATCH_ENV), LemonConstants.TRUE);
    }
}
