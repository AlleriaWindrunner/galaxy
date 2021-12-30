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

package com.galaxy.lemon.framework.context;

import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.common.log.Keywords;
import com.galaxy.lemon.common.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonContextUtils {

    private static final String LEMON_CONTEXT_TX_NAME = "LC_TX_NAME";
    private static final String LEMON_CONTEXT_REQUIRED_PROCESS_ALERTING = "LC_REQUIRED_PROCESS_ALERTING";
    private static final String LEMON_CONTEXT_TRADE_STARTING_TIME = "LC_TRADE_STARTING_TIME";
    private static final String LEMON_CONTEXT_REMOTE_INVOCATION_START_TIME = "LC_REMOTE_INVOCATION_START_TIME";
    private static final String LEMON_CONTEXT_ALERT_PARAMETERS = "LC_ALERT_PARAMETERS";
    private static final String LEMON_CONTEXT_RESPONSE_DTO_TYPE = "LC_RESPONSE_DTO_TYPE";
    private static final String LEMON_CONTEXT_LOGGING_KEYWORDS = "LC_LOGGING_KEYWORDS";
    private static final String LEMON_CONTEXT_REQUIRED_LOGGING_KEYWORDS = "LC_REQUIRED_LOGGING_KEYWORDS";
    private static final String LEMON_CONTEXT_ACCESS_CONCURRENT = "LC_ACCESS_CONCURRENT";
    private static final String LEMON_CONTEXT_REMOTE_INVOCATION_CONCURRENT = "LC_REMOTE_INVOCATION_CONCURRENT";

    /**
     * 设置交易名称到上下文
     * @param txName
     */
    public static void setCurrentTxName(String txName) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_TX_NAME, txName);
    }

    /**
     * 获取交易名称
     * @return
     */
    public static String getCurrentTxName() {
        return LemonContext.getCurrentContext().getString(LEMON_CONTEXT_TX_NAME);
    }


    /**
     * 设置需要处理alerting
     */
    public static void setRequiredProcessAlerting() {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_REQUIRED_PROCESS_ALERTING, Boolean.valueOf(true));
    }

    /**
     * 是否需要处理alerting
     * @return
     */
    public static boolean requiredProcessAlerting() {
        return LemonContext.getCurrentContext().getBoolean(LEMON_CONTEXT_REQUIRED_PROCESS_ALERTING, false);
    }

    /**
     * 设置当前交易开始执行时间
     */
    public static void setTradeStartTime() {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_TRADE_STARTING_TIME, DateTimeUtils.getCurrentLocalDateTime());
    }

    /**
     * 获取当前交易开始执行时间
     * @return
     */
    public static LocalDateTime getTradeStartTime() {
        return LemonContext.getCurrentContext().getLocalDataTime(LEMON_CONTEXT_TRADE_STARTING_TIME);
    }

    /**
     * 设置当前上下文的远程调用开始时间
     */
    public static void setRemoteInvocationStartTime() {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_REMOTE_INVOCATION_START_TIME, DateTimeUtils.getCurrentLocalDateTime());
    }

    /**
     * 获取当前上下文的远程调用开始时间
     * @return
     */
    public static LocalDateTime getRemoteInvocationStartTime() {
        return LemonContext.getCurrentContext().getLocalDataTime(LEMON_CONTEXT_REMOTE_INVOCATION_START_TIME);
    }

    /**
     * 清除当前上下文的远程调用开始时间
     */
    public static void clearRemoteInvocationStartTime() {
        LemonContext.getCurrentContext().remove(LEMON_CONTEXT_REMOTE_INVOCATION_START_TIME);
    }

    /**
     * 设置当前上下文的alerting 参数
     * @param alertParameters
     */
    public static void setAlertParameters(String... alertParameters) {
        setRequiredProcessAlerting();
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_ALERT_PARAMETERS, alertParameters);
    }

    /**
     * 获取当前上下文的 alerting parameters
     * @return
     */
    public static String[] getAlertParameters() {
        Object alertParameters =  LemonContext.getCurrentContext().get(LEMON_CONTEXT_ALERT_PARAMETERS);
        return Optional.ofNullable(alertParameters).map(a -> (String[]) a).orElse(null);
    }

    /**
     * 设置当前上下文的Response DTO class
     * @param responseDTOType
     */
    public static void setResponseDTOType(Class<?> responseDTOType) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_RESPONSE_DTO_TYPE, responseDTOType);
    }

    /**
     * 获取当前上下文的Response DTO class
     * @return
     */
    public static Class<?> getResponseDTOType() {
        return Optional.ofNullable(LemonContext.getCurrentContext().get(LEMON_CONTEXT_RESPONSE_DTO_TYPE)).map(t -> (Class) t).orElse(null);
    }

    /**
     * 设置当前上下文的日志关键字
     * @param strs
     */
    public static void addKeywords(String... strs) {
        Keywords keywords;
        Object keywordsObject = LemonContext.getCurrentContext().get(LEMON_CONTEXT_LOGGING_KEYWORDS);
        if (null == keywordsObject) {
            keywords = new Keywords();
            LemonContext.getCurrentContext().put(LEMON_CONTEXT_LOGGING_KEYWORDS, keywords);
        } else {
            keywords = (Keywords) keywordsObject;
        }
        keywords.append(strs);
    }

    /**
     * 获取当前上下文的日志关键值
     * @return
     */
    public static Keywords getKeywords() {
        return Optional.ofNullable(LemonContext.getCurrentContext().get(LEMON_CONTEXT_LOGGING_KEYWORDS)).map(Keywords.class::cast).orElse(null);
    }

    /**
     * 设置不需要日志关键字
     */
    public static void setNotRequiredLogKeywords() {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_REQUIRED_LOGGING_KEYWORDS, Boolean.FALSE);
    }

    /**
     * 是否需要日志关键字
     * @return
     */
    public static boolean requiredLogKeywords() {
        return LemonContext.getCurrentContext().getBoolean(LEMON_CONTEXT_REQUIRED_LOGGING_KEYWORDS, Boolean.TRUE);
    }


    /**
     * 设置当前上下文的日志关键值
     * @param keywords
     */
    public static void setKeywords(Keywords keywords) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_LOGGING_KEYWORDS, keywords);
    }

    /**
     * 设置当前访问的并发量
     * @param concurrent
     */
    public static void setAccessConcurrent(int concurrent) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_ACCESS_CONCURRENT, concurrent);
    }

    /**
     * 获取当前访问的并发量
     * @return
     */
    public static int getAccessConcurrent() {
        return LemonContext.getCurrentContext().getInteger(LEMON_CONTEXT_ACCESS_CONCURRENT);
    }

    /**
     * 设置当前远程请求并发量
     * @param concurrent
     */
    public static void setRemoteInvocationConcurrent(int concurrent) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_REMOTE_INVOCATION_CONCURRENT, concurrent);
    }

    /**
     * 获取当前远程请求并发量
     * @return
     */
    public static int getRemoteInvocationConcurrent() {
        return LemonContext.getCurrentContext().getInteger(LEMON_CONTEXT_REMOTE_INVOCATION_CONCURRENT);
    }
}
