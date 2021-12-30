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

package com.galaxy.lemon.framework.stream.client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class BindingMetadataHolder {

    private List<BindingMetadata> bindingMetadatas = new ArrayList<>();

    public void addBindingMetadata(String prefix, String group, String bindingName) {
        this.bindingMetadatas.add(new BindingMetadata(prefix, group, bindingName));
    }

    public List<BindingMetadata> getBindingMetadatas() {
        return bindingMetadatas;
    }

    public static class BindingMetadata {
        private String prefix;
        private String group;
        private String bindingName;

        public BindingMetadata(String prefix, String group, String bindingName) {
            this.prefix = prefix;
            this.group = group;
            this.bindingName = bindingName;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getGroup() {
            return group;
        }

        public String getBindingName() {
            return bindingName;
        }
    }
}
