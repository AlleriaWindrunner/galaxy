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

import com.galaxy.lemon.common.context.LemonContext;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.common.extension.SpringExtensionLoader;
import com.galaxy.lemon.common.security.Digests;
import com.galaxy.lemon.common.utils.Encodes;
import com.galaxy.lemon.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 签名验签解决
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public abstract class AbstractSignatureVerifier<T> implements SignatureVerifier<T> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractSignatureVerifier.class);
    protected static final String LEMON_CONTEXT_ALREADY_SIGNATURE_VERIFY = "LEMON_CONTEXT_ALREADY_SIGNATURE_VERIFY";

    protected SignatureMetadataExtractor<SignatureDataSource<T>, String> signatureMetadataExtractor;
    private Map<String, SignatureVerifyCustomizer> signatureVerifyCustomizerMap;

    public AbstractSignatureVerifier(SignatureMetadataExtractor<SignatureDataSource<T>, String> signatureMetadataExtractor) {
        this.signatureMetadataExtractor = signatureMetadataExtractor;
    }

    /**
     * 是否需要验签
     * @return
     */
    @Override
    public boolean shouldVerify(SignatureDataSource<T> signatureDataSource) {
        if(hasAlreadyVerify()) {
            return false;
        }
        return doShouldVerify(signatureDataSource);
    }

    protected boolean hasAlreadyVerify() {
        return LemonContext.getCurrentContext().getBoolean(alreadyVerifyKey(), false);
    }

    /**
     * 是否需要验签
     * @return
     */
    protected abstract boolean doShouldVerify(SignatureDataSource<T> signatureDataSource);

    /**
     * 验签
     * @return
     */
    @Override
    public boolean verify(SignatureDataSource<T> signatureDataSource) {
        boolean flag = false;
        try {
            SignatureMetadataExtractor.SignatureMetadata<String> signatureMetadata = this.signatureMetadataExtractor.extract(signatureDataSource);
            if (null == signatureMetadata) {
                if (logger.isErrorEnabled()) {
                    logger.error("Failed to signature verify, because \"SignatureMetadata\" extract with index \"{}\" failure. ");
                }
                return false;
            }
            flag = doVerify(signatureDataSource, signatureMetadata);
            setAlreadyVerify();
        } catch (InvalidSignatureException e) {
            flag = false;
            if (logger.isErrorEnabled()) {
                logger.error("Failed to signature verify.", e);
            }
        }
        return flag;
    }

    /**
     * * do verify
     * @param signatureMetadata
     * @return
     */
    protected boolean doVerify(SignatureDataSource<T> signatureDataSource, SignatureMetadataExtractor.SignatureMetadata<String> signatureMetadata) {
        Algorithm algorithm = signatureMetadata.getAlgorithm();
        String secure = signatureMetadata.getSecure();
        boolean customizeVerifyFlag = false;
        boolean flag = false;
        String content = null;
        String signedMsg = null;
        String contentSignedValue = null;
        switch (algorithm) {
            case MD5:
                content = resolveSignatureContent(signatureDataSource);
                signedMsg = resolveSignatureSignedValue(signatureDataSource);
                contentSignedValue = Encodes.encodeHex(Digests.md5((content + secure).getBytes()));
                flag = StringUtils.equals(signedMsg, contentSignedValue);
                break;
            case SHA1:
                content = resolveSignatureContent(signatureDataSource);
                signedMsg = resolveSignatureSignedValue(signatureDataSource);
                contentSignedValue = Encodes.encodeHex(Digests.sha1((content + secure).getBytes()));
                flag = StringUtils.equals(signedMsg, contentSignedValue);
                break;
            case CUSTOMIZE:
                customizeVerifyFlag = true;
                SignatureVerifyCustomizer signatureVerifyCustomizer = getSignatureVerifyCustomizer(secure);
                Optional.ofNullable(signatureVerifyCustomizer).orElseThrow(() -> LemonException.create(ErrorMsgCode.SYS_ERROR, "Could not found spring bean \""+secure+"\" to customize signature verify."));
                flag = signatureVerifyCustomizer.customizeVerify();
                break;
            default:
                LemonException.throwLemonException(ErrorMsgCode.SIGNATURE_EXCEPTION.getMsgCd(),"Does not support algorithm \""+algorithm+"\" for signature.");
                break;
        }

        if (! flag && logger.isErrorEnabled()) {
            if(customizeVerifyFlag ) {
                logger.error("Failed to do signature verify by customizer \"{}\".", secure);
            } else {
                logger.error("Failed to do signature verify, signed value \"{}\", content \"{}\", secure \"{}\", algorithm \"{}\", required signed value \"{}\".",
                        signedMsg, content, secure, algorithm, contentSignedValue);
            }
        }

        return flag;
    }

    /**
     * 已签名的内容
     * @return
     */
    protected abstract String resolveSignatureSignedValue(SignatureDataSource<T> signatureDataSource) throws InvalidSignatureException;

    /**
     * 需要验签的内容
     * @return
     */
    protected abstract String resolveSignatureContent(SignatureDataSource<T> signatureDataSource) throws InvalidSignatureException;

    protected void setAlreadyVerify() {
        LemonContext.getCurrentContext().put(alreadyVerifyKey(), Boolean.TRUE);
    }

    protected String alreadyVerifyKey() {
        return LEMON_CONTEXT_ALREADY_SIGNATURE_VERIFY + this.getClass().getSimpleName();
    }

    private SignatureVerifyCustomizer getSignatureVerifyCustomizer(String beanName) {
        if (null != this.signatureVerifyCustomizerMap) {
            return this.signatureVerifyCustomizerMap.get(beanName);
        }
        this.signatureVerifyCustomizerMap = SpringExtensionLoader.getSpringBeansOfType(SignatureVerifyCustomizer.class);
        if (null == signatureVerifyCustomizerMap) {
            this.signatureVerifyCustomizerMap = new ConcurrentHashMap<>();
        }
        return this.signatureVerifyCustomizerMap.get(beanName);
    }
    /**
     * 签名验签异常
     * @author yuzhou
     * @date 2018/3/24
     * @time 15:38
     * @since 2.0.0
     */
    public static class InvalidSignatureException extends LemonException{
        public InvalidSignatureException(String errorInfo) {
            super(ErrorMsgCode.SIGNATURE_EXCEPTION.getMsgCd(), errorInfo);
        }
    }

}
