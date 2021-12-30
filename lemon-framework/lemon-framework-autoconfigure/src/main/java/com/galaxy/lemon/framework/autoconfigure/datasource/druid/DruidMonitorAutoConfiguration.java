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

package com.galaxy.lemon.framework.autoconfigure.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.galaxy.lemon.framework.autoconfigure.datasource.druid.DruidMonitorProperties;
import com.galaxy.lemon.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({StatViewServlet.class, WebStatFilter.class, DruidDataSource.class})
@EnableConfigurationProperties({DruidMonitorProperties.class})
public class DruidMonitorAutoConfiguration {

    public static final String LOGIN_USER_NAME = "lemon";
    public static final String LOGIN_PASSWORD = "lemon123";
    public static final String STAT_SERVLET_URL_MAPPINGS = "/druid/*";
    public static final String STAT_FILTER_URL_PATTERNS = "/*";

    @Autowired
    public DruidMonitorProperties druidMonitorProperties;

    /**
     * 注册druidServlet
     * @return
     */
    @Bean
    public ServletRegistrationBean druidServletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings(StringUtils.getDefaultIfEmpty(druidMonitorProperties.getStatServletUrlMappings(), STAT_SERVLET_URL_MAPPINGS));
        servletRegistrationBean.addInitParameter("loginUsername", StringUtils.getDefaultIfEmpty(druidMonitorProperties.getLoginUsername(), LOGIN_USER_NAME));
        servletRegistrationBean.addInitParameter("loginPassword", StringUtils.getDefaultIfEmpty(druidMonitorProperties.getLoginPassword(), LOGIN_PASSWORD));
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean duridFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        Map<String, String> initParams = new HashMap<>();
        //设置忽略请求
        initParams.put("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.setInitParameters(initParams);
        filterRegistrationBean.addUrlPatterns(StringUtils.getDefaultIfEmpty(druidMonitorProperties.getStatFilterUrlPatterns(), STAT_FILTER_URL_PATTERNS));
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");
        filterRegistrationBean.addInitParameter("principalSessionName", "USER_SESSION");
        return filterRegistrationBean;
    }

}
