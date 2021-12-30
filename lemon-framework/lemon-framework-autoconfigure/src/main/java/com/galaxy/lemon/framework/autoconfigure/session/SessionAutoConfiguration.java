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

package com.galaxy.lemon.framework.autoconfigure.session;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.autoconfigure.session.SessionObjectMapperCustomizer;
import com.galaxy.lemon.framework.session.CookieAndHeaderHttpSessionStrategy;
import com.galaxy.lemon.framework.session.SessionIdStrategy;
import com.galaxy.lemon.framework.session.match.RequestMatcher;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HttpSessionStrategy;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({EnableRedisHttpSession.class, RequestMatcher.class})
@EnableConfigurationProperties({SessionProperties.class})
@EnableRedisHttpSession(maxInactiveIntervalInSeconds=1800, redisNamespace="LMS", redisFlushMode = RedisFlushMode.ON_SAVE)
@AutoConfigureAfter(SessionRedisAutoConfiguration.class)
public class SessionAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SessionAutoConfiguration.class);

    @Value("${lemon.session.strategy.cookieName:}")
    private String cookieName;

    @Value("${lemon.session.strategy.headerName:}")
    private String headerName;

    private SessionProperties sessionProperties;

    private SessionIdStrategy sessionIdStrategy;

//    @Value("${lemon.session.strategy.onlyHeader:false}")
//    private Boolean onlyHeader;
//
//    @Value("${lemon.session.strategy.onlyCookie:false}")
//    private Boolean onlyCookie;
//
//    @Value("${lemon.session.strategy.preferredCookie:true}")
//    private Boolean preferredCookie;

    private List<SessionObjectMapperCustomizer> sessionObjectMapperCustomizers;

    public SessionAutoConfiguration(ObjectProvider<List<SessionObjectMapperCustomizer>> sessionObjectMapperCustomizers,
                                    SessionProperties sessionProperties) {
        this.sessionObjectMapperCustomizers = sessionObjectMapperCustomizers.getIfAvailable();
        this.sessionProperties = sessionProperties;
    }

    @PostConstruct
    public void afterPropertySet() {
        SessionProperties.SessionId sessionId = this.sessionProperties.getSessionId();

        if (JudgeUtils.isBlank(this.cookieName)) {
            this.cookieName = JudgeUtils.isNull(sessionId) ||
                JudgeUtils.isBlank(sessionId.getCookieName()) ? LemonConstants.COOKIE_SESSION_ID : sessionId.getCookieName();
        }

        if (JudgeUtils.isBlank(this.headerName)) {
            this.headerName = JudgeUtils.isNull(sessionId) ||
                JudgeUtils.isBlank(sessionId.getHeaderName()) ? LemonConstants.HTTP_HEADER_TOKEN : sessionId.getHeaderName();
        }

        this.sessionIdStrategy = JudgeUtils.isNotNull(sessionId) && JudgeUtils.isNotNull(sessionId.getStrategy()) ?
                sessionId.getStrategy() : SessionIdStrategy.HeaderOrCookie;
    }

    @Bean
    @ConditionalOnProperty(value = "lemon.session.strategy.cookieAndHeader", matchIfMissing = true)
    public HttpSessionStrategy cookieAndHeaderHttpSessionStrategy() {
        return new CookieAndHeaderHttpSessionStrategy(this.cookieName, this.headerName, this.sessionIdStrategy);
    }

    @Bean
    @ConditionalOnProperty(prefix = "lemon.session.redis", name = "serializer", havingValue = "jackson", matchIfMissing = true)
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        //jsr310,localeDate 等java8 解决
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        Optional.ofNullable(this.sessionObjectMapperCustomizers).ifPresent(s -> s.stream().forEach(c -> c.customize(objectMapper)));

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }
}
