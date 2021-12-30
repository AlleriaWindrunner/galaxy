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

package com.galaxy.lemon.framework.autoconfigure.dao;

import com.galaxy.lemon.common.exception.ExceptionConverter;
import com.galaxy.lemon.framework.autoconfigure.idgen.IdGeneratorAutoConfiguration;
import com.galaxy.lemon.framework.dao.BeanFactoryDaoAdvisor;
import com.galaxy.lemon.framework.dao.DaoInterceptor;
import com.galaxy.lemon.framework.id.GeneratedValueResolver;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.dao.DataAccessException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@AutoConfigureAfter(IdGeneratorAutoConfiguration.class)
@Import(DaoAspectConfiguration.class)
public class DaoAutoConfiguration {

    @Configuration
    @ConditionalOnClass({Mapper.class})
    public static class DaoAopConfiguration {
        private GeneratedValueResolver generatedValueResolver;
        private List<Class<? extends Annotation>> annotations;
        private List<String> daoQualifiedMethodNameMaches;

        public DaoAopConfiguration(GeneratedValueResolver generatedValueResolver) {
            this.generatedValueResolver = generatedValueResolver;
            annotations = new ArrayList<>();
            annotations.add(Mapper.class);

            daoQualifiedMethodNameMaches = new ArrayList<>();
            daoQualifiedMethodNameMaches.add("com.galaxy..*Dao.*");
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public DaoInterceptor daoInterceptor() {
            DaoInterceptor interceptor = new DaoInterceptor(this.generatedValueResolver);
            return interceptor;
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public BeanFactoryDaoAdvisor daoAdvisor() {
            BeanFactoryDaoAdvisor advisor = new BeanFactoryDaoAdvisor(this.annotations, this.daoQualifiedMethodNameMaches);
            advisor.setAdvice(daoInterceptor());
            advisor.setOrder(1);
            return advisor;
        }
    }

    @Bean
    @ConditionalOnClass(DataAccessException.class)
    public ExceptionConverter dataAccessExceptionConverter() {
        return new DataAccessExceptionConverter();
    }


}
