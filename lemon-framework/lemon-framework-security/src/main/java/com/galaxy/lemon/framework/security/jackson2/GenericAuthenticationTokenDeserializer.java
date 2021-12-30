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

package com.galaxy.lemon.framework.security.jackson2;

import com.galaxy.lemon.common.exception.ErrorMsgCode;
import com.galaxy.lemon.common.exception.LemonException;
import com.galaxy.lemon.framework.security.auth.GenericAuthenticationToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class GenericAuthenticationTokenDeserializer extends JsonDeserializer<GenericAuthenticationToken> {
    @Override
    public GenericAuthenticationToken deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        GenericAuthenticationToken token = null;
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        Boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();
        JsonNode principalNode = readJsonNode(jsonNode, "principal");
        Object principal = null;
        if(principalNode.isObject()) {
            principal = mapper.readValue(principalNode.traverse(mapper), Object.class);
        } else {
            principal = principalNode.asText();
        }
//        JsonNode credentialsNode = readJsonNode(jsonNode, "credentials");
//        Object credentials;
//        if (credentialsNode.isNull()) {
//            credentials = null;
//        } else {
//            credentials = credentialsNode.asText();
//        }
        List<GrantedAuthority> authorities = mapper.readValue(
                readJsonNode(jsonNode, "authorities").traverse(mapper), new TypeReference<List<GrantedAuthority>>() {
                });
        if (authenticated) {
            token = new GenericAuthenticationToken(principal, authorities);
        } else {
            //token = new GenericAuthenticationToken(principal);
            LemonException.throwLemonException(ErrorMsgCode.SYS_ERROR, "Could not support unauthenticated GenericAuthenticationToken in json deserializer.");
        }
        JsonNode detailsNode = readJsonNode(jsonNode, "details");
        if (detailsNode.isNull()) {
            token.setDetails(null);
        } else {
            token.setDetails(detailsNode);
        }
        return token;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
