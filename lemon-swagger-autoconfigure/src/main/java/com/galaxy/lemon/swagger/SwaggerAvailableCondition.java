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

package com.galaxy.lemon.swagger;

import com.galaxy.lemon.common.Env;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.utils.JudgeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * swagger abailable condition
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SwaggerAvailableCondition extends SpringBootCondition {
    public static final String SWAGGER_AVAILABLE_PROFILE_KEY = "lemon.swagger.profiles";
    public static final String SWAGGER_AVAILABLE_PROFILES_SEPARATOR = ",";
    public static final String DEFAULT_SWAGGER_AVAILABLE_PROFILES = "dev,sit,uat,ci";
    
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
            AnnotatedTypeMetadata metadata) {
        boolean enabled = swaggerAvailable(context);
        ConditionMessage.Builder message = ConditionMessage
                .forCondition("Swagger Available");
        if (enabled) {
            return ConditionOutcome.match(message.available(getCurrentEnv(context)).append(appendSwaggerAvailableConditionMessage(context)));
        }
        return ConditionOutcome.noMatch(message.notAvailable(getCurrentEnv(context)).append(appendSwaggerAvailableConditionMessage(context)));
    }

    public boolean swaggerAvailable(ConditionContext context) {
        String availableProfiles = getSwaggerAvailableProfiles(context);
        String currentEnv = getCurrentEnv(context);
        boolean enabled = false;
        if(JudgeUtils.isNotBlankAll(availableProfiles, currentEnv)) {
            List<?> list = Stream.of(availableProfiles.split(SWAGGER_AVAILABLE_PROFILES_SEPARATOR)).filter(env -> StringUtils.equals(env, currentEnv)).collect(Collectors.toList());
            if(JudgeUtils.isNotEmpty(list)) {
                enabled = true;
            }
        }
        if(JudgeUtils.isBlank(availableProfiles) && JudgeUtils.isNotBlank(currentEnv)) {
            if(! Env.isPrd(Env.valueOf(currentEnv.toUpperCase()))) {
                enabled = true;
            }
        }
        return enabled;
    }

    public String getSwaggerAvailableProfiles(ConditionContext context) {
        return Optional.ofNullable(context).map(ConditionContext::getEnvironment).map(e -> e.getProperty(SWAGGER_AVAILABLE_PROFILE_KEY)).filter(JudgeUtils::isNotBlank).orElse(DEFAULT_SWAGGER_AVAILABLE_PROFILES);
    }

    public String getCurrentEnv(ConditionContext context) {
        return Optional.ofNullable(context).map(ConditionContext::getEnvironment).map(e -> e.getProperty(LemonConstants.PROFILES_ACTIVE)).orElse(null);
    }

    public String appendSwaggerAvailableConditionMessage(ConditionContext context) {
        return "Swagger available profiles " + getSwaggerAvailableProfiles(context) + ".";
    }
}
