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

import com.galaxy.lemon.framework.security.auth.AbstractAuthenticationProvider;
import com.galaxy.lemon.framework.security.auth.AuthenticationProcessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

/**
 * 认证
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonAuthenticationProvider extends AbstractAuthenticationProvider {
    protected AuthenticationProcessor authenticationProcessor;

    public LemonAuthenticationProvider(AuthenticationProcessor authenticationProcessor) {
        this.authenticationProcessor = authenticationProcessor;
    }
    
    @Override
    protected Authentication preProcessAuthentication(Authentication authentication) {
        LemonUsernamePasswordAuthenticationToken lemonUsernamePasswordAuthenticationToken = null;
        if (authentication instanceof LemonUsernamePasswordAuthenticationToken) {
            lemonUsernamePasswordAuthenticationToken = (LemonUsernamePasswordAuthenticationToken) authentication;
        } else {
            if(authentication instanceof UsernamePasswordAuthenticationToken) {
                String random = "";
                Object details = authentication.getDetails();
                if(details instanceof Map) {
                    random = (String)((Map<?, ?>) details).get(LemonUsernamePasswordAuthenticationFilter.RANDOM_PARAM_NAME);
                }
                lemonUsernamePasswordAuthenticationToken = new LemonUsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), random);
            }
        }
        if(null == lemonUsernamePasswordAuthenticationToken) {
            throw new LemonAuthenticationException("only support UsernamePasswordAuthenticationToken token.");
        }
        return lemonUsernamePasswordAuthenticationToken;
    }

    @Override
    protected AuthenticationProcessor getAuthenticationProcessor(Authentication authentication) {
        return this.authenticationProcessor;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

//    @Override
//    protected Authentication createAuthenticationToken(Object principal, Authentication authentication, UserDetails user) {
//        UsernamePasswordAuthenticationToken result = new LemonUsernamePasswordAuthenticationToken(
//                principal, authentication.getCredentials(), user.getAuthorities());
//        result.setDetails(authentication.getDetails());
//        return result;
//    }

}
