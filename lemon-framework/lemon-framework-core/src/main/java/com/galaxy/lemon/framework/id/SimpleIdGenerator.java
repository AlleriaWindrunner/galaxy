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

package com.galaxy.lemon.framework.id;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class SimpleIdGenerator extends AbstractIdGenerator {

    private Map<String, AtomicLong> idCountMap = new ConcurrentHashMap<>();

    public SimpleIdGenerator(IdGenProperties idGenProperties) {
        super(idGenProperties);
    }

    @Override
    public String generateId(String idName) {
        return String.valueOf(this.incrementAndGet(idName));
    }

    @Override
    public String generateGlobalId(String idName) {
        return String.valueOf(this.incrementAndGet(idName));
    }

    private Long incrementAndGet(String idName) {
        AtomicLong idCount = this.idCountMap.get(idName);
        if (null == idCount) {
            synchronized (this.idCountMap) {
                idCount = this.idCountMap.get(idName);
                if (null == idCount) {
                    idCount = new AtomicLong();
                    this.idCountMap.put(idName, idCount);
                }
            }
        }
        long value = idCount.incrementAndGet();
        long maxValue = this.getMaxValue(idName);
        if (-1 != maxValue && value > maxValue) {
            synchronized (this.idCountMap) {
                idCount = this.idCountMap.get(idName);
                value = idCount.incrementAndGet();
                if (-1 != maxValue && value > maxValue) {
                    AtomicLong newIdCount = new AtomicLong();
                    value = newIdCount.incrementAndGet();
                    this.idCountMap.put(idName, newIdCount);
                }
            }
        }
        return value;
    }

    private AtomicLong newIdCount(String idName) {
        Long minValue = this.getMinValue(idName);
        return new AtomicLong(minValue);
    }
}
