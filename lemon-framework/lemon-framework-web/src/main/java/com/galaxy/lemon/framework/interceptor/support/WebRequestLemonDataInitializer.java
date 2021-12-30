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

package com.galaxy.lemon.framework.interceptor.support;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.instantiator.LemonDataInstantiator;
import com.galaxy.lemon.framework.data.support.AbstractContextLemonDataInitializer;
import com.galaxy.lemon.framework.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class WebRequestLemonDataInitializer<L extends BaseLemonData> extends AbstractContextLemonDataInitializer<L> {
    private static final Logger logger = LoggerFactory.getLogger(WebRequestLemonDataInitializer.class);

    public WebRequestLemonDataInitializer(LemonDataInstantiator<L> lemonDataInstantiator) {
        super(lemonDataInstantiator);
    }

    @Override
    protected void doInitialLemonData(L lemonData) {
        HttpServletRequest request = WebUtils.getHttpServletRequest();
        lemonData.setRequestId(WebUtils.resolveRequestId());
        lemonData.setLocale(WebUtils.resolveLocale());
        lemonData.setStartDateTime(DateTimeUtils.getCurrentLocalDateTime());
        lemonData.setUserId(request.getHeader(LemonConstants.HTTP_HEADER_USER_ID));
        lemonData.setClientIp(WebUtils.resolveClientIP(request));
        lemonData.setBusiness(request.getHeader(LemonConstants.HTTP_HEADER_BUSINESS));
        lemonData.setUri(request.getHeader(LemonConstants.HTTP_HEADER_URI));
        lemonData.setToken(request.getHeader(LemonConstants.HTTP_HEADER_TOKEN));
        lemonData.setLoginName(request.getHeader(LemonConstants.HTTP_HEADER_LOGIN_NAME));
        lemonData.setVersionId(request.getHeader(LemonConstants.HTTP_HEADER_VERSIONID));
        lemonData.setEntryTx(LemonContextUtils.getCurrentTxName());
        if(logger.isDebugEnabled()) {
            logger.debug("Initialized lemon data at http request ==> {}", lemonData);
        }
    }
}
