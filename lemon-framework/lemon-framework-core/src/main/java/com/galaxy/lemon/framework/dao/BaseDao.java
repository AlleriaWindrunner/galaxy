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

package com.galaxy.lemon.framework.dao;

import com.galaxy.lemon.framework.data.DOBasicOperation;

import java.util.List;

/**
 * dao 接口
 * 所有的DAO都必须继承或实现此接口
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface BaseDao<T extends DOBasicOperation, K> {
    /**
     * 根据ID查找
     * @param id
     * @return
     */
    T get(K id);
    
    /**
     * 根据条件查找
     * @param entity
     * @return
     */
    List<T> find(T entity);
    
    /**
     * 插入数据
     * @param entity
     * @return
     */
    int insert(T entity);
    
    /**
     * 更新数据
     * @param entity
     * @return
     */
    int update(T entity);
    
    /**
     * 删除数据
     * @param id
     * @return
     */
    int delete(K id);
}
