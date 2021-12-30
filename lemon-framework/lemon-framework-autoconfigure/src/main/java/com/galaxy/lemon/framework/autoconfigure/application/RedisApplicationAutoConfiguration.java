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

package com.galaxy.lemon.framework.autoconfigure.application;

import com.galaxy.lemon.framework.cumulative.Cumulative;
import com.galaxy.lemon.framework.cumulative.RedisCumulative;
import com.galaxy.lemon.framework.random.BindingTokenRandomTemplate;
import com.galaxy.lemon.framework.random.RandomTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis 应用扩展
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({RedisTemplate.class})
@AutoConfigureAfter(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
public class RedisApplicationAutoConfiguration {

    @Configuration
    @ConditionalOnClass({RandomTemplate.class, BindingTokenRandomTemplate.class})
    @ConditionalOnBean(name = "stringRedisTemplate")
    public static class BindingTokenRandomTemplateConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public RandomTemplate bindingTokenRandomTemplate(StringRedisTemplate stringRedisTemplate) {
            return new BindingTokenRandomTemplate(stringRedisTemplate);
        }
    }

    @Configuration
    @ConditionalOnClass({Cumulative.class, RedisCumulative.class})
    public static class CumulativeConfiguration {

        @Bean
        @ConditionalOnBean(name = "stringRedisTemplate")
        @ConditionalOnMissingBean
        public Cumulative redisCumulative(RedisTemplate<String, String> stringRedisTemplate) {
            return new RedisCumulative(stringRedisTemplate);
        }
    }

}
