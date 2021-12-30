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

package com.galaxy.lemon.framework.interceptor.support;

import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.InternalDataHelper;
import com.galaxy.lemon.framework.data.LemonDataInitializer;
import com.galaxy.lemon.framework.data.instantiator.LemonDataInstantiator;
import com.galaxy.lemon.framework.interceptor.support.AbstractTradeLemonDataCustomizer;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultTradeLemonDataCustomizer extends AbstractTradeLemonDataCustomizer {

    public DefaultTradeLemonDataCustomizer(LemonDataInstantiator<?> lemonDataInstantiator,
                                           LemonDataInitializer lemonDataInitializer,
                                           InternalDataHelper internalDataHelper) {
        super(lemonDataInstantiator, lemonDataInitializer, internalDataHelper);
    }

    @Override
    public <D extends BaseDTO, L extends BaseLemonData> void doCustomizeRequestEntry(D baseDTO, L lemonData) {

    }

    @Override
    protected <D extends BaseDTO, L extends BaseLemonData> void doCustomizeRequestNonEntry(D baseDTO, L lemonData) {

    }

    @Override
    protected <R extends BaseDTO & ConfigurableAlerting, L extends BaseLemonData> void doCustomizeResponse(R responseDTO, L lemonData) {

    }
}
