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

package com.galaxy.lemon.framework.random;

import com.galaxy.lemon.common.utils.RandomUtils;

/**
 * 随机数操作模版
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface RandomTemplate {
    /**
     * 申请随机数
     * @param key       申请随机数对应的key
     * @param leaseTime 自动失效时间,单位毫秒
     * @param randomType
     * @param length
     * @return
     */
    String apply(String key, int leaseTime, RandomType randomType, int length);
    
    /**
     * 获取随机数，只能获取一次
     * @param key
     * @return
     */
    String acquireOnce(String key);
    
    /**
     * 验证一次失效
     * @param key
     * @param random
     * @return
     */
    boolean validateOnce(String key, String random);
    
    enum RandomType {
        NUMERIC, LETTER, ASCII, NUMERIC_LETTER
    }
    
    default String genRandom(RandomType randomType, int length) {
        String random = null;
        switch (randomType) {
        case NUMERIC:
            random = RandomUtils.randomNumeric(length);
            break;
        case LETTER:
            random = RandomUtils.randomLetterFixLength(length);
            break;
        case ASCII:
            random = RandomUtils.randomAsciiFixLength(length);
            break;
        case NUMERIC_LETTER:
            random = RandomUtils.randomStringFixLength(length);
            break;
        default:
            break;
        }
        return random;
    }
}
