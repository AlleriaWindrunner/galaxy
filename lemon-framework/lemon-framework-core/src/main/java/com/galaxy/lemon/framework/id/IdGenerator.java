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

/**
 * 唯一顺序号生成器接口
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface IdGenerator {
    /**
     *
     * 生成ID
     * idName 对应的ID在application 范围内要唯一
     * 如果不需要多个应用共用ID生成器，建议使用此方法生成ID
     *
     * @param idName ID name
     * @return ID
     */
    String generateId(String idName);

    /**
     * 在全局范围内唯一ID
     * @param idName ID name
     * @return ID
     */
    String generateGlobalId(String idName);
}
