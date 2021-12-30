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

package com.galaxy.lemon.framework.security;

import com.galaxy.lemon.common.AlertCapable;
import org.springframework.security.core.AuthenticationException;

/**
 * 认证失败
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = -3089720146026816187L;
    private AlertCapable alertCapable;
    private Integer statusCode;//http response status code
    
    public LemonAuthenticationException(String msgInfo) {
        super(msgInfo);
        this.statusCode = HttpServletResponseStatusCode.SC_OK;
    }

    public LemonAuthenticationException(AlertCapable alertCapable) {
        this(alertCapable, HttpServletResponseStatusCode.SC_OK);
    }
    
    public LemonAuthenticationException(AlertCapable alertCapable, Integer statusCode) {
        super(alertCapable.getMsgCd()+" : "+alertCapable.getMsgInfo());
        this.alertCapable = alertCapable;
        this.statusCode = statusCode;
    }
    
    public AlertCapable getAlertCapable() {
        return this.alertCapable;
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

}
