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

package com.galaxy.lemon.framework.xss.jackson;

import com.galaxy.lemon.framework.xss.resolver.EncodingXssResolver;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;
import java.util.Optional;

/**
 * jackson 2.9之前的版本不可以继承StringDeserializer，2.9以后可以直接继承StringDeserializer
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class XssStringDeserializer extends StdScalarDeserializer<String> {

    private EncodingXssResolver xssResolver;

    public XssStringDeserializer(EncodingXssResolver xssResolver) {
        super(String.class);
        this.xssResolver = xssResolver;
    }

    // @since 2.8.8
    protected final static int FEATURES_ACCEPT_ARRAYS =
            DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS.getMask() |
                    DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT.getMask();

    /**
     * @since 2.2
     */
    public final static StringDeserializer instance = new StringDeserializer();

    // since 2.6, slightly faster lookups for this very common type
    @Override
    public boolean isCachable() { return true; }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return xssResolve(p.getText());
        }
        JsonToken t = p.getCurrentToken();
        // [databind#381]
        if (t == JsonToken.START_ARRAY) {
            return xssResolve(_deserializeFromArray(p, ctxt));
        }
        // need to gracefully handle byte[] data, as base64
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (ob instanceof byte[]) {
                return xssResolve(ctxt.getBase64Variant().encode((byte[]) ob, false));
            }
            // otherwise, try conversion using toString()...
            return xssResolve(ob.toString());
        }
        // allow coercions for other scalar types
        // 17-Jan-2018, tatu: Related to [databind#1853] avoid FIELD_NAME by ensuring it's
        //   "real" scalar
        if (t.isScalarValue()) {
            String text = p.getValueAsString();
            if (text != null) {
                return xssResolve(text);
            }
        }

        return xssResolve((String) ctxt.handleUnexpectedToken(_valueClass, p));
    }

    // Since we can never have type info ("natural type"; String, Boolean, Integer, Double):
    // (is it an error to even call this version?)
    @Override
    public String deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return deserialize(p, ctxt);
    }

    // @since 2.8.8
    @Override
    protected String _deserializeFromArray(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException
    {
        JsonToken t;
        if (ctxt.hasSomeOfFeatures(FEATURES_ACCEPT_ARRAYS)) {
            t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
                    return getNullValue(ctxt);
                }
            }
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final String parsed = _parseString(p, ctxt);
                if (p.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(p, ctxt);
                }
                return parsed;
            }
        } else {
            t = p.getCurrentToken();
        }
        return (String) ctxt.handleUnexpectedToken(_valueClass, t, p, null);
    }

    private String xssResolve(String text) {
        return Optional.ofNullable(text).map(xssResolver::resolveXss).orElse(null);
    }

}
