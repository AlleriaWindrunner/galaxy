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

package com.galaxy.lemon.common.log;

import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class Keywords {
    private static final Logger logger = LoggerFactory.getLogger(Keywords.class);

    public static final Keywords EMPTY  = new Keywords(0);

    private static final String SEPARATOR = "|";
    private StringBuilder stringBuilder;
    private int count;
    private int capacity;

    public Keywords() {
        this(3);
    }

    public Keywords(int capacity) {
        this.stringBuilder = new StringBuilder();
        this.capacity = capacity;
    }

    public void append(String... keywords) {
        if (keywords != null && keywords.length > 0) {
            for (String str : keywords) {
                if (overload()) {
                    break;
                }
                if (StringUtils.isBlank(str)) {
                    continue;
                }
                if (count > 0) {
                    this.stringBuilder.append(SEPARATOR);
                }
                this.stringBuilder.append(str);
                count++;
            }
        }
    }

    public void combine(Keywords keywords) {
        Optional.ofNullable(keywords).map(Keywords::toString).map(s -> StringUtils.split(s, SEPARATOR)).ifPresent(ks -> {
            Arrays.stream(ks).forEach(Keywords.this::append);
        });
    }

    public void safeCombine(Keywords keywords) {
        try {
            combine(keywords);
        } catch (LemonException e) {
        }
    }

    public int getCount() {
        return this.count;
    }

    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public String toString() {
        return this.stringBuilder.toString();
    }

    private boolean overload() {
        if (this.getCount() >= this.getCapacity()) {
            logger.warn("Keywords exceeds its capacity [{}], ignore other.", String.valueOf(this.getCapacity()));
            return true;
        }
        return false;
    }

}
