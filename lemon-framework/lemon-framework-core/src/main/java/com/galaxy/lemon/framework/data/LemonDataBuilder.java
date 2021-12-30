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

package com.galaxy.lemon.framework.data;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonDataBuilder {
    /**
     * 请求流水号
     */
    private String requestId;
    /**
     * 交易流水号
     */
    private String msgId;
    /**
     * 交易发起时间
     */
    private LocalDateTime startDateTime;
    /**
     * 区域
     */
    private Locale locale;

    /**
     * gateway 业务标志
     */
    private String business;

    /**
     * http request uri
     */
    private String uri;
    /**
     * 入口交易
     */
    private String entryTx;
    /**
     * 登录用户ID
     */
    private String userId;

    /**
     * login name
     */
    private String loginName;
    /**
     * 客户端IP
     */
    private String clientIp;
    /**
     * session token
     */
    private String token;

    public static LemonDataBuilder builder() {
        return new LemonDataBuilder();
    }

    public BaseLemonData build() {
        return build(new DefaultLemonData());
    }

    public BaseLemonData build(BaseLemonData lemonData) {
        lemonData.setRequestId(this.requestId);
        lemonData.setMsgId(this.msgId);
        lemonData.setStartDateTime(this.startDateTime);
        lemonData.setLocale(this.locale);
        lemonData.setUserId(this.userId);
        lemonData.setClientIp(this.clientIp);
        lemonData.setToken(this.token);
        lemonData.setEntryTx(this.entryTx);
        lemonData.setLoginName(this.loginName);
        lemonData.setBusiness(this.business);
        lemonData.setUri(this.uri);
        return lemonData;
    }

    public LemonDataBuilder setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public LemonDataBuilder setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public LemonDataBuilder setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public LemonDataBuilder setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public LemonDataBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public LemonDataBuilder setClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public LemonDataBuilder setToken(String token) {
        this.token = token;
        return this;
    }

    public LemonDataBuilder setBusiness(String business) {
        this.business = business;
        return this;
    }

    public LemonDataBuilder setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public LemonDataBuilder setEntryTx(String entryTx) {
        this.entryTx = entryTx;
        return this;
    }

    public LemonDataBuilder setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }
}
