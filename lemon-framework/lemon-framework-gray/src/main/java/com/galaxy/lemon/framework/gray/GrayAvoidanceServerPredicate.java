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

package com.galaxy.lemon.framework.gray;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import static com.galaxy.lemon.framework.gray.GrayUtils.*;

/**
 * 回避灰度服务
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class GrayAvoidanceServerPredicate extends AbstractServerPredicate {

    /**
     * 失败请求 默认返回正常版本
     *
     * @param input
     * @return
     */
    @Override
    public boolean apply(PredicateKey input) {
        DiscoveryEnabledServer server = (DiscoveryEnabledServer) input.getServer();
        return !isGray(getGrayVersion(server));
    }
}
