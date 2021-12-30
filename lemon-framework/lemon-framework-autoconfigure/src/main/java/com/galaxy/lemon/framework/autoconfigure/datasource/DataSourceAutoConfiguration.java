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

package com.galaxy.lemon.framework.autoconfigure.datasource;

import com.galaxy.lemon.framework.autoconfigure.datasource.druid.DruidDataSourceInitializer;
import com.galaxy.lemon.framework.autoconfigure.datasource.dynamic.DynamicDataSourceInitializer;
import com.galaxy.lemon.framework.datasource.DynamicDataSource;
import com.galaxy.lemon.framework.datasource.EnableDynamicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass(DataSource.class)
@Import(DataSourceAutoConfiguration.Druid.class)
public class DataSourceAutoConfiguration {
    public static final Logger logger = LoggerFactory.getLogger(DataSourceAutoConfiguration.class);

    @Configuration
    @ConditionalOnClass(DynamicDataSource.class)
    @EnableDynamicDataSource
    public static class DynamicDataSourceConfiguration{

        @Bean
        public DynamicDataSourceInitializer dynamicDataSourceInitializer() {
            return new DynamicDataSourceInitializer();
        }

    }

    @ConditionalOnClass(com.alibaba.druid.pool.DruidDataSource.class)
    static class Druid extends AbstractDataSourceConfiguration {

        @Bean
        @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource")
        @ConfigurationProperties(prefix = "spring.datasource.druid")
        public com.alibaba.druid.pool.DruidDataSource dataSource(DataSourceProperties properties) {
            com.alibaba.druid.pool.DruidDataSource dataSource = createDataSource(
                    properties, com.alibaba.druid.pool.DruidDataSource.class);
            return dataSource;
        }

        @Bean
        public DruidDataSourceInitializer druidDataSourceInitializer() {
            return new DruidDataSourceInitializer();
        }

    }

    @Configuration
    public static class InitializationDataSource {

        private List<DataSource> dataSources;
        private List<DataSourceInitializer> dataSourceInitializers;

        public InitializationDataSource(ObjectProvider<List<DataSource>> dataSources, ObjectProvider<List<DataSourceInitializer>> dataSourceInitializers) {
            this.dataSources = dataSources.getIfAvailable();
            this.dataSourceInitializers = dataSourceInitializers.getIfAvailable();
            processDynamicDataSource(this.dataSourceInitializers);
        }

        private void processDynamicDataSource(List<DataSourceInitializer> dataSourceInitializers) {
            if (null == dataSourceInitializers) return;
            List<DataSourceInitializer<?>> otherDataSourceInitializers = new ArrayList<>();
            DynamicDataSourceInitializer dynamicDataSourceInitializer = null;
            for (DataSourceInitializer dataSourceInitializer : dataSourceInitializers) {
                if (dataSourceInitializer instanceof DynamicDataSourceInitializer) {
                    dynamicDataSourceInitializer = (DynamicDataSourceInitializer)dataSourceInitializer;
                } else {
                    otherDataSourceInitializers.add(dataSourceInitializer);                }
            }
            if (null != dynamicDataSourceInitializer && null == dynamicDataSourceInitializer.getDataSourceInitializers()) {
                dynamicDataSourceInitializer.setDataSourceInitializers(otherDataSourceInitializers);
            }
        }

        @Bean
        public CommandLineRunner initializationDataSourceCommandLineRunner(){

            return args -> {
                Optional.ofNullable(this.dataSources).ifPresent(s -> s.stream().forEach(this::init));
            };

        }

        private void init(DataSource dataSource) {
            DataSourceInitializer dataSourceInitializer = this.dataSourceInitializers.stream().filter(i -> i.canInitialize(dataSource)).findFirst().orElse(null);
            if (null == dataSourceInitializer) {
                if (logger.isInfoEnabled()) {
                    logger.info("Could not init data source {} because of no matched Initializer.", dataSource);
                }
                return;
            }
            dataSourceInitializer.initialize(dataSource);
        }
    }

    static class AbstractDataSourceConfiguration {

        protected <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
            return (T) properties.initializeDataSourceBuilder().type(type).build();
        }
    }

}
