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

package com.galaxy.lemon.framework.autoconfigure.core;

import com.galaxy.lemon.framework.jackson.log.DefaultLoggingCodec;
import com.galaxy.lemon.common.log.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class LoggingConfiguration {

    @Value("${lemon.log.keywords:}")
    private String[] expressions;

    @Bean
    public LoggingCodec loggingCodec(Jackson2ObjectMapperBuilder builder,
                                     ApplicationContext applicationContext) {
        return new DefaultLoggingCodec(builder.build(), applicationContext);
    }

    @Bean
    public static LogKeywordsAnnotationParser logKeywordsAnnotationParser() {
        return new LogKeywordsAnnotationParser();
    }

    @Bean
    public KeywordsExpressionSource keywordsExpressionSource(LogKeywordsAnnotationParser logKeywordsAnnotationParser) {
        return new LogKeywordsExpressionSource(logKeywordsAnnotationParser);
    }

    @Bean
    @ConditionalOnMissingBean
    public KeywordsExpressionEvaluator logKeywordsExpressionEvaluator() {
        return new KeywordsExpressionEvaluator();
    }

    @Bean
    @ConditionalOnMissingBean
    public KeywordsResolver logKeywordsResolver(KeywordsExpressionEvaluator logKeywordsExpressionEvaluator,
                                                KeywordsExpressionSource keywordsExpressionSource) {
        return new DefaultKeywordsResolver(logKeywordsExpressionEvaluator, keywordsExpressionSource, expressions);
    }

}
