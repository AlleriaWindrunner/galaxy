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

package com.galaxy.lemon.framework.autoconfigure.mybatis;

import com.galaxy.lemon.common.IndicatePackages;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.mybatis.LemonMapperScannerRegistrar;
import com.galaxy.lemon.framework.mybatis.typehandler.EnumValueTypeHandler;
import com.galaxy.lemon.framework.valuable.ValuableHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({SqlSessionFactory.class})
@ConditionalOnBean(DataSource.class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfigureAfter({org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class, org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration.class, com.galaxy.lemon.framework.autoconfigure.datasource.DataSourceAutoConfiguration.class, org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration.class})
@Import(MybatisCustomizationBeanPostProcessor.Registrar.class)
public class MybatisAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

    @Configuration
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnClass(LemonMapperScannerRegistrar.class)
    @Import(LemonMapperScannerRegistrar.class)
    public static class LemonMybatisConfiguration {
        private static final String[] DEFAULT_LEMON_BASE_PACKAGE = new String[]{"com.galaxy.lemon"};

        @Bean
        @ConditionalOnMissingBean
        public IndicatePackages lemonMybatisScannerPackages() {
            return new IndicatePackages(DEFAULT_LEMON_BASE_PACKAGE);
        }
    }

    @Bean
    public ConfigurationCustomizer lemonConfigurationCustomizer() {
        return configuration -> processTypeHandler(configuration.getTypeHandlerRegistry());
    }

    private void processTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
        List<Class<?>> valuableEnumClasses = ValuableHelper.getValuableEnumClasses();
        Optional.ofNullable(valuableEnumClasses).filter(JudgeUtils::isNotEmpty).ifPresent(l ->
                l.stream().forEach(c -> registerTypeHandler(c, typeHandlerRegistry)));
    }

    private void registerTypeHandler(Class<?> clazz, TypeHandlerRegistry typeHandlerRegistry) {
        typeHandlerRegistry.register(clazz, EnumValueTypeHandler.class);
        if (logger.isInfoEnabled()) {
            logger.info("Mybatis add type handler {} for enum class {}.", clazz, EnumValueTypeHandler.class);
        }
    }

//    @Configuration
//    @ConditionalOnClass(EnumValueTypeHandler.class)
//    public static class ValuableEnumTypeHandlerConfiguration {
//        private SqlSessionFactory sqlSessionFactory;
//
//        public ValuableEnumTypeHandlerConfiguration(SqlSessionFactory sqlSessionFactory) {
//            this.sqlSessionFactory = sqlSessionFactory;
//        }
//
//        @PostConstruct
//        public void afterPropertySet() {
//            TypeHandlerRegistry typeHandlerRegistry = this.sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
//            List<Class<?>> valuableEnumClasses = ValuableHelper.getValuableEnumClasses();
//            Optional.ofNullable(valuableEnumClasses).filter(JudgeUtils::isNotEmpty).ifPresent(l ->
//            l.stream().forEach(c -> registerTypeHandler(c, typeHandlerRegistry)));
//        }
//
//        private void registerTypeHandler(Class<?> clazz, TypeHandlerRegistry typeHandlerRegistry) {
//            typeHandlerRegistry.register(clazz, EnumValueTypeHandler.class);
//            if (logger.isInfoEnabled()) {
//                logger.info("Mybatis add type handler {} for enum class {}.", clazz, EnumValueTypeHandler.class);
//            }
//        }
//    }

}
