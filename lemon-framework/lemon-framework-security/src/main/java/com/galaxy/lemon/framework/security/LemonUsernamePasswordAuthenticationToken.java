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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@JsonIgnoreProperties({"name"})
public class LemonUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = 3879132540866188019L;
    private String random;

    public LemonUsernamePasswordAuthenticationToken(Object principal, Object credentials, String random) {
        super(principal, credentials);
        this.random = random;
    }

    @JsonCreator
    public LemonUsernamePasswordAuthenticationToken(@JsonProperty("principal") Object principal,
                                                    @JsonProperty("credentials") Object credentials,
                                                    @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        try {
            super.setAuthenticated(isAuthenticated);
        }catch (IllegalArgumentException e){}
    }

    @JsonIgnore
    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }
    
}
