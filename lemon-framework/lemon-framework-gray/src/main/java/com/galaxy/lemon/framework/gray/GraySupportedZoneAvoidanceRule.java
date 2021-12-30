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
/**
 * 支持灰度的ZoneAvoidanceRule
 * @author yuzhou
 * @date 2019/3/29
 * @time 10:17
 * @since 1.4.6
 */

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class GraySupportedZoneAvoidanceRule extends ZoneAvoidanceRule {
    private AbstractServerPredicate cachedServerPredicate;

    public GraySupportedZoneAvoidanceRule(){
        super();
        initialServerPredicate();
    }

    public void initialServerPredicate() {
        AbstractServerPredicate abstractServerPredicate = super.getPredicate();
        if (abstractServerPredicate instanceof CompositePredicate) {
            abstractServerPredicate = new CompositePredicateGrayDecorator().decorate((CompositePredicate) abstractServerPredicate);
        }
        this.cachedServerPredicate = abstractServerPredicate;
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return this.cachedServerPredicate;
    }

}
