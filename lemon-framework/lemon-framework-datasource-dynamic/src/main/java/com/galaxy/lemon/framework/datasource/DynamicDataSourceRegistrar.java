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

package com.galaxy.lemon.framework.datasource;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.extension.SPIExtensionLoader;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.OrderUtils;
import com.galaxy.lemon.framework.datasource.DataSourceCreator;
import com.galaxy.lemon.framework.datasource.EnableDynamicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.galaxy.lemon.common.utils.ReflectionUtils.forNameThrowRuntimeExceptionIfNecessary;
import static com.galaxy.lemon.common.utils.ReflectionUtils.newInstance;

/**
 * 动态数据源
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DynamicDataSourceRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceRegistrar.class);
    //兼容1.0
    protected static final String PROPERTY_PREFIX_DATASOURCE = "dataSource";
    protected static final String PROPERTY_PREFIX_LEMON_DATASOURCES = "lemon.dataSources";

    private static final String PROPERTY_PREFIX_DYNAMIC_DATASOURCE = "lemon.dynamicDataSource.";
    private Environment environment;
    //默认数据源名称
    private String defaultDataSourceName = "primary";
    // 默认数据源
    private DataSource defaultDataSource;
    // 动态数据源
    private Map<String, DataSource> dynamicDataSources = new HashMap<>();
    private List<DataSourceCreator> dataSourceCreators = OrderUtils.sortByOrder(SPIExtensionLoader.getExtensionServices(DataSourceCreator.class));

    {
        dataSourceCreators.add(newInstance((Class<DataSourceCreator>) forNameThrowRuntimeExceptionIfNecessary("com.galaxy.lemon.framework.datasource.GenericDataSourceCreator")));
    }

    /**
     * 加载多数据源配置
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    /* 
     * 注册动态数据源
     * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.data.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            findDefaultDataSource(importingClassMetadata);
            registerDataSources();
            registerDynamicDataSource(registry);
        } catch (DisableDynamicDataSourceException disableDynamicDataSourceException) {
            if (logger.isWarnEnabled()) {
                logger.warn("Warning encountered during dynamic dataSource registration. ==> {}", disableDynamicDataSourceException.getMsgInfo());
            }
        }
    }
    
    private void findDefaultDataSource(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> defaultAttrs = importingClassMetadata.getAnnotationAttributes(EnableDynamicDataSource.class.getName(), true);
        if(defaultAttrs.containsKey("defaultDataSource")) {
            String defaultDataSource = (String) defaultAttrs.get("defaultDataSource");
            if(JudgeUtils.isNotBlank(defaultDataSource)) {
                this.defaultDataSourceName = defaultDataSource;
            }
        }
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(this.environment, PROPERTY_PREFIX_DYNAMIC_DATASOURCE);
        if (JudgeUtils.isNotBlank(propertyResolver.getProperty("defaultDataSource"))) {
            this.defaultDataSourceName = propertyResolver.getProperty("defaultDataSource");
        } else {
            RelaxedPropertyResolver dynamicDatasourcePropertyResolver = new RelaxedPropertyResolver(this.environment, PROPERTY_PREFIX_DYNAMIC_DATASOURCE);
            String firstDataSourceInConfiguration = Optional.ofNullable(dynamicDatasourcePropertyResolver.getProperty("dataSources")).map(d -> d.split(",")).map(ns -> ns[0])
                .orElseGet(() -> {
                    Map<String,Object> dataSourceMap = new RelaxedPropertyResolver(this.environment, "dataSource").getSubProperties(".");
                    return JudgeUtils.isEmpty(dataSourceMap) ? null : dataSourceMap.keySet().iterator().next().split("\\.")[0];
                });
            if (JudgeUtils.isNotBlank(firstDataSourceInConfiguration)) {
                this.defaultDataSourceName = firstDataSourceInConfiguration;
            }
        }
        if(logger.isInfoEnabled()) {
            logger.info("default data source name is {}", this.defaultDataSourceName);
        }
    }
    
    private void registerDataSources() {

        RelaxedPropertyResolver dynamicDatasourcePropertyResolver = new RelaxedPropertyResolver(this.environment, PROPERTY_PREFIX_DYNAMIC_DATASOURCE);

        String[] dataSourceNames = Optional.ofNullable(dynamicDatasourcePropertyResolver.getProperty("dataSources")).map(d -> d.split(","))
                .orElseGet(this::resolveDataSourceNames);

        if (JudgeUtils.isEmpty(dataSourceNames)) {
           // throw new LemonException("No dynamic dataSource was found in configuration, please check your configuration. If you do not use dynamic dataSource, set the property \"lemon.dynamicDataSource.enabled\" to false.");
            throw new DisableDynamicDataSourceException("No dynamic dataSource was found in configuration, please check your configuration. If you do not use dynamic dataSource, ignore this warning or set the property \"lemon.dynamicDataSource.enabled\" to false.");
        }

        for (String datasourceName : dataSourceNames) {// 多个数据源
            if(JudgeUtils.isBlank(datasourceName)) {
                continue;
            }

            DataSource dataSource = createDataSource(datasourceName);
            // 设置默认数据源
            if(JudgeUtils.equals(this.defaultDataSourceName, datasourceName)) {
                defaultDataSource = dataSource;
            } else {
                dynamicDataSources.put(datasourceName, dataSource);
            }
            if(logger.isInfoEnabled()) {
                logger.info("[dynamic] create data source {} ==> {}", datasourceName, dataSource);
            }
        }
    }

    private void registerDynamicDataSource(BeanDefinitionRegistry registry) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(this.defaultDataSourceName, defaultDataSource);
        targetDataSources.putAll(dynamicDataSources);
        
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        beanDefinition.setPrimary(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetDataSources);
        registry.registerBeanDefinition("dataSource", beanDefinition);
    }
    /**
     * 初始化数据源
     * @param dataSourceName 数据源名称
     * @return  数据源
     */
    private DataSource createDataSource(String dataSourceName) {
        DataSource dataSource = null;
        for(DataSourceCreator dataSourceCreator : dataSourceCreators) {
            if(dataSourceCreator.support(dataSourceName, this.environment)) {
                dataSource = dataSourceCreator.createDataSource(dataSourceName, this.environment);
                break;
            }
        }
        return Optional.ofNullable(dataSource).orElseThrow(() -> LemonException.create(LemonException.SYS_ERROR_MSGCD, "No DataSourceCreator was found at creating datasource " + dataSourceName));
    }

    private String[] resolveDataSourceNames() {
        RelaxedPropertyResolver datasourceNamesResolver = new RelaxedPropertyResolver(this.environment, PROPERTY_PREFIX_LEMON_DATASOURCES);
        Map<String, Object> dataSourceProperties = datasourceNamesResolver.getSubProperties(".");
        //兼容1.0
        if(JudgeUtils.isEmpty(dataSourceProperties)) {
            datasourceNamesResolver = new RelaxedPropertyResolver(this.environment, PROPERTY_PREFIX_DATASOURCE);
            dataSourceProperties = datasourceNamesResolver.getSubProperties(".");
        }
        return Optional.ofNullable(dataSourceProperties).filter(JudgeUtils::isNotEmpty).map(ds -> ds.keySet().stream()
                .filter(JudgeUtils::isNotBlank).map(k -> k.split("\\.")[0]).distinct().collect(Collectors.toList())).map(dsl -> dsl.toArray(new String[dsl.size()])).orElse(null);
    }

    private static class DisableDynamicDataSourceException extends LemonException {
        private static final long serialVersionUID = -3057505902216195353L;

        DisableDynamicDataSourceException(String msgInfo) {
            super(ErrorMsgCode.WARNING.getMsgCd(), msgInfo);
        }
    }

}
