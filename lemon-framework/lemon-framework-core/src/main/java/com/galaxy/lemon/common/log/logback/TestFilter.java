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

package com.galaxy.lemon.common.log.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class TestFilter extends Filter<ILoggingEvent>{

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if(event.getThreadName().startsWith("lemon-task")) {
            return FilterReply.DENY;
        }
        if(event.getThreadName().startsWith("Eureka-JerseyClient-Conn-Cleaner")) {
            return FilterReply.DENY;
        }
        if(event.getThreadName().startsWith("DiscoveryClient-CacheRefreshExecutor")) {
            return FilterReply.DENY;
        }
        if(event.getThreadName().startsWith("DiscoveryClient-HeartbeatExecutor")) {
            return FilterReply.DENY;
        }
        if(event.getThreadName().startsWith("PollingServerListUpdater-")) {
            return FilterReply.DENY;
        }
        if(event.getThreadName().startsWith("NFLoadBalancer-PingTimer-")) {
            return FilterReply.DENY;
        }
        if(event.getThreadName().startsWith("SimpleHostRoutingFilter.connectionManagerTimer")) {
            return FilterReply.DENY;
        }
        if(event.getThreadName().startsWith("DiscoveryClient-InstanceInfoReplicator")) {
            return FilterReply.DENY;
        }
        if("com.netflix.discovery.DiscoveryClient".equals(event.getLoggerName())) {
            return FilterReply.DENY;
        }
        
        if(event.getLoggerName().startsWith("com.netflix.discovery.shared")) {
            return FilterReply.DENY;
        }
        if("org.apache.http.headers".equals(event.getLoggerName())) {
            return FilterReply.DENY; 
        }
        if("org.apache.http.wire".equals(event.getLoggerName())) {
            return FilterReply.DENY; 
        }
        return FilterReply.ACCEPT;
    }

}
