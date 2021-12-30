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

package com.galaxy.lemon.framework.desensitization;

/**
 * 脱敏执行者
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public interface Desensitizer<O, D> {
    D desensitize(O object);

    class ChineseNameDesensitizer implements Desensitizer<Object, String> {

        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.chineseName(String.valueOf(object));
        }
    }

    class IdCardDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.idCardNum(String.valueOf(object));
        }
    }

    class PhoneNoDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.phoneNo(String.valueOf(object));
        }
    }

    class MobileNoDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.mobileNo(String.valueOf(object));
        }
    }

    class AddressDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.address(String.valueOf(object), 6);
        }
    }

    class EmailDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.email(String.valueOf(object));
        }
    }

    class BankCardDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.bankCard(String.valueOf(object));
        }
    }

    class CNAPSCodeDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.cnapsCode(String.valueOf(object));
        }
    }

    class LeftDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.left(String.valueOf(object));
        }
    }

    class RightDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.right(String.valueOf(object));
        }
    }

    class MiddleDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.middle(String.valueOf(object));
        }
    }

    class AllDesensitizer implements Desensitizer<Object, String> {
        @Override
        public String desensitize(Object object) {
            return DesensitizationUtils.all(String.valueOf(object));
        }
    }
}
