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

package com.galaxy.lemonframework.entrypoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class EntryPointEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //设置当前环境为渠道
        this.addDefaultProperty(environment, "lemon.entrypoint.enabled", "true");
        //日志关键字设置为从session取手机号码
        this.addDefaultProperty(environment,"lemon.log.keywords","httpSession.mblNo");
    }

    private void addDefaultProperty(ConfigurableEnvironment environment, String name,
                                    String value) {
        MutablePropertySources sources = environment.getPropertySources();
        Map<String, Object> map = null;
        if (sources.contains("defaultProperties")) {
            PropertySource<?> source = sources.get("defaultProperties");
            if (source instanceof MapPropertySource) {
                map = ((MapPropertySource) source).getSource();
            }
        }
        else {
            map = new LinkedHashMap<>();
            sources.addLast(new MapPropertySource("defaultProperties", map));
        }
        if (map != null) {
            map.put(name, value);
        }
    }
}
