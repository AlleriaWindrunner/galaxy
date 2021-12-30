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

package com.galaxy.lemon.client;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;

/**
 * 客户端适配器
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class ClientAdapter implements Client {

    @Override
    public void send(byte[] bytes) {
        throw new UnsupportClientMethodException(this.getClass().getName(), "send");
    }
    
    @Override
    public void send(Object object) {
        throw new UnsupportClientMethodException(this.getClass().getName(), "send");
    }
    
    public static class UnsupportClientMethodException extends LemonException {
        private static final long serialVersionUID = 1L;

        public UnsupportClientMethodException(String className, String methodName) {
            super(ErrorMsgCode.SYS_ERROR.getMsgCd(), "The method \""+className+"."+methodName + "\" is not support.");
        }
    }

}
