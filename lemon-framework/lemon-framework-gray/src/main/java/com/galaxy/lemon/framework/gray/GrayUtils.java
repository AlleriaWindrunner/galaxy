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

package com.galaxy.lemon.framework.gray;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.data.BaseLemonData;
import com.galaxy.lemon.framework.data.LemonDataHolder;
import com.galaxy.lemon.framework.gray.constant.GrayConstants;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.Map;
import java.util.Optional;

import static com.galaxy.lemon.framework.gray.constant.GrayConstants.VERSION_ID;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class GrayUtils {

    /**
     * 获取当前服务的灰度版本
     * @param server
     * @return
     */
    public static String getGrayVersion(DiscoveryEnabledServer server) {
        Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        return Optional.ofNullable(metadata).map(m -> m.get(VERSION_ID)).orElse(null);
    }

    /**
     * 判断当前灰度标签是否为灰度环境
     * @param version request gray tag or server gray version
     * @return
     */
    public static boolean isGray(String version) {
        return StringUtils.isNotBlank(version) && version.endsWith(GrayConstants.GRAY_TAG);
    }

    /**
     * matching request gray tag and server gray version
     * @param requestGrayTag
     * @param serverGrayVersion
     * @return
     */
    public static boolean matchGray(String requestGrayTag, String serverGrayVersion) {
        return isGray(requestGrayTag) && requestGrayTag.equals(serverGrayVersion);
    }

    /**
     * 从上下文获取当前请求灰度标志
     * @return
     */
    public static String getRequestGrayVersion() {
        return Optional.ofNullable(LemonDataHolder.getLemonData()).map(BaseLemonData::getVersionId).orElse(LemonConstants.EMPTY_STRING);
    }
}
