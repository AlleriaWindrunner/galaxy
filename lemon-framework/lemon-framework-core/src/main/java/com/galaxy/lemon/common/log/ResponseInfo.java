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

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ResponseInfo<T> {
    private String action = "response";
    private String requestId;
    private String msgId;
    private Keywords keywords;
    private Long duration;
    private String status;
    private T result;

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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Keywords getKeywords() {
        return keywords;
    }

    public void setKeywords(Keywords keywords) {
        this.keywords = keywords;
    }

    public static class Builder<T> {
        private String action = "response";
        private String requestId;
        private String msgId;
        private Keywords keywords;
        private Long duration;
        private String status;
        private T result;

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
        public Builder duration(Long duration) {
            this.duration = duration;
            return this;
        }
        public Builder status(String status) {
            this.status = status;
            return this;
        }
        public Builder result(T result) {
            this.result = result;
            return this;
        }

        public Builder keywords(Keywords keywords) {
            this.keywords = keywords;
            return this;
        }

        public ResponseInfo<T> build() {
            ResponseInfo<T> responseInfo = new ResponseInfo<>();
            append(responseInfo);
            return responseInfo;
        }

        public <R extends ResponseInfo> ResponseInfo build(Class<R> responseInfoSubClass) {
            if (!ClassUtils.isAssignable(ResponseInfo.class, responseInfoSubClass)) {
                LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Class must be assign from ResponseInfo.");
            }
            ResponseInfo<T> responseInfo = ReflectionUtils.newInstance(responseInfoSubClass);
            append(responseInfo);
            return responseInfo;
        }

        private void append(ResponseInfo<T> responseInfo) {
            if (JudgeUtils.isNotBlank(this.action)) {
                responseInfo.setAction(this.action);
            }
            responseInfo.setRequestId(this.requestId);
            responseInfo.setMsgId(this.msgId);
            responseInfo.setKeywords(this.keywords);
            responseInfo.setDuration(this.duration);
            responseInfo.setStatus(this.status);
            responseInfo.setResult(this.result);
        }
    }
}
