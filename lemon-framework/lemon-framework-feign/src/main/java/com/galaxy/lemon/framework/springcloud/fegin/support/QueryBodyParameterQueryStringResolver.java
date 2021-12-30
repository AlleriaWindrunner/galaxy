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

package com.galaxy.lemon.framework.springcloud.fegin.support;

import com.galaxy.lemon.common.HttpMethod;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.AnnotationUtils;
import com.galaxy.lemon.common.utils.ClassUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.framework.annotation.NestQueryBody;
import com.galaxy.lemon.framework.data.BaseDTO;
import com.galaxy.lemon.framework.data.PropertyWrapper;
import com.galaxy.lemon.framework.data.support.RelaxedPropertyWrapper;
import com.galaxy.lemon.framework.springcloud.fegin.QueryBodyParameterParser;
import com.galaxy.lemon.framework.springcloud.fegin.QueryBodyParameterResolver;
import feign.Param;
import feign.RequestTemplate;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cloud.netflix.feign.AnnotatedParameterProcessor;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class QueryBodyParameterQueryStringResolver implements QueryBodyParameterParser, QueryBodyParameterResolver {
    private static final String NEST_OBJECT_PATH_SEPARATOR = LemonConstants.DOT;
    private static final String HTTP_HEADER_SERVICE_NAME = "x-lemon-svrnm";
    private static final String PROPERTY_NAME_CLASS = "class";
    private static final String PROPERTY_NAME_BODY = LemonConstants.BASE_DTO_PROPERTY_BODY;
    private Map<String, Properties> parametersProperties = new HashMap<>();
    private Map<Class<?>, Map<String, NestQueryBodyMetadata>> nestQueryBodyMetadatas = new HashMap<>();
    private List<String> ignoredPropertyNames;

    public QueryBodyParameterQueryStringResolver(List<String> ignoredPropertyNames) {
        this.ignoredPropertyNames = new ArrayList<>();
        this.ignoredPropertyNames.addAll(ignoredPropertyNames);
        this.ignoredPropertyNames.add(PROPERTY_NAME_CLASS);
        this.ignoredPropertyNames.remove(PROPERTY_NAME_BODY);//body 属性特殊处理
    }

    /**
     *
     * @param requestTemplate
     * @param objectParameter
     * @return true : query string ； false ：default
     */
    @Override
    public boolean resolveQueryBodyParameter(RequestTemplate requestTemplate, Object objectParameter, Param.Expander expander) {
        if(! HttpMethod.GET.toString().equals(requestTemplate.method())) {
            return false;
        }
        String serviceName = Optional.ofNullable(requestTemplate.headers()).map(m -> m.get(this.getHttpHeaderNameServiceName())).map(c -> c.iterator().next()).orElse(null);
        if(null == serviceName || ! hasParameterProperties(serviceName)) {
            return false;
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(objectParameter);
        getParameterProperties(serviceName).stream().forEach(p -> doResolveQueryBodyParameter(requestTemplate, expander, beanWrapper, p));
        return true;
    }

    @Override
    public void parseQueryBodyParameter(Class<?> parameterType, AnnotatedParameterProcessor.AnnotatedParameterContext context) {
        if(hasParameterProperties(context.getMethodMetadata().configKey())) {
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR.getMsgCd(), "Reduplicate feign client service \"" + context.getMethodMetadata().configKey() +"\"");
        }
        addParameterProperties(context.getMethodMetadata().configKey(), parameterType);
        context.getMethodMetadata().template().header(this.getHttpHeaderNameServiceName(), context.getMethodMetadata().configKey());
        parseNestQueryBody(parameterType);
    }

    protected void parseNestQueryBody(Class<?> parameterType) {
        Arrays.stream(ReflectionUtils.getAllFields(parameterType, BaseDTO.class)).filter(f -> AnnotationUtils.isAnnotationPresent(f, NestQueryBody.class))
                .forEach(f -> this.addNestQueryBody(parameterType, f.getName(), f.getType()));
    }

    private void doResolveQueryBodyParameter(RequestTemplate requestTemplate, Param.Expander expander, BeanWrapper beanWrapper, String propertyName) {
        Object propertyValue = beanWrapper.getPropertyValue(propertyName);
        if (null == propertyValue) return;
        NestQueryBodyMetadata nestQueryBodyMetadata = this.getNestQueryBodyMetadata(beanWrapper.getWrappedClass(), propertyName);
        if (null == nestQueryBodyMetadata) {
            requestTemplate.query(propertyName, expand(expander, propertyValue));
        } else {
            nestQueryBodyMetadata.getNestedPaths().stream().forEach(p -> requestTemplate.query(p , expand(expander, beanWrapper.getPropertyValue(p)) ));
        }
    }

    protected void addNestQueryBody(Class<?> parameterType, String propertyName, Class<?> propertyType) {
        LemonException.throwLemonExceptionIfNecessary(ClassUtils.isPrimitiveOrWrapper(propertyType), ErrorMsgCode.SYS_ERROR.getMsgCd(), "@NestQueryBody can only be specified on an non primitive field.");
        Map<String, NestQueryBodyMetadata> nestQueryBodyMetadataMap = this.nestQueryBodyMetadatas.get(parameterType);
        if (null == nestQueryBodyMetadataMap) {
            nestQueryBodyMetadataMap = new HashMap<>();
            this.nestQueryBodyMetadatas.put(parameterType, nestQueryBodyMetadataMap);
        }
        if (nestQueryBodyMetadataMap.containsKey(propertyName)) {
            return;
        }
        nestQueryBodyMetadataMap.put(propertyName, NestQueryBodyMetadata.resolve(propertyType, propertyName));
    }

    private NestQueryBodyMetadata getNestQueryBodyMetadata(Class<?> nestClass, String propertyName) {
        Map<String, NestQueryBodyMetadata> nestQueryBodyMetadataMap = this.nestQueryBodyMetadatas.get(nestClass);
        if (null == nestQueryBodyMetadataMap) {
            return null;
        }
        return nestQueryBodyMetadataMap.get(propertyName);
    }

    /**
     * 是否存在参数属性
     * @param serviceName
     * @return
     */
    protected boolean hasParameterProperties(String serviceName) {
        return parametersProperties.containsKey(serviceName);
    }

    /**
     * 添加参数属性
     * @param serviceName
     * @param parameterType
     */
    protected void addParameterProperties(String serviceName, Class<?> parameterType) {
        parametersProperties.put(serviceName, new Properties(parameterType));
    }

    /**
     * 获取参数属性
     * @param serviceName
     * @return
     */
    protected Properties getParameterProperties(String serviceName) {
        return parametersProperties.get(serviceName);
    }

    private String expand(Param.Expander expander, Object target) {
        return expander.expand(target);
    }

    private String getHttpHeaderNameServiceName() {
        return HTTP_HEADER_SERVICE_NAME;
    }

    private boolean nonExcludeProperty(String propertyName) {
        return ! this.ignoredPropertyNames.stream().anyMatch(s -> s.equals(propertyName));
    }

    /**
     * 解析class属性
     * @author yuzhou
     * @date 2018/4/9
     * @time 09:45
     * @since 3.0.0
     */
    class Properties extends ArrayList<String> {
        Properties(Class<?> clazz) {
            Arrays.stream(ReflectionUtils.getPropertyDescriptors(clazz)).map(PropertyDescriptor::getName).filter(QueryBodyParameterQueryStringResolver.this::nonExcludeProperty).forEach(this::add);
        }
    }

    /**
     * 嵌套查询对象元数据
     * @author yuzhou
     * @date 2018/7/10
     * @time 18:19
     * @since 3.0.0
     */
    static class NestQueryBodyMetadata {
        private Class<?> nestClass;
        private List<String> nestedPaths;

        public NestQueryBodyMetadata(Class<?> nestClass, List<String> nestedPaths) {
            this.nestClass = nestClass;
            this.nestedPaths = nestedPaths;
        }

        public static NestQueryBodyMetadata resolve(Class<?> nestType, String propertyName) {
            PropertyWrapper propertyWrapper = new RelaxedPropertyWrapper(nestType);
            String[] nestPropertyNames = propertyWrapper.getProperties();
            if (JudgeUtils.isEmpty(nestPropertyNames)) {
                LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Class specified by @NestQueryBody must be have properties.");
            }
            List<String> nestPaths = Stream.of(nestPropertyNames).map(p -> propertyName + NEST_OBJECT_PATH_SEPARATOR + p).collect(Collectors.toList());
            return new NestQueryBodyMetadata(nestType, nestPaths);
        }

        public Class<?> getNestClass() {
            return nestClass;
        }

        public void setNestClass(Class<?> nestClass) {
            this.nestClass = nestClass;
        }

        public List<String> getNestedPaths() {
            return nestedPaths;
        }

        public void setNestedPaths(List<String> nestedPaths) {
            this.nestedPaths = nestedPaths;
        }
    }

}
