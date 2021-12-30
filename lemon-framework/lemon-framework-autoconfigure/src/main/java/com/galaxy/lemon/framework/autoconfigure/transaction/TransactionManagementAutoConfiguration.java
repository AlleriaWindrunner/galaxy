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

package com.galaxy.lemon.framework.autoconfigure.transaction;

import com.galaxy.lemon.framework.autoconfigure.datasource.DataSourceAutoConfiguration;
import com.galaxy.lemon.framework.autoconfigure.transaction.DataSourceTransactionManagementConfigurer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({DataSourceTransactionManager.class, DataSource.class})
@ConditionalOnBean(DataSource.class)
@EnableTransactionManagement(proxyTargetClass=true)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class TransactionManagementAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TransactionManagementConfigurer.class)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DataSourceTransactionManagementConfigurer transactionManagementConfiguration(DataSource dataSource) {
        return new DataSourceTransactionManagementConfigurer(dataSource);
    }
}
