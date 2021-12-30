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
import com.galaxy.lemon.common.utils.JudgeUtils;

import static com.galaxy.lemon.common.utils.StringUtils.formatString;

/**
 * lemon framework 统一异常
 * 抛出该异常会回滚事务
 * 消息码会做统一处理返回给调用方
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class LemonException extends RuntimeException implements AlertCapable {
    private static final long serialVersionUID = 5539431043384054654L;
    /**
     * 系统异常消息码
     */
    public static final String SYS_ERROR_MSGCD = ErrorMsgCode.SYS_ERROR.getMsgCd();
    
    private String msgCd;
    private String msgInfo;
    private Throwable cause;
    /**
     * 是否业务异常，业务代码中只能抛出业务异常
     */
    private boolean businessException = false;
    
    /**
     * @param msgCd
     * @param msgInfo
     * @param cause
     */
    public LemonException(String msgCd, String msgInfo, Throwable cause) {
        super(msgCd+" : "+msgInfo, cause);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
        this.cause = cause;
    }
    
    /**
     * @param msgCd
     * @param msgInfo
     */
    public LemonException(String msgCd, String msgInfo) {
        super(msgCd + " :  " + msgInfo);
        this.msgCd = msgCd;
        this.msgInfo = msgInfo;
    }
    
    /**
     * @param alertCapable
     */
    public LemonException(AlertCapable alertCapable) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo());
    }
    
    /**
     * @param alertCapable
     * @param cause
     */
    public LemonException(AlertCapable alertCapable, Throwable cause) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause);
    }

    /**
     * @param alertCapable
     * @param cause
     *
     * @deprecated as of lemon 1.4.x, in favor of usring ${@link BusinessException} or ${@link BusinessNoRollbackException}
     */
    public LemonException(AlertCapable alertCapable, Throwable cause, boolean isBusinessException) {
        this(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), cause);
        this.businessException = isBusinessException;
    }
    
    /**
     * @param msgCd
     */
    public LemonException(String msgCd) {
        super(msgCd);
        this.msgCd = msgCd;
    }

    /**
     *
     * @param msgCd
     * @param businessException
     *
     * @deprecated as of lemon 1.4.x, in favor of usring ${@link BusinessException} or ${@link BusinessNoRollbackException}
     *
     */
    public LemonException(String msgCd, boolean businessException) {
        super(msgCd);
        this.msgCd = msgCd;
        this.businessException = businessException;
    }

    /**
     *
     * @param alertCapable
     * @param businessException
     *
     * @deprecated as of lemon 1.4.x, in favor of usring ${@link BusinessException} or ${@link BusinessNoRollbackException}
     *
     */
    public LemonException(AlertCapable alertCapable, boolean businessException) {
        this(alertCapable);
        this.businessException = businessException;
    }
    
    /**
     * @param cause
     */
    public LemonException(Throwable cause) {
        super(SYS_ERROR_MSGCD, cause);
        this.cause = cause;
        this.msgCd = SYS_ERROR_MSGCD;
    }
    
    /**
     * @param msgInfo
     * @param cause
     */
    public LemonException(String msgInfo, Throwable cause) {
        super(SYS_ERROR_MSGCD + " : " + msgInfo);
        this.msgCd = SYS_ERROR_MSGCD;
        this.msgInfo = msgInfo;
        this.cause = cause;
    }
    
    public LemonException() {
        super(SYS_ERROR_MSGCD);
        this.msgCd = SYS_ERROR_MSGCD;
    }

    /**
     * 抛出业务异常
     * 业务异常不会打印error logging
     * @param msgCd
     *
     * @deprecated as of lemon 1.4.x, in favor of usring ${@link BusinessException} or ${@link BusinessNoRollbackException}
     */
    @Deprecated
    public static void throwBusinessException(String msgCd) {
        throw new LemonException(msgCd, true);
    }

    /**
     * 抛出业务异常
     * 业务异常不会打印error logging
     * @param alertCapable
     *
     * @deprecated as of lemon 1.4.x, in favor of usring ${@link BusinessException} or ${@link BusinessNoRollbackException}
     */
    @Deprecated
    public static void throwBusinessException(AlertCapable alertCapable) {
        throw new LemonException(alertCapable, true);
    }

    /**
     *
     */
    public static void throwLemonException() {
        throw new LemonException();
    }
    
    /**
     * @param msgCd
     */
    public static void throwLemonException(String msgCd) {
        throw new LemonException(msgCd);
    }
    
    /**
     * @param t
     */
    public static void throwLemonException(Throwable t) {
        if(t instanceof LemonException) {
            throw (LemonException)t;
        }
        throw new LemonException(t);
    }
    
    /**
     * @param msgInfo
     * @param t
     */
    public static void throwLemonException(String msgInfo, Throwable t) {
        throw new LemonException(msgInfo, t);
    }
    
    /**
     * @param msgCd
     * @param msgInfo
     */
    public static void throwLemonException(String msgCd, String msgInfo) {
        throw new LemonException(msgCd, msgInfo);
    }

    /**
     * @param msgCd
     * @param msgInfo
     * @param args format string args
     */
    public static void throwLemonException(String msgCd, String msgInfo, String[] args) {
        throw new LemonException(msgCd, formatString(msgInfo, args));
    }

    /**
     * @param alertCapable
     * @param msgInfo
     */
    public static void throwLemonException(AlertCapable alertCapable, String msgInfo) {
        throw new LemonException(alertCapable.getMsgCd(), msgInfo);
    }

    /**
     * @param alertCapable
     * @param msgInfo
     * @param args format string args for msgInfo
     */
    public static void throwLemonException(AlertCapable alertCapable, String msgInfo, String[] args) {
        throw new LemonException(alertCapable.getMsgCd(), formatString(msgInfo, args));
    }
    
    /**
     * @param msgCd
     * @param msgInfo
     * @param throwable
     */
    public static void throwLemonException(String msgCd, String msgInfo, Throwable throwable) {
        throw new LemonException(msgCd, msgInfo, throwable);
    }

    /**
     * @param msgCd
     * @param msgInfo
     * @param args format string args for msgInfo
     * @param throwable
     */
    public static void throwLemonException(String msgCd, String msgInfo, String[] args, Throwable throwable) {
        throw new LemonException(msgCd, formatString(msgInfo, args), throwable);
    }
    
    /**
     * @param alertCapable
     */
    public static void throwLemonException(AlertCapable alertCapable) {
        throw new LemonException(alertCapable.getMsgCd(), alertCapable.getMsgInfo());
    }
    
    /**
     * @param alertCapable
     * @param throwable
     */
    public static void throwLemonException(AlertCapable alertCapable, Throwable throwable) {
        throw new LemonException(alertCapable.getMsgCd(), alertCapable.getMsgInfo(), throwable);
    }

    /**
     *
     * @param flag
     * @param msgCd
     * @param msgInfo
     * @param throwable
     */
    public static void throwLemonExceptionIfNecessary(boolean flag, String msgCd, String msgInfo, Throwable throwable) {
        if (flag) {
            throw new LemonException(msgCd, msgInfo, throwable);
        }
    }

    /**
     *
     * @param flag
     * @param throwable
     */
    public static void throwLemonExceptionIfNecessary(boolean flag, Throwable throwable) {
        if (flag) {
            throw new LemonException(throwable);
        }
    }

    /**
     *
     * @param flag
     * @param alertCapable
     * @param throwable
     */
    public static void throwLemonExceptionIfNecessary(boolean flag, AlertCapable alertCapable, Throwable throwable) {
        if (flag) {
            throw new LemonException(alertCapable, throwable);
        }
    }

    /**
     *
     * @param flag
     * @param alertCapable
     * @param msgInfo
     * @param args
     */
    public static void throwLemonExceptionIfNecessary(boolean flag, AlertCapable alertCapable, String msgInfo, String[] args) {
        if (flag) {
            throwLemonException(alertCapable, msgInfo, args);
        }
    }

    /**
     *
     * @param flag
     * @param alertCapable
     * @param msgInfo
     */
    public static void throwLemonExceptionIfNecessary(boolean flag, AlertCapable alertCapable, String msgInfo) {
        if (flag) {
            throwLemonException(alertCapable, msgInfo);
        }
    }

    /**
     *
     * @param flag
     * @param msgCd
     * @param msgInfo
     */
    public static void throwLemonExceptionIfNecessary(boolean flag, String msgCd, String msgInfo) {
        if (flag) {
            throw new LemonException(msgCd, msgInfo);
        }
    }

    /**
     *
     * @param flag
     * @param alertCapable
     */
    public static void throwLemonExceptionIfNecessary(boolean flag, AlertCapable alertCapable) {
        if (flag) {
            throw new LemonException(alertCapable);
        }
    }

    /**
     * 创建LemonException
     * 
     * @param t
     * @return
     */
    public static LemonException create(Throwable t) {
        return isLemonException(t) ? (LemonException) t : new LemonException(t);
    }
    
    public static LemonException create(String msgCd) {
        return new LemonException(msgCd);
    }
    
    public static LemonException create(String msgCd, String msgInfo) {
        return new LemonException(msgCd, msgInfo);
    }

    public static LemonException create(String msgCd, String msgInfo, String[] args) {
        return new LemonException(msgCd, formatString(msgInfo, args));
    }
    
    public static LemonException create(AlertCapable alertCapable) {
        return new LemonException(alertCapable);
    }

    /**
     * @param alertCapable
     * @param msgInfo
     */
    public static LemonException create(AlertCapable alertCapable, String msgInfo) {
        return new LemonException(alertCapable.getMsgCd(), msgInfo);
    }

    /**
     * @param alertCapable
     * @param msgInfo
     * @param args
     */
    public static LemonException create(AlertCapable alertCapable, String msgInfo, String[] args) {
        return new LemonException(alertCapable.getMsgCd(), formatString(msgInfo, args));
    }

    public static LemonException create(AlertCapable alertCapable, Throwable throwable) {
        return new LemonException(alertCapable, throwable);
    }

    /**
     * 业务异常
     * @param alertCapable
     * @param throwable
     * @return
     *
     * @deprecated as of lemon 1.4.x, in favor of usring ${@link BusinessException} or ${@link BusinessNoRollbackException}
     */
    @Deprecated
    public static LemonException createBusinessException(AlertCapable alertCapable, Throwable throwable) {
        return new LemonException(alertCapable, throwable, true);
    }

    /**
     * 判断是否为LemonException
     * @param throwable
     * @return
     */
    public static boolean isLemonException(Throwable throwable) {
        return JudgeUtils.isNotNull(throwable) && throwable instanceof LemonException;
    }
    
    @Override
    public String getMsgCd() {
        return msgCd;
    }

    public void setMsgCd(String msgCd) {
        this.msgCd = msgCd;
    }

    @Override
    public String getMsgInfo() {
        return msgInfo;
    }

    public void setMsgInfo(String msgInfo) {
        this.msgInfo = msgInfo;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public boolean isBusinessException() {
        return businessException;
    }

    /**
     * 业务异常
     * @param businessException
     * @return
     *
     * @deprecated as of lemon 1.4.x, in favor of usring ${@link BusinessException} or ${@link BusinessNoRollbackException}
     */
    @Deprecated
    public void setBusinessException(boolean businessException) {
        this.businessException = businessException;
    }
    
}
