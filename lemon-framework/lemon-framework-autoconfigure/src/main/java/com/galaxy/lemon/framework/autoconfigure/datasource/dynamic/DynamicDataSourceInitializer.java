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

package com.galaxy.lemon.framework.autoconfigure.datasource.dynamic;

import com.galaxy.lemon.framework.autoconfigure.datasource.DataSourceInitializer;
import com.galaxy.lemon.framework.datasource.DynamicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DynamicDataSourceInitializer implements DataSourceInitializer<DynamicDataSource> {
    public static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceInitializer.class);

    public static final String PROPERTY_RESOLVED_DATASOURCES = "resolvedDataSources";

    private List<DataSourceInitializer<?>> dataSourceInitializers;

    public DynamicDataSourceInitializer() {
    }

    public DynamicDataSourceInitializer(List<DataSourceInitializer<?>> dataSourceInitializers) {
        this.dataSourceInitializers = dataSourceInitializers;
    }

    @Override
    public void initialize(DynamicDataSource dynamicDataSource) {
        if (this.dataSourceInitializers == null) {
            if (logger.isInfoEnabled()) {
                logger.info("Could not init dynamic data source because of empty \"dataSourceInitializers\".");
            }
            return;
        }
        DirectFieldAccessFallbackBeanWrapper directFieldAccessFallbackBeanWrapper = new DirectFieldAccessFallbackBeanWrapper(dynamicDataSource);
        Object value = directFieldAccessFallbackBeanWrapper.getPropertyValue(PROPERTY_RESOLVED_DATASOURCES);
        Optional.ofNullable(value).filter(v -> v instanceof Map).map(m -> (Map<Object, DataSource>)m).map(m -> m.values())
                .ifPresent(c -> c.stream().forEach(this::internalInitialize));
    }

    private void internalInitialize(DataSource dataSource) {
        DataSourceInitializer dataSourceInitializer = this.dataSourceInitializers.stream().filter(i -> i.canInitialize(dataSource)).findFirst().orElse(null);
        if (null == dataSourceInitializer) {
            if (logger.isInfoEnabled()) {
                logger.info("Could not init data source {} because of no matched Initializer.", dataSource);
            }
            return;
        }
        dataSourceInitializer.initialize(dataSource);
    }

    public void setDataSourceInitializers(List<DataSourceInitializer<?>> dataSourceInitializers) {
        this.dataSourceInitializers = dataSourceInitializers;
    }

    public List<DataSourceInitializer<?>> getDataSourceInitializers() {
        return dataSourceInitializers;
    }
}
