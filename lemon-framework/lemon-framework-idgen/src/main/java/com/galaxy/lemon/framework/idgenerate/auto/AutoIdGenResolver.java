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

package com.galaxy.lemon.framework.idgenerate.auto;

import com.galaxy.lemon.common.LemonFramework;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.scanner.ClassPathClassFileScanner;
import com.galaxy.lemon.common.utils.AnnotationUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.idgenerate.auto.AutoIdGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class AutoIdGenResolver implements BeanFactoryPostProcessor, EnvironmentAware{
    private static final Logger logger = LoggerFactory.getLogger(AutoIdGenResolver.class);
    
    private static final String DEFAULT_DO_PACKAGE = "";
    private static final String DO_PACKAGE_SEPARATOR = ",";
    private static final String LEMON_DO_PACKAGE = "com.galaxy.lemon.*.entity";
    private static final String DATA_OBJECT_ANNOTATION_NAME = "com.galaxy.lemon.framework.annotation.DataObject";
    private static final String DATA_OBJECT_SUPER_CLASS_NAME = "com.galaxy.lemon.framework.data.BaseDO";
    private static final Map<Class<?>, IdGenMetadata> IDGEN_METEDATA_MAP = new HashMap<>();
    private static final Map<Class<? extends IdGenStrategy>, IdGenStrategy> IDGEN_STRATEGY_MAP = new HashMap<>();

    private final String[] doPackages;

    private Environment environment;

    public AutoIdGenResolver(String doPackage) {
        if (JudgeUtils.isNotBlank(doPackage)) {
            this.doPackages = StringUtils.split(doPackage, DO_PACKAGE_SEPARATOR);
        } else {
            this.doPackages = new String[]{};
        }
    }

    public AutoIdGenResolver(String[] doPackages) {
        this.doPackages = doPackages;
    }
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if(logger.isInfoEnabled()) {
            logger.info("Starting scan DO package {}.", Arrays.stream(resolveDOPackage()).collect(Collectors.joining(DO_PACKAGE_SEPARATOR)));
        }
        ClassPathClassFileScanner scanner = new ClassPathClassFileScanner();
        scanner.scan(this::processDataObject, resolveDOPackage());
    }

    private void processDataObject(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        String className = annotationMetadata.getClassName();
        if (JudgeUtils.isBlank(className)
                || StringUtils.startsWith(className, "java.")
                || StringUtils.startsWith(className, "javax.")
                || StringUtils.startsWith(className, "org.springframework")
                || StringUtils.startsWith(className, "sun.")
                ) {
            return;
        }
        if(isDataObjectClass(annotationMetadata)) {
            if(logger.isInfoEnabled()) {
                logger.info("Found data object class {}", className);
            }
            Class<?> clazz = null;
            try {
                clazz = ReflectionUtils.forName(className);
            } catch (ClassNotFoundException ise) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Class \"{}\" can not be loaded by class loader \"{}\".", className, ReflectionUtils.getDefaultClassLoader());
                }
            }
            Optional.ofNullable(clazz).ifPresent(this::analyseDO);
        }

    }

    private void analyseDO(Class<?> clazz) {
        Field[] fields = ReflectionUtils.getDeclaredFields(clazz);
        Arrays.stream(fields).filter(field -> AnnotationUtils.isAnnotationPresent(field, AutoIdGen.class)).forEach(field -> analyseAutoIdgen(clazz, field));
    }

    private void analyseAutoIdgen(Class<?> clazz, Field field) {
        AutoIdGen autoIdGen = AnnotationUtils.findAnnotation(field, AutoIdGen.class);
        Method setIdMethod = ReflectionUtils.getAccessibleWriteMethodByField(clazz, field);
        IdGenStrategy idGenStrategy;
        if (IDGEN_STRATEGY_MAP.containsKey(autoIdGen.idGenStrategy())) {
            idGenStrategy = IDGEN_STRATEGY_MAP.get(autoIdGen.idGenStrategy());
        } else {
            idGenStrategy = ReflectionUtils.newInstance(autoIdGen.idGenStrategy());
            IDGEN_STRATEGY_MAP.put(autoIdGen.idGenStrategy(), idGenStrategy);
        }
        IDGEN_METEDATA_MAP.put(clazz, new IdGenMetadata(StringUtils.getDefaultIfEmpty(autoIdGen.key(), autoIdGen.value()), autoIdGen.prefix(), setIdMethod, idGenStrategy));
    }
    
    /**
     * 解决自动ID赋值
     * @param obj Data Object
     */
    public void resolveIdGen(Object obj) {
        Class<?> clazz = obj.getClass();
        if(IDGEN_METEDATA_MAP.containsKey(clazz)) {
            IdGenMetadata idGenMetadata = IDGEN_METEDATA_MAP.get(clazz);
            try {
                idGenMetadata.getMethod().invoke(obj, idGenMetadata.getIdGenStrategy().genId(idGenMetadata.getKey(), idGenMetadata.getPrefix()));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new LemonException(e);
            }
        }
    }
    
    private String[] resolveDOPackage() {
        if(JudgeUtils.isEmpty(this.doPackages)) {
            if (JudgeUtils.isBlank(LemonFramework.getScanPackage())) {
                return new String[]{DEFAULT_DO_PACKAGE};
            } else {
                return new String[]{LEMON_DO_PACKAGE, LemonFramework.getScanPackage()};
            }
        }
        String[] configDoPackages = this.doPackages;
        List<String> scanDoPackages = new ArrayList<>();
        scanDoPackages.add(LEMON_DO_PACKAGE);
        for (int i = 0; i< configDoPackages.length; i++) {
            String basePackages = this.environment.resolvePlaceholders(configDoPackages[i]);
            Optional.ofNullable(basePackages).filter(JudgeUtils::isNotEmpty).map(s -> s.split(",")).map(Arrays::asList).ifPresent(scanDoPackages::addAll);
        }
        return scanDoPackages.toArray(new String[scanDoPackages.size()]);
    }

    private boolean isDataObjectClass(AnnotationMetadata annotationMetadata) {
        return annotationMetadata.hasAnnotation(DATA_OBJECT_ANNOTATION_NAME)
                || StringUtils.equals(annotationMetadata.getSuperClassName(), DATA_OBJECT_SUPER_CLASS_NAME);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    static class IdGenMetadata {
        private String key;
        private String prefix;
        private Method method;
        private IdGenStrategy idGenStrategy;
        
        IdGenMetadata(String key, String prefix, Method method, IdGenStrategy idGenStrategy) {
            this.key = key;
            this.prefix = prefix;
            this.method = method;
            this.idGenStrategy = idGenStrategy;
        }
        
        public String getPrefix() {
            return this.prefix;
        }
        public Method getMethod() {
            return this.method;
        }
        IdGenStrategy getIdGenStrategy() {
            return this.idGenStrategy;
        }
        public String getKey() {
            return this.key;
        }
    }

}
