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

package com.galaxy.lemon.framework.autoconfigure.datasource.druid;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * druid web stat properties
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties("lemon.druid.monitor")
public class DruidMonitorProperties {

    private String loginUsername;
    private String loginPassword;
    private String statServletUrlMappings;
    private String statFilterUrlPatterns;

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getStatServletUrlMappings() {
        return statServletUrlMappings;
    }

    public void setStatServletUrlMappings(String statServletUrlMappings) {
        this.statServletUrlMappings = statServletUrlMappings;
    }

    public String getStatFilterUrlPatterns() {
        return statFilterUrlPatterns;
    }

    public void setStatFilterUrlPatterns(String statFilterUrlPatterns) {
        this.statFilterUrlPatterns = statFilterUrlPatterns;
    }
}
