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

package com.galaxy.lemon.framework.utils;

import com.galaxy.lemon.framework.page.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.galaxy.lemon.framework.data.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 分页辅助类
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class PageUtils {
    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 8;
    private static final Integer CONFIG_PAGE_NUM;
    private static final Integer CONFIG_PAGE_SIZE;

    static {
        CONFIG_PAGE_NUM = Optional.ofNullable(LemonUtils.getProperty("lemon.pagehelper.defaultPageNum")).map(Integer::valueOf).orElseGet(() -> Optional.ofNullable(LemonUtils.getProperty("pagehelper.defaultPageNum")).map(Integer::valueOf).orElse(DEFAULT_PAGE_NUM) );
        CONFIG_PAGE_SIZE = Optional.ofNullable(LemonUtils.getProperty("lemon.pagehelper.defaultPageSize")).map(Integer::valueOf).orElseGet(() -> Optional.ofNullable(LemonUtils.getProperty("pagehelper.defaultPageSize")).map(Integer::valueOf).orElse(DEFAULT_PAGE_SIZE) );
    }

//    /**
//     * 分页查询
//     * @param pageNum   页数
//     * @param pageSize  每页数量
//     * @param count     是否进行count操作，原则上都不允许count操作，请设置为false
//     * @param callback  分页操作
//     *
//     * @return   分页查询结果
//     *
//     * @deprecated as of lemon 2.0.0, in favor of using {@link #pageQuery(int, int, boolean, Supplier)}
//     */
//    @Deprecated
//    public static <T> List<T> pageQuery(int pageNum, int pageSize, boolean count, Callback<List<T>> callback) {
//        try {
//            PageHelper.startPage(pageNum, pageSize, count);
//            return callback.callback();
//        } finally {
//            PageHelper.clearPage();
//        }
//    }

     /**
     * 分页查询
     * @param pageNum   页数
     * @param pageSize  每页数量
     * @param count     是否进行count操作，原则上都不允许count操作，请设置为false
     * @param supplier  分页操作
     *
     * @return   分页查询结果
     */
    public static <T> List<T> pageQuery(int pageNum, int pageSize, boolean count, Supplier<List<T>> supplier) {
        try {
            if(pageNum < 1 ) pageNum = getDefaultPageNum();
            if(pageSize < 1 ) pageSize = getDefaultPageSize();
            PageHelper.startPage(pageNum, pageSize, count);
            return supplier.get();
        } finally {
            PageHelper.clearPage();
        }
    }

    /**
     * 分页查询
     * @param pageable 可分页对象
     * @param count 是否进行count操作，原则上都不允许count操作，请设置为false
     * @param supplier 分页操作
     * @param <T>
     * @return
     */
    public static <T> List<T> pageQuery(Pageable pageable, boolean count, Supplier<List<T>> supplier) {
        return pageQuery(pageable.getPageNum(), pageable.getPageSize(), count, supplier);
    }

//    /**
//     * 分页查询
//     * 不进行count计算
//     * @param pageNum   页数
//     * @param pageSize  每页数量
//     * @param callback  分页操作
//     *
//     * @return   分页查询结果
//     *
//     * @deprecated as of lemon 2.0.0, in favor of using {@link #pageQuery(int, int, Supplier)}
//     */
//    @Deprecated
//    public static <T> List<T> pageQuery(int pageNum, int pageSize, Callback<List<T>> callback) {
//        return pageQuery(pageNum, pageSize, false, callback);
//    }

    /**
     * 分页查询
     * 不进行count计算
     * @param pageNum   页数
     * @param pageSize  每页数量
     * @param supplier  分页操作
     *
     * @return   分页查询结果
     */
    public static <T> List<T> pageQuery(int pageNum, int pageSize, Supplier<List<T>> supplier) {
        return pageQuery(pageNum, pageSize, false, supplier);
    }

    /**
     * 分页查询
     * @param pageable 可分页对象
     * @param supplier 分页操作
     * @param <T>
     * @return
     */
    public static <T> List<T> pageQuery(Pageable pageable, Supplier<List<T>> supplier) {
        return pageQuery(pageable.getPageNum(), pageable.getPageSize(), false, supplier);
    }
    
//    /**
//     * 分页查询
//     * 进行count计算
//     * @param pageNum 页数
//     * @param pageSize 每页数量
//     * @param callback
//     * @return
//     *
//     * @deprecated as of lemon 2.0.0, in favor of using {@link #pageQueryWithCount(int, int, Supplier)}
//     */
//    @Deprecated
//    public static <T> PageInfo<T> pageQueryWithCount(int pageNum, int pageSize, Callback<List<T>> callback) {
//        try {
//            Page<T> page = PageHelper.startPage(pageNum, pageSize, true);
//            callback.callback();
//            return new PageInfo<T>(page);
//        } finally {
//            PageHelper.clearPage();
//        }
//    }

    /**
     * 分页查询
     * 进行count计算
     * @param pageNum 页数
     * @param pageSize  每页数量
     * @param supplier 查询操作
     * @return
     */
    public static <T> PageInfo<T> pageQueryWithCount(int pageNum, int pageSize, Supplier<List<T>> supplier) {
        if(pageNum < 1 ) pageNum = getDefaultPageNum();
        if(pageSize < 1 ) pageSize = getDefaultPageSize();
        try {
            Page<T> page = PageHelper.startPage(pageNum, pageSize, true);
            supplier.get();
            return new PageInfo<T>(page);
        } finally {
            PageHelper.clearPage();
        }
    }

    /**
     *
     * @param pageable 可分页对象
     * @param supplier 查询操作
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> pageQueryWithCount(Pageable pageable, Supplier<List<T>> supplier) {
        return pageQueryWithCount(pageable.getPageNum(), pageable.getPageSize(), supplier);
    }
    
    /**
     * 默认每页数量
     * @return
     */
    public static int getDefaultPageSize() {
        return CONFIG_PAGE_SIZE;
    }
    
    /**
     * 默认页数
     * @return
     */
    public static int getDefaultPageNum() {
        return CONFIG_PAGE_NUM;
    }
}
