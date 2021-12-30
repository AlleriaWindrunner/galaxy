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

package com.galaxy.lemon.framework.context;

import com.galaxy.lemon.common.LemonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 错误扫描路径告警
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ConfigurationWarningsApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationWarningsApplicationContextInitializer.class);


    @Override
    public void initialize(ConfigurableApplicationContext context) {
        context.addBeanFactoryPostProcessor(
                new ConfigurationWarningsPostProcessor(getChecks()));
    }

    /**
     * Returns the checks that should be applied.
     * @return the checks to apply
     */
    protected Check[] getChecks() {
        return new Check[] { new ComponentScanPackageCheck() };
    }

    /**
     * {@link BeanDefinitionRegistryPostProcessor} to report warnings.
     */
    protected final static class ConfigurationWarningsPostProcessor
            implements PriorityOrdered, BeanDefinitionRegistryPostProcessor {

        private Check[] checks;

        public ConfigurationWarningsPostProcessor(Check[] checks) {
            this.checks = checks;
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE - 1;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                throws BeansException {
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
                throws BeansException {
            for (Check check : this.checks) {
                String message = check.getWarning(registry);
                if (StringUtils.hasLength(message)) {
                    warn(message);
                }
            }

        }

        private void warn(String message) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("%n%n** WARNING ** : %s%n%n", message));
            }
        }

    }

    /**
     * A single check that can be applied.
     */
    protected interface Check {

        /**
         * Returns a warning if the check fails or {@code null} if there are no problems.
         * @param registry the {@link BeanDefinitionRegistry}
         * @return a warning message or {@code null}
         */
        String getWarning(BeanDefinitionRegistry registry);

    }

    /**
     * {@link Check} for {@code @ComponentScan} on problematic package.
     */
    protected static class ComponentScanPackageCheck implements Check {

        private static final Set<String> PROBLEM_PACKAGES;

        static {
            Set<String> packages = new HashSet<>();
            packages.add(LemonConstants.FRAMEWORK_BASE_PACKAGE);
            packages.add(LemonConstants.COMPANY_BASE_PACKAGE);
            PROBLEM_PACKAGES = Collections.unmodifiableSet(packages);
        }

        @Override
        public String getWarning(BeanDefinitionRegistry registry) {
            Set<String> scannedPackages = getComponentScanningPackages(registry);
            List<String> problematicPackages = getProblematicPackages(scannedPackages);
            if (problematicPackages.isEmpty()) {
                return null;
            }
            return "Your ApplicationContext is unlikely to "
                    + "start due to a @ComponentScan of "
                    + StringUtils.collectionToDelimitedString(problematicPackages, ", ")
                    + ".";
        }

        protected Set<String> getComponentScanningPackages(
                BeanDefinitionRegistry registry) {
            Set<String> packages = new LinkedHashSet<String>();
            String[] names = registry.getBeanDefinitionNames();
            for (String name : names) {
                BeanDefinition definition = registry.getBeanDefinition(name);
                if (definition instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition annotatedDefinition = (AnnotatedBeanDefinition) definition;
                    addComponentScanningPackages(packages,
                            annotatedDefinition.getMetadata());
                }
            }
            return packages;
        }

        private void addComponentScanningPackages(Set<String> packages,
                                                  AnnotationMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata
                    .getAnnotationAttributes(ComponentScan.class.getName(), true));
            if (attributes != null) {
                addPackages(packages, attributes.getStringArray("value"));
                addPackages(packages, attributes.getStringArray("basePackages"));
                addClasses(packages, attributes.getStringArray("basePackageClasses"));
                if (packages.isEmpty()) {
                    packages.add(ClassUtils.getPackageName(metadata.getClassName()));
                }
            }
        }

        private void addPackages(Set<String> packages, String[] values) {
            if (values != null) {
                Collections.addAll(packages, values);
            }
        }

        private void addClasses(Set<String> packages, String[] values) {
            if (values != null) {
                for (String value : values) {
                    packages.add(ClassUtils.getPackageName(value));
                }
            }
        }

        private List<String> getProblematicPackages(Set<String> scannedPackages) {
            List<String> problematicPackages = new ArrayList<String>();
            for (String scannedPackage : scannedPackages) {
                if (isProblematicPackage(scannedPackage)) {
                    problematicPackages.add(getDisplayName(scannedPackage));
                }
            }
            return problematicPackages;
        }

        private boolean isProblematicPackage(String scannedPackage) {
            if (scannedPackage == null || scannedPackage.isEmpty()) {
                return true;
            }
            return PROBLEM_PACKAGES.contains(scannedPackage);
        }

        private String getDisplayName(String scannedPackage) {
            if (scannedPackage == null || scannedPackage.isEmpty()) {
                return "the default package";
            }
            return "'" + scannedPackage + "'";
        }

    }
}
