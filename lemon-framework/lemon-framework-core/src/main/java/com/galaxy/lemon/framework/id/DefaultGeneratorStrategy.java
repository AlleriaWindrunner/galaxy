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

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.Validate;
import com.galaxy.lemon.framework.utils.IdGenUtils;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultGeneratorStrategy implements GeneratorStrategy {
    @Override
    public String generatedValue(String key, String prefix) {
        Validate.notBlank(key, "Property \"key\" or \"value\" cloud not be blank in @AutoIdGen");
        if(JudgeUtils.isBlank(prefix)){
            return IdGenUtils.generateCommonId(key);
        }
        return IdGenUtils.generateIdWithShortDate(key, prefix, IdGenUtils.getCommonIdSeqLength());
    }

}
