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

package com.galaxy.lemon.framework.autoconfigure.redisson;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redisson 配置
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties(prefix = "lemon.redisson")
public class RedissonProperties {
    private String mode;
    private String address;
    private String password;
    private Integer database;
    private Integer poolSize;
    private Integer idleSize;
    private Integer idleTimeout;
    private Integer connectionTimeout;
    private Integer timeout;
    private String masterAddress;
    private String[] slaveAddress;
    private String masterName;
    private String[] sentinelAddress;
    private String[] nodeAddress;

    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getDatabase() {
        return database;
    }
    public void setDatabase(Integer database) {
        this.database = database;
    }
    public Integer getPoolSize() {
        return poolSize;
    }
    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }
    public Integer getIdleSize() {
        return idleSize;
    }
    public void setIdleSize(Integer idleSize) {
        this.idleSize = idleSize;
    }
    public Integer getIdleTimeout() {
        return idleTimeout;
    }
    public void setIdleTimeout(Integer idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    public Integer getTimeout() {
        return timeout;
    }
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public void setMasterAddress(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    public String[] getSlaveAddress() {
        return slaveAddress;
    }

    public void setSlaveAddress(String[] slaveAddress) {
        this.slaveAddress = slaveAddress;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String[] getSentinelAddress() {
        return sentinelAddress;
    }

    public void setSentinelAddress(String[] sentinelAddress) {
        this.sentinelAddress = sentinelAddress;
    }

    public String[] getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String[] nodeAddress) {
        this.nodeAddress = nodeAddress;
    }
}
