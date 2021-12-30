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

import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.framework.data.DOBasicOperation;
import com.galaxy.lemon.framework.idgenerate.auto.AutoIdGenResolver;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Dao层切面
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Aspect
@Configuration
public class DaoAspect {
    private static final Logger logger = LoggerFactory.getLogger(DaoAspect.class);
    
    @Autowired
    private AutoIdGenResolver autoIdGenResolver;

    @Pointcut("execution (* com.cmpay..*Dao.*(..))")
    public void anyCmapyDaoMethod(){
    }
    
    @Pointcut("@within(org.apache.ibatis.annotations.Mapper)")
    public void anyMapperMethod(){}
    
    @Around("anyCmapyDaoMethod()||anyMapperMethod()")
    public Object doAroundDao(ProceedingJoinPoint pjp) throws Throwable{
        Instant startInstant = Instant.now();
        Signature signature = pjp.getSignature();
        String methodName = signature.getName();

        Object obj;
        try{
            Object[] args = pjp.getArgs();
            if(StringUtils.startsWith(methodName, "insert")) {
                Stream.of(args).filter(arg -> arg instanceof DOBasicOperation).forEach(autoIdGenResolver::resolveIdGen);
            }
            obj = pjp.proceed();
        }catch(Throwable e){
            throw e;
        }finally{
            if(logger.isTraceEnabled()) {
                logger.trace("It takes '{}'/ms to executing method '{}'.", DateTimeUtils.durationMillis(startInstant, Instant.now()), signature.toString());
            }
        }
        return obj;
    }
}
