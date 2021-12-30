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

package com.galaxy.lemon.gateway.zuul;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties("zuul")
public class ZuulExtensionProperties {
    /**
     * Map of route names to properties.
     */
    private Map<String, ZuulRoute> routes = new LinkedHashMap<>();
    
    public Map<String, ZuulRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, ZuulRoute> routes) {
        this.routes = routes;
    }
    
    public ZuulRoute getZuulRoute(String key) {
        return this.routes.get(key);
    }
    
    @PostConstruct
    public void init() {
        for (Entry<String, ZuulRoute> entry : this.routes.entrySet()) {
            ZuulRoute value = entry.getValue();
            if (!StringUtils.hasText(value.getLocation())) {
                value.serviceId = entry.getKey();
            }
            if (!StringUtils.hasText(value.getId())) {
                value.id = entry.getKey();
            }
            if (!StringUtils.hasText(value.getPath())) {
                value.path = "/" + entry.getKey() + "/**";
            }
            if(null == value.getAuthenticated()) {
                value.setAuthenticated(true);
            }
            if(null == value.getSignatured()) {
                value.setSignatured(true);
            }
        }
    }

    public static class ZuulRoute {
        /**
         * The ID of the route (the same as its map key by default).
         */
        private String id;

        /**
         * The path (pattern) for the route, e.g. /foo/**.
         */
        private String path;

        /**
         * The service ID (if any) to map to this route. You can specify a
         * physical URL or a service, but not both.
         */
        private String serviceId;

        /**
         * A full physical URL to map to the route. An alternative is to use a
         * service ID and service discovery to find the physical address.
         */
        private String url;

        /**
         * Flag to determine whether the prefix for this route (the path, minus
         * pattern patcher) should be stripped before forwarding.
         */
        private boolean stripPrefix = true;
        
        /**
         * Flag to determine whether the request send after user authenticated.
         */
        private Boolean authenticated = true;
        
        /**
         * Flag to determine whether the request for this route should be signatured before forwarding
         * 
         */
        private Boolean signatured = true;
        
        private Boolean mercSignatured = false;
        
        /**
         * 通过@PathVariable\form时，参与签名的path Variable
         * 暂时不用
         */
        private String signaturedParameters;
        
        public ZuulRoute(){}
        
        public ZuulRoute(String id, String path, String serviceId,
                String url, boolean stripPrefix, Boolean authenticated,
                Boolean signatured) {
            super();
            this.id = id;
            this.path = path;
            this.serviceId = serviceId;
            this.url = url;
            this.stripPrefix = stripPrefix;
            this.authenticated = authenticated;
            this.signatured = signatured;
        }
        
        public String getLocation() {
            if (StringUtils.hasText(this.url)) {
                return this.url;
            }
            return this.serviceId;
        }

        public void setLocation(String location) {
            if (location != null
                    && (location.startsWith("http:") || location.startsWith("https:"))) {
                this.url = location;
            }
            else {
                this.serviceId = location;
            }
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isStripPrefix() {
            return stripPrefix;
        }

        public void setStripPrefix(boolean stripPrefix) {
            this.stripPrefix = stripPrefix;
        }

        public Boolean getAuthenticated() {
            return authenticated;
        }

        public void setAuthenticated(Boolean authenticated) {
            this.authenticated = authenticated;
        }

        public Boolean getSignatured() {
            return signatured;
        }

        public void setSignatured(Boolean signatured) {
            this.signatured = signatured;
        }

        public String getSignaturedParameters() {
            return signaturedParameters;
        }

        public void setSignaturedParameters(String signaturedParameters) {
            this.signaturedParameters = signaturedParameters;
        }

        public Boolean getMercSignatured() {
            return mercSignatured;
        }

        public void setMercSignatured(Boolean mercSignatured) {
            this.mercSignatured = mercSignatured;
        }
        
    }
}
