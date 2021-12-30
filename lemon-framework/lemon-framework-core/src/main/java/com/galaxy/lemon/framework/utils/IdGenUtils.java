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

package com.galaxy.lemon.framework.utils;

import com.galaxy.lemon.common.Holder;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.extension.SpringExtensionLoader;
import com.galaxy.lemon.common.utils.DateTimeUtils;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.id.IdGenerator;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Id 生成工具类
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class IdGenUtils {
    public static final DateTimeFormatter REQUEST_ID_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");
    public static final String IG_GENERATOR_BEAN_NAME  = "idGenerator";
    public static final String COMMON_ID_SEQUENCE_LENGTH = "lemon.idgen.common-id-sequence-len";
    public static final int DEFAULT_COMMON_ID_SEQ_LENGTH = 10;
    public static final char DEFAULT_SEQUENCE_PAD_CHAR = '0';
    public static final String MSG_ID_PREFIX_KEY = "MSGID_";
    public static final String MSG_ID_SEQUENCE_LENGTH = "lemon.idgen.msg-id-sequence-len";
    public static final int DEFAULT_MSG_ID_LENGTH_SEQ = 10;
    public static final String REQUEST_ID_PREFIX_KEY = "REQUESTID_";
    public static final String REQUEST_ID_SEQUENCE_LENGTH = "lemon.idgen.request-id-sequence-len";
    public static final int DEFAULT_REQUEST_ID_LENGTH_SEQ = 10;
    
    private static final Holder<IdGenerator> idGeneratorHolder = new Holder<>();
    private static final Holder<Integer> commonIdSeqLengthHolder = new Holder<>();
    private static final Holder<Integer> msgIdSeqLengthHolder = new Holder<>();
    private static final Holder<Integer> requestIdSeqLengthHolder = new Holder<>();

    /**
     * 生成Id
     * @param idName
     * @return
     */
    public static String generateId(String idName) {
        return getGenerator().generateId(idName);
    }
    
    /**
     * Id = prefix + id
     * @param idName
     * @param prefix
     * @return
     */
    public static String generateId(String idName, String prefix) {
        return prefix + generateId(idName);
    }

    /**
     * @param idName
     * @param length 序列号的长度
     * @return
     */
    public static String generateId(String idName, int length) {
        return StringUtils.leftPad(generateId(idName), length, getPaddingChar());
    }
    
    /**
     * @param idName
     * @param prefix ID前缀
     * @param length 序列号的长度
     * @return
     */
    public static String generateId(String idName, String prefix, int length) {
        return prefix + generateId(idName, length);
    }

    /**
     * reverse id
     * @param idName
     * @return
     */
    public static String generateReversedId(String idName) {
        return StringUtils.reverse(generateId(idName));
    }
    
    /**
     * reverse id
     * @param idName
     * @param length 序列号的长度
     * @return
     */
    public static String generateReversedId(String idName, int length) {
        return StringUtils.leftPad(StringUtils.reverse(generateId(idName)), length, getPaddingChar());
    }
    
    /**
     * prefix + yyyymmdd + id
     * 
     * @param idName
     * @param prefix
     * @param length
     * @return
     */
    public static String generateIdWithDate(String idName, String prefix, int length) {
        return prefix + DateTimeUtils.getCurrentDateStr() + generateId(idName, length);
    }
    
    /**
     * prefix + yymmdd + id
     * 
     * @param idName
     * @param prefix
     * @param length
     * @return
     */
    public static String generateIdWithShortDate(String idName, String prefix, int length) {
        return prefix + DateTimeUtils.getCurrentShortDateStr() + generateId(idName, length);
    }
    
    /**
     * yyyymmdd + id
     * 
     * @param idName
     * @param length
     * @return
     */
    public static String generateIdWithDate(String idName, int length) {
        return DateTimeUtils.getCurrentDateStr() + generateId(idName, length);
    }
    
    /**
     * yymmdd + id
     * 
     * @param idName
     * @param length
     * @return
     */
    public static String generateIdWithShortDate(String idName, int length) {
        return DateTimeUtils.getCurrentShortDateTimeStr() + generateId(idName, length);
    }
    
    /**
     * prefix + yyyymmddHHmmss + id
     * 
     * @param idName
     * @param prefix
     * @param length
     * @return
     */
    public static String generateIdWithDateTime(String idName, String prefix, int length) {
        return prefix + DateTimeUtils.getCurrentDateTimeStr() + generateId(idName, length);
    }
    
    /**
     * prefix + yymmddHHmmss + id
     * 
     * @param idName
     * @param prefix
     * @param length
     * @return
     */
    public static String generateIdWithShortDateTime(String idName, String prefix, int length) {
        return prefix + DateTimeUtils.getCurrentShortDateTimeStr() + generateId(idName, length);
    }
    
    /**
     * yyyymmddHHmmss + id
     * 
     * @param idName
     * @param length
     * @return
     */
    public static String generateIdWithDateTime(String idName, int length) {
        return DateTimeUtils.getCurrentDateTimeStr() + generateId(idName, length);
    }
    
    /**
     * yymmddHHmmss + id
     * 
     * @param idName
     * @param length
     * @return
     */
    public static String generateIdWithShortDateTime(String idName, int length) {
        return DateTimeUtils.getCurrentShortDateTimeStr() + generateId(idName, length);
    }
    
    /**
     * 通用的Id生成方法
     * key 在同一应用内不能重复
     * @param idName
     * @return
     */
    public static String generateCommonId(String idName) {
        return generateIdWithShortDate(idName, getCommonIdSeqLength());
    }
    
    /**
     * 生成交易流水号
     * @return
     */
    public static String generateMsgId() {
        return LemonUtils.getApplicationName() + generateIdWithShortDateTime(MSG_ID_PREFIX_KEY, getMsgIdSeqLength());
    }
    
    /**
     * 生成请求流水号
     * @return
     */
    public static String generateRequestId() {
        StringBuilder requestIdBuilder = new StringBuilder(LemonUtils.getApplicationName())
                //.append(LemonUtils.getNodeName())
                .append(DateTimeUtils.getCurrentLocalDateTime().format(REQUEST_ID_DATETIME_FORMATTER).substring(1))
                .append(generateId(REQUEST_ID_PREFIX_KEY, getRequestIdSeqLength()));
        return requestIdBuilder.toString();
    }
    
    /**
     * global unique id
     * @param idName
     * @return
     *
     * @deprecated as of lemon 1.4.x, in favor of using {@link #generateGlobalId(String)}
     */
    @Deprecated
    public static String generatorGlobalId(String idName) {
        return getGenerator().generateGlobalId(idName);
    }

    /**
     * global unique id
     * @param idName
     * @return
     */
    public static String generateGlobalId(String idName) {
        return getGenerator().generateGlobalId(idName);
    }

    /**
     * global unique id
     * padding the left side of the ID with 0 until the specified length
     * @param idName idName
     * @param length id length after padding
     * @return
     */
    public static String generateGlobalId(String idName, int length) {
        return StringUtils.leftPad(generateGlobalId(idName), length, getPaddingChar());
    }

    /**
     *
     * @param idName
     * @param length
     * @return
     */
    public static String generateGlobalIdWithShortDateTime(String idName, int length) {
        return DateTimeUtils.getCurrentShortDateTimeStr() + generateGlobalId(idName, length);
    }

    /**
     *
     * @param idName
     * @param prefix
     * @param length
     * @return
     */
    public static String generateGlobalIdWithShortDateTime(String idName, String prefix, int length) {
        return prefix + generateGlobalIdWithShortDateTime(idName, length);
    }

    /**
     * reverse global id
     * @param idName
     * @return
     */
    public static String generateReversedGlobalId(String idName) {
        return StringUtils.reverse(generateGlobalId(idName));
    }

    /**
     * reverse global id
     * padding the left side of the ID with 0 until the specified length
     *
     * @param idName
     * @param length
     * @return
     */
    public static String generateReversedGlobalId(String idName, int length) {
        return StringUtils.leftPad(generateReversedGlobalId(idName), length, getPaddingChar());
    }

    private static IdGenerator getGenerator() {
        if (! idGeneratorHolder.isEmpty()) {
            return idGeneratorHolder.get();
        }
        IdGenerator idGenerator = SpringExtensionLoader.getSpringBean(IG_GENERATOR_BEAN_NAME, IdGenerator.class);
        LemonException.throwLemonExceptionIfNecessary(JudgeUtils.isNull(idGenerator), ErrorMsgCode.SYS_ERROR.getMsgCd(), "No bean \"" + IG_GENERATOR_BEAN_NAME + "\" found in spring application context.");
        idGeneratorHolder.set(idGenerator);
        return idGenerator;
    }
    
    public static int getCommonIdSeqLength() {
        if (!commonIdSeqLengthHolder.isEmpty()) {
            return commonIdSeqLengthHolder.get();
        }
        Integer commonIdSeqLength = Optional.ofNullable(LemonUtils.getProperty(COMMON_ID_SEQUENCE_LENGTH, Integer.class)).orElse(DEFAULT_COMMON_ID_SEQ_LENGTH);
        commonIdSeqLengthHolder.set(commonIdSeqLength);
        return commonIdSeqLength;
    }
    
    public static int getMsgIdSeqLength() {
        if (!msgIdSeqLengthHolder.isEmpty()) {
            return msgIdSeqLengthHolder.get();
        }
        Integer msgIdSeqLength = Optional.ofNullable(LemonUtils.getProperty(MSG_ID_SEQUENCE_LENGTH, Integer.class)).orElse(DEFAULT_MSG_ID_LENGTH_SEQ);
        msgIdSeqLengthHolder.set(msgIdSeqLength);
        return msgIdSeqLength;
    }
    
    public static int getRequestIdSeqLength() {
        if (!requestIdSeqLengthHolder.isEmpty()) {
            return requestIdSeqLengthHolder.get();
        }
        Integer requestIdSeqLength = Optional.ofNullable(LemonUtils.getProperty(REQUEST_ID_SEQUENCE_LENGTH, Integer.class)).orElse(DEFAULT_REQUEST_ID_LENGTH_SEQ);
        requestIdSeqLengthHolder.set(requestIdSeqLength);
        return requestIdSeqLength;
    }
    
    public static char getPaddingChar() {
        return DEFAULT_SEQUENCE_PAD_CHAR;
    }

}
