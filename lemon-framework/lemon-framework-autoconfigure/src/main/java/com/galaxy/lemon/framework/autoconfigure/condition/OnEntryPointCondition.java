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

package com.galaxy.lemon.framework.autoconfigure.condition;

import com.alibaba.druid.util.StringUtils;
import com.galaxy.lemon.framework.autoconfigure.condition.ConditionalOnEntryPoint;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class OnEntryPointCondition extends SpringBootCondition {
    private String prefix = "lemon.entrypoint.";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String matchValue = (String) metadata
                .getAnnotationAttributes(ConditionalOnEntryPoint.class.getName())
                .get("value");
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
                context.getEnvironment(), this.prefix);
        if (resolver.containsProperty("enabled")) {
            String enabled = resolver.getProperty("enabled", String.class, "false");
            return new ConditionOutcome(StringUtils.equals(enabled, matchValue),
                    ConditionMessage.forCondition(ConditionalOnEntryPoint.class.getName()).because(
                            this.prefix  + ".enabled is " + enabled +", required value is "+ matchValue));
        } else {
            return new ConditionOutcome(false, ConditionMessage.forCondition(ConditionalOnEntryPoint.class.getName())
            .because(this.prefix+ ".enabled not found."));
        }
    }
}
