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

package com.galaxy.lemon.framework.security.refresh.redis;

import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.config.Mode;
import com.galaxy.lemon.framework.security.refresh.*;
import com.galaxy.lemon.framework.security.refresh.redis.JdkSerializationStrategy;
import com.galaxy.lemon.framework.security.refresh.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class RedisTokenStore implements TokenStore {
    private static final String REFRESH = "refresh:";
    private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
    
    private final RedisConnectionFactory connectionFactory;
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
    
    private String prefix = "LMS:";
    private Mode mode = Mode.SINGLE;

    public RedisTokenStore(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        try {
            if (null != connectionFactory.getClusterConnection()) {
                this.mode = Mode.CLUSTER;
            } else if (null != connectionFactory.getSentinelConnection()) {
                this.mode = Mode.SENTINEL;
            }
        }catch (InvalidDataAccessApiUsageException idaaue) {

        }catch (Exception e) {

        }
    }
    
    public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
        this.serializationStrategy = serializationStrategy;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    private byte[] serializeKey(String object) {
        return serialize(prefix + object);
    }
    
    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    private String deserializeString(byte[] bytes) {
        return serializationStrategy.deserializeString(bytes);
    }

    @Override
    public void storeRefreshToken(RefreshToken refreshToken) {
        byte[] refreshKey = serializeKey(REFRESH + refreshToken.getValue());
        byte[] serializedRefreshToken = serialize(refreshToken);
        RedisConnection conn = getConnection();
        try {
            if (Mode.CLUSTER != mode) {
                conn.openPipeline();
            }
            conn.set(refreshKey, serializedRefreshToken);
            if (refreshToken instanceof LemonRefreshToken) {
                LemonRefreshToken expiringRefreshToken = (LemonRefreshToken) refreshToken;
                LocalDateTime expiration = expiringRefreshToken.getExpiration();
                if (expiration != null) {
                    int seconds = Long.valueOf((DateTimeUtils.toEpochMilli(expiration) - System.currentTimeMillis()) / 1000L)
                            .intValue();
                    conn.expire(refreshKey, seconds);
                }
            }
            if (Mode.CLUSTER != mode) {
                conn.closePipeline();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public void removeRefreshToken(RefreshToken refreshToken) {
        removeRefreshToken(refreshToken.getValue());
    }
    
    public void removeRefreshToken(String tokenValue) {
        byte[] refreshKey = serializeKey(REFRESH + tokenValue);
        byte[] refresh2AccessKey = serializeKey(REFRESH_TO_ACCESS + tokenValue);
        byte[] access2RefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        RedisConnection conn = getConnection();
        try {
            if (Mode.CLUSTER != mode) {
                conn.openPipeline();
            }
            conn.del(refreshKey);
            conn.del(refresh2AccessKey);
            conn.del(access2RefreshKey);
            if (Mode.CLUSTER != mode) {
                conn.closePipeline();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public void storeAccessToken(AccessToken token) {
        RedisConnection conn = getConnection();
        try {
            if (Mode.CLUSTER != mode) {
                conn.openPipeline();
            }
            RefreshToken refreshToken = token.getRefreshToken();
            if (refreshToken != null && refreshToken.getValue() != null) {
                byte[] refresh = serialize(token.getRefreshToken().getValue());
                byte[] auth = serialize(token.getValue());
                byte[] refreshToAccessKey = serializeKey(REFRESH_TO_ACCESS + token.getRefreshToken().getValue());
                conn.set(refreshToAccessKey, auth);
                byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + token.getValue());
                conn.set(accessToRefreshKey, refresh);
                if (refreshToken instanceof LemonRefreshToken) {
                    LemonRefreshToken expiringRefreshToken = (LemonRefreshToken) refreshToken;
                    LocalDateTime expiration = expiringRefreshToken.getExpiration();
                    if (expiration != null) {
                        int seconds = Long.valueOf((DateTimeUtils.toEpochMilli(expiration) - System.currentTimeMillis()) / 1000L)
                                .intValue();
                        conn.expire(refreshToAccessKey, seconds);
                        conn.expire(accessToRefreshKey, seconds);
                    }
                }
            }
            if (Mode.CLUSTER != mode) {
                conn.closePipeline();
            }

        } finally {
            conn.close();
        }
    }
    
    @Override
    public RefreshToken readRefreshToken(String tokenValue) {
        byte[] key = serializeKey(REFRESH + tokenValue);
        byte[] bytes = null;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(key);
        } finally {
            conn.close();
        }
        RefreshToken refreshToken = deserializeRefreshToken(bytes);
        return refreshToken;
    }
    
    private RefreshToken deserializeRefreshToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, RefreshToken.class);
    }

    @Override
    public String removeAccessTokenUsingRefreshToken(String refreshTokenValue) {
        byte[] key = serializeKey(REFRESH_TO_ACCESS + refreshTokenValue);
        byte[] accessTokenBytes = null;
        RedisConnection conn = getConnection();
        try {
            switch (mode) {
                case CLUSTER:
                    accessTokenBytes = conn.get(key);
                    conn.del(key);
                    break;
                default:
                    conn.openPipeline();
                    conn.get(key);
                    conn.del(key);
                    List<Object> results = conn.closePipeline();
                    if (null != results) {
                        accessTokenBytes = (byte[]) results.get(0);
                    }
            }
        } finally {
            conn.close();
        }

        if (null == accessTokenBytes) {
            return null;
        }
        String accessToken = deserializeString(accessTokenBytes);
        if (accessToken != null) {
            removeAccessToken(accessToken);
        }
        return accessToken;
    }
    
    public void removeAccessToken(String tokenValue) {
        byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        RedisConnection conn = getConnection();
        try {
           // conn.openPipeline();
            conn.del(accessToRefreshKey);
          //  conn.closePipeline();
        } finally {
            conn.close();
        }
    }

    @Override
    public AccessToken readAccessToken(String tokenValue) {
        byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        String refreshTokenValue = null;
        RedisConnection conn = getConnection();
        try {
            //conn.openPipeline();
            byte[] refresh = conn.get(accessToRefreshKey);
            //List<Object> results = conn.closePipeline();
            //byte[] refresh = (byte[]) results.get(0);
            refreshTokenValue = deserializeString(refresh);
        } finally {
            conn.close();
        }
        LemonAccessToken accessToken = new LemonAccessToken(tokenValue);
        if(JudgeUtils.isNotBlank(refreshTokenValue)) {
            accessToken.setRefreshToken(readRefreshToken(refreshTokenValue));
        }
        return accessToken;
    }
}
