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

package com.galaxy.lemon.framework.stream.client.interceptor;

import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.stream.client.BindingMetadataHolder;
import com.galaxy.lemon.framework.stream.client.RequestInterceptor;
import com.galaxy.lemon.framework.stream.client.StreamClientFactoryBean.StreamMetadata;
import com.galaxy.lemon.framework.stream.logging.OutputLogger;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Order
public class LoggerRequestInterceptor implements RequestInterceptor {

    private OutputLogger outputLogger;
    private BindingMetadataHolder bindingMetadataHolder;

    public LoggerRequestInterceptor(OutputLogger outputLogger,
                                    BindingMetadataHolder bindingMetadataHolder) {
        this.outputLogger = outputLogger;
        this.bindingMetadataHolder = bindingMetadataHolder;
    }

    @Override
    public <T extends BaseDTO> void apply(T payload, Method method, StreamMetadata streamMetadata) {
        this.outputLogger.log(new StreamLogInfo(method.getDeclaringClass().getSimpleName(), method.getName(), streamMetadata.getBindingName(), payload));
    }

    static class StreamLogInfo {
        private String className;
        private String methodName;
        private String binding;
        private BaseDTO message;

        public StreamLogInfo(String className, String methodName, String binding, BaseDTO message) {
            this.className = className;
            this.methodName = methodName;
            this.binding = binding;
            this.message = message;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getBinding() {
            return binding;
        }

        public void setBinding(String binding) {
            this.binding = binding;
        }

        public BaseDTO getMessage() {
            return message;
        }

        public void setMessage(BaseDTO message) {
            this.message = message;
        }
    }
}
