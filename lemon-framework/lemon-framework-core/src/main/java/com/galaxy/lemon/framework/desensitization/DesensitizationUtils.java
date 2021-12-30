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

import com.galaxy.lemon.common.LemonConstants;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class DesensitizationUtils {

    public static final String PAD_STR = "*";
    public static final String AT_STR = "@";
    public static final String EMPTY_STR = LemonConstants.EMPTY_STRING;

    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
     */
    public static String chineseName(final String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return EMPTY_STR;
        }
        return StringUtils.rightPad(StringUtils.left(fullName, 1), StringUtils.length(fullName), PAD_STR);
    }


    /**
     * [身份证号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：*************5762>
     */
    public static String idCardNum(final String id) {
        if (StringUtils.isBlank(id)) {
            return EMPTY_STR;
        }
        return StringUtils.left(id, 3).concat(StringUtils.leftPad(StringUtils.right(id, 4), StringUtils.length(id)-3, PAD_STR));
    }

    /**
     * [固定电话] 后四位，其他隐藏<例子：****1234>
     */
    public static String phoneNo(final String num) {
        if (StringUtils.isBlank(num)) {
            return EMPTY_STR;
        }
        return StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), PAD_STR);
    }

    /**
     * [手机号码] 前三位，后四位，其他隐藏<例子:138****1234>
     */
    public static String mobileNo(final String num) {
        if (StringUtils.isBlank(num)) {
            return EMPTY_STR;
        }
        return StringUtils.left(num, 3).concat(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num)-3, PAD_STR));

    }

    /**
     * [地址] 只显示到地区，不显示详细地址；我们要对个人信息增强保护<例子：北京市海淀区****>
     *
     * @param sensitiveSize 敏感信息长度
     */
    public static String address(final String address, final int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return EMPTY_STR;
        }
        final int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, PAD_STR);
    }

    /**
     * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示<例子:g**@163.com>
     */
    public static String email(final String email) {
        if (StringUtils.isBlank(email)) {
            return EMPTY_STR;
        }
        final int index = StringUtils.indexOf(email, AT_STR);
        if (index <= 1) {
            return email;
        } else {
            return StringUtils.rightPad(StringUtils.left(email, 1), index, PAD_STR)
                    .concat(StringUtils.mid(email, index, StringUtils.length(email)));
        }
    }

    /**
     * [银行卡号] 前六位，后四位，其他用星号隐藏每位1个星号<例子:6222600**********1234>
     */
    public static String bankCard(final String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return EMPTY_STR;
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum)-6, PAD_STR));
    }

    /**
     * [公司开户银行联号] 公司开户银行联行号,显示前两位，其他用星号隐藏，每位1个星号<例子:12********>
     */
    public static String cnapsCode(final String code) {
        if (StringUtils.isBlank(code)) {
            return EMPTY_STR;
        }
        return StringUtils.rightPad(StringUtils.left(code, 2), StringUtils.length(code), PAD_STR);
    }

    /**
     * 右边脱敏
     *
     * @param sensitiveStr
     * @return
     */
    public static String right(final String sensitiveStr) {
        if (StringUtils.isBlank(sensitiveStr)) {
            return EMPTY_STR;
        }
        return StringUtils.rightPad(StringUtils.left(sensitiveStr, sensitiveStr.length() / 2), StringUtils.length(sensitiveStr), PAD_STR);
    }

    /**
     * 左边脱敏
     *
     * @param sensitiveStr
     * @return
     */
    public static String left(final String sensitiveStr) {
        if (StringUtils.isBlank(sensitiveStr)) {
            return EMPTY_STR;
        }
        return StringUtils.leftPad(StringUtils.right(sensitiveStr, sensitiveStr.length() / 2), StringUtils.length(sensitiveStr),PAD_STR);
    }

    /**
     * 中间脱敏，保留两端
     * @param sensitiveStr
     * @return
     */
    public static String middle(final String sensitiveStr) {
        if (StringUtils.isBlank(sensitiveStr)) {
            return EMPTY_STR;
        }
        int length = sensitiveStr.length();
        if (length < 3) {
            return StringUtils.leftPad(EMPTY_STR, length, PAD_STR);
        }
        char firstChar = sensitiveStr.charAt(0);
        char lastChar = sensitiveStr.charAt(sensitiveStr.length() - 1);
        return StringUtils.rightPad(StringUtils.rightPad(String.valueOf(firstChar), length - 1, PAD_STR), length, lastChar);
    }

    /**
     * 全部脱敏
     * @param sensitiveStr
     * @return
     */
    public static String all(final String sensitiveStr){
        if (StringUtils.isBlank(sensitiveStr)) {
            return EMPTY_STR;
        }
        return StringUtils.leftPad(EMPTY_STR,sensitiveStr.length(),PAD_STR);
    }

}
