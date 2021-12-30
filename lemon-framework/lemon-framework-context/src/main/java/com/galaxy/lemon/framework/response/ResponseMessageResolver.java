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

package com.galaxy.lemon.framework.response;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import com.galaxy.lemon.framework.data.BaseDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * response resolver
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface ResponseMessageResolver<R extends BaseDTO & ConfigurableAlerting> {
    /**
     * create and flush response
     * @param request
     * @param response
     * @param alertCapable
     * @throws IOException
     */
    void resolve(HttpServletRequest request, HttpServletResponse response, AlertCapable alertCapable) throws IOException;

    /**
     * create and flush response
     * @param request
     * @param response
     * @param msgCode
     * @throws IOException
     */
    void resolve(HttpServletRequest request, HttpServletResponse response, String msgCode) throws IOException;

    /**
     * flush response
     * @param request
     * @param response
     * @param responseDTO
     * @throws IOException
     */
    void resolveResponse(HttpServletRequest request, HttpServletResponse response, R responseDTO) throws IOException;

    /**
     * create byte array response
     * @param alertCapable
     * @return
     * @throws IOException
     */
    byte[] generateBytes(AlertCapable alertCapable) throws IOException;

    /**
     * create byte array response
     * @param msgCode
     * @return
     * @throws IOException
     */
    byte[] generateBytes(String msgCode) throws IOException;

    /**
     * create string response
     * @param alertCapable
     * @return
     */
    String generateString(AlertCapable alertCapable);

    /**
     * create string response
     * @param msgCode
     * @return
     */
    String generateString(String msgCode);
}
