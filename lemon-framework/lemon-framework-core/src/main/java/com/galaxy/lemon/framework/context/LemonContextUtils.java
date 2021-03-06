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
     * ??????????????????????????????
     * @param txName
     */
    public static void setCurrentTxName(String txName) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_TX_NAME, txName);
    }

    /**
     * ??????????????????
     * @return
     */
    public static String getCurrentTxName() {
        return LemonContext.getCurrentContext().getString(LEMON_CONTEXT_TX_NAME);
    }


    /**
     * ??????????????????alerting
     */
    public static void setRequiredProcessAlerting() {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_REQUIRED_PROCESS_ALERTING, Boolean.valueOf(true));
    }

    /**
     * ??????????????????alerting
     * @return
     */
    public static boolean requiredProcessAlerting() {
        return LemonContext.getCurrentContext().getBoolean(LEMON_CONTEXT_REQUIRED_PROCESS_ALERTING, false);
    }

    /**
     * ????????????????????????????????????
     */
    public static void setTradeStartTime() {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_TRADE_STARTING_TIME, DateTimeUtils.getCurrentLocalDateTime());
    }

    /**
     * ????????????????????????????????????
     * @return
     */
    public static LocalDateTime getTradeStartTime() {
        return LemonContext.getCurrentContext().getLocalDataTime(LEMON_CONTEXT_TRADE_STARTING_TIME);
    }

    /**
     * ????????????????????????????????????????????????
     */
    public static void setRemoteInvocationStartTime() {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_REMOTE_INVOCATION_START_TIME, DateTimeUtils.getCurrentLocalDateTime());
    }

    /**
     * ????????????????????????????????????????????????
     * @return
     */
    public static LocalDateTime getRemoteInvocationStartTime() {
        return LemonContext.getCurrentContext().getLocalDataTime(LEMON_CONTEXT_REMOTE_INVOCATION_START_TIME);
    }

    /**
     * ????????????????????????????????????????????????
     */
    public static void clearRemoteInvocationStartTime() {
        LemonContext.getCurrentContext().remove(LEMON_CONTEXT_REMOTE_INVOCATION_START_TIME);
    }

    /**
     * ????????????????????????alerting ??????
     * @param alertParameters
     */
    public static void setAlertParameters(String... alertParameters) {
        setRequiredProcessAlerting();
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_ALERT_PARAMETERS, alertParameters);
    }

    /**
     * ???????????????????????? alerting parameters
     * @return
     */
    public static String[] getAlertParameters() {
        Object alertParameters =  LemonContext.getCurrentContext().get(LEMON_CONTEXT_ALERT_PARAMETERS);
        return Optional.ofNullable(alertParameters).map(a -> (String[]) a).orElse(null);
    }

    /**
     * ????????????????????????Response DTO class
     * @param responseDTOType
     */
    public static void setResponseDTOType(Class<?> responseDTOType) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_RESPONSE_DTO_TYPE, responseDTOType);
    }

    /**
     * ????????????????????????Response DTO class
     * @return
     */
    public static Class<?> getResponseDTOType() {
        return Optional.ofNullable(LemonContext.getCurrentContext().get(LEMON_CONTEXT_RESPONSE_DTO_TYPE)).map(t -> (Class) t).orElse(null);
    }

    /**
     * ???????????????????????????????????????
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
     * ???????????????????????????????????????
     * @return
     */
    public static Keywords getKeywords() {
        return Optional.ofNullable(LemonContext.getCurrentContext().get(LEMON_CONTEXT_LOGGING_KEYWORDS)).map(Keywords.class::cast).orElse(null);
    }

    /**
     * ??????????????????????????????
     */
    public static void setNotRequiredLogKeywords() {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_REQUIRED_LOGGING_KEYWORDS, Boolean.FALSE);
    }

    /**
     * ???????????????????????????
     * @return
     */
    public static boolean requiredLogKeywords() {
        return LemonContext.getCurrentContext().getBoolean(LEMON_CONTEXT_REQUIRED_LOGGING_KEYWORDS, Boolean.TRUE);
    }


    /**
     * ???????????????????????????????????????
     * @param keywords
     */
    public static void setKeywords(Keywords keywords) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_LOGGING_KEYWORDS, keywords);
    }

    /**
     * ??????????????????????????????
     * @param concurrent
     */
    public static void setAccessConcurrent(int concurrent) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_ACCESS_CONCURRENT, concurrent);
    }

    /**
     * ??????????????????????????????
     * @return
     */
    public static int getAccessConcurrent() {
        return LemonContext.getCurrentContext().getInteger(LEMON_CONTEXT_ACCESS_CONCURRENT);
    }

    /**
     * ?????????????????????????????????
     * @param concurrent
     */
    public static void setRemoteInvocationConcurrent(int concurrent) {
        LemonContext.getCurrentContext().put(LEMON_CONTEXT_REMOTE_INVOCATION_CONCURRENT, concurrent);
    }

    /**
     * ?????????????????????????????????
     * @return
     */
    public static int getRemoteInvocationConcurrent() {
        return LemonContext.getCurrentContext().getInteger(LEMON_CONTEXT_REMOTE_INVOCATION_CONCURRENT);
    }
}
