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

package com.galaxy.lemon.framework.datasource;

import com.galaxy.lemon.framework.datasource.DynamicDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringValueResolver;

/**
 * 动态数据源AOP
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@Aspect
public class DynamicDataSourceAspect implements EmbeddedValueResolverAware, Ordered {
    private Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    private StringValueResolver stringValueResolver;
    
    @Before("@annotation(targetDataSource)")
    public void changeDataSource(JoinPoint joinPoint, TargetDataSource targetDataSource) {
        DynamicDataSource.setDatasource(stringValueResolver.resolveStringValue(targetDataSource.value()));
        if(logger.isDebugEnabled()) {
            logger.debug("dataSource change to {} at method {}." , targetDataSource.value(), resolveMethodName(joinPoint));
        }
    }
    
    @After("@annotation(targetDataSource)")
    public void releaseDataSource(JoinPoint joinPoint, TargetDataSource targetDataSource) {
        DynamicDataSource.clearDatasource();
        if(logger.isDebugEnabled()) {
            logger.debug("release dataSource {} at method {}.", targetDataSource.value(), resolveMethodName(joinPoint));
        }
    }
    
    @AfterThrowing("@annotation(targetDataSource)")
    public void releaseDataSourceAfterThrowing(JoinPoint joinPoint, TargetDataSource targetDataSource) {
        DynamicDataSource.clearDatasource();
        if(logger.isDebugEnabled()) {
            logger.debug("release data source {} at method {} after throwing exception.", targetDataSource.value(), resolveMethodName(joinPoint));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    private String resolveMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }
}
