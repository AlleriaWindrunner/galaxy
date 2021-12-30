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

package com.galaxy.lemon.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import springfox.documentation.service.ResponseMessage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties("swagger")
public class SwaggerProperties {
    private boolean show;
    private String description;
    private String version;
    private String contactName;
    private String contactUrl;
    private String contactEmail;
    private String serviceUrl;
    private String scanPackage;
    private String enabledDefaultGlobalRequestParams;
    
    private Map<String, GlobalParam> globalRequestParams = new LinkedHashMap<>();
    private Map<String, ResponseMessage> globalResponseMessage = new LinkedHashMap<>();
    
    
    public boolean isShow() {
        return show;
    }
    public void setShow(boolean show) {
        this.show = show;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public String getContactUrl() {
        return contactUrl;
    }
    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }
    public String getContactEmail() {
        return contactEmail;
    }
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    public String getServiceUrl() {
        return serviceUrl;
    }
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    public String getScanPackage() {
        return scanPackage;
    }
    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }
    public Map<String, GlobalParam> getGlobalRequestParams() {
        return globalRequestParams;
    }
    public void setGlobalRequestParams(Map<String, GlobalParam> globalRequestParams) {
        this.globalRequestParams = globalRequestParams;
    }

    public Map<String, ResponseMessage> getGlobalResponseMessage() {
        return globalResponseMessage;
    }
    public void setGlobalResponseMessage(
            Map<String, ResponseMessage> globalResponseMessage) {
        this.globalResponseMessage = globalResponseMessage;
    }

    public String getEnabledDefaultGlobalRequestParams() {
        return enabledDefaultGlobalRequestParams;
    }

    public void setEnabledDefaultGlobalRequestParams(String enabledDefaultGlobalRequestParams) {
        this.enabledDefaultGlobalRequestParams = enabledDefaultGlobalRequestParams;
    }

    public static class GlobalParam {
        private String name;
        private String desc;
        private String type;
        private String modelRef;
        private String defaultValue;
        private boolean required = false;

        public GlobalParam() {
        }

        public GlobalParam(String desc, String type, String modelRef) {
            this.desc = desc;
            this.type = type;
            this.modelRef = modelRef;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDesc() {
            return desc;
        }
        public void setDesc(String desc) {
            this.desc = desc;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getModelRef() {
            return modelRef;
        }
        public void setModelRef(String modelRef) {
            this.modelRef = modelRef;
        }
        public boolean isRequired() {
            return required;
        }
        public void setRequired(boolean required) {
            this.required = required;
        }
        public String getDefaultValue() {
            return defaultValue;
        }
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
        
    }
}
