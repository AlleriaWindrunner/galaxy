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

package com.galaxy.lemon.gateway.zuul.filter;

import com.netflix.zuul.ZuulFilter;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class ZuulFilterCreatingSupport {

    protected ZuulFilter createZuulFilter(boolean shouldFilter, FilterType filterType, int filterOrder, Supplier<Object> supplier) {
        return createZuulFilter(() -> shouldFilter, filterType, filterOrder, supplier);
    }

    protected ZuulFilter createZuulFilter(BooleanSupplier shouldFilter, FilterType filterType, int filterOrder, Supplier<Object> supplier) {
        return new ZuulFilter(){
            @Override
            public boolean shouldFilter() {
                return shouldFilter.getAsBoolean();
            }

            @Override
            public Object run() {
                return supplier.get();
            }

            @Override
            public String filterType() {
                return filterType.lowerCaseName();
            }

            @Override
            public int filterOrder() {
                return filterOrder;
            }
        };
    }
}
