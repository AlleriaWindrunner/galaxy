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

package com.galaxy.lemon.framework.utils;

import com.galaxy.lemon.common.Env;
import com.galaxy.lemon.common.Holder;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.LemonFramework;
import com.galaxy.lemon.common.extension.SpringExtensionLoader;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.config.LemonEnvironment;
import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.LemonDataHolder;

import java.util.Locale;
import java.util.Optional;

import static com.galaxy.lemon.framework.data.LemonDataHolder.getLemonData;

/**
 * Lemon 框架服务工具类
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonUtils {
    private static final String LEMON_HOME = LemonConstants.LEMON_HOME;
    private static final String CURRENT_ENV = LemonConstants.PROFILES_ACTIVE;
    private static final String BATCH_ENV = LemonFramework.BATCH_ENV;

    private static final String LEMON_ENVIRONMENT_BEAN_NAME = "lemonEnvironment";
    
    private static final Holder<LemonEnvironment> lemonEnvironment = new Holder<>();
    private static final Holder<String> successMsgCd = new Holder<>();
    
    /**
     * 应用名
     * @return
     */
    public static String getApplicationName() {
        return getLemonEnvironment().getApplicationName();
    }
    
    /**
     * 获取当前环境配置
     * @return
     */
    public static LemonEnvironment getLemonEnvironment() {
        if (lemonEnvironment.isEmpty()) {
            lemonEnvironment.set(SpringExtensionLoader.getSpringBean(LEMON_ENVIRONMENT_BEAN_NAME, LemonEnvironment.class));
        }
        return lemonEnvironment.get();
    }
    
    /**
     * 获取当前环境的属性值
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return getLemonEnvironment().getProperty(key);
    }
    
    /**
     * 获取当前环境的属性值
     * @param key
     * @param targetType
     * @return
     */
    public static <T> T getProperty(String key, Class<T> targetType) {
        return getLemonEnvironment().getProperty(key, targetType);
    }
    
    /**
     * 获取Home目录
     * @return
     */
    public static String getLemonHome() {
        return getProperty(LEMON_HOME);
    }
    
    /**
     * 获取当前环境
     * @return
     */
    public static Env getEnv() {
        return Env.valueOf(getProperty(CURRENT_ENV).toUpperCase());
    }
    
    /**
     * 批量环境
     * @return
     */
    public static boolean isBatchEnv() {
        return Boolean.valueOf(getProperty(BATCH_ENV));
    }
    
    public static String[] getGateways() {
        return getLemonEnvironment().getLemonProperties().getGateways();
    }
    
    public static String getNodeName() {
        return System.getProperty(LemonConstants.NODE_NAME, "");
    }
    /**
     * 获取流水号
     * @return
     */
    public static String getRequestId() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(d -> d.getRequestId()).orElse(null);
    }
    
    /**
     * 获取消息Id
     * @return
     */
    public static String getMsgId() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(d -> d.getMsgId()).orElse(null);
    }

    /**
     * 获取区域信息
     * @return
     */
    public static Locale getLocale() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(BaseLemonData::getLocale).orElseGet(() -> LemonUtils.getLemonEnvironment().getDefaultLocale());

    }
    
    /**
     * 获取登录用户ID
     * @return
     * @Deprecated as of lemon 1.1.1, get user id by business code
     */
    @Deprecated
    public static String getUserId() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(d -> d.getUserId()).orElse(null);
    }
    
    /**
     * 客户端IP
     * @return
     */
    public static String getClientIp() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(d -> d.getClientIp()).orElse(null);
    }

    /**
     * 客户端请求URI
     * @return
     */
    @Deprecated
    public static String getURI() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(d -> d.getUri()).orElse(null);
    }
    
    /**
     * 网关配置的业务
     * @return
     */
    public static String getBusiness() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(d -> d.getBusiness()).orElse(null);
    }
    
    /**
     * 获取登录用户绑定token
     * @return
     */
    public static String getToken() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(d -> d.getToken()).orElse(null);
    }

    /**
     *
     * @return
     * @deprecated  as of lemon 1.1.1, get user name by business code
     */
    @Deprecated
    public static String getLoginName() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(d -> d.getLoginName()).orElse(null);
    }

    /**
     * 判断是否已登录
     * @return
     * @deprecated as of lemon 1.1.1, judge user login by business code
     */
    @Deprecated
    public static boolean isUserLogin() {
        if(null == LemonDataHolder.getLemonData()) {
            return false;
        }
        if(JudgeUtils.isNotBlank(LemonDataHolder.getLemonData().getUserId())) {
            return true;
        }
        return false;
    }
    
    /**
     * 获取成功消息码
     * @return
     */
    public static String getSuccessMsgCd() {
        if (successMsgCd.isEmpty()) {
            successMsgCd.set(Optional.ofNullable(getLemonEnvironment().getProperty(LemonConstants.MSG_CD_PREFIX)).orElse(LemonUtils.getApplicationName()) + LemonConstants.SUCCESS_MSG_CD_SUFFIX);
        }
        return successMsgCd.get();
    }

    /**
     * 格式化msgInfo
     * @param msgInfo
     * @return
     */
    public static String formatMsgInfo(String msgInfo) {
        return StringUtils.formatString(msgInfo, LemonContextUtils.getAlertParameters());
    }
}
