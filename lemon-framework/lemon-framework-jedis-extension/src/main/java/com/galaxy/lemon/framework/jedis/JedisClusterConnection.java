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

package com.galaxy.lemon.framework.jedis;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.ExceptionTranslationStrategy;
import org.springframework.data.redis.FallbackExceptionTranslationStrategy;
import org.springframework.data.redis.connection.ClusterCommandExecutor;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.jedis.JedisConverters;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisNoScriptException;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展Spring data
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class JedisClusterConnection extends org.springframework.data.redis.connection.jedis.JedisClusterConnection {
    private static final ExceptionTranslationStrategy EXCEPTION_TRANSLATION = new FallbackExceptionTranslationStrategy(
            JedisConverters.exceptionConverter());

    private JedisCluster jedisCluster;

    public JedisClusterConnection(redis.clients.jedis.JedisCluster cluster) {
        super(cluster);
        this.jedisCluster = cluster;
    }

    public JedisClusterConnection(redis.clients.jedis.JedisCluster cluster, ClusterCommandExecutor executor) {
        super(cluster, executor);
        this.jedisCluster = cluster;
    }

    public JedisCluster getJedisCluster() {
        return this.jedisCluster;
    }
    /*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.connection.RedisScriptingCommands#eval(byte[], org.springframework.data.redis.connection.ReturnType, int, byte[][])
	 */
    @Override
    public <T> T eval(byte[] script, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        Object result = this.getJedisCluster().eval(script, numKeys, keysAndArgs);
        return (new JedisEvalResultsConverter<T>(returnType)).convert(result);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisScriptingCommands#evalSha(java.lang.String, org.springframework.data.redis.connection.ReturnType, int, byte[][])
     */
    @Override
    public <T> T evalSha(String scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        List<byte[]> keys = new ArrayList<>();
        for (int i = 0; i< numKeys; i++) {
            keys.add(keysAndArgs[i]);
        }
        List<byte[]> args = new ArrayList<>();
        for (int i = numKeys; i < keysAndArgs.length; i ++) {
            args.add(keysAndArgs[i]);
        }
        Object result;
        try {
            result = this.getJedisCluster().evalsha(scriptSha.getBytes(), keys, args);
        } catch (JedisNoScriptException jedisNoScriptException) {
            throw convertJedisAccessException(jedisNoScriptException);
        }

        return (new JedisEvalResultsConverter<T>(returnType)).convert(result);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisScriptingCommands#evalSha(byte[], org.springframework.data.redis.connection.ReturnType, int, byte[][])
     */
    @Override
    public <T> T evalSha(byte[] scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        Object result = this.getJedisCluster().evalsha(scriptSha, numKeys, keysAndArgs);
        return (new JedisEvalResultsConverter<T>(returnType)).convert(result);
    }

    private class JedisEvalResultsConverter<T> implements Converter<Object, T> {
        private ReturnType returnType;

        public JedisEvalResultsConverter(ReturnType returnType) {
            this.returnType = returnType;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public T convert(Object source) {
            if (returnType == ReturnType.MULTI) {
                List resultList = (List) source;
                for (Object obj : resultList) {
                    if (obj instanceof Exception) {
                        throw convertJedisAccessException((Exception)obj);
                    }
                }
            }
            return (T) source;
        }
    }
//
//    protected DataAccessException convertJedisAccessException(Exception ex) {
//
//        DataAccessException exception = EXCEPTION_TRANSLATION.translate(ex);
//
//        return exception != null ? exception : new RedisSystemException(ex.getMessage(), ex);
//    }

}
