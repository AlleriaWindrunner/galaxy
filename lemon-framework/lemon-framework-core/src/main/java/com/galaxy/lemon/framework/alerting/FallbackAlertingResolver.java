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

package com.galaxy.lemon.framework.alerting;

import com.galaxy.lemon.common.utils.StringUtils;

import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class FallbackAlertingResolver extends AbstractAlertingResolver {
    private static final String FALLBACK_MSG_INFO = "对不起,系统正忙!";

    private AlertingResolver alertingResolver;
    private String defaultFallbackMsgInfo;

    public FallbackAlertingResolver() {
        this(null, null);
    }

    public FallbackAlertingResolver(AlertingResolver alertingResolver) {
        this(alertingResolver, null);
    }

    public FallbackAlertingResolver(AlertingResolver alertingResolver, String defaultFallbackMsgInfo) {
        this.alertingResolver = alertingResolver;
        this.defaultFallbackMsgInfo = StringUtils.getDefaultIfEmpty(defaultFallbackMsgInfo, FALLBACK_MSG_INFO);
    }

    @Override
    protected ConfigurableAlerting doResolve(ConfigurableAlerting configurableAlerting) {
        configurableAlerting.setMsgInfo(null);
        Optional.ofNullable(getAlertingResolver()).ifPresent(a -> a.resolve(configurableAlerting));
        return this.doFallbackResolve(configurableAlerting);
    }

    protected AlertingResolver getAlertingResolver() {
        return this.alertingResolver;
    }

    protected ConfigurableAlerting doFallbackResolve(ConfigurableAlerting configurableAlerting) {
        if (StringUtils.isBlank(configurableAlerting.getMsgInfo())) {
            configurableAlerting.setMsgInfo(this.getDefaultFallbackMsgInfo());
        }
        return configurableAlerting;
    }

    public String getDefaultFallbackMsgInfo() {
        return this.defaultFallbackMsgInfo;
    }
}
