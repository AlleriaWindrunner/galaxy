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

package com.galaxy.lemon.framework.data;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.stream.Stream;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DataOperationPrivilegeCheckerPostProcessor implements BeanPostProcessor {

    private static String DEFAULT_PRIVILEGE_PACKAGE ="com.galaxy.lemon";

    private static String[] DEFAULT_IGNORED_CHECK_PACKAGE = new String[]{DEFAULT_PRIVILEGE_PACKAGE, "org.springframework", "java", "javax", "com.netflix"};

    private String[] privilegePackages;

    public DataOperationPrivilegeCheckerPostProcessor() {
        this(new String[]{DEFAULT_PRIVILEGE_PACKAGE});
    }

    public DataOperationPrivilegeCheckerPostProcessor(String[] privilegePackages) {
        this.privilegePackages = privilegePackages;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (! ignoreChecke(bean.getClass())) {
            checkDataOperationPrivilege(bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void checkDataOperationPrivilege(Object bean) {
        Class<?> clazz = bean.getClass();
        if (hasDataOperationPrivilege(clazz)) {
            return;
        }
        while (true) {
            if (Stream.of(clazz.getDeclaredFields()).anyMatch(p -> InternalDataHelper.class.isAssignableFrom(p.getType()))
                    && ! hasDataOperationPrivilege(clazz)) {
                LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR.getMsgCd(), "Business code has no privilege using \"InternalDataHelper\".");
            }
            clazz = clazz.getSuperclass();
            if (clazz == Object.class) {
                break;
            }
        }
    }

    private boolean hasDataOperationPrivilege(Class<?> clazz) {
        return Stream.of(this.privilegePackages).anyMatch(p -> clazz.getName().startsWith(p));
    }

    private boolean ignoreChecke(Class<?> clazz) {
        return Stream.of(DEFAULT_IGNORED_CHECK_PACKAGE).anyMatch(p -> clazz.getName().startsWith(p));
    }

    public static class Registry implements ImportBeanDefinitionRegistrar {

        private static final String BEAN_NAME = "dataOperationPrivilegeCheckerPostProcessor";
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(DataOperationPrivilegeCheckerPostProcessor.class);
            //beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new String[]{DEFAULT_PRIVILEGE_PACKAGE});
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(BEAN_NAME, beanDefinition);

        }
    }

}
