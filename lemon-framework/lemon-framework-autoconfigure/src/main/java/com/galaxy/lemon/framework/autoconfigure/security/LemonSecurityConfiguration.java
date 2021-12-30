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

package com.galaxy.lemon.framework.autoconfigure.security;

import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.autoconfigure.web.IndicateLemonDataInitialUrlPattern;
import com.galaxy.lemon.framework.logger.http.HttpAccessLogger;
import com.galaxy.lemon.framework.security.auth.GenericAuthenticationFilter;
import com.galaxy.lemon.framework.security.auth.GenericAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * spring security 配置
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
public class LemonSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(LemonSecurityConfiguration.class);
    public static final String LOGOUT_PATH = "/security/logout";

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    @Autowired
    private AuthenticationSuccessHandler lemonAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler lemonAuthenticationFailureHandler;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private SessionInformationExpiredStrategy lemonSessionInformationExpiredStrategy;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;

    private List<LogoutHandler> logoutHandlers;

    private GenericAuthenticationProvider genericAuthenticationProvider;

    private SecurityProperties securityProperties;

    private String[] permitAllAntPatterns = null;

    private HttpAccessLogger httpAccessLogger;

    private List<HttpSecurityCustomizer> httpSecurityCustomizers;

    private List<AuthenticationManagerBuilderCustomizer> authenticationManagerBuilderCustomizers;
    
    public LemonSecurityConfiguration(SecurityProperties securityProperties,
                                      ObjectProvider<List<LogoutHandler>> logoutHandlers,
                                      ObjectProvider<List<IndicatePermitAllUrlPattern>> indicatePermitAllUrlPatterns,
                                      GenericAuthenticationProvider genericAuthenticationProvider,
                                      ObjectProvider<List<HttpSecurityCustomizer>> httpSecurityCustomizers,
                                      ObjectProvider<List<AuthenticationManagerBuilderCustomizer>> authenticationManagerBuilderCustomizers,
                                      HttpAccessLogger httpAccessLogger) {
        this.securityProperties = securityProperties;
        this.logoutHandlers = logoutHandlers.getIfAvailable();
        this.permitAllAntPatterns = Optional.ofNullable(indicatePermitAllUrlPatterns.getIfAvailable()).map(s -> resolvePermitAllUrlPattern(s)).orElse(new String[]{});
        this.genericAuthenticationProvider = genericAuthenticationProvider;
        this.httpSecurityCustomizers = httpSecurityCustomizers.getIfAvailable();
        this.authenticationManagerBuilderCustomizers = authenticationManagerBuilderCustomizers.getIfAvailable();
        this.httpAccessLogger = httpAccessLogger;
    }

    @PostConstruct
    public void postConstruct() {
        List<String> permitAllFromConfiguration = Optional.ofNullable(this.securityProperties.getAuthorizeRequests()).map(SecurityProperties.AuthorizeRequests::getPermitAll).orElse(null);
        if (JudgeUtils.isNotEmpty(permitAllFromConfiguration)) {
            if (logger.isInfoEnabled()) {
                logger.info("PermitAll from configuration is {}.", StringUtils.join(permitAllFromConfiguration.toArray(), ","));
            }
            permitAllFromConfiguration.addAll(Arrays.asList(this.permitAllAntPatterns));
            this.permitAllAntPatterns = permitAllFromConfiguration.toArray(new String[permitAllFromConfiguration.size()]);
        }
        if (logger.isInfoEnabled()) {
            logger.info("PermitAll access urls is \"{}\".", Optional.ofNullable(this.permitAllAntPatterns).map(s -> Arrays.stream(s).collect(Collectors.joining(","))).orElse("None"));
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LogoutConfigurer logoutConfigurer = http.authorizeRequests()
            .antMatchers(permitAllAntPatterns).permitAll()
            .anyRequest().authenticated()
        .and()
            //.authenticationProvider(genericAuthenticationProvider)
            //.authenticationProvider(refreshTokenAuthenticationProvider)
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        .and()
            .logout().permitAll()
            .logoutRequestMatcher(new AntPathRequestMatcher(getLogoutPath(), "DELETE"))
            .logoutSuccessHandler(logoutSuccessHandler);
        Optional.ofNullable(this.logoutHandlers).ifPresent(s -> s.stream().forEach(l -> logoutConfigurer.addLogoutHandler(l)));
        Optional.ofNullable(this.httpSecurityCustomizers).ifPresent(s -> s.stream().forEach(c -> c.customize(http, resolveHttpSecurityCustomizerMetadata())));
        http.addFilterAt(genericAuthenticationFilter() , UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(concurrentSessionFilter(), WebAsyncManagerIntegrationFilter.class);
        http.csrf().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        Optional.ofNullable(this.authenticationManagerBuilderCustomizers).ifPresent(s -> s.stream().forEach(c -> c.customize(auth, new AuthenticationManagerBuilderCustomizer.Metadata())));
        auth.authenticationProvider(genericAuthenticationProvider);
    }

    public ConcurrentSessionFilter concurrentSessionFilter() {
        return new ConcurrentSessionFilter(sessionRegistry, lemonSessionInformationExpiredStrategy);
    }

    public GenericAuthenticationFilter genericAuthenticationFilter() {
        GenericAuthenticationFilter genericAuthenticationFilter = new GenericAuthenticationFilter(getLoginPathPrefix() + "/**",
                this.httpAccessLogger, Optional.ofNullable(this.securityProperties.getAuthentication()).filter(a -> a.isPostOnly()).map(a -> "POST").orElse(null));
        try {
            genericAuthenticationFilter.setAuthenticationManager(authenticationManager());
        } catch (Exception e) {
            LemonException.throwLemonException(e);
        }
        genericAuthenticationFilter.setAuthenticationSuccessHandler(this.lemonAuthenticationSuccessHandler);
        genericAuthenticationFilter.setAuthenticationFailureHandler(this.lemonAuthenticationFailureHandler);
        genericAuthenticationFilter.setSessionAuthenticationStrategy(this.sessionAuthenticationStrategy);
        genericAuthenticationFilter.setPostOnly(this.securityProperties.getAuthentication().isPostOnly());
        return genericAuthenticationFilter;
    }

    @Bean
    public IndicateLemonDataInitialUrlPattern indicateLemonDataInitialUrlPattern() {
        List urls = new ArrayList();
        urls.add(getLoginPathPrefix()+"/*");
        urls.add(getLogoutPath());
        return new IndicateLemonDataInitialUrlPattern(urls);
    }

    private HttpSecurityCustomizer.Metadata resolveHttpSecurityCustomizerMetadata() {
        try {
            return new HttpSecurityCustomizer.Metadata(authenticationManager(), this.lemonAuthenticationFailureHandler, this.sessionAuthenticationStrategy);
        } catch (Exception e) {
            throw LemonException.create(e);
        }
    }

    private String[] resolvePermitAllUrlPattern(List<IndicatePermitAllUrlPattern> indicatePermitAllUrlPatterns) {
        List<String> urlList = new ArrayList();
        Optional.ofNullable(indicatePermitAllUrlPatterns).ifPresent(s ->
                s.stream().map(IndicatePermitAllUrlPattern::getUrlPatterns).forEach(urlList::addAll));
        return urlList.toArray(new String[urlList.size()]);
    }

    private String getLoginPathPrefix() {
        return Optional.ofNullable(this.securityProperties.getAuthentication()).map(a
                -> a.getLoginPathPrefix()).orElse(GenericAuthenticationFilter.DEFAULT_PREFIX_FILTER_PROCESSES_URL);
    }

    private String getLogoutPath() {
        return StringUtils.getDefaultIfEmpty(this.securityProperties.getAuthentication().getLogoutPath(), LOGOUT_PATH);
    }

}
