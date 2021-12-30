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

package com.galaxy.lemon.gradle

import com.galaxy.lemon.gradle.mybatis.MybatisExtension
import com.galaxy.lemon.gradle.mybatis.MybatisTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Zip
import org.gradle.internal.reflect.Instantiator
import org.gradle.invocation.DefaultGradle

/**
 * lemon gradle plugin
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

class LemonPlugin implements Plugin<Project>{
    public static final String GROUP = "lemon"
    public static final String START_URL = "http://120.76.22.56/microservices-platform/framework/tree/master/lemon-test/shell/start.sh"
    private Project project
    private Instantiator instantiator

    @Override
    void apply(Project project) {
        this.project = project
        this.instantiator = ((DefaultGradle) project.getGradle()).getServices().get(Instantiator.class)
        createLemonTask()
        createMybatisGenTask()
        createDistTask()
    }

    void createLemonTask() {
        Task task = this.project.task('lemonHelp').doLast {
            println "*********************************************"
            println "This is lemon gradle plugin, list tasks: "
            println "[mybatisGen]   mybatis code auto generation"
            println "[clearTarget]  clear directory 'build/target'"
            println "[copyTarget]   copy file to 'build/target'"
            println "[dist]         distribution package generation"
            println "*********************************************"
        }
        task.setGroup(GROUP)
    }

    void createMybatisGenTask() {
        this.project.container(MybatisExtension.class)
        MybatisExtension extension = project.getExtensions().create("mybatisGen", MybatisExtension.class)
        MybatisTask mybatisTask = this.project.getTasks().create("mybatisGen", MybatisTask.class)
        mybatisTask.mybatisExtension = extension
        mybatisTask.setGroup(GROUP)
    }

    void createDistTask() {
        String buildPath = project.getBuildDir().getPath()
        String targetPath = buildPath + "/target"

        Delete deleteTask = this.project.getTasks().create("clearTarget", Delete.class)
        deleteTask.delete = 'build/target'
        deleteTask.setGroup(GROUP)


        Copy copyTask = this.project.getTasks().create("copyTarget", Copy.class)
        copyTask.dependsOn( "clearTarget", "build")
        copyTask.from(buildPath+"/libs").include("*.jar").exclude("*-sources.jar").into(targetPath)
        copyTask.doLast {
//            String binDir = targetPath+"/bin"
//            this.project.getTasks().create("copyTarget2", Copy.class).from(project.getBuildDir().getParent()+"/shell").into(binDir).execute()
//            ExtensionResolver resolver = new URLResolver();
//            resolver.setDestdir(new File(binDir))
//            resolver.setUrl(new URL(START_URL))
//            resolver.resolve(null, this.project.getAnt().getProject())
//            println "Download... $START_URL"
        }
        copyTask.setGroup(GROUP)

        Zip zip = this.project.getTasks().create("dist", Zip.class)
        zip.from(targetPath)
        zip.dependsOn("copyTarget")
        zip.setGroup(GROUP)
    }
}
