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

package com.galaxy.lemon.common.log;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.ClassUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;

import java.time.LocalDateTime;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RequestInfo<T> {
    private String action = "request";
    private String requestId;
    private String msgId;
    private Keywords keywords;
    private LocalDateTime requestTime;
    private String method;
    private String uri;
    private String clientIp;
    private T target;

    public static Builder builder() {
        return new Builder();
    }

    public static Builder stringBuilder() {
        return new Builder<String>();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Keywords getKeywords() {
        return keywords;
    }

    public void setKeywords(Keywords keywords) {
        this.keywords = keywords;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public static class Builder<T> {
        private String action;
        private String requestId;
        private String msgId;
        private Keywords keywords;
        private LocalDateTime reqeustTime;
        private String method;
        private String uri;
        private String clientIp;

        private T target;

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder msgId(String msgId) {
            this.msgId = msgId;
            return this;
        }

        public Builder reqeustTime(LocalDateTime reqeustTime) {
            this.reqeustTime = reqeustTime;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public Builder keywords(Keywords keywords) {
            this.keywords = keywords;
            return this;
        }

//        public Builder keywords(Keywords keywords) {
//            this.keywords = Optional.ofNullable(keywords).map(k -> k.toString()).orElse(null);
//            return this;
//        }

        public Builder target(T target) {
            this.target = target;
            return this;
        }

        public RequestInfo<T> build() {
            RequestInfo<T> requestInfo = new RequestInfo<>();
            append(requestInfo);
            return requestInfo;
        }

        public <R extends RequestInfo> RequestInfo build(Class<R> requestInfoSubClass) {
            if (!ClassUtils.isAssignable(RequestInfo.class, requestInfoSubClass)) {
                LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Class must be assign from RequestInfo.");
            }
            RequestInfo<T> requestInfo = ReflectionUtils.newInstance(requestInfoSubClass);
            append(requestInfo);
            return requestInfo;
        }

        private void append(RequestInfo<T> requestInfo) {
            if (JudgeUtils.isNotBlank(this.action)) {
                requestInfo.setAction(this.action);
            }
            requestInfo.setRequestId(this.requestId);
            requestInfo.setMsgId(this.msgId);
            requestInfo.setClientIp(this.clientIp);
            requestInfo.setMethod(this.method);
            requestInfo.setUri(this.uri);
            requestInfo.setRequestTime(this.reqeustTime);
            requestInfo.setKeywords(this.keywords);
            requestInfo.setTarget(this.target);
        }
    }
}
