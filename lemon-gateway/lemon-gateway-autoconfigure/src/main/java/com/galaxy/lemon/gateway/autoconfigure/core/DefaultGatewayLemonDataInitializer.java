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

package com.galaxy.lemon.gateway.autoconfigure.core;

import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.instantiator.LemonDataInstantiator;
import com.galaxy.lemon.framework.data.support.AbstractContextLemonDataInitializer;
import com.galaxy.lemon.framework.utils.WebUtils;
import com.galaxy.lemon.gateway.core.GatewayHelper;

import javax.servlet.http.HttpServletRequest;

import static com.galaxy.lemon.common.LemonConstants.HTTP_HEADER_VERSIONID;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultGatewayLemonDataInitializer<L extends BaseLemonData> extends AbstractContextLemonDataInitializer<L> {

    public DefaultGatewayLemonDataInitializer(LemonDataInstantiator<L> lemonDataInstantiator) {
        super(lemonDataInstantiator);
    }

    @Override
    protected void doInitialLemonData(L lemonData) {
        HttpServletRequest request = WebUtils.getHttpServletRequest();
        lemonData.setRequestId(WebUtils.resolveRequestId(request));
        lemonData.setLocale(GatewayHelper.resolveLocale(request));
        lemonData.setStartDateTime(LemonContextUtils.getTradeStartTime() == null ? DateTimeUtils.getCurrentLocalDateTime() : LemonContextUtils.getTradeStartTime());
        lemonData.setClientIp(GatewayHelper.resolveClientIp(request));
        lemonData.setToken(GatewayHelper.getToken(request));
        lemonData.setVersionId(request.getHeader(HTTP_HEADER_VERSIONID));
    }

}
