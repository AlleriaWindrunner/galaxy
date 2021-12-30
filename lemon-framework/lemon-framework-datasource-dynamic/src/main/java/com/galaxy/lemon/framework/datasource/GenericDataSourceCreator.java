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

import com.galaxy.lemon.framework.datasource.AbstractDataSourceCreator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * 通用数据源创建者
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Order
public class GenericDataSourceCreator extends AbstractDataSourceCreator {

    @Override
    public boolean support(String dataSourceName, Environment environment) {
        return true;
    }

    @Override
    protected void processPropertyValues(Map<String, Object> dataSourcePropertyValues) {

    }

}
