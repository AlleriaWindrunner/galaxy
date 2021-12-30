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

package com.galaxy.lemon.common.exception;

import com.galaxy.lemon.common.AlertCapable;
import com.galaxy.lemon.common.AlertParameterizable;
import com.galaxy.lemon.common.BusinessObjectCapable;


/**
 * 业务异常，抛出该异常会回滚事务
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see BusinessNoRollbackException
 * @since 1.0.0
 */

public class BusinessException extends RuntimeException implements AlertCapable, AlertParameterizable, BusinessObjectCapable<Object> {
    private static final long serialVersionUID = 5539431043384054654L;

    private String msgCd;
    private String msgInfo;
    private Throwable cause;
    private BusinessObjectWrapper businessObject;
    private String[] alertParameters;

    /**
     * @param msgCd
     * @param msgInfo
     * @param cause
     */
    public BusinessException(String msgCd, String msgInfo, Throwable cause) {
        super(msgCd+" : "+msgInfo, cause);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
        this.cause = cause;
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param cause
     * @param businessObject
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #BusinessException(String, String, Throwable, BusinessObjectWrapper)}
     */
    @Deprecated
    public BusinessException(String msgCd, String msgInfo, Throwable cause, Object businessObject) {
        super(msgCd+" : "+msgInfo, cause);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
        this.cause = cause;
        this.businessObject = BusinessObjectWrapper.wrap(businessObject);
    }

    public BusinessException(String msgCd, String msgInfo, Throwable cause, BusinessObjectWrapper businessObject) {
        super(msgCd+" : "+msgInfo, cause);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
        this.cause = cause;
        this.businessObject = businessObject;
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param cause
     * @param businessObject
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #BusinessException(String, String, Throwable, BusinessObjectWrapper, String...)}
     */
    @Deprecated
    public BusinessException(String msgCd, String msgInfo, Throwable cause, Object businessObject, String... alertParameters) {
        super(msgCd+" : "+msgInfo, cause);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
        this.cause = cause;
        this.businessObject = BusinessObjectWrapper.wrap(businessObject);
        setAlertParameters(alertParameters);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param cause
     * @param businessObject
     * @param alertParameters
     */
    public BusinessException(String msgCd, String msgInfo, Throwable cause, BusinessObjectWrapper businessObject, String... alertParameters) {
        super(msgCd+" : "+msgInfo, cause);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
        this.cause = cause;
        this.businessObject = businessObject;
        setAlertParameters(alertParameters);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param cause
     * @param alertParameters
     */
    public BusinessException(String msgCd, String msgInfo, Throwable cause, String... alertParameters) {
        super(msgCd+" : "+msgInfo, cause);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
        this.cause = cause;
        setAlertParameters(alertParameters);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #BusinessException(String, String, BusinessObjectWrapper)}
     */
    @Deprecated
    public BusinessException(String msgCd, String msgInfo, Object businessObject) {
        this(msgCd, msgInfo);
        this.businessObject = BusinessObjectWrapper.wrap(businessObject);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     */
    public BusinessException(String msgCd, String msgInfo, BusinessObjectWrapper businessObject) {
        this(msgCd, msgInfo);
        this.businessObject = businessObject;
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #BusinessException(String, String, BusinessObjectWrapper, String...)}
     */
    @Deprecated
    public BusinessException(String msgCd, String msgInfo, Object businessObject, String... alertParameters) {
        this(msgCd, msgInfo);
        this.businessObject = BusinessObjectWrapper.wrap(businessObject);
        setAlertParameters(alertParameters);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     * @param alertParameters
     */
    public BusinessException(String msgCd, String msgInfo, BusinessObjectWrapper businessObject, String... alertParameters) {
        this(msgCd, msgInfo);
        this.businessObject = businessObject;
        setAlertParameters(alertParameters);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param alertParameters
     */
    public BusinessException(String msgCd, String msgInfo, String... alertParameters) {
        this(msgCd, msgInfo);
        setAlertParameters(alertParameters);
    }

    /**
     * @param msgCd
     * @param msgInfo
     */
    public BusinessException(String msgCd, String msgInfo) {
        super(msgCd + " :  " + msgInfo);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
    }

    /**
     * @param alertCapable
     */
    public BusinessException(AlertCapable alertCapable) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo());
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     *
     *  @deprecated as of lemon 1.4.0, in favor of using {@link #BusinessException(AlertCapable, BusinessObjectWrapper)}
     */
    @Deprecated
    public BusinessException(AlertCapable alertCapable, Object businessObject) {
        this(alertCapable);
        this.businessObject = BusinessObjectWrapper.wrap(businessObject);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     */
    public BusinessException(AlertCapable alertCapable, BusinessObjectWrapper businessObject) {
        this(alertCapable);
        this.businessObject = businessObject;
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     * @param alertParameters
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #BusinessException(AlertCapable, BusinessObjectWrapper, String...)}
     */
    @Deprecated
    public BusinessException(AlertCapable alertCapable, Object businessObject, String... alertParameters) {
        this(alertCapable);
        this.businessObject = BusinessObjectWrapper.wrap(businessObject);
        setAlertParameters(alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     * @param alertParameters
     */
    public BusinessException(AlertCapable alertCapable, BusinessObjectWrapper businessObject, String... alertParameters) {
        this(alertCapable);
        this.businessObject = businessObject;
        setAlertParameters(alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param alertParameters
     */
    public BusinessException(AlertCapable alertCapable, String... alertParameters) {
        this(alertCapable);
        setAlertParameters(alertParameters);
    }

    /**
     * @param alertCapable
     * @param cause
     */
    public BusinessException(AlertCapable alertCapable, Throwable cause) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause);
    }

    /**
     *
     * @param alertCapable
     * @param cause
     * @param businessObject
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #BusinessException(AlertCapable, Throwable, BusinessObjectWrapper)}
     */
    @Deprecated
    public BusinessException(AlertCapable alertCapable, Throwable cause, Object businessObject) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause, businessObject);
    }

    /**
     *
     * @param alertCapable
     * @param cause
     * @param businessObject
     */
    public BusinessException(AlertCapable alertCapable, Throwable cause, BusinessObjectWrapper businessObject) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause, businessObject);
    }

    /**
     *
     * @param alertCapable
     * @param cause
     * @param businessObject
     * @param alertParameters
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #BusinessException(AlertCapable, Throwable, BusinessObjectWrapper, String...)}
     */
    @Deprecated
    public BusinessException(AlertCapable alertCapable, Throwable cause, Object businessObject, String... alertParameters) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause, businessObject, alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param cause
     * @param businessObject
     * @param alertParameters
     */
    public BusinessException(AlertCapable alertCapable, Throwable cause, BusinessObjectWrapper businessObject, String... alertParameters) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause, businessObject, alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param cause
     * @param alertParameters
     */
    public BusinessException(AlertCapable alertCapable, Throwable cause, String... alertParameters) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause, alertParameters);
    }

    /**
     * @param msgCd
     */
    public BusinessException(String msgCd) {
        super(msgCd);
        this.msgCd = msgCd;
    }

    /**
     * @param msgCd
     */
    public static void throwBusinessException(String msgCd) {
        throw new BusinessException(msgCd);
    }

    /**
     * @param msgCd
     * @param msgInfo
     */
    public static void throwBusinessException(String msgCd, String msgInfo) {
        throw new BusinessException(msgCd, msgInfo);
    }

    /**
     * @param alertCapable
     */
    public static void throwBusinessException(AlertCapable alertCapable) {
        throw new BusinessException(alertCapable.getMsgCd(), alertCapable.getMsgInfo());
    }
    
    /**
     * @param msgCd
     * @param msgInfo
     * @param throwable
     */
    public static void throwBusinessException(String msgCd, String msgInfo, Throwable throwable) {
        throw new BusinessException(msgCd, msgInfo, throwable);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #throwBusinessException(AlertCapable, BusinessObjectWrapper)}
     */
    @Deprecated
    public static void throwBusinessException(AlertCapable alertCapable, Object businessObject) {
        throw new BusinessException(alertCapable, businessObject);
    }

    public static void throwBusinessException(AlertCapable alertCapable, BusinessObjectWrapper businessObject) {
        throw new BusinessException(alertCapable, businessObject);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     * @param alertParameters
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #throwBusinessException(AlertCapable, BusinessObjectWrapper, String...)}
     */
    @Deprecated
    public static void throwBusinessException(AlertCapable alertCapable, Object businessObject, String... alertParameters) {
        throw new BusinessException(alertCapable, businessObject, alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     * @param alertParameters
     */
    public static void throwBusinessException(AlertCapable alertCapable, BusinessObjectWrapper businessObject, String... alertParameters) {
        throw new BusinessException(alertCapable, businessObject, alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param alertParameters
     */
    public static void throwBusinessException(AlertCapable alertCapable, String... alertParameters) {
        throw new BusinessException(alertCapable, alertParameters);
    }

    /**
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #throwBusinessException(String, String, BusinessObjectWrapper)}
     */
    @Deprecated
    public static void throwBusinessException(String msgCd, String msgInfo, Object businessObject) {
        throw new BusinessException(msgCd, msgInfo, businessObject);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     */
    public static void throwBusinessException(String msgCd, String msgInfo, BusinessObjectWrapper businessObject) {
        throw new BusinessException(msgCd, msgInfo, businessObject);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param alertParameters
     */
    public static void throwBusinessException(String msgCd, String msgInfo, String... alertParameters) {
        throw new BusinessException(msgCd, msgInfo, alertParameters);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     * @param alertParameters
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #throwBusinessException(String, String, BusinessObjectWrapper, String...)}
     */
    @Deprecated
    public static void throwBusinessException(String msgCd, String msgInfo, Object businessObject, String alertParameters) {
        throw new BusinessException(msgCd, msgInfo, businessObject, alertParameters);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     * @param alertParameters
     */
    public static void throwBusinessException(String msgCd, String msgInfo, BusinessObjectWrapper businessObject, String... alertParameters) {
        throw new BusinessException(msgCd, msgInfo, businessObject, alertParameters);
    }

    /**
     * @param alertCapable
     * @param throwable
     */
    public static void throwBusinessException(AlertCapable alertCapable, Throwable throwable) {
        throw new BusinessException(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), throwable);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param cause
     * @param businessObject
     * @param alertParameters
     */
    public static void throwBusinessException(String msgCd, String msgInfo, Throwable cause, BusinessObjectWrapper businessObject, String... alertParameters) {
        throw new BusinessException(msgCd, msgInfo, cause, businessObject, alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param cause
     * @param businessObject
     * @param alertParameters
     */
    public static void throwBusinessException(AlertCapable alertCapable, Throwable cause, BusinessObjectWrapper businessObject, String... alertParameters) {
        throw new BusinessException(alertCapable, cause, businessObject, alertParameters);
    }

    public static BusinessException create(String msgCd) {
        return new BusinessException(msgCd);
    }
    
    public static BusinessException create(String msgCd, String msgInfo) {
        return new BusinessException(msgCd, msgInfo);
    }
    
    public static BusinessException create(AlertCapable alertCapable) {
        return new BusinessException(alertCapable);
    }

    public static BusinessException create(AlertCapable alertCapable, Throwable throwable) {
        return new BusinessException(alertCapable, throwable);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #create(AlertCapable, BusinessObjectWrapper)}
     */
    @Deprecated
    public static BusinessException create(AlertCapable alertCapable, Object businessObject) {
        return new BusinessException(alertCapable, businessObject);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     * @return
     */
    public static BusinessException create(AlertCapable alertCapable, BusinessObjectWrapper businessObject) {
        return new BusinessException(alertCapable, businessObject);
    }

    /**
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     *
     * @deprecated as of lemon 1.4.0, in favor of using {@link #create(String, String, BusinessObjectWrapper)}
     */
    @Deprecated
    public static BusinessException create(String msgCd, String msgInfo, Object businessObject) {
        return new BusinessException(msgCd, msgInfo, businessObject);
    }

    public static BusinessException create(String msgCd, String msgInfo, BusinessObjectWrapper businessObject) {
        return new BusinessException(msgCd, msgInfo, businessObject);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param cause
     * @param businessObject
     * @param alertParameters
     */
    public static BusinessException create(String msgCd, String msgInfo, Throwable cause, BusinessObjectWrapper businessObject, String... alertParameters) {
        return new BusinessException(msgCd, msgInfo, cause, businessObject, alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param cause
     * @param businessObject
     * @param alertParameters
     */
    public static BusinessException create(AlertCapable alertCapable, Throwable cause, BusinessObjectWrapper businessObject, String... alertParameters) {
        return new BusinessException(alertCapable, cause, businessObject, alertParameters);
    }

    public String getMsgCd() {
        return msgCd;
    }

    public void setMsgCd(String msgCd) {
        this.msgCd = msgCd;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public void setMsgInfo(String msgInfo) {
        this.msgInfo = msgInfo;
    }

    public Throwable getCause() {
        return cause;
    }

    @Override
    public Object getBusinessObject() {
        return this.businessObject == null ? null : this.businessObject.getBusinessObject();
    }

    @Override
    public String[] getParameters() {
        return alertParameters;
    }

    private void setAlertParameters(String... alertParameters) {
        if (null != alertParameters && alertParameters.length > 0) {
            this.alertParameters = new String[alertParameters.length];
            for (int i = 0; i < alertParameters.length; i++) {
                this.alertParameters[i] = alertParameters[i];
            }
        }
    }

}
