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

import com.galaxy.lemon.framework.config.Mode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties(prefix = "lemon.idgen")
public class IdGenProperties {

    private String prefix;
    private boolean permitUnlimited = false;
    private Map<String, Integer> delta = new LinkedHashMap<>();
    private Map<String, Long> maxValue = new LinkedHashMap<>();
    private Map<String, Long> minValue = new LinkedHashMap<>();
    private Auto auto;
    private Mode mode;

    
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isPermitUnlimited() {
        return permitUnlimited;
    }

    public void setPermitUnlimited(boolean permitUnlimited) {
        this.permitUnlimited = permitUnlimited;
    }

    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    public Map<String, Integer> getDelta() {
        return delta;
    }
    public void setDelta(Map<String, Integer> delta) {
        this.delta = delta;
    }
    public Map<String, Long> getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Map<String, Long> maxValue) {
        this.maxValue = maxValue;
    }

    public Map<String, Long> getMinValue() {
        return minValue;
    }

    public void setMinValue(Map<String, Long> minValue) {
        this.minValue = minValue;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public static class Auto {
        /**
         * @deprecated as of lemon framework 2.0.0, in favor of using {@link #basePackages}
         */
        @Deprecated
        private String DOPackage;
        private String[] basePackages;


        @Deprecated
        public String getDOPackage() {
            return DOPackage;
        }
        @Deprecated
        public void setDOPackage(String dOPackage) {
            DOPackage = dOPackage;
        }

        public String[] getBasePackages() {
            return basePackages;
        }

        public void setBasePackages(String[] basePackages) {
            this.basePackages = basePackages;
        }

    }
}
