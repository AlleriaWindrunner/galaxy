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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * principal
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonUser extends User {
    private static final long serialVersionUID = 8957124042836925579L;

    /**
     * 详细用户信息
     */
    private UserInfoBase userInfo;

    @JsonCreator
    public LemonUser(@JsonProperty("username") String username,
                     @JsonProperty("userInfo") UserInfoBase userInfo,
                     @JsonProperty("enabled") boolean enabled,
                     @JsonProperty("accountNonExpired") boolean accountNonExpired,
                     @JsonProperty("credentialsNonExpired") boolean credentialsNonExpired,
                     @JsonProperty("accountNonLocked") boolean accountNonLocked,
                     @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
        super(username, "", enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
        this.userInfo = userInfo;
    }

    /**
     * 获取用户详情
     *
     * @return
     */
    public UserInfoBase getUserInfo() {
        return this.userInfo;
    }

}
