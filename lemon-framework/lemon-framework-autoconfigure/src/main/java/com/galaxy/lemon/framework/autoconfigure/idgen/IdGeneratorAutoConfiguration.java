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

package com.galaxy.lemon.framework.autoconfigure.idgen;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.autoconfigure.idgen.redis.IdGeneratorRedisConfiguration;
import com.galaxy.lemon.framework.config.Mode;
import com.galaxy.lemon.framework.dao.DaoAspect;
import com.galaxy.lemon.framework.id.GeneratedValueResolverRegistryPostProcessor;
import com.galaxy.lemon.framework.id.IdGenProperties;
import com.galaxy.lemon.framework.id.IdGenerator;
import com.galaxy.lemon.framework.id.SimpleIdGenerator;
import com.galaxy.lemon.framework.idgenerate.auto.AutoIdGenResolver;
import com.galaxy.lemon.framework.idgenerate.redis.RedisHashIdGenerator;
import com.galaxy.lemon.framework.idgenerate.redis.RedisStringIdGenerator;
import com.galaxy.lemon.framework.utils.IdGenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link IdGenerator}.
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(IdGenerator.class)
@EnableConfigurationProperties(IdGenProperties.class)
@Import(IdGeneratorRedisConfiguration.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class IdGeneratorAutoConfiguration {
    public static final String IDGEN_REDIS_TEMPLATE_BEAN_ANME = "idgenRedisTemplate";
    public static final String IDGEN_REDIS_CONNECTION_FACTORY_BEAN_NAME = "redisConnectionFactory";

    public static final Integer DEFAULT_MSG_ID_DELTA = 1000;
    public static final Integer DEFAULT_REQUEST_ID_DELTA = 1000;

    public static final Long DEFAULT_REQUEST_ID_MAX_VAL = 9999999999L;
    public static final Long DEFAULT_MSG_ID_MAX_VAL = 9999999999L;

    private IdGenProperties idGenProperties;

    @Autowired
    public IdGeneratorAutoConfiguration(IdGenProperties idGenProperties) {
        this.idGenProperties = idGenProperties;
    }

    @PostConstruct
    public void afterPropertySet() {
        JudgeUtils.callbackIfNecessary(JudgeUtils.isNull(this.idGenProperties.getDelta().get(IdGenUtils.MSG_ID_PREFIX_KEY)), () -> this.idGenProperties.getDelta().put(IdGenUtils.MSG_ID_PREFIX_KEY, DEFAULT_MSG_ID_DELTA));
        JudgeUtils.callbackIfNecessary(JudgeUtils.isNull(this.idGenProperties.getDelta().get(IdGenUtils.REQUEST_ID_PREFIX_KEY)), () -> this.idGenProperties.getDelta().put(IdGenUtils.REQUEST_ID_PREFIX_KEY, DEFAULT_REQUEST_ID_DELTA));
        JudgeUtils.callbackIfNecessary(JudgeUtils.isNull(this.idGenProperties.getMaxValue().get(IdGenUtils.MSG_ID_PREFIX_KEY)), () -> this.idGenProperties.getMaxValue().put(IdGenUtils.MSG_ID_PREFIX_KEY, DEFAULT_REQUEST_ID_MAX_VAL));
        JudgeUtils.callbackIfNecessary(JudgeUtils.isNull(this.idGenProperties.getMaxValue().get(IdGenUtils.REQUEST_ID_PREFIX_KEY)), () -> this.idGenProperties.getMaxValue().put(IdGenUtils.REQUEST_ID_PREFIX_KEY, DEFAULT_MSG_ID_MAX_VAL));
        JudgeUtils.callbackIfNecessary(JudgeUtils.isNull(this.idGenProperties.getMode()), () -> this.idGenProperties.setMode(Mode.SINGLE));

    }

//    @Configuration
//    @ConditionalOnClass({RedisConnectionFactory.class})
//    @ConditionalOnBean(name=IDGEN_REDIS_CONNECTION_FACTORY_BEAN_NAME)
//    @Conditional(CreateRedisTemplateConditional.class)
//    public static class RedisIdGeneratorConfiguration {
//
//        @Bean
//        @ConditionalOnMissingBean(name = IDGEN_REDIS_TEMPLATE_BEAN_ANME)
//        public RedisTemplate<String, Long> idgenRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//            RedisTemplate<String, Long> template =  new RedisTemplate<>();
//            template.setConnectionFactory(redisConnectionFactory);
//            template.setKeySerializer( new StringRedisSerializer() );
//            template.setHashValueSerializer( new GenericToStringSerializer<>( Long.class ) );
//            template.setValueSerializer( new GenericToStringSerializer<>( Long.class ) );
//            return template;
//        }
//
//    }

    @Bean
    @ConditionalOnProperty(prefix = "lemon.idgen", name = "generator", havingValue = "simple")
    public IdGenerator idGenerator(IdGenProperties idGenProperties) {
        return new SimpleIdGenerator(idGenProperties);
    }

    @Configuration
    @ConditionalOnClass({RedisHashIdGenerator.class})
    @ConditionalOnProperty(prefix = "lemon.idgen", name = "generator", havingValue = "redisHash")
    public static class RedisHashIdGeneratorConfiguration {

        @Bean
        public IdGenerator idGenerator(RedisTemplate<String, Long> idgenRedisTemplate, IdGenProperties idGenProperties) {
            try{
                RedisClusterConnection redisClusterConnection = idgenRedisTemplate.getConnectionFactory().getClusterConnection();
                if (null != redisClusterConnection) {
                    idGenProperties.setMode(Mode.CLUSTER);
                }
            }catch (InvalidDataAccessApiUsageException invalidDataAccessApiUsageException) {
            }catch (Exception e) {
            }
            return new RedisHashIdGenerator(idgenRedisTemplate, idGenProperties);
        }
    }

    @Configuration
    @ConditionalOnClass({RedisStringIdGenerator.class})
    @ConditionalOnProperty(prefix = "lemon.idgen", name = "generator", havingValue = "redisString", matchIfMissing = true)
    public static class RedisStringIdGeneratorConfiguration {

        @Bean
        public IdGenerator idGenerator(RedisTemplate<String, Long> idgenRedisTemplate, IdGenProperties idGenProperties) {
            try{
                RedisClusterConnection redisClusterConnection = idgenRedisTemplate.getConnectionFactory().getClusterConnection();
                if (null != redisClusterConnection) {
                    idGenProperties.setMode(Mode.CLUSTER);
                }
            }catch (InvalidDataAccessApiUsageException invalidDataAccessApiUsageException) {
            }catch (Exception e) {
            }
            return new RedisStringIdGenerator(idgenRedisTemplate, idGenProperties);
        }
    }

    @Configuration
    @ConditionalOnClass({AutoIdGenResolver.class, DaoAspect.class})
    @ConditionalOnProperty(value = "lemon.idgen.auto.enabled")
    public static class AutoIdGenResolverConfiguration {

        //doPackage 为兼容1.0.0版本
        @Bean
        public static AutoIdGenResolver autoIdGenResolver(@Value("${lemon.idgen.auto.basePackages:}") String basePackages) {
            return new AutoIdGenResolver(basePackages);
        }

    }

    @Configuration
    @ConditionalOnClass(GeneratedValueResolverRegistryPostProcessor.class)
    @ConditionalOnProperty(value = "lemon.idgen.auto.enabled", matchIfMissing = true)
    public static class GeneratedValueResolverConfiguration {

        @Bean
        public static GeneratedValueResolverRegistryPostProcessor generatedValueResolverRegistryPostProcessor() {
            return new GeneratedValueResolverRegistryPostProcessor();
        }
    }
//
//    @Order
//    public static class CreateRedisTemplateConditional extends AnyNestedCondition {
//        CreateRedisTemplateConditional() {
//            super(ConfigurationPhase.REGISTER_BEAN);
//        }
//
//        @ConditionalOnClass(RedisHashIdGenerator.class)
//        static class HashRedisIdGenerator {
//        }
//
//        @ConditionalOnClass(RedisStringIdGenerator.class)
//        static class StringRedisIdGenerator {
//        }
//    }
}



