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

package com.galaxy.lemon.common;


/**
 * 支持的环境
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public enum Env {
    DEV, CI, SIT, UAT, STR, PRE, PRD;
    
    public static Env getDefaultEnv() {
        return Env.PRD;
    }
    
    public static String getProfile(Env env) {
        return env.toString().toLowerCase();
    }
    
    /**
     * 是否生产环境
     * @param env
     * @return
     */
    public static boolean isPrd(Env env) {
        if(PRD.equals(env)) {
            return true;
        }
        return false;
    }
    
}
