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

package com.galaxy.lemon.framework.data.instantiator;

import com.galaxy.lemon.common.ConfigurableBeanName;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.instantiator.CommandDTOInstantiator;
import com.galaxy.lemon.framework.data.instantiator.LemonDataInstantiator;

/**
 * 聚合lemon框架所有data 实例化接口
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface AggregatedDataInstantiator<D extends BaseDTO, R extends BaseDTO & ConfigurableAlerting, C extends BaseDTO &ConfigurableBeanName, L extends BaseLemonData> extends GenericDTOInstantiator<D>, ResponseDTOInstantiator<R>,CommandDTOInstantiator<C>,LemonDataInstantiator<L> {
}
