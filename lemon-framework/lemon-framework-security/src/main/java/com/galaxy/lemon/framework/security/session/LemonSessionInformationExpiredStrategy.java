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

package com.galaxy.lemon.framework.security.session;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.framework.response.ResponseMessageResolver;
import com.galaxy.lemon.framework.security.HttpServletResponseStatusCode;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SessionInformation expire strategy
 * SessionInformation.expireNow 后进入此策略
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
    private ResponseMessageResolver responseMessageResolver;
    
    public LemonSessionInformationExpiredStrategy(ResponseMessageResolver responseMessageResolver) {
        this.responseMessageResolver = responseMessageResolver;
    }

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletResponse response = event.getResponse();
        HttpServletRequest request = event.getRequest();
        response.setStatus(HttpServletResponseStatusCode.SC_EXPIRED_SESSION);
        this.responseMessageResolver.resolve(request, response, ErrorMsgCode.SESSION_EXPIRED);
    }

}
