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

package com.galaxy.lemon.framework.actuator.endpoint.mvc;

import com.galaxy.lemon.framework.actuator.endpoint.StatusEndpoint;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties(prefix = "endpoints.status")
public class StatusMvcEndpoint extends EndpointMvcAdapter {

    /**
     * Create a new {@link EndpointMvcAdapter}.
     *
     * @param delegate the underlying {@link Endpoint} to adapt.
     */
    public StatusMvcEndpoint(StatusEndpoint delegate) {
        super(delegate);
    }

    @GetMapping(produces = { MediaType.TEXT_PLAIN_VALUE })
    @Override
    public Object invoke() {
        return super.invoke();
    }
    
}
