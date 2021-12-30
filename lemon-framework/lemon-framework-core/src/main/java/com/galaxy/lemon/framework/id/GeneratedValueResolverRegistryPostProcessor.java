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

import com.galaxy.lemon.common.Invoker;
import com.galaxy.lemon.common.LemonFramework;
import com.galaxy.lemon.common.scanner.ClassPathClassFileScanner;
import com.galaxy.lemon.common.scanner.ScannerCallback;
import com.galaxy.lemon.common.utils.AnnotationUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */


public class GeneratedValueResolverRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor,EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(GeneratedValueResolverRegistryPostProcessor.class);

    private static final String PROPERTY_NAME_SCAN_PACKAGE = "lemon.idgen.auto.basePackages";
    private static final String DEFAULT_DO_PACKAGE = "com.galaxy.lemon";
    private static final String DO_PACKAGE_SEPARATOR = ",";
    private static final String LEMON_DO_PACKAGE = "com.galaxy.lemon.framework.*.entity";
    private static final String DATA_OBJECT_ANNOTATION_NAME = "com.galaxy.lemon.framework.annotation.DataObject";
    private static final String DATA_OBJECT_SUPER_CLASS_NAME = "com.galaxy.lemon.framework.data.BaseDO";

    private static final Map<Class<? extends GeneratorStrategy>, GeneratorStrategy> IDGEN_STRATEGY_MAP = new HashMap<>();

    private final String[] doPackages;

    private BeanDefinitionRegistry registry;
    private Environment environment;

    public GeneratedValueResolverRegistryPostProcessor() {
        this.doPackages = new String[]{};
    }

    public GeneratedValueResolverRegistryPostProcessor(String doPackage) {
        if (JudgeUtils.isNotBlank(doPackage)) {
            this.doPackages = StringUtils.split(doPackage, DO_PACKAGE_SEPARATOR);
        } else {
            this.doPackages = new String[]{};
        }
    }

    public GeneratedValueResolverRegistryPostProcessor(String[] doPackages) {
        this.doPackages = Optional.ofNullable(doPackages).orElseGet(() -> new String[]{});
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if(logger.isInfoEnabled()) {
            logger.info("Starting scan DO package {} by \"GeneratedValueResolverRegistryPostProcessor\"..", Arrays.stream(resolveDOPackage(beanFactory)).collect(Collectors.joining(DO_PACKAGE_SEPARATOR)));
        }
        Map<Class<?>, List<Invoker>> invokers = new HashMap<>(1);
        ClassPathClassFileScanner scanner = new ClassPathClassFileScanner();
        scanner.scan((mr, mrf) -> processDataObject(mr, mrf, invokers), resolveDOPackage(beanFactory));

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(SimpleGeneratedValueResolver.class);
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, invokers);
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition("generatedValueResolver", beanDefinition);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    private void processDataObject(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory, Map<Class<?>, List<Invoker>> invokers) {
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        String className = annotationMetadata.getClassName();
        if (ScannerCallback.isIgnoredClass(className)) return;
        if(isDataObjectClass(annotationMetadata)) {
            if(logger.isInfoEnabled()) {
                logger.info("Found data object class {}", className);
            }
            Class<?> clazz = null;
            try {
                clazz = ReflectionUtils.forName(className);
            } catch (ClassNotFoundException ise) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Class \"{}\" can not be loaded by classpath {}.", className, ReflectionUtils.getDefaultClassLoader());
                }
            }
            Optional.ofNullable(clazz).ifPresent(c -> analyseDO(c, invokers));
        }

    }

    private void analyseDO(Class<?> clazz, Map<Class<?>, List<Invoker>> invokers) {
        Field[] fields = ReflectionUtils.getDeclaredFields(clazz);
        Arrays.stream(fields).filter(this::isRequiredGeneratedValue).forEach(field -> {
            Invoker invoker = analyseGeneratedValue(clazz, field);
            List<Invoker> invokersForClass = invokers.get(clazz);
            if (null == invokersForClass) {
                invokersForClass = new ArrayList<>();
                invokers.put(clazz, invokersForClass);
            }
            invokersForClass.add(invoker);
        });
    }

    private boolean isRequiredGeneratedValue(Field field) {
        return AnnotationUtils.isAnnotationPresent(field, GeneratedValue.class);
    }

    private Invoker analyseGeneratedValue(Class<?> clazz, Field field) {
        GeneratedValue generatedValue = AnnotationUtils.findAnnotation(field, GeneratedValue.class);
        Method setIdMethod = ReflectionUtils.getAccessibleWriteMethodByField(clazz, field);
        GeneratorStrategy idGenStrategy;
        if (IDGEN_STRATEGY_MAP.containsKey(generatedValue.generatorStrategy())) {
            idGenStrategy = IDGEN_STRATEGY_MAP.get(generatedValue.generatorStrategy());
        } else {
            idGenStrategy = ReflectionUtils.newInstance(generatedValue.generatorStrategy());
            IDGEN_STRATEGY_MAP.put(generatedValue.generatorStrategy(), idGenStrategy);
        }
        return new SimpleGeneratedValueResolver.GeneratedValueInvoker(generatedValue.value(), generatedValue.prefix(), setIdMethod, idGenStrategy);
    }

    private String[] resolveDOPackage(ConfigurableListableBeanFactory beanFactory) {
        String[] configDoPackages = null;
        if (JudgeUtils.isEmpty(this.doPackages)) {
            String scanPackageStrs = this.environment.getProperty(PROPERTY_NAME_SCAN_PACKAGE);
            configDoPackages = Optional.ofNullable(scanPackageStrs).filter(StringUtils::isNotBlank).map(s -> s.split(DO_PACKAGE_SEPARATOR)).orElseGet(
                    () -> Optional.ofNullable(LemonFramework.getScanPackage()).filter(StringUtils::isNotBlank).map(s -> new String[]{s}).orElseGet(
                            () -> Optional.ofNullable(AutoConfigurationPackages.get(beanFactory)).map(a -> a.toArray(new String[a.size()])).orElseGet(() -> new String[]{DEFAULT_DO_PACKAGE})
                    )
            );
        }

        if (JudgeUtils.isEmpty(configDoPackages)) {
            return new String[]{LEMON_DO_PACKAGE};
        }

        String[] scanDoPackages = new String[configDoPackages.length + 1];
        scanDoPackages[0] = LEMON_DO_PACKAGE;
        if(JudgeUtils.isNotEmpty(configDoPackages)) {
            System.arraycopy(configDoPackages, 0, scanDoPackages, 1, configDoPackages.length);
        }
        return scanDoPackages;
    }

    private boolean isDataObjectClass(AnnotationMetadata annotationMetadata) {
        return annotationMetadata.hasAnnotation(DATA_OBJECT_ANNOTATION_NAME)
                || StringUtils.equals(annotationMetadata.getSuperClassName(), DATA_OBJECT_SUPER_CLASS_NAME);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
