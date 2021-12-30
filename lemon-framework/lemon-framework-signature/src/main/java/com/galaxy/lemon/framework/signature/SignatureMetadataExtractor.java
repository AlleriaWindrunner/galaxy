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

package com.galaxy.lemon.framework.signature;

/**
 * 签名元数据提取
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface SignatureMetadataExtractor<T, S> {

    SignatureMetadata<S> extract(T dataSource);

    class SignatureMetadata<S> {
        private Algorithm algorithm;
        private S secure;

        public SignatureMetadata(Algorithm algorithm, S secure) {
            this.algorithm = algorithm;
            this.secure = secure;
        }

        public Algorithm getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(Algorithm algorithm) {
            this.algorithm = algorithm;
        }

        public S getSecure() {
            return secure;
        }

        public void setSecure(S secure) {
            this.secure = secure;
        }
    }
}
