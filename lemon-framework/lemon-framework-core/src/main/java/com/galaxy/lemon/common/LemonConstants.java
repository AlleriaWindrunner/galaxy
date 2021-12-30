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

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface LemonConstants {
    String COMPANY_BASE_PACKAGE = "com.galaxy";
    String FRAMEWORK_BASE_PACKAGE = "com.galaxy.lemon";

    /**
     * 以下头信息是传送给Controller
     */
    String HTTP_HEADER_CLIENT_IP = "x-lemon-clientip";
    String HTTP_HEADER_LOCALE = "x-lemon-locale";
    String HTTP_HEADER_USER_ID = "x-lemon-usrid";
    String HTTP_HEADER_REQUEST_ID = "x-lemon-reqid";
    String HTTP_HEADER_FOR = "X-Forwarded-For";
    String HTTP_HEADER_SOURCE = "x-lemon-source";
    String HTTP_HEADER_URI = "x-lemon-uri";
    String HTTP_HEADER_BUSINESS = "x-lemon-business";
    String HTTP_HEADER_TOKEN = "x-auth-token";
    String HTTP_HEADER_LOGIN_NAME = "x-lemon-loginnm";

    /**
     * 请求没有BaseDTO类型时，用head 传送DTO信息
     */
    String HTTP_HEADER_DTO = "x-lemon-dto";
    String HTTP_HEADER_DTO_ENCODE = "x-lemon-dtoencode";
    String HTTP_HEADER_SECURE = "x-lemon-secure";
    String HTTP_HEADER_SESSION_ID_STRATEGY = "x-lemon-session";
    String HTTP_HEADER_VERSIONID = "VERSIONID";

    String COOKIE_SESSION_ID = "sid";

    String PROFILES_ACTIVE = "spring.profiles.active";
    String LEMON_HOME = "lemon.home";
    String APPLICATION_NAME = "spring.application.name";
    String LOGGING_PATH = "lemon.log.path";
    String NODE_NAME = "lemon.node.name";
    String MSG_CD_PREFIX = "lemon.alerting.prefix";
    String SHOW_SQL = "lemon.sql.showsql";
    String SQL_LEVEL = "lemon.sql.level";

    String DEFAULT_APPLICATION_NAME = "LME";
    String DEFAULT_CHARSET = "UTF-8";
    String SUCCESS_MSG_CD_SUFFIX = "00000";

    String CACHE_KEY_PREFIX = "CACHE:";
    String MIRROR_QUEUE_NAME_PREFIX = "mirror.";

    String BASE_DTO_PROPERTY_BODY = "body";

    /**
     * 通用常量
     */
    String PROPERTY_IGNORE_START_CHAR = "#";
    String PROPERTY_KEY_VALUE_SEPARATOR = "=";
    String LEFT_PARENTHESIS = "(";
    String RIGHT_PARENTHESIS = ")";
    String COMMA = ",";
    String DOT = ".";
    String TRUE = "true";
    String FALSE = "false";

    /**
     * 空字符串
     */
    String EMPTY_STRING = "";

}
