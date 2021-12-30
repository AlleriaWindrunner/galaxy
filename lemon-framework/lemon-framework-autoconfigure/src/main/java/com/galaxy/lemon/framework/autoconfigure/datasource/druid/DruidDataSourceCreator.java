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

package com.galaxy.lemon.framework.autoconfigure.datasource.druid;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.framework.datasource.AbstractDataSourceCreator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
public class DruidDataSourceCreator extends AbstractDataSourceCreator {
    public static final String DATA_SOURCE_TYPE = "com.alibaba.druid.pool.DruidDataSource";
    public static final String DATA_SOURCE_KEY_TYPE = "type";

    public static final String DATA_SOURCE_KEY_INITIAL_SIZE = "initialSize";
    public static final String DATA_SOURCE_KEY_MIN_IDLE = "minIdle";
    public static final String DATA_SOURCE_KEY_MAX_ACTIVE = "maxActive";
    public static final String DATA_SOURCE_KEY_MAX_WAIT = "maxWait";
    public static final String DATA_SOURCE_KEY_TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
    public static final String DATA_SOURCE_KEY_MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
    public static final String DATA_SOURCE_KEY_VALIDATION_QUERY = "validationQuery";
    public static final String DATA_SOURCE_KEY_TEST_WHILE_IDLE = "testWhileIdle";
    public static final String DATA_SOURCE_KEY_TEST_ON_BORROW = "testOnBorrow";
    public static final String DATA_SOURCE_KEY_TEST_ON_RETURN = "testOnReturn";
    public static final String DATA_SOURCE_KEY_POOL_PREPARED_STATEMENTS = "poolPreparedStatements";
    public static final String DATA_SOURCE_KEY_MAX_POOL_PREPARED_STATEMENTS = "maxOpenPreparedStatements";
    public static final String DATA_SOURCE_KEY_MAX_POOL_PREPARED_STATEMENT_PER_CONNECTION_SIZE = "maxPoolPreparedStatementPerConnectionSize";
    public static final String DATA_SOURCE_KEY_FILTERS = "filters";
    public static final String DATA_SOURCE_KEY_CONNECTION_PROPERTIES = "connectionProperties";
    public static final String DATA_SOURCE_KEY_USE_GLOBAL_DATA_SOURCE_STAT = "useGlobalDataSourceStat";
    public static final String DATA_SOURCE_KEY_REMOVE_ABANDONED = "removeAbandoned";
    public static final String DATA_SOURCE_KEY_LOG_ABANDONED = "logAbandoned";
    public static final String DATA_SOURCE_KEY_REMOVE_ABANDONED_TIMEOUT_MILLIS = "removeAbandonedTimeoutMillis";

    public static final Integer DATA_SOURCE_VAL_INITIAL_SIZE = 5;
    public static final Integer DATA_SOURCE_VAL_MIN_IDLE = 5;
    public static final Integer DATA_SOURCE_VAL_MAX_ACTIVE = 20;
    public static final Integer DATA_SOURCE_VAL_MAX_WAIT = 10000;
    public static final Integer DATA_SOURCE_VAL_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 60000;
    public static final Integer DATA_SOURCE_VAL_MIN_EVICTABLE_IDLE_TIME_MILLIS = 300000;
    public static final String DATA_SOURCE_VAL_VALIDATION_QUERY_MYSQL = "SELECT 1";
    public static final String DATA_SOURCE_VAL_VALIDATION_QUERY_ORACLE = "SELECT 1 FROM DUAL";

    public static final Boolean DATA_SOURCE_VAL_TEST_WHILE_IDLE = true;
    public static final Boolean DATA_SOURCE_VAL_TEST_ON_BORROW = false;
    public static final Boolean DATA_SOURCE_VAL_TEST_ON_RETURN = false;
    public static final Boolean DATA_SOURCE_VAL_POOL_PREPARED_STATEMENTS = true;
    public static final Integer DATA_SOURCE_VAL_MAX_POOL_PREPARED_STATEMENTS = 10;
    public static final Integer DATA_SOURCE_VAL_MAX_POOL_PREPARED_STATEMENT_PER_CONNECTION_SIZE = 20;
    public static final String DATA_SOURCE_VAL_FILTERS = "stat,wall";
    public static final String DATA_SOURCE_VAL_CONNECTION_PROPERTIES = "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000";
    public static final Boolean DATA_SOURCE_VAL_USE_GLOBAL_DATA_SOURCE_STAT = true;
    public static final Boolean DATA_SOURCE_VAL_REMOVE_ABANDONED = false;
    public static final Boolean DATA_SOURCE_VAL_LOG_ABANDONED = true;

    @Override
    protected boolean doSupport(String dataSourceName, Map<String, Object> dataSourceProperties) {
        return JudgeUtils.equals(DATA_SOURCE_TYPE, dataSourceProperties.get(DATA_SOURCE_KEY_TYPE));
    }

    @Override
    protected void processPropertyValues(Map<String, Object> dataSourcePropertyValues) {
        if(JudgeUtils.isNull(dataSourcePropertyValues)) {
            dataSourcePropertyValues = new HashMap<>(30);
        }
        setDefaultValueIfNecessary(dataSourcePropertyValues);
    }

    private void setDefaultValueIfNecessary(Map<String, Object> dataSourcePropertyValues) {
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_INITIAL_SIZE )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_INITIAL_SIZE, DATA_SOURCE_VAL_INITIAL_SIZE);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_MIN_IDLE )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_MIN_IDLE, DATA_SOURCE_VAL_MIN_IDLE);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_MAX_ACTIVE )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_MAX_ACTIVE, DATA_SOURCE_VAL_MAX_ACTIVE);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_MAX_WAIT )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_MAX_WAIT, DATA_SOURCE_VAL_MAX_WAIT);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_TIME_BETWEEN_EVICTION_RUNS_MILLIS )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_TIME_BETWEEN_EVICTION_RUNS_MILLIS, DATA_SOURCE_VAL_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_MIN_EVICTABLE_IDLE_TIME_MILLIS )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_MIN_EVICTABLE_IDLE_TIME_MILLIS, DATA_SOURCE_VAL_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_VALIDATION_QUERY )) {
            if (ClassUtils.isPresent("com.mysql.jdbc.Driver", ReflectionUtils.getDefaultClassLoader())) {
                dataSourcePropertyValues.put(DATA_SOURCE_KEY_VALIDATION_QUERY, DATA_SOURCE_VAL_VALIDATION_QUERY_MYSQL);
            } else if (ClassUtils.isPresent("oracle.jdbc.driver.OracleDriver", ReflectionUtils.getDefaultClassLoader())) {
                dataSourcePropertyValues.put(DATA_SOURCE_KEY_VALIDATION_QUERY, DATA_SOURCE_VAL_VALIDATION_QUERY_ORACLE);
            }
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_TEST_WHILE_IDLE )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_TEST_WHILE_IDLE, DATA_SOURCE_VAL_TEST_WHILE_IDLE);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_TEST_ON_BORROW )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_TEST_ON_BORROW, DATA_SOURCE_VAL_TEST_ON_BORROW);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_TEST_ON_RETURN )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_TEST_ON_RETURN, DATA_SOURCE_VAL_TEST_ON_RETURN);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_POOL_PREPARED_STATEMENTS )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_POOL_PREPARED_STATEMENTS, DATA_SOURCE_VAL_POOL_PREPARED_STATEMENTS);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_MAX_POOL_PREPARED_STATEMENTS )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_MAX_POOL_PREPARED_STATEMENTS, DATA_SOURCE_VAL_MAX_POOL_PREPARED_STATEMENTS);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_MAX_POOL_PREPARED_STATEMENT_PER_CONNECTION_SIZE )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_MAX_POOL_PREPARED_STATEMENT_PER_CONNECTION_SIZE, DATA_SOURCE_VAL_MAX_POOL_PREPARED_STATEMENT_PER_CONNECTION_SIZE);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_FILTERS )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_FILTERS, DATA_SOURCE_VAL_FILTERS);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_CONNECTION_PROPERTIES )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_CONNECTION_PROPERTIES, DATA_SOURCE_VAL_CONNECTION_PROPERTIES);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_USE_GLOBAL_DATA_SOURCE_STAT )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_USE_GLOBAL_DATA_SOURCE_STAT, DATA_SOURCE_VAL_USE_GLOBAL_DATA_SOURCE_STAT);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_REMOVE_ABANDONED )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_REMOVE_ABANDONED, DATA_SOURCE_VAL_REMOVE_ABANDONED);
        }
        if(requireDefaultValue(dataSourcePropertyValues, DATA_SOURCE_KEY_LOG_ABANDONED )) {
            dataSourcePropertyValues.put(DATA_SOURCE_KEY_LOG_ABANDONED, DATA_SOURCE_VAL_LOG_ABANDONED);
        }

    }

    private boolean requireDefaultValue(Map<String, Object> dataSourcePropertyValues, String propertyKey) {
        return ! dataSourcePropertyValues.containsKey(propertyKey);
    }

}
