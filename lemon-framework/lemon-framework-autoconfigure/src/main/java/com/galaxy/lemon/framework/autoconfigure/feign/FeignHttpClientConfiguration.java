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

package com.galaxy.lemon.framework.autoconfigure.feign;

import com.galaxy.lemon.common.utils.NumberUtils;
import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * feign httpclient auto configuration
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({Feign.class, ApacheHttpClient.class, HttpClient.class})
@ConditionalOnProperty(value = "lemon.feign.httpclient.enabled", matchIfMissing = true)
@EnableConfigurationProperties(FeignHttpclientProperties.class)
public class FeignHttpClientConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(FeignHttpClientConfiguration.class);
    public static final int DEFAULT_MAX_TOTAL = 200;
    public static final int DEFAULT_MAX_PER_ROUTE = 20;
    public static final long DEFAULT_ALIVE = 60000;
    public static final long DEFAULT_MAX_IDLE_TIME = 10;    //unit second

    private FeignHttpclientProperties feignHttpclientProperties;

    public FeignHttpClientConfiguration(FeignHttpclientProperties feignHttpclientProperties) {
        this.feignHttpclientProperties = feignHttpclientProperties;
    }

    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingHttpClientConnectionManager.setMaxTotal(NumberUtils.getDefaultIfNull(this.feignHttpclientProperties.getMaxTotal(), DEFAULT_MAX_TOTAL));
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(NumberUtils.getDefaultIfNull(this.feignHttpclientProperties.getMaxPerRoute(), DEFAULT_MAX_PER_ROUTE));
        return poolingHttpClientConnectionManager;
    }

    @Bean
    public CloseableHttpClient httpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManagerShared(false)
                .evictIdleConnections(feignHttpclientProperties.getMaxIdleTime() ==null ? DEFAULT_MAX_IDLE_TIME : feignHttpclientProperties.getMaxIdleTime().longValue(), TimeUnit.SECONDS)
                .setConnectionManager(poolingHttpClientConnectionManager())
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
                    @Override
                    public long getKeepAliveDuration(HttpResponse response,HttpContext context) {
                        long alive = super.getKeepAliveDuration(response, context);
                        if (alive == -1) {
                            // not set keepalive ,use this value
                            alive = feignHttpclientProperties.getAlive() == null ? DEFAULT_ALIVE : feignHttpclientProperties.getAlive();
                        }
                        return alive;
                    }
                }).build();
        if (logger.isInfoEnabled()) {
            logger.info("Creating shared instance of singleton bean \"httpClient\" {}.", httpClient);
        }
        return httpClient;
    }

}
