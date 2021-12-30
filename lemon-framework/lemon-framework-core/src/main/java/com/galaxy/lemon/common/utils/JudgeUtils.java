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

package com.galaxy.lemon.common.utils;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.exception.LemonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展业务执行结果判断
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class JudgeUtils extends CommonUtils{

    private static final Logger logger = LoggerFactory.getLogger(JudgeUtils.class);

    /**
     * 判断消息是否成功
     * @param msgCd
     * @return
     */
    public static boolean isSuccess(String msgCd) {
        if(StringUtils.isEmpty(msgCd)) {
            throw new LemonException("MsgCd is null.");
        }
        return StringUtils.equals(msgCd.substring(msgCd.length() - 5), LemonConstants.SUCCESS_MSG_CD_SUFFIX);
    }

    /**
     * 判断消息是否成功
     * @param alertCapable
     * @return
     */
    public static boolean isSuccess(AlertCapable alertCapable) {
        return isSuccess(alertCapable.getMsgCd());
    }
    /**
     * 交易执行失败
     * @param msgCd
     * @return
     */
    public static boolean isNotSuccess(String msgCd) {
        return ! isSuccess(msgCd);
    }

    /**
     * 交易执行失败
     * @param alertCapable
     * @return
     */
    public static boolean isNotSuccess(AlertCapable alertCapable) {
        return ! isSuccess(alertCapable);
    }


}
