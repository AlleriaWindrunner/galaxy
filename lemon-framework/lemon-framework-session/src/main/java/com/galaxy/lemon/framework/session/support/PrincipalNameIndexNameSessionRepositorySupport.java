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

package com.galaxy.lemon.framework.session.support;

import org.springframework.session.FindByIndexNameSessionRepository;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * PrincipalNameIndexName 即登录用户名
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class PrincipalNameIndexNameSessionRepositorySupport {

    /**
     * set index name of principal name
     * @param session
     * @param principalNameIndexName
     */
    public void setPrincipalNameIndexNameToSession(HttpSession session, Object principalNameIndexName) {
        Optional.ofNullable(session).ifPresent(s  -> s.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, principalNameIndexName));
    }

    /**
     * get index name of principal name
     * @param session
     * @return
     */
    public Object getPrincipalNameIndexNameFromSession(HttpSession session) {
        return Optional.ofNullable(session).map(s -> s.getAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME)).orElse(null);
    }


}
