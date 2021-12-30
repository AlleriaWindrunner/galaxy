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

package com.galaxy.lemon.framework.springcloud.fegin;

import com.galaxy.lemon.common.HttpMethod;
import com.galaxy.lemon.framework.annotation.QueryBody;
import feign.MethodMetadata;
import org.springframework.cloud.netflix.feign.AnnotatedParameterProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 处理 @QueryBody
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class QueryBodyParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<QueryBody> ANNOTATION = QueryBody.class;

    private QueryBodyParameterParser queryBodyParameterParser;

    public QueryBodyParameterProcessor(QueryBodyParameterParser queryBodyParameterParser) {
        this.queryBodyParameterParser = queryBodyParameterParser;
    }

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {
        MethodMetadata metadata = context.getMethodMetadata();
        if(! HttpMethod.GET.toString().equals(metadata.template().method())) {
            throw new IllegalArgumentException("Annotation \"@QueryBody\" can only be used for http \"GET\" method, illegal method \"" +method.getName() + "\"");
        }
        int parameterIndex = context.getParameterIndex();
        Class<?> parameterType = method.getParameterTypes()[parameterIndex];
        queryBodyParameterParser.parseQueryBodyParameter(parameterType, context);
        return false;
    }

}
