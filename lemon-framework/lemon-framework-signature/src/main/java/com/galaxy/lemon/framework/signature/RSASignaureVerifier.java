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
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface RSASignaureVerifier {

    /**
     * RSA验签
     * @param rSASignatureMetadata
     * @return
     */
    boolean verify(RSASignatureMetadata rSASignatureMetadata);

    class RSASignatureMetadata<T> {
        private byte[] content;
        private byte[] signature;
        private T target;

        public RSASignatureMetadata(byte[] content, byte[] signature, T target) {
            this.content = content;
            this.signature = signature;
            this.target = target;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }

        public byte[] getSignature() {
            return signature;
        }

        public void setSignature(byte[] signature) {
            this.signature = signature;
        }

        public T getTarget() {
            return target;
        }

        public void setTarget(T target) {
            this.target = target;
        }
    }
}
