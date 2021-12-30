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

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;


/**
 * 相同principal name index name 只允许一个session存在
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ExpiringAllRequiredExpireSessionExtractor extends AbstractRequiredExpireSessionExtractor {

    public ExpiringAllRequiredExpireSessionExtractor(SessionRegistry sessionRegistry,
                                                     PrincipalNameResolver principalNameResolver) {
        super(sessionRegistry, principalNameResolver);
    }

    @Override
    public boolean requireExpire(SessionInformation sessionInformation, Object currentPrincipal) {
        return true;
    }
}
