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

package com.galaxy.lemon.framework.security.auth;


import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.security.auth.GenericAuthenticationToken;
import com.galaxy.lemon.framework.security.auth.MatchableAuthenticationProcessor;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class GenericAuthenticationProvider extends AbstractAuthenticationProvider {

    private List<MatchableAuthenticationProcessor> authenticationProcessors;

    public GenericAuthenticationProvider(List<MatchableAuthenticationProcessor> authenticationProcessors) {
        if (JudgeUtils.isEmpty(authenticationProcessors)) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "No AuthenticationProcessor found.");
        }
        this.authenticationProcessors = authenticationProcessors;
    }

    @Override
    protected AuthenticationProcessor getAuthenticationProcessor(Authentication authentication) {
        return this.authenticationProcessors.stream().filter(p -> p.match(authentication)).findFirst()
                .orElseThrow(() -> LemonException.create(ErrorMsgCode.SYS_ERROR, "No matched authentication \"AuthenticationProcessor\" found."));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (GenericAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
