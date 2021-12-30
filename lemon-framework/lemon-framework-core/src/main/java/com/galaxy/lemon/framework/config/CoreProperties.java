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

package com.galaxy.lemon.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties(prefix = "lemon.core")
public class CoreProperties {
    private GenericDTOProperties genericDTO;

    private ResponseDTOProperties responseDTO;

    private CommandDTOProperties commandDTO;

    private LemonDataProperties lemonData;

    public GenericDTOProperties getGenericDTO() {
        return genericDTO;
    }

    public void setGenericDTO(GenericDTOProperties genericDTO) {
        this.genericDTO = genericDTO;
    }

    public ResponseDTOProperties getResponseDTO() {
        return responseDTO;
    }

    public void setResponseDTO(ResponseDTOProperties responseDTO) {
        this.responseDTO = responseDTO;
    }

    public CommandDTOProperties getCommandDTO() {
        return commandDTO;
    }

    public void setCommandDTO(CommandDTOProperties commandDTO) {
        this.commandDTO = commandDTO;
    }

    public LemonDataProperties getLemonData() {
        return lemonData;
    }

    public void setLemonData(LemonDataProperties lemonData) {
        this.lemonData = lemonData;
    }

    public static class GenericDTOProperties {
        private String className;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static class ResponseDTOProperties {
        private String className;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static class CommandDTOProperties {
        private String className;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static class LemonDataProperties {
        private String className;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }
}
