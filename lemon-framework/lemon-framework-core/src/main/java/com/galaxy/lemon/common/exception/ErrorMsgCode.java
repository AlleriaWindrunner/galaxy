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

package com.galaxy.lemon.common.exception;

import com.galaxy.lemon.common.AlertCapable;

/**
 * 框架错误码
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public enum ErrorMsgCode implements AlertCapable {
    /**
     * 系统异常消息码
     */
    SYS_ERROR("SYS00001"),
    /**
     * 访问数据库异常
     */
    ACCESS_DATABASE_ERROR("SYS00002"),
    /**
     * 签名异常
     */
    SIGNATURE_EXCEPTION("SYS00003"),
    /**
     * 404异常
     */
    NO_HANDLER_FOUND_ERROR("SYS00404"),
    /**
     * 401异常
     */
    NO_AUTH_ERROR("SYS00401"),
    /**
     * session 被强制过期
     */
    SESSION_EXPIRED("SYS01401"),
    /**
     * refresh token 失效
     */
    REFRESH_TOKEN_INVALID("SYS02401"),
    /**
     * 认证失败
     */
    AUTHENTICATION_FAILURE("SYS03401"),
    /**
     * 禁止操作
     */
    FORBIDDEN_OPERATION("SYS00403","FORBIDDEN_OPERATION"),
    /**
     * task schedule exception
     */
    SCHEDULE_TASK_EXCEPTION("SYS00005"),
    /**
     * 服务端404错误
     */
    SERVER_RESOURCE_NOT_FOUND("SYS00006"),
    /**
     * 服务不可用
     */
    SERVER_NOT_AVAILABLE("SYS00007"),
    /**
     * 不能获取分布式锁
     */
    UNABLE_ACQUIRE_DISTRIBUTED_LOCK("SYS00100"),
    /**
     * 累计操作异常
     */
    CUMULATIVE_ERROR("SYS00101"),
    /**
     * bean validation exception
     */
    BEAN_VALIDATION_ERROR("SYS10001"),
    /**
     * client exception
     */
    CLIENT_EXCEPTION("SYS20000"),
    /**
     * UnknownHostException
     */
    CLIENT_EXCEPTION_UNKNOWN_HOST("SYS20001"),
    /**
     * timeout excepiton
     */
    CLIENT_TIMEOUT("SYS20002"),
    /**
     * illegal parameter
     */
    ILLEGAL_PARAMETER("SYS30001"),
    /**
     * producer of rabbit exception
     */
    PRODUCER_RABBIT_EXCEPTION("SYS40001"),
    /**
     * consumer of rabbit exception
     */
    CONSUMER_RABBIT_EXCEPTION("SYS40021"),
    /**
     * no msg_cd set
     */
    MSG_CD_NOT_EXISTS("SYS99999"),
    /**
     * 警告类型
     */
    WARNING("SYS11111");

    private String msgCd;
    private String msgInfo;
    
    /**
     * @param msgCd
     * @param msgInfo
     */
    ErrorMsgCode(String msgCd, String msgInfo) {
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
    }
    
    ErrorMsgCode(String msgCd) {
        this.msgCd = msgCd;
    }

    @Override
    public String getMsgCd() {
        return this.msgCd;
    }
    @Override
    public String getMsgInfo() {
        return this.msgInfo;
    }
}
