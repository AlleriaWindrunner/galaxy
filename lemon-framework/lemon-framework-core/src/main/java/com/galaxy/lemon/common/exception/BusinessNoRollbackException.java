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
 * 业务异常，抛出该异常不回滚事务
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see BusinessException
 * @since 1.0.0
 */

public class BusinessNoRollbackException extends Exception implements AlertCapable, AlertParameterizable, BusinessObjectCapable<Object> {

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
    public BusinessNoRollbackException(String msgCd, String msgInfo, Throwable cause) {
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
     * @param alertParameters
     *
     * @deprecated as fo lemon 1.4.0, in favor of using {@link #BusinessNoRollbackException(String, String, Throwable, BusinessObjectWrapper, String...)}
     */
    @Deprecated
    public BusinessNoRollbackException(String msgCd, String msgInfo, Throwable cause, Object businessObject, String... alertParameters) {
        this(msgCd, msgInfo, cause, BusinessObjectWrapper.wrap(businessObject), alertParameters);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param cause
     * @param businessObject
     * @param alertParameters
     */
    public BusinessNoRollbackException(String msgCd, String msgInfo, Throwable cause, BusinessObjectWrapper businessObject, String... alertParameters) {
        super(msgCd+" : "+msgInfo, cause);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
        this.cause = cause;
        this.businessObject = businessObject;
        setAlertParameters(alertParameters);
    }

    /**
     * @param msgCd
     * @param msgInfo
     */
    public BusinessNoRollbackException(String msgCd, String msgInfo) {
        super(msgCd + " :  " + msgInfo);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     *
     * @deprecated as fo lemon 1.4.0, in favor of using {@link #BusinessNoRollbackException(String, String, BusinessObjectWrapper)}
     */
    @Deprecated
    public BusinessNoRollbackException(String msgCd, String msgInfo, Object businessObject) {
        this(msgCd, msgInfo, BusinessObjectWrapper.wrap(businessObject));
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     */
    public BusinessNoRollbackException(String msgCd, String msgInfo, BusinessObjectWrapper businessObject) {
        this(msgCd, msgInfo);
        this.businessObject = businessObject;
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     * @param alertParameters
     *
     * @deprecated as fo lemon 1.4.0, in favor of using {@link #BusinessNoRollbackException(String, String, BusinessObjectWrapper, String...)}
     */
    @Deprecated
    public BusinessNoRollbackException(String msgCd, String msgInfo, Object businessObject, String... alertParameters) {
        this(msgCd, msgInfo, BusinessObjectWrapper.wrap(businessObject), alertParameters);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     * @param alertParameters
     */
    public BusinessNoRollbackException(String msgCd, String msgInfo, BusinessObjectWrapper businessObject, String... alertParameters) {
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
    public BusinessNoRollbackException(String msgCd, String msgInfo, String... alertParameters) {
        this(msgCd, msgInfo);
        setAlertParameters(alertParameters);
    }

    /**
     * @param alertCapable
     */
    public BusinessNoRollbackException(AlertCapable alertCapable) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo());
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     *
     * @deprecated as fo lemon 1.4.0, in favor of using {@link #BusinessNoRollbackException(AlertCapable, BusinessObjectWrapper)}
     */
    @Deprecated
    public BusinessNoRollbackException(AlertCapable alertCapable, Object businessObject) {
        this(alertCapable, BusinessObjectWrapper.wrap(businessObject));
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     */
    public BusinessNoRollbackException(AlertCapable alertCapable, BusinessObjectWrapper businessObject) {
        this(alertCapable);
        this.businessObject = businessObject;
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     * @param alertParameters
     *
     * @deprecated as fo lemon 1.4.0, in favor of using {@link #BusinessNoRollbackException(AlertCapable, BusinessObjectWrapper, String...)}
     */
    public BusinessNoRollbackException(AlertCapable alertCapable, Object businessObject, String... alertParameters) {
        this(alertCapable, BusinessObjectWrapper.wrap(businessObject), alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     * @param alertParameters
     */
    public BusinessNoRollbackException(AlertCapable alertCapable, BusinessObjectWrapper businessObject, String... alertParameters) {
        this(alertCapable);
        this.businessObject = businessObject;
        setAlertParameters(alertParameters);
    }

    public BusinessNoRollbackException(AlertCapable alertCapable, String... alertParameters) {
        this(alertCapable);
        setAlertParameters(alertParameters);
    }

    /**
     * @param alertCapable
     * @param cause
     */
    public BusinessNoRollbackException(AlertCapable alertCapable, Throwable cause) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause);
    }

    /**
     *
     * @param alertCapable
     * @param cause
     * @param businessObject
     *
     * @deprecated as fo lemon 1.4.0, in favor of using {@link #BusinessNoRollbackException(AlertCapable, Throwable, BusinessObjectWrapper)}
     */
    @Deprecated
    public BusinessNoRollbackException(AlertCapable alertCapable, Throwable cause, Object businessObject) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause, businessObject);
    }

    public BusinessNoRollbackException(AlertCapable alertCapable, Throwable cause, BusinessObjectWrapper businessObject) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause, businessObject);
    }

    public BusinessNoRollbackException(AlertCapable alertCapable, Throwable cause, String... alertParameters) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause, null, alertParameters);
    }

    /**
     * @param msgCd
     */
    public BusinessNoRollbackException(String msgCd) {
        super(msgCd);
        this.msgCd = msgCd;
    }

    public static BusinessNoRollbackException create(String msgCd) {
        return new BusinessNoRollbackException(msgCd);
    }

    public static BusinessNoRollbackException create(String msgCd, String msgInfo) {
        return new BusinessNoRollbackException(msgCd, msgInfo);
    }

    public static BusinessNoRollbackException create(AlertCapable alertCapable) {
        return new BusinessNoRollbackException(alertCapable);
    }

    public static BusinessNoRollbackException create(AlertCapable alertCapable, Throwable throwable) {
        return new BusinessNoRollbackException(alertCapable, throwable);
    }

    /**
     *
     * @param msgCd
     * @param msgInfo
     * @param businessObject
     * @return
     *
     * @deprecated as fo lemon 1.4.0, in favor of using {@link #create(String, String, BusinessObjectWrapper)}
     */
    @Deprecated
    public static BusinessNoRollbackException create(String msgCd, String msgInfo, Object businessObject) {
        return new BusinessNoRollbackException(msgCd, msgInfo, businessObject);
    }

    public static BusinessNoRollbackException create(String msgCd, String msgInfo, BusinessObjectWrapper businessObject) {
        return new BusinessNoRollbackException(msgCd, msgInfo, businessObject);
    }

    public static BusinessNoRollbackException create(String msgCd, String msgInfo, String... alertParameters) {
        return new BusinessNoRollbackException(msgCd, msgInfo, alertParameters);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     * @return
     *
     * @deprecated as fo lemon 1.4.0, in favor of using {@link #create(AlertCapable, BusinessObjectWrapper)}
     */
    @Deprecated
    public static BusinessNoRollbackException create(AlertCapable alertCapable, Object businessObject) {
        return new BusinessNoRollbackException(alertCapable, businessObject);
    }

    public static BusinessNoRollbackException create(AlertCapable alertCapable, BusinessObjectWrapper businessObject) {
        return new BusinessNoRollbackException(alertCapable, businessObject);
    }

    /**
     *
     * @param alertCapable
     * @param businessObject
     * @param alertParameters
     * @return
     *
     * @deprecated as fo lemon 1.4.0, in favor of using {@link #create(AlertCapable, BusinessObjectWrapper, String...)}
     */
    @Deprecated
    public static BusinessNoRollbackException create(AlertCapable alertCapable, Object businessObject, String... alertParameters) {
        return new BusinessNoRollbackException(alertCapable, businessObject, alertParameters);
    }

    public static BusinessNoRollbackException create(AlertCapable alertCapable, BusinessObjectWrapper businessObject, String... alertParameters) {
        return new BusinessNoRollbackException(alertCapable, businessObject, alertParameters);
    }

    public static BusinessNoRollbackException create(AlertCapable alertCapable, String... alertParameters) {
        return new BusinessNoRollbackException(alertCapable, alertParameters);
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
        return null == this.businessObject ? null : this.businessObject.getBusinessObject();
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
