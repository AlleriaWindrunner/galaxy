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

package com.galaxy.lemon.framework.actuator.endpoint;

import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.framework.offline.OfflineListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties(prefix = "endpoints.offline")
public class OfflineEndpoint extends AbstractEndpoint<Map<String, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(OfflineEndpoint.class);

    private static final Map<String, Object> OFFLINE_MESSAGE = Collections
            .unmodifiableMap(Collections.<String, Object>singletonMap("message", "Offline, bye..."));
    private List<OfflineListener> offlineListeners;

    public OfflineEndpoint(List<OfflineListener> offlineListeners) {
        super("offline", true, false);
        this.offlineListeners = offlineListeners;
    }

    @Override
    public Map<String, Object> invoke() {
        try {
            return OFFLINE_MESSAGE;
        }
        finally {
            if (JudgeUtils.isNotEmpty(offlineListeners)) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500L);
                        }
                        catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        offlineListeners.stream().forEachOrdered(l -> safe(l, new OfflineListener.OfflineEvent()));
                    }
                });
                thread.setContextClassLoader(getClass().getClassLoader());
                thread.start();
            }
        }
    }

    private void safe(OfflineListener offlineListener, OfflineListener.OfflineEvent offlineEvent) {
        try {
            offlineListener.onOffline(offlineEvent);
        } catch (Throwable throwable) {
            if (logger.isWarnEnabled()) {
                logger.warn("Offline failure.", throwable);
            }
        }
    }

}
