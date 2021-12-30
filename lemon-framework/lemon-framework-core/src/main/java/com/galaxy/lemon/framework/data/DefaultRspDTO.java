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

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.utils.LemonUtils;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultRspDTO<T> extends BaseDTO<T> implements ConfigurableAlerting {
    private String msgCd;
    private String msgInfo;

    public DefaultRspDTO(){}

    public DefaultRspDTO(AlertCapable alertCapable) {
        this.msgCd = alertCapable.getMsgCd();
        this.msgInfo = alertCapable.getMsgInfo();
    }

    public DefaultRspDTO(String msgCd, String msgInfo) {
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
    }

    public static DefaultRspDTO newInstance(AlertCapable alertCapable) {
        return new DefaultRspDTO(alertCapable);
    }

    public static DefaultRspDTO newInstance(String msgCd, String msgInfo) {
        return new DefaultRspDTO(msgCd, msgInfo);
    }

    /**
     * 创建成功GenericRspDTO<T>实例,并setBody(T)
     * @param  body 嵌套对象
     * @return
     */
    public static <T> DefaultRspDTO<T> newSuccessInstance(T body){
        DefaultRspDTO<T> defaultRspDTO = new DefaultRspDTO<>();
        defaultRspDTO.setMsgCd(LemonUtils.getSuccessMsgCd());
        defaultRspDTO.setBody(body);
        return defaultRspDTO;
    }

    /**
     * 创建成功的GenericRspDTO<NoBody>实例
     * @return
     */
    public static DefaultRspDTO<NoBody> newSuccessInstance(){
        DefaultRspDTO<NoBody> defaultRspDTO = new DefaultRspDTO<>();
        defaultRspDTO.setMsgCd(LemonUtils.getSuccessMsgCd());
        return defaultRspDTO;
    }

    public void setMsgCd(String msgCd) {
        this.msgCd = msgCd;
    }

    public void setMsgInfo(String msgInfo) {
        this.msgInfo = msgInfo;
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
