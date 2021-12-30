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

package com.galaxy.lemon.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Locale;

/**
 * 配置文件
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties(prefix = "lemon")
public class LemonProperties {
    private LocaleProperties locale;
    private String[] gateways;

    public LocaleProperties getLocale() {
        return locale;
    }

    public void setLocale(LocaleProperties locale) {
        this.locale = locale;
    }

    public String[] getGateways() {
        return gateways;
    }

    public void setGateways(String[] gateways) {
        this.gateways = gateways;
    }

    public static class LocaleProperties {
        private Locale defaultLocale;
        private List<Locale> supportLocales;
        public Locale getDefaultLocale() {
            return defaultLocale;
        }
        public void setDefaultLocale(Locale defaultLocale) {
            this.defaultLocale = defaultLocale;
        }
        public List<Locale> getSupportLocales() {
            return supportLocales;
        }
        public void setSupportLocales(List<Locale> supportLocales) {
            this.supportLocales = supportLocales;
        }
    }

}

