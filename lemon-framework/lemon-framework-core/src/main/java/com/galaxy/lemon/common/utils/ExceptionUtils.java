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

package com.galaxy.lemon.common.utils;

import com.galaxy.lemon.common.exception.BusinessException;
import com.galaxy.lemon.common.exception.BusinessNoRollbackException;
import com.galaxy.lemon.common.exception.LemonException;

import java.util.Optional;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class ExceptionUtils {
    /**
     * 业务异常
     * @param exception
     * @return
     */
    public static boolean isBusinessException(Exception exception) {
        return exception instanceof BusinessException ||
                exception instanceof BusinessNoRollbackException ||
                Optional.of(exception).filter(e -> e instanceof LemonException).map(e -> (LemonException)e).map(l -> l.isBusinessException()).orElse(false);
    }

    /**
     * 业务异常
     * @param throwable
     * @return
     */
    public static boolean isBusinessException(Throwable throwable) {
        return throwable instanceof Exception && isBusinessException((Exception) throwable);
    }
}
