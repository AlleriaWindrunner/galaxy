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

package com.galaxy.lemon.gradle.mybatis

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

class MybatisTask extends DefaultTask{

    @Optional
    public MybatisExtension mybatisExtension

    @Nested
    MybatisExtension getMybatisExtension() {
        return mybatisExtension
    }

    @TaskAction
    void generate() {
        String generatorConfig = mybatisExtension.configFile
        if (null == generatorConfig || 0 == generatorConfig.length()) {
            println "use default generator config file 'generatorConfig.xml' "
            generatorConfig = "generatorConfig.xml"
        }

        println "** configfile $generatorConfig"
        println "** overwrite $mybatisExtension.overwrite"

        List<String> args = new ArrayList<>()
        args.add("-configfile")
        args.add(generatorConfig)
        if(mybatisExtension.overwrite) {
            args.add("-overwrite")
        }
        MybatisGenerator.exec(args.toArray(new String [args.size()]))
    }

    void setGeneratorConfig(String generatorConfig) {
        this.generatorConfig = generatorConfig
    }
}
