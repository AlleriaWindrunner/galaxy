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

package com.galaxy.lemon.zookeeper.configure;

import com.galaxy.lemon.zookeeper.ZookeeperClient;
import com.galaxy.lemon.zookeeper.configure.ZookeeperProperties;
import com.galaxy.lemon.zookeeper.zkclient.ZkClientZookeeperClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperConfiguration {

    private ZookeeperProperties zookeeperProperties;

    public ZookeeperConfiguration(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
    }

    @Bean
    public ZookeeperClient zookeeperClient() {
        Optional.ofNullable(this.zookeeperProperties.getAddresses()).orElseThrow(() -> new IllegalStateException("Properties of zookeeper must not be null, please configure it with \"lemon.register.zookeeper\"."));
        ZookeeperClient zkClientZookeeperClient = new ZkClientZookeeperClient(this.zookeeperProperties.getAddresses());
        return zkClientZookeeperClient;
    }
}
