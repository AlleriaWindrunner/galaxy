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

package com.galaxy.lemon.framework.jackson.log;

import com.galaxy.lemon.common.log.LogIgnore;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface LogIgnorePredicate {
    /**
     * 判断是否需要忽略日志打印
     * @param logIgnoreMetadata
     * @param object
     * @return
     */
    boolean predicate(LogIgnoreMetadata logIgnoreMetadata, Object object);

    /**
     * 需要打印
     */
    class NotRequiredLogIgnorePredicate implements LogIgnorePredicate {

        @Override
        public boolean predicate(LogIgnoreMetadata logIgnoreMetadata, Object object) {
            return false;
        }
    }

    class RequiredLogIgnorePredicate implements LogIgnorePredicate {

        @Override
        public boolean predicate(LogIgnoreMetadata logIgnoreMetadata, Object object) {
            return true;
        }
    }

    class LogIgnoreMetadata {
        private LogIgnore.Condition condition;

        public LogIgnoreMetadata(LogIgnore.Condition condition) {
            this.condition = condition;
        }

        public LogIgnore.Condition getCondition() {
            return condition;
        }
    }
}
