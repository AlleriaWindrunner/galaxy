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

package com.galaxy.lemon.framework.springcloud.fegin.logging;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.log.AccessLogger;
import com.galaxy.lemon.common.log.RequestInfo;
import com.galaxy.lemon.common.log.ResponseInfo;
import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.common.utils.IOUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.context.LemonContextUtils;
import com.galaxy.lemon.framework.utils.LemonUtils;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DefaultFeignAccessLoggerAdapter extends FeignAccessLoggerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DefaultFeignAccessLoggerAdapter.class);
    private static final String JOIN_CHAR = "&";
    private static final String QUERY_KEY = "query";
    private static final String BODY_KEY = "body";

    public DefaultFeignAccessLoggerAdapter(AccessLogger accessLogger) {
        super(accessLogger);
    }

    @Override
    public void request(RequestTemplate template) {
        RequestInfo<Object> requestInfo = RequestInfo.builder()
                .requestId(LemonUtils.getRequestId())
                .msgId(LemonUtils.getMsgId())
                .clientIp(LemonUtils.getClientIp())
                .method(template.method())
                .uri(template.url())
                .reqeustTime(DateTimeUtils.getCurrentLocalDateTime())
                .keywords(LemonContextUtils.getKeywords())
                .target(target(template))
                .build();
        this.getAccessLogger().request(requestInfo);
    }

    @Override
    public void response(Object rspDTO) {
        ResponseInfo<Object> responseInfo = ResponseInfo.builder()
                .requestId(LemonUtils.getRequestId())
                .msgId(LemonUtils.getMsgId())
                .duration(DateTimeUtils.durationMillis(LemonContextUtils.getRemoteInvocationStartTime(), DateTimeUtils.getCurrentLocalDateTime()))
                .status(Optional.ofNullable(rspDTO).filter(r -> r instanceof AlertCapable).map(AlertCapable.class::cast).map(AlertCapable::getMsgCd).orElse(LemonConstants.EMPTY_STRING))
                .result(rspDTO)
                .build();
        this.getAccessLogger().response(responseInfo);
    }

    @Override
    public void response(Exception e) {
        ResponseInfo<String> responseInfo = ResponseInfo.stringBuilder()
                .requestId(LemonUtils.getRequestId())
                .msgId(LemonUtils.getMsgId())
                .duration(DateTimeUtils.durationMillis(LemonContextUtils.getRemoteInvocationStartTime(), DateTimeUtils.getCurrentLocalDateTime()))
                .result(e.getMessage())
                .build();
        this.getAccessLogger().response(responseInfo);
    }

    private Object target(RequestTemplate template) {
        Map<String, Collection<String>> queries = template.queries();
        if (JudgeUtils.isNullAny(template.body(), Optional.ofNullable(queries).filter(s -> ! s.isEmpty()).orElse(null) )) {
            if (template.body() != null) {
                return bodyString(template.body());
            }
            if (queries != null && !queries.isEmpty()) {
                return queryString(queries);
            }
        } else {
            Map<String, String> map = new HashMap<>();
            map.put(QUERY_KEY, queryString(template.queries()));
            map.put(BODY_KEY, bodyString(template.body()));
            return map;
        }
        return LemonConstants.EMPTY_STRING;
    }

    private String bodyString(byte[] body) {
        try {
            return IOUtils.toString(body, LemonConstants.DEFAULT_CHARSET);
        } catch (IOException e) {
            logger.warn("Failed to converting byte array to string.", e);
            return LemonConstants.EMPTY_STRING;
        }
    }

    private String queryString(Map<String, Collection<String>> queries) {
        StringBuilder log = new StringBuilder();
        queries.entrySet().stream().forEach( e -> {
            if (log.length() != 0) {
                log.append(JOIN_CHAR);
            }
            log.append(e.getKey()).append(LemonConstants.PROPERTY_KEY_VALUE_SEPARATOR).append(e.getValue());
        });
        return log.toString();
    }
}
