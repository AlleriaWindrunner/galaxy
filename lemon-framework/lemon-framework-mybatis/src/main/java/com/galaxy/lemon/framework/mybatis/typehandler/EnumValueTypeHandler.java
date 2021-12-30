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

package com.galaxy.lemon.framework.mybatis.typehandler;

import com.galaxy.lemon.framework.valuable.Valuable;
import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class EnumValueTypeHandler<E extends Enum<E> & Valuable<T>, T> extends BaseTypeHandler<E> {

    private Class<E> type;

    public EnumValueTypeHandler(Class<E> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {

        if (jdbcType == null){
            T value = parameter.getValue();
            LemonException.throwLemonExceptionIfNecessary(null == value, ErrorMsgCode.SYS_ERROR.getMsgCd(), "Enum value could not be null.");
            if(value instanceof Integer ||value instanceof Short || value instanceof Character || value instanceof Byte){
                ps.setInt(i,(Integer)value);
            }else if(value instanceof String){
                ps.setString(i,(String)value);
            }else if(value instanceof Boolean){
                ps.setBoolean(i, (Boolean)value);
            }else if(value instanceof Long){
                ps.setLong(i, (Long)value);
            }else if(value instanceof Double){
                ps.setDouble(i,(Double)value);
            }else if(value instanceof Float){
                ps.setFloat(i, (Float)value);
            }else{
                LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Unsupported [value] type \"" + value.getClass() + "\" of enum ");
            }
        } else {
            ps.setObject(i, parameter.getValue(), jdbcType.TYPE_CODE);
        }

    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        return toEnum(columnValue);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValue = rs.getString(columnIndex);
        return toEnum(columnValue);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValue = cs.getString(columnIndex);
        return toEnum(columnValue);
    }

    private E toEnum(String value){
        EnumSet<E> set =EnumSet.allOf(type);
        if (set == null || set.size()<= 0) {
            return null;
        }
        for (E e : set) {
            T t = e.getValue();
            if(t != null){
                if(String.valueOf(t).equals(value)){
                    return e;
                }
            }
        }
        return null;
    }
}
