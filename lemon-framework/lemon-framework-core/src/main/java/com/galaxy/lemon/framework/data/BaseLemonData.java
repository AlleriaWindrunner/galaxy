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

import com.galaxy.lemon.common.log.LogIgnore;
import com.galaxy.lemon.framework.annotation.ResponseIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Locale;


/**
 * lemon data
 * 交易进入时，根据DTO生成LemonData放入上下文
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class BaseLemonData {
    /**
     * 请求流水号，同一笔业务，请求所有系统的请求流水号一致
     *
     */
    @ApiModelProperty(hidden = true)
    private String requestId;
    /**
     * 交易流水号，只代表一次交易，请求和返回DTO对象msgID一致
     */
    @ResponseIgnore
    @ApiModelProperty(hidden = true)
    private String msgId;
    /**
     * 交易发起时间
     */
    @ResponseIgnore
    @ApiModelProperty(hidden = true)
    private LocalDateTime startDateTime;
    /**
     * 区域
     */
    @ResponseIgnore
    @ApiModelProperty(hidden = true)
    @LogIgnore
    private Locale locale;
    /**
     * 客户端IP
     */
    @ResponseIgnore
    @ApiModelProperty(hidden=true)
    private String clientIp;
    /**
     * 业务
     */
    @ResponseIgnore
    @ApiModelProperty(hidden=true)
    private String business;

    /**
     * 入口交易
     */
    //@ApiModelProperty(hidden=true)
    @ResponseIgnore
    private String entryTx;

    /**
     * 请求URI
     */
    @ApiModelProperty(hidden=true)
    @LogIgnore
    @ResponseIgnore
    @Deprecated
    private String uri;
    /**
     * 登录用户ID
     *
     */
    @ApiModelProperty(hidden = true)
    @Deprecated
    @ResponseIgnore
    @LogIgnore
    private String userId;
    /**
     * sessionId
     */
    @ApiModelProperty(hidden=true)
    @ResponseIgnore
    private String token;

    @ApiModelProperty(hidden=true)
    @LogIgnore
    @ResponseIgnore
    @Deprecated
    private String loginName;

    /**
     * 交易版本号
     */
    @ApiModelProperty(hidden=true)
    @ResponseIgnore
    private String versionId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEntryTx() {
        return entryTx;
    }

    public void setEntryTx(String entryTx) {
        this.entryTx = entryTx;
    }

    /**
     * 已过时，后续版本会删除，业务DTO自定义用户ID
     * @return
     */
    @Deprecated
    public String getUserId() {
        return userId;
    }

    @Deprecated
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 已过时，后续版本会删除，业务DTO自定义loginName
     * @return
     */
    @Deprecated
    public String getLoginName() {
        return loginName;
    }

    @Deprecated
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append("{");
        sb.append("requestId='").append(this.getRequestId()).append('\'');
        sb.append(", msgId='").append(this.getMsgId()).append('\'');
        sb.append(", startDateTime=").append(this.getStartDateTime());
        sb.append(", locale=").append(this.getLocale());
        sb.append(", business='").append(this.getBusiness()).append('\'');
        sb.append(", uri='").append(this.getUri()).append('\'');
        sb.append(", entryTx='").append(this.getEntryTx()).append('\'');
        sb.append(", userId='").append(this.getUserId()).append('\'');
        sb.append(", clientIp='").append(this.getClientIp()).append('\'');
        sb.append(", token='").append(this.getToken()).append('\'');
        sb.append(", versionId='").append(this.getVersionId()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
