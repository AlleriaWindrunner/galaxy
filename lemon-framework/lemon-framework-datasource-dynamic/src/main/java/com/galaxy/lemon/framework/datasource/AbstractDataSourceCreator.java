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

import com.galaxy.lemon.common.utils.BeanUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.framework.datasource.DataSourceCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractDataSourceCreator implements DataSourceCreator {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractDataSourceCreator.class);

    @Deprecated
    protected static final String DATA_SOURCE_PREFIX = "dataSource";

    protected static final String LEMON_DATA_SOURCES_PREFIX = "lemon.dataSources";

    protected static final String DRIVER_CLASS_NAME = "driverClassName";
    protected static final String URL = "url";
    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";
    protected static final String TYPE = "type";

    @Override
    public boolean support(String dataSourceName, Environment environment) {
        RelaxedPropertyResolver dataSourcesPropertyResolver = getMultiDataSourcesPropertyResolver(environment);
        Map<String, Object> dataSourceProperties = dataSourcesPropertyResolver.getSubProperties("." + dataSourceName + ".");
        return doSupport(dataSourceName, dataSourceProperties);
    }

    protected boolean doSupport(String dataSourceName, Map<String, Object> dataSourceProperties) {
        return false;
    }

    @Override
    public DataSource createDataSource(String dataSourceName, Environment environment) {
        RelaxedPropertyResolver dataSourcesPropertyResolver = getMultiDataSourcesPropertyResolver(environment);
        Map<String, Object> dataSourceProperties = dataSourcesPropertyResolver.getSubProperties("." + dataSourceName + ".");
        DataSource dataSource = initDataSource(dataSourceProperties);
        dataBinder(dataSourceName, dataSource, environment);
        return dataSource;
    }


    /**
     * 初始化数据源
     * @param dataSourceProperties
     * @return
     */
    @SuppressWarnings("unchecked")
    protected DataSource initDataSource(Map<String, Object> dataSourceProperties) {
        String driverClassName = dataSourceProperties.get(DRIVER_CLASS_NAME).toString();
        String url = dataSourceProperties.get(URL).toString();
        String username = dataSourceProperties.get(USERNAME).toString();
        String password = dataSourceProperties.get(PASSWORD).toString();
        String type = dataSourceProperties.get(TYPE).toString();
        Class<DataSource> dataSourceType = (Class<DataSource>)ReflectionUtils.forNameThrowRuntimeExceptionIfNecessary(type);
        return DataSourceBuilder.create().driverClassName(driverClassName).url(url).username(username).password(password).type(dataSourceType).build();
    }

    /**
     * 数据源数据绑定
     * @param dataSourceName
     * @param dataSource
     * @param environment
     */
    protected void dataBinder(String dataSourceName, DataSource dataSource, Environment environment) {
        String [] disallowedFields = new String[]{DRIVER_CLASS_NAME, URL, USERNAME, PASSWORD, TYPE};
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
        dataBinder.setIgnoreNestedProperties(false);
        dataBinder.setIgnoreInvalidFields(false);
        dataBinder.setIgnoreUnknownFields(true);
        dataBinder.setDisallowedFields(disallowedFields);
        Map<String, Object> dataSourcePropertyValues = new HashMap<>(getMultiDataSourcesPropertyResolver(environment, dataSourceName).getSubProperties("."));
        processPropertyValues(dataSourcePropertyValues);
        dataBinder.bind(new MutablePropertyValues(dataSourcePropertyValues));
        if (logger.isInfoEnabled()) {
            Map<String, Object> protectedDataSourceProperties = BeanUtils.copyPropertiesReturnDest(new HashMap<>(), dataSourcePropertyValues);
            protectedDataSourceProperties.put(PASSWORD, "PROTECTED");
            logger.info("Creating dataSource {} using parameters {}", dataSource, protectedDataSourceProperties);
        }
    }

    /**
     * 加工属性值
     * @param dataSourcePropertyValues
     */
    protected abstract void processPropertyValues(Map<String, Object> dataSourcePropertyValues);

    /**
     * 兼容1.0.0
     * @param environment
     * @param subProperties
     * @return
     */
    protected RelaxedPropertyResolver getMultiDataSourcesPropertyResolver(Environment environment, String... subProperties){
        StringBuilder propertyName = new StringBuilder();
        if (useV1Config(environment)) {
            propertyName.append(DATA_SOURCE_PREFIX);
        } else {
            propertyName.append(LEMON_DATA_SOURCES_PREFIX);
        }
        if (JudgeUtils.isNotEmpty(subProperties)) {
            for (String p : subProperties) {
                propertyName.append(".").append(p);
            }
        }
        return new RelaxedPropertyResolver(environment, propertyName.toString());
    }

    //兼容1.0.0
    private boolean useV1Config(Environment environment) {
        RelaxedPropertyResolver rpr = new RelaxedPropertyResolver(environment, DATA_SOURCE_PREFIX);
        return JudgeUtils.isNotEmpty(rpr.getSubProperties("."));
    }
}
