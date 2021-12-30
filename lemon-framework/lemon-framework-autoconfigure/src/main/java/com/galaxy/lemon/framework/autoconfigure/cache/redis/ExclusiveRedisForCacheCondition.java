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

package com.galaxy.lemon.framework.autoconfigure.cache.redis;

import com.galaxy.lemon.common.utils.JudgeUtils;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * 是否对缓存使用的redis独立配置
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ExclusiveRedisForCacheCondition extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage
                .forCondition("Exclusive Redis for Cache Condition");
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
                context.getEnvironment(), "lemon.cache.redis.");
        String url = resolver.getProperty("url");
        String host = resolver.getProperty("host");
        Map<String, Object> sentinelConfig = resolver.getSubProperties("sentinel.");
        Map<String, Object> clusterConfig = resolver.getSubProperties("cluster.");
        if (JudgeUtils.isBlank(url)
                && JudgeUtils.isBlank(host)
                && JudgeUtils.isEmpty(sentinelConfig)
                && JudgeUtils.isEmpty(clusterConfig)) {
            return ConditionOutcome.noMatch(
                    message.didNotFind("lemon.cache.redis property").items("url", "host", "sentinel", "cluster"));
        }

        return ConditionOutcome.match(message.because("one of property 'lemon.cache.redis.url/host/sentinel/cluster' found. "));
    }

}
